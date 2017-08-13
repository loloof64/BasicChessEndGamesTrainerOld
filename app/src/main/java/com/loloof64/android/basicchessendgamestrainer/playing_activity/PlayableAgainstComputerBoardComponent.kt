package com.loloof64.android.basicchessendgamestrainer.playing_activity

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import com.loloof64.android.basicchessendgamestrainer.PlayingActivity
import com.loloof64.android.basicchessendgamestrainer.R
import karballo.Board
import karballo.Move
import karballo.search.SearchEngine
import karballo.evaluation.Evaluator
import java.util.logging.Logger

data class PromotionInfo(val startFile: Int, val startRank: Int,
                         val endFile: Int, val endRank: Int)

fun Int.convertSquareIndexToCoords(): SquareCoordinates {
    return SquareCoordinates(file = 7 - this%8, rank = this/8)
}

class PlayableAgainstComputerBoardComponent(context: Context, override val attrs: AttributeSet?,
                             defStyleAttr: Int) : BoardComponent(context, attrs, defStyleAttr),
                                                    SimpleUciObserver {
    override fun consumeMove(move: Int) {
        if (!_waitingForPlayerGoal){
            val isComputerToMove = _playerHasWhite != isWhiteToPlay()
            if (isComputerToMove){
                val moveFrom = Move.getFromIndex(move).convertSquareIndexToCoords()
                val moveTo = Move.getToIndex(move).convertSquareIndexToCoords()
                
                handler.post {
                    addMoveToList(move)
                    _relatedPosition.doMove(move)
                    _moveToHighlightFrom = moveFrom
                    _moveToHighlightTo = moveTo
                    updateHighlightedMove()

                    invalidate()
                    checkIfGameFinished()
                }
            }
        }
    }

    override fun consumeScore(score: Int) {
        if (_waitingForPlayerGoal){
            val MIN_MATE_SCORE = 20500
            val stringId = if (score > MIN_MATE_SCORE) {
                if (isWhiteToPlay()) R.string.white_play_for_mate
                else R.string.black_play_for_mate
            } else if (score > Evaluator.KNOWN_WIN) {
                if (isWhiteToPlay()) R.string.white_play_for_win
                else R.string.black_play_for_win
            } 
            else if (score == Evaluator.DRAW) R.string.should_be_draw
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

    fun isWaitingForPlayerGoal() = _waitingForPlayerGoal

    // Mainly used for serialisation purpose
    fun setWaitingForPlayerGoalFlag(waiting: Boolean){
        _waitingForPlayerGoal = waiting
    }

    fun waitForPlayerGoal() {
        _waitingForPlayerGoal = true
        engineInteraction.evaluate(_relatedPosition.fen)
    }

    override fun relatedPosition(): Board {
        return _relatedPosition
    }

    override fun replacePositionWith(fen: String) {
        _relatedPosition.setFenMove(fen, null)
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
                        val startFile = _highlightedStartCell!!.file
                        val startRank = _highlightedStartCell!!.rank
                        val endFile = _highlightedTargetCell!!.file
                        val endRank = _highlightedTargetCell!!.rank

                        val legalMovesStore = IntArray(MAX_LEGAL_POSITIONS_COUNT)
                        relatedPosition().getLegalMoves(legalMovesStore)
                        val matchingMoves = legalMovesStore.filter { move ->
                            val matchingMoveStartCell = Move.getFromSquare(move)
                            val matchingMoveEndCell = Move.getToSquare(move)
                            val playerMoveStartCell = coordinatesToSquare(startFile, startRank)
                            val playerMoveEndCell = coordinatesToSquare(endFile, endRank)

                            (matchingMoveStartCell == playerMoveStartCell) && (matchingMoveEndCell == playerMoveEndCell)
                        }

                        val isPromotionMove = matchingMoves.isNotEmpty() && Move.isPromotion(matchingMoves[0])

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
                                addMoveToList(matchingMoves[0])
                                _relatedPosition.doMove(matchingMoves[0])
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
                    moveToHighlightFromRank in 0..7) SquareCoordinates(file = moveToHighlightFromFile, rank = moveToHighlightFromRank) else null
            _moveToHighlightTo = if (moveToHighlightToFile in 0..7 &&
                    moveToHighlightToRank in 0..7) SquareCoordinates(file = moveToHighlightToFile, rank = moveToHighlightToRank) else null
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
            engineInteraction.evaluate(_relatedPosition.fen)
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

        if (!checkIfGameFinished(R.string.checkmate){ isMate })
            if (!checkIfGameFinished(R.string.missing_material_draw){ isDrawByMissingMatingMaterial() })
                if (!checkIfGameFinished(R.string.position_repetitions_draw){ isDrawByThreeFoldsRepetition() })
                    if (!checkIfGameFinished(R.string.stalemate){ isDrawByStaleMate()})
                        if (!checkIfGameFinished(R.string.fiftyMoveDraw){ isDrawByFiftyMovesRule() }){}
    }

    private fun addMoveToList(move: Int) {
        when (context) {
            is PlayingActivity -> {
                val isWhiteTurnBeforeMove = _relatedPosition.turn
                val moveSan = Move.toSan(_relatedPosition, move)

                val moveNumberBeforeMoveCommit = getMoveNumber()

                _relatedPosition.doMove(move)
                val fromSquare = Move.getFromSquare(move)
                val toSquare = Move.getToSquare(move)
                val (startFile, startRank) = squareToCoordinates(fromSquare)
                val (endFile, endRank) = squareToCoordinates(toSquare)
                val fenAfterMove = _relatedPosition.fen

                if (!_startedToWriteMoves && !isWhiteTurnBeforeMove){
                    with(context as PlayingActivity){
                        addPositionInMovesList(moveNumberBeforeMoveCommit.toString(), "", MoveToHighlight(-1,-1,-1,-1))
                        addPositionInMovesList("..", "",MoveToHighlight(-1,-1,-1,-1))
                        addPositionInMovesList(Move.sanToFigurines(moveSan)!!,
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
                        if (isWhiteTurnBeforeMove) addPositionInMovesList(moveNumberBeforeMoveCommit.toString(), "",
                                MoveToHighlight(-1,-1,-1,-1))
                        addPositionInMovesList(
                                san = Move.sanToFigurines(moveSan)!!,
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
                    val move = matchingMoves[0]
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
        _moveToHighlightFrom = SquareCoordinates(file = fromFile, rank = fromRank)
        _moveToHighlightTo = SquareCoordinates(file = toFile, rank = toRank)
    }

    fun getMoveToHighlightFromFile() = if (_moveToHighlightFrom != null) _moveToHighlightFrom!!.file
                                    else -1

    fun getMoveToHighlightFromRank() = if (_moveToHighlightFrom != null) _moveToHighlightFrom!!.rank
                                    else -1

    fun getMoveToHighlightToFile() = if (_moveToHighlightTo != null) _moveToHighlightTo!!.file
                                    else -1

    fun getMoveToHighlightToRank() = if (_moveToHighlightTo != null) _moveToHighlightTo!!.rank
                                    else -1

    private fun updateHighlightedMove(){
        setHighlightedMove(
                _moveToHighlightFrom?.file ?: -1, _moveToHighlightFrom?.rank ?: -1,
                _moveToHighlightTo?.file ?: -1, _moveToHighlightTo?.rank ?: -1
        )
        invalidate()
    }

    private var _playerHasWhite = true
    private var _gameFinished = false
    private var _waitingForPlayerGoal = true
    private var _startedToWriteMoves = false
    private var _moveToHighlightFrom:SquareCoordinates? = null
    private var _moveToHighlightTo:SquareCoordinates? = null
    private val engineInteraction = EngineInteraction(this)
}