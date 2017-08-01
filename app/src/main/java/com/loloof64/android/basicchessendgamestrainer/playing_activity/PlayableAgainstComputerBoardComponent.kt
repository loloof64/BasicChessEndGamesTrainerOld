package com.loloof64.android.basicchessendgamestrainer.playing_activity

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import com.loloof64.android.basicchessendgamestrainer.PlayingActivity
import com.loloof64.android.basicchessendgamestrainer.R
import com.loloof64.android.basicchessendgamestrainer.chess_abstraction.IEngine
import com.loloof64.android.basicchessendgamestrainer.chess_abstraction.IPosition
import com.loloof64.android.basicchessendgamestrainer.chess_abstraction.IMove
import com.loloof64.android.basicchessendgamestrainer.chess_abstraction.Square
import com.loloof64.android.basicchessendgamestrainer.chess_abstraction.Color
import com.loloof64.android.basicchessendgamestrainer.chess_abstraction.PieceType
import com.loloof64.android.basicchessendgamestrainer.chess_abstraction.SimpleUciObserver
import com.loloof64.android.basicchessendgamestrainer.chess_abstraction.Evaluator
import java.util.logging.Logger
import com.loloof64.android.basicchessendgamestrainer.karballo_chess_implementation.Position
import com.loloof64.android.basicchessendgamestrainer.karballo_chess_implementation.Game
import com.loloof64.android.basicchessendgamestrainer.karballo_chess_implementation.Move
import com.loloof64.android.basicchessendgamestrainer.karballo_chess_implementation.EngineInteraction

data class PromotionInfo(val startFile: Int, val startRank: Int,
                         val endFile: Int, val endRank: Int)

class PlayableAgainstComputerBoardComponent(context: Context, override val attrs: AttributeSet?,
                             defStyleAttr: Int) : BoardComponent(context, attrs, defStyleAttr),
                                                    SimpleUciObserver {
    override fun consumeMove(move: IMove) {
        if (!_waitingForPlayerGoal){
            val isComputerToMove = _playerHasWhite != isWhiteToPlay()
            if (isComputerToMove){
                handler.post {
                    addMoveToList(move)
                    _relatedGame.doMove(move)
                    _moveToHighlightFrom = move.getFrom()
                    _moveToHighlightTo = move.getTo()
                    updateHighlightedMove()

                    invalidate()
                    checkIfGameFinished()
                }
            }
        }
    }

    override fun consumeScore(score: Int) {
        if (_waitingForPlayerGoal){
            val stringId = if (score >= Evaluator.MINIMAL_MAT_SCORE) {
                if (isWhiteToPlay()) R.string.white_play_for_mate
                else R.string.black_play_for_mate
            } else if (score >= Evaluator.KNOWN_WIN) {
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

    }

    fun isWaitingForPlayerGoal() = _waitingForPlayerGoal

    // Mainly used for serialisation purpose
    fun setWaitingForPlayerGoalFlag(waiting: Boolean){
        _waitingForPlayerGoal = waiting
    }

    fun waitForPlayerGoal() {
        _waitingForPlayerGoal = true
        engineInteraction.evaluate(_relatedGame.getCurrentPosition().toFen())
    }

    override fun relatedPosition(): IPosition {
        return _relatedGame.getCurrentPosition()
    }

    override fun replacePositionWith(fen: String) {
        _relatedGame.resetFromFen(fen)
    }

    override fun highlightedTargetCell(): Square? {
        return _highlightedTargetCell
    }

    override fun highlightedStartCell(): Square? {
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
    private val _relatedGame = Game()
    private var _highlightedTargetCell:Square? = null
    private var _highlightedStartCell:Square? = null
    private var _pendingPromotionInfo:PromotionInfo? = null

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val whiteTurn = _relatedGame.getCurrentPosition().getPlayerTurn() == Color.WHITE
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

                        val matchingMove = Move(
                            Square(startFile, startRank),
                            Square(endFile, endRank),
                            PieceType.QUEEN
                        )

                        if (_relatedGame.getCurrentPosition().isLegalMove(matchingMove)){
                            if (_relatedGame.getCurrentPosition().isPromotionMove(matchingMove)){
                                _pendingPromotionInfo = PromotionInfo(startFile, startRank, endFile, endRank)
                                askForPromotionPiece()
                            }
                            else {
                                updateHighlightedMove()
                                addMoveToList(matchingMove)
                                _relatedGame.doMove(matchingMove)
                            }
                        }
                        else {
                            val sameCellSelected = (startFile == endFile) && (startRank == endRank)
                            if (!sameCellSelected) reactForIllegalMove()
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
                    val movedPiece = _relatedGame.getCurrentPosition().getPieceAtSquare(Square(file = file, rank = rank))
                    val isOccupiedSquare = movedPiece != null
                    val isWhiteTurn = _relatedGame.getCurrentPosition().getPlayerTurn() == Color.WHITE
                    val isWhitePiece = movedPiece?.toFen()?.isUpperCase() ?: false
                    val isBlackPiece = movedPiece?.toFen()?.isLowerCase() ?: false
                    val isOneOfOurPiece = (isWhiteTurn && isWhitePiece) || (!isWhiteTurn && isBlackPiece)

                    _highlightedTargetCell = if (isOccupiedSquare && isOneOfOurPiece) Square(file = file, rank = rank) else null
                    _highlightedStartCell = _highlightedTargetCell
                    invalidate()
                }
                MotionEvent.ACTION_MOVE -> {
                    val moveSelectionHasStarted = _highlightedTargetCell != null && _highlightedStartCell != null
                    if (moveSelectionHasStarted) {
                        _highlightedTargetCell = Square(file = file, rank = rank)
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
            _relatedGame.resetFromFen(fen)
            _playerHasWhite = playerHasWhite
            _startedToWriteMoves = hasStartedToWriteMoves
            _moveToHighlightFrom = if (moveToHighlightFromFile in 0..7 &&
                    moveToHighlightFromRank in 0..7) Square(file = moveToHighlightFromFile, rank = moveToHighlightFromRank) else null
            _moveToHighlightTo = if (moveToHighlightToFile in 0..7 &&
                    moveToHighlightToRank in 0..7) Square(file = moveToHighlightToFile, rank = moveToHighlightToRank) else null
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

            _relatedGame.resetFromFen(startFen)
            _playerHasWhite = isWhiteToPlay()
            waitForPlayerGoal()
            invalidate()
        }
        catch (e:IllegalArgumentException) {
            Logger.getLogger("BasicChessEndgamesTrainer").severe("Position $startFen is invalid and could not be load.")
        }
    }

    fun isWhiteToPlay() : Boolean {
        return _relatedGame.getCurrentPosition().getPlayerTurn() == Color.WHITE
    }

    fun makeComputerPlay(){
        val isComputerToMove = _playerHasWhite != isWhiteToPlay()
        if (isComputerToMove) {
            engineInteraction.evaluate(_relatedGame.getCurrentPosition().toFen())
        }
    }

    fun checkIfGameFinished() {
        val finishGameWithMessage = { messageID:Int -> 
            _gameFinished = true
            when(context){
                is PlayingActivity -> with(context as PlayingActivity){
                    setPlayerGoalTextId(messageID, alertMode = true)
                    activatePositionNavigation()
                }
            }
        }

        if (_relatedGame.isMate()) {
            ///////////////////////////
            println("Game finished by Checkmate")
            //////////////////////////////
            finishGameWithMessage(R.string.checkmate)
        }
        else if (_relatedGame.isDrawByStaleMate()){
            ///////////////////////////
            println("Game finished by Stalemate")
            //////////////////////////////
            finishGameWithMessage(R.string.stalemate)
        }
        else if (_relatedGame.isDrawBy50MovesRule()){
            ///////////////////////////
            println("Game finished by 50 moves rule")
            //////////////////////////////
            finishGameWithMessage(R.string.fiftyMoveDraw)
        }
        else if (_relatedGame.isDrawByThreeFoldsRepetition()){
            ///////////////////////////
            println("Game finished by Three folds repetition")
            //////////////////////////////
            finishGameWithMessage(R.string.position_repetitions_draw)
        }
        else if (_relatedGame.isDrawByMissingMatingMaterial()){
            ///////////////////////////
            println("Game finished by Missing mating material")
            //////////////////////////////
            finishGameWithMessage(R.string.position_draw)
        }
    }

    private fun addMoveToList(move: IMove) {
        when (context) {
            is PlayingActivity -> {
                val isWhiteTurnBeforeMove = _relatedGame.getCurrentPosition().getPlayerTurn() == Color.WHITE
                val moveSan = _relatedGame.getCurrentPosition().getMoveSAN(move)

                val moveNumberBeforeMoveCommit = getMoveNumber()

                _relatedGame.doMove(move)
                val startFile = move.getFrom().file
                val startRank = move.getFrom().rank
                val endFile = move.getTo().file
                val endRank = move.getTo().rank
                val fenAfterMove = _relatedGame.getCurrentPosition().toFen()

                if (!_startedToWriteMoves && !isWhiteTurnBeforeMove){
                    with(context as PlayingActivity){
                        addPositionInMovesList(moveNumberBeforeMoveCommit.toString(), "", MoveToHighlight(-1,-1,-1,-1))
                        addPositionInMovesList("..", "",MoveToHighlight(-1,-1,-1,-1))
                        addPositionInMovesList(IMove.replacePiecesByFigurineInSan(moveSan),
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
                                san = IMove.replacePiecesByFigurineInSan(moveSan),
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

    fun validatePromotionMove(promotedPieceType: PieceType) {
        when(_pendingPromotionInfo) {
            null -> {}
            else -> {
                val startSquare = Square(file = _pendingPromotionInfo!!.startFile,
                        rank = _pendingPromotionInfo!!.startRank)
                val endSquare = Square(file = _pendingPromotionInfo!!.endFile,
                        rank = _pendingPromotionInfo!!.endRank)
                val matchingMove = Move(startSquare, endSquare, promotedPieceType)
                if (!_relatedGame.getCurrentPosition().isLegalMove(matchingMove)) Logger.getLogger("BasicChessEndgamesTrainer").severe("Illegal move ! (When validating promotion)")
                else {
                    addMoveToList(matchingMove)
                    _relatedGame.doMove(matchingMove)
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
        return _relatedGame.getCurrentPosition().getMoveNumber()
    }

    override fun setHighlightedMove(fromFile: Int, fromRank: Int, toFile: Int, toRank: Int) {
        super.setHighlightedMove(fromFile, fromRank, toFile, toRank)
        _moveToHighlightFrom = Square(file = fromFile, rank = fromRank)
        _moveToHighlightTo = Square(file = toFile, rank = toRank)
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
    private var _moveToHighlightFrom:Square? = null
    private var _moveToHighlightTo:Square? = null
    private val engineInteraction:IEngine = EngineInteraction()
}