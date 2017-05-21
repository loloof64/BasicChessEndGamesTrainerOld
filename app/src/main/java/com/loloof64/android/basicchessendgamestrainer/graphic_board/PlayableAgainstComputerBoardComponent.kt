package com.loloof64.android.basicchessendgamestrainer.graphic_board

import android.content.Context
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
import java.lang.ref.WeakReference
import java.util.logging.Logger

class MyUciCommandCallback(playingComponent: PlayableAgainstComputerBoardComponent) : UCICommandAnswerCallback {
    private val playingComponentRef:WeakReference<PlayableAgainstComputerBoardComponent> = WeakReference(playingComponent)

    override fun execute(answer: String) {
        if (playingComponentRef.get()?.isReadyToPlay() ?: false) {
            playingComponentRef.get()?.processComponentMove(answer)
        } else {
            playingComponentRef.get()?.notifyPlayerGoal(answer)
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
            if (!_gameFinished) makeComputerPlay()
        }

        return true
    }

    fun isReversed() : Boolean = reversed

    fun setReversedState(newReversedState: Boolean) {
        reversed = newReversedState
    }

    fun activateHighlightedCell(highlightedCellFile : Int, highlightedCellRank : Int) {
        _highlightedCell = Pair(highlightedCellFile, highlightedCellRank)
    }

    fun playerHasWhite() = _playerHasWhite

    fun setFinishedState(finished: Boolean){
        _gameFinished = finished
    }

    fun new_game(startFen: String, playerHasWhite: Boolean ?) {
        try {
            _gameFinished = false
            _relatedPosition = Position(startFen)
            _playerHasWhite = if (playerHasWhite == null) isWhiteToPlay()
            else playerHasWhite
            invalidate()
        }
        catch (e:IllegalArgumentException) {
            java.util.logging.Logger.getLogger("BasicChessEndgamesTrainer").severe("Position $startFen is invalid and could not be load.")
        }
        val isComputerToMove = _playerHasWhite != isWhiteToPlay()
        if (isComputerToMove) makeComputerPlay()
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

    fun isReadyToPlay() = _readyToPlay

    fun checkIfGameFinished() {
        if (_relatedPosition.isMate) {
            Toast.makeText(context, R.string.checkmate, Toast.LENGTH_LONG).show()
            _gameFinished = true
            _readyToPlay = false
        }
        if (_relatedPosition.isStaleMate){
            Toast.makeText(context, R.string.stalemate, Toast.LENGTH_LONG).show()
            _gameFinished = true
            _readyToPlay = false
        }
        else if (_relatedPosition.halfMoveClock >= 100){
            Toast.makeText(context, R.string.fiftyMoveDraw, Toast.LENGTH_LONG).show()
            _gameFinished = true
            _readyToPlay = false
        }
    }

    fun notifyPlayerGoal(longUCICommandAnswer: String){
        val infoLine = longUCICommandAnswer.split("\n").filter { it.isNotEmpty() && it.startsWith("info")}.last()
        handler.post {
            Logger.getLogger("BasicChessEndgamesTrainer").info("UCI info is '$infoLine'")
            val isWhiteTurn = _relatedPosition.toPlay == Chess.WHITE
            when (positionResultFromPositionInfo(infoLine, isWhiteTurn)){
                ChessResult.WHITE_WIN -> Toast.makeText(MyApplication.getApplicationContext(), R.string.white_play_for_mate, Toast.LENGTH_SHORT).show()
                ChessResult.BLACK_WIN -> Toast.makeText(MyApplication.getApplicationContext(), R.string.black_play_for_mate, Toast.LENGTH_SHORT).show()
                ChessResult.DRAW -> Toast.makeText(MyApplication.getApplicationContext(), R.string.should_be_draw, Toast.LENGTH_SHORT).show()
                ChessResult.UNDECIDED -> {}
            }
            _readyToPlay = true
        }
    }

    fun gameFinished() = _gameFinished

    private var _playerHasWhite = true
    private var _gameFinished = false
    private var _readyToPlay = false

}