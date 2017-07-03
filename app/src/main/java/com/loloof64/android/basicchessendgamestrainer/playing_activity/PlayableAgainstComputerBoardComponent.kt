package com.loloof64.android.basicchessendgamestrainer.playing_activity

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.view.MotionEvent
import com.loloof64.android.basicchessendgamestrainer.MyApplication
import com.loloof64.android.basicchessendgamestrainer.PlayingActivity
import com.loloof64.android.basicchessendgamestrainer.R
import com.loloof64.android.basicchessendgamestrainer.UCICommandAnswerCallback
import karballo.Board
import karballo.Move
import karballo.Piece
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

        val MAX_LEGAL_POSITIONS_COUNT = 250

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
            'p', 'P' -> Piece.PAWN
            'n', 'N' -> Piece.KNIGHT
            'b', 'B' -> Piece.BISHOP
            'r', 'R' -> Piece.ROOK
            'q', 'Q' -> Piece.QUEEN
            'k', 'K' -> Piece.KING
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

    override fun relatedPosition(): Board {
        return _relatedPosition
    }

    override fun replacePositionWith(positionFen: String) {
        _relatedPosition.setFenMove(positionFen, null)
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
    private val _relatedPosition = Board()
    private var _highlightedTargetCell:Pair<Int, Int>? = null
    private var _highlightedStartCell:Pair<Int, Int>? = null
    private var _pendingPromotionInfo:PromotionInfo? = null

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val whiteTurn = _relatedPosition.turn
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

                        val legalMovesStore = IntArray(MAX_LEGAL_POSITIONS_COUNT)
                        relatedPosition().getLegalMoves(legalMovesStore)
                        val matchingMoves = legalMovesStore.filter { move ->
                            val matchingMoveStartCell = Move.getFromSquare(move)
                            val matchingMoveEndCell = Move.getToSquare(move)
                            val playerMoveStartCell = coordinatesToSquare(startFile, startRank)
                            val playerMoveEndCell = coordinatesToSquare(endFile, endRank)

                            (matchingMoveStartCell == playerMoveStartCell) && (matchingMoveEndCell == playerMoveEndCell)
                        }

                        val isPromotionMove = matchingMoves.isNotEmpty() && Move.isPromotion(matchingMoves.first())

                        if (isPromotionMove) {
                            _pendingPromotionInfo = PromotionInfo(startFile, startRank, endFile, endRank)
                            askForPromotionPiece()
                        }
                        else {
                            val sameCellSelected = (startFile == endFile) && (startRank == endRank)
                            if (matchingMoves.isEmpty()) {
                                if (!sameCellSelected) reactForIllegalMove()
                            } else {
                                updateHighlightedMove()
                                addMoveToList(matchingMoves.first())
                                _relatedPosition.doMove(matchingMoves.first())
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
                    val movedPiece = _relatedPosition.getPieceAt(coordinatesToSquare(file = file, rank = rank))
                    val isOccupiedSquare = movedPiece != '.'
                    val isWhiteTurn = _relatedPosition.turn
                    val isWhitePiece = movedPiece.isUpperCase()
                    val isBlackPiece = movedPiece.isLowerCase()
                    val isOneOfOurPiece = (isWhiteTurn && isWhitePiece) || (!isWhiteTurn && isBlackPiece)

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
            _relatedPosition.setFenMove(fen, lastMove = _relatedPosition.lastMoveSan)
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

            _relatedPosition.fen = startFen
            _playerHasWhite = isWhiteToPlay()
            waitForPlayerGoal()
            invalidate()
        }
        catch (e:IllegalArgumentException) {
            Logger.getLogger("BasicChessEndgamesTrainer").severe("Position $startFen is invalid and could not be load.")
        }
    }

    fun isWhiteToPlay() : Boolean {
        return _relatedPosition.turn
    }

    fun makeComputerPlay(){
        val isComputerToMove = _playerHasWhite != isWhiteToPlay()
        if (isComputerToMove) {
            val myApp = context.applicationContext as MyApplication
            myApp.uciInteract("position fen ${_relatedPosition.fen}")
            myApp.uciInteract("go")
        }
    }

    fun processComponentMove(longUCICommandAnswer: String) {
        val moveStr = longUCICommandAnswer.split("\n").filter { it.isNotEmpty() }.last().split(" ")[1]
        val chessMovePossibilities = if (moveStr.length > 4) {
            val (sFile, sRank, eFile, eRank, promotion) = moveStr.toCharArray()
            val legalMovesStorage = IntArray(MAX_LEGAL_POSITIONS_COUNT)
            _relatedPosition.getLegalMoves(legalMovesStorage)
            legalMovesStorage.filter { currentMove ->
                val currentMoveIsPromotion = Move.isPromotion(currentMove)
                val currentMoveFromSquare = Move.getFromSquare(currentMove)
                val currentMoveToSquare = Move.getToSquare(currentMove)
                val currentMovePromotionPiece = Move.getPiecePromoted(currentMove)

                val commandMoveFromSquare = coordinatesToSquare(sFile.toFile(), sRank.toRank())
                val commandMoveToSquare = coordinatesToSquare(eFile.toFile(), eRank.toRank())

                currentMoveIsPromotion
                        && commandMoveFromSquare == currentMoveFromSquare
                        && commandMoveToSquare == currentMoveToSquare
                        && currentMovePromotionPiece == promotion.toPromotionPiece()
            }
            legalMovesStorage
        }
        else {
            val (sFile, sRank, eFile, eRank) = moveStr.toCharArray()
            val legalMovesStorage = IntArray(MAX_LEGAL_POSITIONS_COUNT)
            _relatedPosition.getLegalMoves(legalMovesStorage)
            legalMovesStorage.filter { currentMove ->
                val currentMoveFromSquare = Move.getFromSquare(currentMove)
                val currentMoveToSquare = Move.getToSquare(currentMove)

                val commandMoveFromSquare = coordinatesToSquare(sFile.toFile(), sRank.toRank())
                val commandMoveToSquare = coordinatesToSquare(eFile.toFile(), eRank.toRank())

                commandMoveFromSquare == currentMoveFromSquare
                        && commandMoveToSquare == currentMoveToSquare
            }
            legalMovesStorage
        }
        handler.post {
            if (chessMovePossibilities.isNotEmpty()) {
                val move = chessMovePossibilities.first()
                val fromSquare = Move.getFromSquare(move)
                val toSquare = Move.getToSquare(move)
                val (startFile, startRank) = squareToCoordinates(fromSquare)
                val (endFile, endRank) = squareToCoordinates(toSquare)
                addMoveToList(move)
                _relatedPosition.doMove(move)
                _moveToHighlightFrom = Pair(startFile, startRank)
                _moveToHighlightTo = Pair(endFile, endRank)
                updateHighlightedMove()

                invalidate()
                checkIfGameFinished()
            }
        }
    }

    fun checkIfGameFinished() {
        val endGameStatus = _relatedPosition.isEndGame
        val isCheckMate = Math.abs(endGameStatus) == 1
        val isDraw = endGameStatus == 99
        if (isCheckMate) {
            when(context){
                is PlayingActivity -> with(context as PlayingActivity){
                    setPlayerGoalTextId(R.string.checkmate, alertMode = true)
                    activatePositionNavigation()
                }
            }
            _gameFinished = true
        }
        if (isDraw){
            when(context){
                is PlayingActivity -> with(context as PlayingActivity){
                    setPlayerGoalTextId(R.string.position_draw, alertMode = true)
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
                    val isWhiteTurn = _relatedPosition.turn

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

    private fun addMoveToList(move: Int) {
        when (context) {
            is PlayingActivity -> {
                _relatedPosition.doMove(move)
                val fromSquare = Move.getFromSquare(move)
                val toSquare = Move.getToSquare(move)
                val (startFile, startRank) = squareToCoordinates(fromSquare)
                val (endFile, endRank) = squareToCoordinates(toSquare)
                val fenAfterMove = _relatedPosition.fen
                val isWhiteTurn = _relatedPosition.turn
                if (!hasStartedToWriteMoves() && !isWhiteTurn){
                    with(context as PlayingActivity){
                        addPositionInMovesList(getMoveNumber().toString(), "", MoveToHighlight(-1,-1,-1,-1))
                        addPositionInMovesList("..", "",MoveToHighlight(-1,-1,-1,-1))
                        addPositionInMovesList(Move.sanToFigurines(Move.toSan(_relatedPosition, move))!!,
                            fen = fenAfterMove, moveToHighlight = MoveToHighlight(
                            startFile = startFile,
                            startRank = startRank,
                            endFile = endFile,
                            endRank = endRank
                        ))
                    }
                }
                else {
                    with(context as PlayingActivity){
                        if (isWhiteTurn) addPositionInMovesList(getMoveNumber().toString(), "",
                                MoveToHighlight(-1,-1,-1,-1))
                        addPositionInMovesList(
                                san = Move.sanToFigurines(Move.toSan(_relatedPosition, move))!!,
                                fen = fenAfterMove,
                                moveToHighlight = MoveToHighlight(
                                        startFile = startFile,
                                        startRank = startRank,
                                        endFile = endFile,
                                        endRank = endRank
                                )
                        )
                    }
                }
            }
        }
        _startedToWriteMoves = true
    }

    fun validatePromotionMove(promotedPieceType: Int) {
        when(_pendingPromotionInfo) {
            null -> {}
            else -> {
                val startSquare = coordinatesToSquare(file = _pendingPromotionInfo!!.startFile,
                        rank = _pendingPromotionInfo!!.startRank)
                val endSquare = coordinatesToSquare(file = _pendingPromotionInfo!!.endFile,
                        rank = _pendingPromotionInfo!!.endRank)
                val matchingMoves = IntArray(MAX_LEGAL_POSITIONS_COUNT)
                _relatedPosition.getLegalMoves(matchingMoves)
                matchingMoves.filter { currentMove ->
                    val currentMoveStartSquare = Move.getFromSquare(currentMove)
                    val currentMoveEndSquare = Move.getToSquare(currentMove)
                    val isPromotion = Move.isPromotion(currentMove)
                    val promotionPiece = Move.getPiecePromoted(currentMove)

                    currentMoveStartSquare == startSquare
                    && currentMoveEndSquare == endSquare
                    && isPromotion
                    && promotionPiece == promotedPieceType
                }
                if (matchingMoves.isEmpty()) Logger.getLogger("BasicChessEndgamesTrainer").severe("Illegal move ! (When validating promotion)")
                else {
                    val move = matchingMoves.first()
                    addMoveToList(move)
                    _relatedPosition.doMove(move)
                }
                _pendingPromotionInfo = null
                _highlightedTargetCell = null
                invalidate()
            }
        }
    }

    fun gameFinished() = _gameFinished
    fun hasStartedToWriteMoves() = _startedToWriteMoves

    fun getMoveNumber(): Int {
        return (_relatedPosition.moveNumber / 2) + 1
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