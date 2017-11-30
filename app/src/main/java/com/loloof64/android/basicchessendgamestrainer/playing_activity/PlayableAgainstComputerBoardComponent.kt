package com.loloof64.android.basicchessendgamestrainer.playing_activity

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import com.github.bhlangonijr.chesslib.*
import com.github.bhlangonijr.chesslib.move.Move
import com.github.bhlangonijr.chesslib.move.MoveGenerator
import com.github.bhlangonijr.chesslib.move.MoveList
import com.loloof64.android.basicchessendgamestrainer.PlayingActivity
import com.loloof64.android.basicchessendgamestrainer.R
import com.loloof64.android.basicchessendgamestrainer.exercise_chooser.buildSquare
import java.lang.Math.abs
import java.util.logging.Logger

val MIN_MATE_SCORE = 1000
val MIN_DRAW_SCORE = 10
val MIN_KNOWN_WIN = 250

fun String.getFANforMove(move: Move): String {
    val board = Board()
    board.loadFromFEN(this)
    val moveList = MoveList(board.fen)
    moveList.add(move)
    return moveList.toFANArray()[0]
}

data class PromotionInfo(val startFile: Int, val startRank: Int,
                         val endFile: Int, val endRank: Int)

class PlayableAgainstComputerBoardComponent(context: Context, attrs: AttributeSet?,
                             defStyleAttr: Int) : BoardComponent(context, attrs, defStyleAttr),
                                                    SimpleUciObserver {

    fun isDrawByRepetitions():Boolean {
        val historySize = _relatedPosition.history.size
        val i = Math.min(historySize - 1, _relatedPosition.getHalfMoveCounter())
        var rept = 0
        if (historySize >= 4) {
            val lastKey = _relatedPosition.getHistory().get(historySize - 1)
            var x = 2
            while (x <= i) {
                val k = _relatedPosition.getHistory().get(historySize - x - 1)
                if (k == lastKey) {
                    rept++
                    if (rept >= 2) {
                        return true
                    }
                }
                x += 2
            }
        }
        return false
    }

    override fun consumeMove(move: Move) {
        if (!_waitingForPlayerGoal){
            val isComputerToMove = _playerHasWhite != isWhiteToPlay()
            if (isComputerToMove){
                handler.post {
                    addMoveToList(move, _relatedPosition.fen.getFANforMove(move))
                    _moveToHighlightFrom = move.from
                    _moveToHighlightTo = move.to
                     updateHighlightedMove()

                    invalidate()
                    checkIfGameFinished()
                }
            }
        }
    }

    override fun consumeScore(score: Int) {
        if (_waitingForPlayerGoal){

            val stringId = if (abs(score) > MIN_MATE_SCORE) {
                if (isWhiteToPlay()) R.string.white_play_for_mate
                else R.string.black_play_for_mate
            } else if (abs(score) > MIN_KNOWN_WIN) {
                if (isWhiteToPlay()) R.string.white_play_for_win
                else R.string.black_play_for_win
            } 
            else if (abs(score) <= MIN_DRAW_SCORE) R.string.should_be_draw
            else R.string.empty_string

            when (context){
                is PlayingActivity -> (context as PlayingActivity).setPlayerGoalTextId(stringId, false)
            }
            _waitingForPlayerGoal = false
        }
    }

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null, 0)

    private val minSpacePercentage: Int

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.PlayableAgainstComputerBoardComponent)
        val computed = typedArray.getInt(R.styleable
                .PlayableAgainstComputerBoardComponent_min_dimension_percentage, 100)
        typedArray.recycle()
        minSpacePercentage = computed
        EngineInteraction.setUciObserver(this)
    }

    override fun computeMinAvailableSpacePercentage():Int {
        return minSpacePercentage
    }

    fun isWaitingForPlayerGoal() = _waitingForPlayerGoal

    // Mainly used for serialisation purpose
    private fun setWaitingForPlayerGoalFlag(waiting: Boolean){
        _waitingForPlayerGoal = waiting
    }

    private fun waitForPlayerGoal() {
        _waitingForPlayerGoal = true
        EngineInteraction.evaluate(_relatedPosition.fen)
    }

    override fun relatedPosition(): Board {
        return _relatedPosition
    }

    override fun replacePositionWith(positionFEN: String) {
        _relatedPosition.loadFromFEN(positionFEN)
    }

    override fun highlightedTargetCell(): SquareCoordinates? {
        return _highlightedTargetCell
    }

    override fun highlightedStartCell(): SquareCoordinates? {
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
    private var _highlightedTargetCell:SquareCoordinates? = null
    private var _highlightedStartCell:SquareCoordinates? = null
    private var _pendingPromotionInfo:PromotionInfo? = null

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val whiteTurn = _relatedPosition.sideToMove == Side.WHITE
        val notPlayerTurn = _playerHasWhite != whiteTurn
        if (notPlayerTurn || _gameFinished) return true

        _waitingForPlayerGoal = false

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
                        val startFile = _highlightedStartCell!!.file
                        val startRank = _highlightedStartCell!!.rank
                        val endFile = _highlightedTargetCell!!.file
                        val endRank = _highlightedTargetCell!!.rank

                        val legalMovesList = MoveGenerator.getInstance().generateLegalMoves(_relatedPosition)
                        val matchingMoves = legalMovesList.filter { move ->
                            val matchingMoveStartCell = move.from
                            val matchingMoveEndCell = move.to
                            val playerMoveStartCell = buildSquare(startRank, startFile)
                            val playerMoveEndCell = buildSquare(endRank, endFile)

                            (matchingMoveStartCell == playerMoveStartCell) && (matchingMoveEndCell == playerMoveEndCell)
                        }

                        val isPromotionMove = matchingMoves.isNotEmpty() && matchingMoves[0].promotion != Piece.NONE

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
                                val move = matchingMoves[0]
                                addMoveToList(move, _relatedPosition.fen.getFANforMove(move))
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
                    val movedPiece = _relatedPosition.getPiece(buildSquare(file = file, rank = rank))
                    val isOccupiedSquare = movedPiece != Piece.NONE
                    val isWhiteTurn = _relatedPosition.sideToMove == Side.WHITE
                    val isWhitePiece = movedPiece.pieceSide == Side.WHITE
                    val isOneOfOurPiece = (isWhiteTurn && isWhitePiece) || (!isWhiteTurn && !isWhitePiece)

                    _highlightedTargetCell = if (isOccupiedSquare && isOneOfOurPiece) SquareCoordinates(file = file, rank = rank) else null
                    _highlightedStartCell = _highlightedTargetCell
                    invalidate()
                }
                MotionEvent.ACTION_MOVE -> {
                    val moveSelectionHasStarted = _highlightedTargetCell != null && _highlightedStartCell != null
                    if (moveSelectionHasStarted) {
                        _highlightedTargetCell = SquareCoordinates(file = file, rank = rank)
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
                       moveToHighlightToFile: Int, moveToHighlightToRank: Int,
                       blacksAreDown: Boolean){
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
            setBlackDown(blacksAreDown)
            _relatedPosition.loadFromFEN(fen)
            _playerHasWhite = playerHasWhite
            _startedToWriteMoves = hasStartedToWriteMoves
            _moveToHighlightFrom = if (moveToHighlightFromFile in 0..7 &&
                    moveToHighlightFromRank in 0..7) buildSquare(file = moveToHighlightFromFile, rank = moveToHighlightFromRank) else null
            _moveToHighlightTo = if (moveToHighlightToFile in 0..7 &&
                    moveToHighlightToRank in 0..7) buildSquare(file = moveToHighlightToFile, rank = moveToHighlightToRank) else null
            updateHighlightedMove()

            setWaitingForPlayerGoalFlag(waitingForPlayerGoal)
            invalidate()
            val weMustNotLetComputerPlay = _gameFinished || playerHasWhite == isWhiteToPlay()
            if (!weMustNotLetComputerPlay) makeComputerPlay()
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

            EngineInteraction.startNewGame()
            if (startFen.isNotEmpty()) _relatedPosition.loadFromFEN(startFen)
            _playerHasWhite = isWhiteToPlay()
            waitForPlayerGoal()
            invalidate()
        }
        catch (e:IllegalArgumentException) {
            Logger.getLogger("BasicChessEndgamesTrainer").severe("Position $startFen is invalid and could not be load.")
        }
    }

    fun isWhiteToPlay() : Boolean {
        return _relatedPosition.sideToMove == Side.WHITE
    }

    fun makeComputerPlay(){
        val isComputerToMove = _playerHasWhite != isWhiteToPlay()
        if (isComputerToMove) {
            EngineInteraction.evaluate(_relatedPosition.fen)
        }
    }

    fun checkIfGameFinished() {
        fun checkIfGameFinished(finishMessageId: Int, finishTest: Board.() -> Boolean) : Boolean {
            if (_relatedPosition.finishTest()){
                _gameFinished = true
                when(context){
                    is PlayingActivity -> with(context as PlayingActivity){
                        setPlayerGoalTextId(finishMessageId, alertMode = true)
                        activatePositionNavigation()
                    }
                }
            }
            return _gameFinished
        }

        if (!checkIfGameFinished(R.string.checkmate){ _relatedPosition.isMated })
            if (!checkIfGameFinished(R.string.missing_material_draw){ _relatedPosition.isInsufficientMaterial })
                if (!checkIfGameFinished(R.string.position_repetitions_draw){ isDrawByRepetitions() })
                    if (!checkIfGameFinished(R.string.stalemate){ _relatedPosition.isStaleMate })
                        if (!checkIfGameFinished(R.string.fiftyMoveDraw){ _relatedPosition.moveCounter >= 100 }){}
    }

    private fun addMoveToList(move: Move, moveFan: String) {
        when (context) {
            is PlayingActivity -> {

                val isWhiteTurnBeforeMove = _relatedPosition.sideToMove == Side.WHITE

                val moveNumberBeforeMoveCommit = getMoveNumber()

                _relatedPosition.doMove(move)
                val fenAfterMove = _relatedPosition.fen

                val moveToHighlight = MoveToHighlight(
                        startFile = move.from.file.ordinal,
                        startRank = move.from.rank.ordinal,
                        endFile = move.to.file.ordinal,
                        endRank = move.to.rank.ordinal
                )

                // Registering move san into history
                if (!_startedToWriteMoves && !isWhiteTurnBeforeMove){
                    with(context as PlayingActivity){
                        addPositionInMovesList(moveNumberBeforeMoveCommit.toString(), "", MoveToHighlight(-1,-1,-1,-1))
                        addPositionInMovesList("..", "",MoveToHighlight(-1,-1,-1,-1))
                        addPositionInMovesList(moveFan,
                            fen = fenAfterMove, moveToHighlight = moveToHighlight)
                    }
                }
                else {
                    with(context as PlayingActivity){
                        if (isWhiteTurnBeforeMove) addPositionInMovesList(moveNumberBeforeMoveCommit.toString(), "",
                                MoveToHighlight(-1,-1,-1,-1))
                        addPositionInMovesList(
                                san = moveFan,
                                fen = fenAfterMove,
                                moveToHighlight = moveToHighlight
                        )
                    }
                }
            }
        }
        _startedToWriteMoves = true
    }

    fun validatePromotionMove(promotedPieceType: PieceType) {
        when(_pendingPromotionInfo) {
            null -> {}
            else -> {

                val startSquare = buildSquare(file = _pendingPromotionInfo!!.startFile,
                        rank = _pendingPromotionInfo!!.startRank)
                val endSquare = buildSquare(file = _pendingPromotionInfo!!.endFile,
                        rank = _pendingPromotionInfo!!.endRank)
                val legalMovesList = MoveGenerator.getInstance().generateLegalMoves(_relatedPosition)
                val matchingMoves = legalMovesList.filter { currentMove ->
                    val currentMoveStartSquare = currentMove.from
                    val currentMoveEndSquare = currentMove.to
                    val promotionPiece = currentMove.promotion

                    currentMoveStartSquare == startSquare
                    && currentMoveEndSquare == endSquare
                    && promotionPiece != Piece.NONE
                    && promotionPiece == promotedPieceType
                }
                if (matchingMoves.isEmpty()) Logger.getLogger("BasicChessEndgamesTrainer").severe("Illegal move ! (When validating promotion)")
                else {
                    val move = matchingMoves[0]
                    addMoveToList(move, _relatedPosition.fen.getFANforMove(move))
                }
                _pendingPromotionInfo = null
                _highlightedTargetCell = null
                invalidate()
            }
        }
    }

    fun gameFinished() = _gameFinished
    fun hasStartedToWriteMoves() = _startedToWriteMoves

    private fun getMoveNumber(): Int {
        return _relatedPosition.moveCounter
    }

    override fun setHighlightedMove(fromFile: Int, fromRank: Int, toFile: Int, toRank: Int) {
        if (fromFile !in 0..7) throw IllegalArgumentException()
        if (fromRank !in 0..7) throw IllegalArgumentException()
        if (toFile !in 0..7) throw IllegalArgumentException()
        if (toRank !in 0..7) throw IllegalArgumentException()

        super.setHighlightedMove(fromFile, fromRank, toFile, toRank)
        _moveToHighlightFrom = buildSquare(file = fromFile, rank = fromRank)
        _moveToHighlightTo = buildSquare(file = toFile, rank = toRank)
    }

    fun getMoveToHighlightFromFile():Int? = _moveToHighlightFrom?.file?.ordinal

    fun getMoveToHighlightFromRank():Int? = _moveToHighlightFrom?.rank?.ordinal

    fun getMoveToHighlightToFile():Int? = _moveToHighlightTo?.file?.ordinal

    fun getMoveToHighlightToRank():Int? = _moveToHighlightTo?.rank?.ordinal

    private fun updateHighlightedMove(){
        val from = _moveToHighlightFrom
        val to = _moveToHighlightTo
        if (from != null && to != null){
            setHighlightedMove(
                    from.file.ordinal, from.rank.ordinal,
                    to.file.ordinal, to.rank.ordinal
            )
        }
        else {
            clearHighlightedMove()
        }

    }

    private var _playerHasWhite = true
    private var _gameFinished = false
    private var _waitingForPlayerGoal = true
    private var _startedToWriteMoves = false
    private var _moveToHighlightFrom:Square? = null
    private var _moveToHighlightTo:Square? = null
}