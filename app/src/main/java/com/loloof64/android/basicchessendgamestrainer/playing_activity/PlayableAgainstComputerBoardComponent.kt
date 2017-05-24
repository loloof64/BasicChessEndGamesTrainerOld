package com.loloof64.android.basicchessendgamestrainer.playing_activity

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.view.MotionEvent
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
        if (playingComponentRef.get()?.isWaitingForPlayerGoal() ?: true) {
            playingComponentRef.get()?.notifyPlayerGoal(answer)
        } else {
            playingComponentRef.get()?.processComponentMove(answer)
        }
    }
}

data class PromotionInfo(val startFile: Int, val startRank: Int,
                         val endFile: Int, val endRank: Int)

class PlayableAgainstComputerBoardComponent(context: Context, override val attrs: AttributeSet?,
                             defStyleAttr: Int) : BoardComponent(context, attrs, defStyleAttr) {

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null, 0)

    private val minSpacePercentage: Int

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.PlayableAgainstComputerBoardComponent)
        val computed = typedArray.getInt(R.styleable
                .PlayableAgainstComputerBoardComponent_min_dimension_percentage, 100)
        typedArray.recycle()
        minSpacePercentage = computed
    }

    override fun computeMinAvailableSpacePercentage():Int {
        return minSpacePercentage
    }

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

    override fun relatedPosition(): Position {
        return _relatedPosition
    }

    override fun replacePositionWith(board: Position) {
        _relatedPosition = board
    }

    override fun highlightedTargetCell(): Pair<Int, Int>? {
        return _highlightedTargetCell
    }

    override fun highlightedStartCell(): Pair<Int, Int>? {
        return _highlightedStartCell
    }

    private fun askForPromotionPiece() {
        when(context) {
            is PlayingActivity -> (context as PlayingActivity).askForPromotionPiece()
            else -> {}
        }
    }

    private fun reactForIllegalMove() {
        when(context) {
            is PlayingActivity -> (context as PlayingActivity).reactForIllegalMove()
            else -> {}
        }
    }

    // Fields from PieceMoveInteraction interface
    private var _relatedPosition = Position()
    private var _highlightedTargetCell:Pair<Int, Int>? = null
    private var _highlightedStartCell:Pair<Int, Int>? = null
    private var _pendingPromotionInfo:PromotionInfo? = null

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val whiteTurn = _relatedPosition.toPlay == Chess.WHITE
        val notPlayerTurn = _playerHasWhite != whiteTurn
        if (notPlayerTurn || _gameFinished) return true

        val x = event.x
        val y = event.y
        val action = event.action

        val cellSize = (measuredWidth.min(measuredHeight)) / 9
        val cellX = ((x-cellSize*0.5) / cellSize).toInt()
        val cellY = ((y-cellSize*0.5) / cellSize).toInt()
        val file = if (reversed) 7-cellX else cellX
        val rank = if (reversed) cellY else 7 - cellY

        if (file in 0..7 && rank in 0..7){
            when (action){
                MotionEvent.ACTION_UP -> {
                    val moveSelectionHasStarted = _highlightedTargetCell != null && _highlightedStartCell != null
                    if (moveSelectionHasStarted){
                        val startFile = _highlightedStartCell!!.first
                        val startRank = _highlightedStartCell!!.second
                        val endFile = _highlightedTargetCell!!.first
                        val endRank = _highlightedTargetCell!!.second

                        val pieceAtStartCellIsPawn = _relatedPosition.getPiece(Chess.coorToSqi(startFile, startRank)).toShort() == Chess.PAWN
                        val isWhiteTurn = _relatedPosition.toPlay == Chess.WHITE
                        val isPromotionMove = pieceAtStartCellIsPawn && (if (isWhiteTurn) endRank == 7 else endRank == 0)

                        if (isPromotionMove) {
                            _pendingPromotionInfo = PromotionInfo(startFile, startRank, endFile, endRank)
                            askForPromotionPiece()
                        }
                        else {
                            val matchMoves = _relatedPosition.allMoves.filter { currentMove ->
                                val currentMoveFrom = Move.getFromSqi(currentMove)
                                val currentMoveTo = Move.getToSqi(currentMove)

                                val commandMoveFrom = Chess.coorToSqi(startFile, startRank)
                                val commandMoveTo = Chess.coorToSqi(endFile, endRank)

                                (commandMoveFrom == currentMoveFrom)
                                        && (commandMoveTo == currentMoveTo)
                            }
                            val sameCellSelected = (startFile == endFile) && (startRank == endRank)
                            if (matchMoves.isEmpty()) {
                                if (!sameCellSelected) reactForIllegalMove()
                            } else {
                                updateHighlightedMove()
                                addMoveToList(matchMoves.first())
                                _relatedPosition.doMove(matchMoves.first())
                            }
                        }

                        invalidate()
                        checkIfGameFinished()
                        if (!_gameFinished) {
                            val computerToPlay = _playerHasWhite != isWhiteToPlay()
                            if (computerToPlay) makeComputerPlay()
                        }
                    }
                    _highlightedStartCell = null
                    _highlightedTargetCell = null
                }
                MotionEvent.ACTION_DOWN -> {
                    val movedPiece = _relatedPosition.getStone(Chess.coorToSqi(file, rank)).toShort()
                    val isOccupiedSquare = movedPiece != Chess.NO_PIECE
                    val isWhiteTurn = _relatedPosition.toPlay == Chess.WHITE
                    val isBlackTurn = _relatedPosition.toPlay != Chess.WHITE
                    val isWhitePiece = movedPiece < 0
                    val isBlackPiece = movedPiece > 0
                    val isOneOfOurPiece = (isWhiteTurn && isWhitePiece) || (isBlackTurn && isBlackPiece)

                    _highlightedTargetCell = if (isOccupiedSquare && isOneOfOurPiece) Pair(file, rank) else null
                    _highlightedStartCell = _highlightedTargetCell
                    invalidate()
                }
                MotionEvent.ACTION_MOVE -> {
                    val moveSelectionHasStarted = _highlightedTargetCell != null && _highlightedStartCell != null
                    if (moveSelectionHasStarted) {
                        _highlightedTargetCell = Pair(file, rank)
                        invalidate()
                    }
                }
            }
        }

        return true
    }

    fun playerHasWhite() = _playerHasWhite

    fun reloadPosition(fen: String, playerHasWhite: Boolean,
                       gameFinished: Boolean, waitingForPlayerGoal: Boolean,
                       hasStartedToWriteMoves: Boolean,
                       moveToHighlightFromFile: Int, moveToHighlightFromRank: Int,
                       moveToHighlightToFile: Int, moveToHighlightToRank: Int){
        try {
            _gameFinished = gameFinished
            when(context){
                is PlayingActivity -> with(context as PlayingActivity){
                    if (_gameFinished) {
                        activatePositionNavigation()
                    } else {
                        disallowPositionNavigation()
                    }
                }
            }
            _relatedPosition = Position(fen)
            _playerHasWhite = playerHasWhite
            _startedToWriteMoves = hasStartedToWriteMoves
            _moveToHighlightFrom = if (moveToHighlightFromFile in 0..7 &&
                    moveToHighlightFromRank in 0..7) Pair(moveToHighlightFromFile, moveToHighlightFromRank) else null
            _moveToHighlightTo = if (moveToHighlightToFile in 0..7 &&
                    moveToHighlightToRank in 0..7) Pair(moveToHighlightToFile, moveToHighlightToRank) else null
            updateHighlightedMove()

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
            when(context){
                is PlayingActivity -> with(context as PlayingActivity){
                    disallowPositionNavigation()
                }
            }
            _startedToWriteMoves = false
            _moveToHighlightFrom = null
            _moveToHighlightTo = null
            updateHighlightedMove()

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
            if (chessMovePossibilities.isNotEmpty()) {
                val move = chessMovePossibilities.first()
                addMoveToList(move)
                _relatedPosition.doMove(move)
                _moveToHighlightFrom = Pair(
                        Chess.sqiToCol(Move.getFromSqi(move)), Chess.sqiToRow(Move.getFromSqi(move))
                )
                _moveToHighlightTo = Pair(
                        Chess.sqiToCol(Move.getToSqi(move)), Chess.sqiToRow(Move.getToSqi(move))
                )
                updateHighlightedMove()

                invalidate()
                checkIfGameFinished()
            }
        }
    }

    fun checkIfGameFinished() {
        if (_relatedPosition.isMate) {
            when(context){
                is PlayingActivity -> with(context as PlayingActivity){
                    setPlayerGoalTextId(R.string.checkmate, alertMode = true)
                    activatePositionNavigation()
                }
            }
            _gameFinished = true
        }
        if (_relatedPosition.isStaleMate){
            when(context){
                is PlayingActivity -> with(context as PlayingActivity){
                    setPlayerGoalTextId(R.string.stalemate, alertMode = true)
                    activatePositionNavigation()
                }
            }
            _gameFinished = true
        }
        else if (_relatedPosition.halfMoveClock >= 100){
            when(context){
                is PlayingActivity -> with(context as PlayingActivity){
                    setPlayerGoalTextId(R.string.fiftyMoveDraw, alertMode = true)
                    activatePositionNavigation()
                }
            }
            _gameFinished = true
        }
        else if (isDrawByThreeFolds()){
            when(context){
                is PlayingActivity -> with(context as PlayingActivity){
                    setPlayerGoalTextId(R.string.position_repetitions_draw, alertMode = true)
                    activatePositionNavigation()
                }
            }
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
                        ChessResult.WHITE_WIN -> R.string.white_play_for_mate
                        ChessResult.BLACK_WIN -> R.string.black_play_for_mate
                        ChessResult.DRAW -> R.string.should_be_draw
                        ChessResult.UNDECIDED -> R.string.empty_string
                    }

                    when (context) {
                        is PlayingActivity -> (context as PlayingActivity).setPlayerGoalTextId(_playerGoal, alertMode = false)
                    }
                    _waitingForPlayerGoal = false
                }
            }
        }
    }

    private fun addMoveToList(move: Short) {
        when (context) {
            is PlayingActivity -> {
                val positionClone = Position(_relatedPosition.fen)
                positionClone.doMove(move)
                val fenAfterMove = positionClone.fen
                val isWhiteTurn = _relatedPosition.toPlay == Chess.WHITE
                if (!hasStartedToWriteMoves() && !isWhiteTurn){
                    with(context as PlayingActivity){
                        addPositionInMovesList(getMoveNumber().toString(), "", MoveToHighlight(-1,-1,-1,-1))
                        addPositionInMovesList("..", "",MoveToHighlight(-1,-1,-1,-1))
                        addPositionInMovesList(localizedSAN(san = Move.getSAN(move, relatedPosition())),
                            fen = fenAfterMove, moveToHighlight = MoveToHighlight(
                            startFile = Chess.sqiToCol(Move.getFromSqi(move)),
                            startRank = Chess.sqiToRow(Move.getFromSqi(move)),
                            endFile = Chess.sqiToCol(Move.getToSqi(move)),
                            endRank = Chess.sqiToRow(Move.getToSqi(move))
                        ))
                    }
                }
                else {
                    with(context as PlayingActivity){
                        if (isWhiteTurn) addPositionInMovesList(getMoveNumber().toString(), "",
                                MoveToHighlight(-1,-1,-1,-1))
                        addPositionInMovesList(
                                san = localizedSAN(Move.getSAN(move, relatedPosition())),
                                fen = fenAfterMove,
                                moveToHighlight = MoveToHighlight(
                                    startFile = Chess.sqiToCol(Move.getFromSqi(move)),
                                    startRank = Chess.sqiToRow(Move.getFromSqi(move)),
                                    endFile = Chess.sqiToCol(Move.getToSqi(move)),
                                    endRank = Chess.sqiToRow(Move.getToSqi(move))
                                )
                        )
                    }
                }
            }
        }
        _startedToWriteMoves = true
    }

    private fun  localizedSAN(san: String): String {
        val originalSan = "PNBRQK"
        val sanTranslations = context.getString(R.string.san_chess_pieces)

        val mappings = originalSan.zip(sanTranslations).toMap()
        return san.map { if (it in originalSan) mappings[it] else it }.joinToString("")
    }

    fun validatePromotionMove(promotedPieceType: Short) {
        when(_pendingPromotionInfo) {
            null -> {}
            else -> {
                val move = _relatedPosition.getPawnMove(_pendingPromotionInfo!!.startFile,
                        Chess.coorToSqi(_pendingPromotionInfo!!.endFile, _pendingPromotionInfo!!.endRank),
                        promotedPieceType.toInt())
                if (move == Move.ILLEGAL_MOVE) Logger.getLogger("BasicChessEndgamesTrainer").severe("Illegal move ! (When validating promotion)")
                else {
                    addMoveToList(move)
                    _relatedPosition.doMove(move)
                }
                _pendingPromotionInfo = null
                _highlightedTargetCell = null
                invalidate()
            }
        }
    }

    private fun isDrawByThreeFolds(): Boolean {
        infix fun String.equalsPosition(other: String): Boolean {
            val thisParts = this.split(" ")
            val otherParts = other.split(" ")
            return (0..3).all { thisParts[it] == otherParts[it] }
        }

        val currentPositionFen = _relatedPosition.fen
        return when (context){
            is PlayingActivity -> (context as PlayingActivity).getAllPositions()
                    .filter { it equalsPosition currentPositionFen }.size >= 3
            else -> false
        }
    }

    fun gameFinished() = _gameFinished
    fun hasStartedToWriteMoves() = _startedToWriteMoves

    fun getMoveNumber(): Int {
        return (_relatedPosition.plyNumber / 2) + 1
    }

    override fun setHighlightedMove(fromFile: Int, fromRank: Int, toFile: Int, toRank: Int) {
        super.setHighlightedMove(fromFile, fromRank, toFile, toRank)
        _moveToHighlightFrom = Pair(fromFile, fromRank)
        _moveToHighlightTo = Pair(toFile, toRank)
    }

    fun getMoveToHighlightFromFile() = if (_moveToHighlightFrom != null) _moveToHighlightFrom!!.first
                                    else -1

    fun getMoveToHighlightFromRank() = if (_moveToHighlightFrom != null) _moveToHighlightFrom!!.second
                                    else -1

    fun getMoveToHighlightToFile() = if (_moveToHighlightTo != null) _moveToHighlightTo!!.first
                                    else -1

    fun getMoveToHighlightToRank() = if (_moveToHighlightTo != null) _moveToHighlightTo!!.second
                                    else -1

    private fun updateHighlightedMove(){
        setHighlightedMove(
                _moveToHighlightFrom?.first ?: -1, _moveToHighlightFrom?.second ?: -1,
                _moveToHighlightTo?.first ?: -1, _moveToHighlightTo?.second ?: -1
        )
        invalidate()
    }

    private var _playerHasWhite = true
    private var _gameFinished = false
    private var _waitingForPlayerGoal = true
    private var _playerGoal = R.string.empty_string
    private var _startedToWriteMoves = false
    private var _moveToHighlightFrom:Pair<Int, Int>? = null
    private var _moveToHighlightTo:Pair<Int, Int>? = null
}