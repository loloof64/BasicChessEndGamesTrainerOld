package com.loloof64.android.basicchessendgamestrainer.graphic_board

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.Toast
import chesspresso.Chess
import chesspresso.move.Move
import chesspresso.position.FEN
import chesspresso.position.Position
import com.loloof64.android.basicchessendgamestrainer.MyApplication
import com.loloof64.android.basicchessendgamestrainer.PlayingActivity
import com.loloof64.android.basicchessendgamestrainer.R
import com.loloof64.android.basicchessendgamestrainer.UCICommandAnswerCallback
import kotlinx.android.synthetic.main.activity_playing.*
import java.lang.ref.WeakReference
import java.util.logging.Logger

class MyUciCommandCallback(playingComponent: PlayableAgainstComputerBoardComponent) : UCICommandAnswerCallback {
    private val playingComponentRef:WeakReference<PlayableAgainstComputerBoardComponent> = WeakReference(playingComponent)

    override fun execute(answer: String) {
        if (playingComponentRef.get()?.isWaitingForPlayerGoal() ?: true) {
            playingComponentRef.get()?.notifyPlayerGoal(answer)
        } else {
            playingComponentRef.get()?.processComponentMove(answer)
        }
    }
}

class PlayableAgainstComputerBoardComponent(context: Context, override val attrs: AttributeSet?,
                             defStyleAttr: Int) : BoardComponent(context, attrs, defStyleAttr), PieceMoveInteraction {

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null, 0)

    companion object {

        enum class ChessResult {
            WHITE_WIN, BLACK_WIN, DRAW, UNDECIDED
        }

        /**
         * Gets the expected game result from a uci position info line (line starting with info).
         * @param positionInfoLine - String - the info line to convert.
         * @param whiteToMove - Boolean - true if was white to move when the evaluation was done, false otherwise.
         * @return ChessResult - ChessResult constant.
         */
        fun positionResultFromPositionInfo(positionInfoLine: String, whiteToMove: Boolean): ChessResult {
            val infoLineStartingAtScore = positionInfoLine.split("score ").last()
            val positionResult = if (infoLineStartingAtScore.startsWith("mate")) {
                val movesCount = Integer.parseInt(infoLineStartingAtScore.split(" ")[1])
                if (whiteToMove) {
                    if (movesCount > 0) ChessResult.WHITE_WIN else ChessResult.BLACK_WIN
                }
                else {
                    if (movesCount > 0) ChessResult.BLACK_WIN else ChessResult.WHITE_WIN
                }
            } else {
                val score = Integer.parseInt(infoLineStartingAtScore.split(" ")[1])
                if (Math.abs(score) > 1000){
                    if (whiteToMove) {
                        if (score > 0) ChessResult.WHITE_WIN else ChessResult.BLACK_WIN
                    }
                    else {
                        if (score > 0) ChessResult.BLACK_WIN else ChessResult.WHITE_WIN
                    }

                } else {
                    if (Math.abs(score) < 50) ChessResult.DRAW else ChessResult.UNDECIDED
                }
            }
            return positionResult
        }
    }

    fun Char.toFile(): Int {
        val intValue = this.toInt()
        if (intValue < 97 || intValue > 104) throw IllegalArgumentException("ToFile() no applicable for $this")
        return intValue - 97
    }

    fun Char.toRank(): Int {
        val intValue = this.toInt()
        if (intValue < 49 || intValue > 56) throw IllegalArgumentException("ToRank() no applicable for $this")
        return intValue - 49
    }

    fun Char.toPromotionPiece():Int {
        return when(this) {
            'p', 'P' -> Chess.PAWN
            'n', 'N' -> Chess.KNIGHT
            'b', 'B' -> Chess.BISHOP
            'r', 'R' -> Chess.ROOK
            'q', 'Q' -> Chess.QUEEN
            'k', 'K' -> Chess.KING
            else -> throw RuntimeException("Unrecognized piece char $this")
        }.toInt()
    }

    init {
        (context.applicationContext as MyApplication).setCallbackForUciCommandAnswer(
                MyUciCommandCallback(this))
    }

    fun isWaitingForPlayerGoal() = _waitingForPlayerGoal

    // Mainly used for serialisation purpose
    fun setWaitingForPlayerGoalFlag(waiting: Boolean){
        _waitingForPlayerGoal = waiting
    }

    fun waitForPlayerGoal() {
        _waitingForPlayerGoal = true
        (context.applicationContext as MyApplication).uciNewGame(_relatedPosition.fen)
        (context.applicationContext as MyApplication).uciInteract("go")
    }

    fun playerGoal() = _playerGoal

    override fun relatedPosition(): Position {
        return _relatedPosition
    }

    override fun replacePositionWith(board: Position) {
        _relatedPosition = board
    }

    override fun highlightedCell(): Pair<Int, Int>? {
        return _highlightedCell
    }

    override fun askForPromotionPiece() {
        when(context) {
            is PlayingActivity -> (context as PlayingActivity).askForPromotionPiece()
            else -> {}
        }
    }

    override fun reactForIllegalMove() {
        when(context) {
            is PlayingActivity -> (context as PlayingActivity).reactForIllegalMove()
            else -> {}
        }
    }

    // Fields from PieceMoveInteraction interface
    override var _relatedPosition = Position()
    override var _highlightedCell:Pair<Int, Int>? = null
    override var _pendingPromotionInfo:PromotionInfo? = null

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val whiteTurn = _relatedPosition.toPlay == Chess.WHITE
        val notPlayerTurn = _playerHasWhite != whiteTurn
        if (notPlayerTurn || _gameFinished) return true

        val x = event.x
        val y = event.y
        val action = event.action

        val cellSize = (measuredWidth.min(measuredHeight)) / 9
        val file = ((x-cellSize*0.5) / cellSize).toInt()
        val rank = 7 - ((y-cellSize*0.5) / cellSize).toInt()

        if (action == MotionEvent.ACTION_DOWN && file >= 0 && file < 8 && rank >= 0 && rank < 8) {
            if (reversed) reactOnClick(7-file, 7-rank) else reactOnClick(file, rank)
            invalidate()
            checkIfGameFinished()
            if (!_gameFinished) {
                val computerToPlay = _playerHasWhite != isWhiteToPlay()
                if (computerToPlay) makeComputerPlay()
            }
        }

        return true
    }

    fun playerHasWhite() = _playerHasWhite

    fun reloadPosition(fen: String, playerHasWhite: Boolean, gameFinished: Boolean, waitingForPlayerGoal: Boolean){
        try {
            _gameFinished = gameFinished
            _relatedPosition = Position(fen)
            _playerHasWhite = playerHasWhite
            setWaitingForPlayerGoalFlag(waitingForPlayerGoal)
            invalidate()
            val computerToPlay = _playerHasWhite != isWhiteToPlay()
            if (computerToPlay) makeComputerPlay()
        }
        catch (e:IllegalArgumentException) {
            Logger.getLogger("BasicChessEndgamesTrainer").severe("Position $fen is invalid and could not be load.")
        }
    }

    fun new_game(startFen: String) {
        try {
            _gameFinished = false
            _relatedPosition = Position(startFen)
            _playerHasWhite = isWhiteToPlay()
            waitForPlayerGoal()
            invalidate()
        }
        catch (e:IllegalArgumentException) {
            Logger.getLogger("BasicChessEndgamesTrainer").severe("Position $startFen is invalid and could not be load.")
        }
    }

    fun isWhiteToPlay() : Boolean {
        return _relatedPosition.toPlay == Chess.WHITE
    }

    fun makeComputerPlay(){
        val isComputerToMove = _playerHasWhite != isWhiteToPlay()
        if (isComputerToMove) {
            val myApp = context.applicationContext as MyApplication
            myApp.uciInteract("position fen ${FEN.getFEN(_relatedPosition)}")
            myApp.uciInteract("go")
        }
    }

    fun processComponentMove(longUCICommandAnswer: String) {
        val moveStr = longUCICommandAnswer.split("\n").filter { it.isNotEmpty() }.last().split(" ")[1]
        val chessMovePossibilities = if (moveStr.length > 4) {
            val (sFile, sRank, eFile, eRank, promotion) = moveStr.toCharArray()
            _relatedPosition.allMoves.filter { currentMove ->
                val currentMoveIsPromotion = Move.isPromotion(currentMove)
                val currentMoveFrom = Move.getFromSqi(currentMove)
                val currentMoveTo = Move.getToSqi(currentMove)
                val currentMovePromotionPiece = Move.getPromotionPiece(currentMove)

                val commandMoveFrom = Chess.coorToSqi(sFile.toFile(), sRank.toRank())
                val commandMoveTo = Chess.coorToSqi(eFile.toFile(), eRank.toRank())

                currentMoveIsPromotion
                        && commandMoveFrom == currentMoveFrom
                        && commandMoveTo == currentMoveTo
                        && currentMovePromotionPiece == promotion.toPromotionPiece()
            }
        }
        else {
            val (sFile, sRank, eFile, eRank) = moveStr.toCharArray()
            _relatedPosition.allMoves.filter { currentMove ->
                val currentMoveFrom = Move.getFromSqi(currentMove)
                val currentMoveTo = Move.getToSqi(currentMove)

                val commandMoveFrom = Chess.coorToSqi(sFile.toFile(), sRank.toRank())
                val commandMoveTo = Chess.coorToSqi(eFile.toFile(), eRank.toRank())

                commandMoveFrom == currentMoveFrom
                        && commandMoveTo == currentMoveTo
            }
        }
        handler.post {
            if (chessMovePossibilities.isEmpty()) reactForIllegalMove()
            else _relatedPosition.doMove(chessMovePossibilities.first())
            invalidate()
            checkIfGameFinished()
        }
    }

    fun checkIfGameFinished() {
        if (_relatedPosition.isMate) {
            Toast.makeText(context, R.string.checkmate, Toast.LENGTH_LONG).show()
            _gameFinished = true
        }
        if (_relatedPosition.isStaleMate){
            Toast.makeText(context, R.string.stalemate, Toast.LENGTH_LONG).show()
            _gameFinished = true
        }
        else if (_relatedPosition.halfMoveClock >= 100){
            Toast.makeText(context, R.string.fiftyMoveDraw, Toast.LENGTH_LONG).show()
            _gameFinished = true
        }
    }

    fun notifyPlayerGoal(longUCICommandAnswer: String) {
        val commandAnswerParts = longUCICommandAnswer.split("\n")
        if (commandAnswerParts.isNotEmpty()) {
            val infoLines = commandAnswerParts.filter { it.isNotEmpty() && it.startsWith("info") }
            if (infoLines.isNotEmpty()) {
                Handler(context.mainLooper).post {
                    Logger.getLogger("BasicChessEndgamesTrainer").info("UCI info is '${infoLines.last()}'")
                    val isWhiteTurn = _relatedPosition.toPlay == Chess.WHITE

                    _playerGoal = when (positionResultFromPositionInfo(infoLines.last(), isWhiteTurn)) {
                        ChessResult.WHITE_WIN -> context.getString(R.string.white_play_for_mate)
                        ChessResult.BLACK_WIN -> context.getString(R.string.black_play_for_mate)
                        ChessResult.DRAW -> context.getString(R.string.should_be_draw)
                        ChessResult.UNDECIDED -> ""
                    }

                    when (context) {
                        is PlayingActivity -> (context as PlayingActivity).label_player_goal.text = _playerGoal
                    }
                    _waitingForPlayerGoal = false
                }
            }
        }
    }

    fun gameFinished() = _gameFinished

    private var _playerHasWhite = true
    private var _gameFinished = false
    private var _waitingForPlayerGoal = true
    private var _playerGoal = ""
}