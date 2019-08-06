/*
 * Basic Chess Endgames : generates a position of the endgame you want, then play it against computer.
    Copyright (C) 2017-2018  Laurent Bernabe <laurent.bernabe@gmail.com>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.loloof64.android.basicchessendgamestrainer.playing_activity

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import com.loloof64.android.basicchessendgamestrainer.PlayingActivity
import com.loloof64.android.basicchessendgamestrainer.R
import com.loloof64.android.basicchessendgamestrainer.utils.*
import java.lang.Math.abs
import java.util.logging.Logger

const val MIN_MATE_SCORE = 1000
const val MIN_DRAW_SCORE = 10
const val MIN_KNOWN_WIN = 250

const val default_position = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1\n"

data class PromotionInfo(val startFile: Int, val startRank: Int,
                         val endFile: Int, val endRank: Int)

class PlayableAgainstComputerBoardComponent(context: Context, attrs: AttributeSet?,
                             defStyleAttr: Int) : BoardComponent(context, attrs, defStyleAttr),
                                                    SimpleUciObserver{

    override fun consumeMove(move: ChessMove, promotionPieceType: PromotionPieceType) {
        if (!_waitingForPlayerGoal && !_gameFinished){
            val isComputerToMove = _playerHasWhite != isWhiteToPlay()
            if (isComputerToMove){
                handler.post {
                    playMoveAndAddItToList(move, moveFan = _relatedPosition.getMoveFAN(move)!!, promotionPieceType = promotionPieceType)
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
        EngineInteraction.evaluate(_relatedPosition.toFEN())
    }

    override fun relatedPosition(): IPosition {
        return _relatedPosition
    }

    override fun replacePositionWith(positionFEN: String) {
        _relatedPosition = ICTKChessLib.buildPositionFromString(positionFEN, checkIfResultPositionLegal = true)
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
    private var _relatedPosition:IPosition = ICTKChessLib.buildPositionFromString(default_position)
    private var _highlightedTargetCell:SquareCoordinates? = null
    private var _highlightedStartCell:SquareCoordinates? = null
    private var _pendingPromotionInfo:PromotionInfo? = null

    @SuppressWarnings("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val whiteTurn = _relatedPosition.isWhiteToMove()
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

                        val move = ChessMove(from = ChessCell(file = startFile, rank = startRank),
                                to = ChessCell(file = endFile, rank = endRank))
                        val legalMove = _relatedPosition.isLegalMove(move)

                        if (legalMove){
                            val isPromotionMove = _relatedPosition.isPromotionMove(move)

                            if (isPromotionMove) {
                                _pendingPromotionInfo = PromotionInfo(startFile, startRank, endFile, endRank)
                                askForPromotionPiece()
                            }
                            else {
                                updateHighlightedMove()
                                playMoveAndAddItToList(move, moveFan = _relatedPosition.getMoveFAN(move)!!)
                            }
                        }
                        else {
                            val sameCellSelected = (startFile == endFile) && (startRank == endRank)
                            if (!sameCellSelected) reactForIllegalMove()
                        }


                        invalidate()
                        checkIfGameFinished()
                        if (legalMove && !_gameFinished) {
                            val computerToPlay = _playerHasWhite != isWhiteToPlay()
                            if (computerToPlay) makeComputerPlay()
                        }
                    }
                    _highlightedStartCell = null
                    _highlightedTargetCell = null
                }
                MotionEvent.ACTION_DOWN -> {
                    val movedPiece = _relatedPosition.getPieceAtCell(ChessCell(file = file, rank = rank))
                    val isOccupiedSquare = movedPiece != null
                    val isWhiteTurn = _relatedPosition.isWhiteToMove()
                    val isWhitePiece = movedPiece?.whiteOwner
                    val isOneOfOurPiece = (isWhiteTurn && isWhitePiece ?: false) || (!isWhiteTurn && !(isWhitePiece ?: false))

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
            _relatedPosition = ICTKChessLib.buildPositionFromString(fen)
            _playerHasWhite = playerHasWhite
            _startedToWriteMoves = hasStartedToWriteMoves
            _moveToHighlightFrom = if (moveToHighlightFromFile in 0..7 &&
                    moveToHighlightFromRank in 0..7) ChessCell(file = moveToHighlightFromFile, rank = moveToHighlightFromRank) else null
            _moveToHighlightTo = if (moveToHighlightToFile in 0..7 &&
                    moveToHighlightToRank in 0..7) ChessCell(file = moveToHighlightToFile, rank = moveToHighlightToRank) else null
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
            if (startFen.isNotEmpty()) {
                _relatedPosition = ICTKChessLib.buildPositionFromString(startFen)
                _blackStartedTheGame = ! _relatedPosition.isWhiteToMove()
            }
            _playerHasWhite = isWhiteToPlay()
            waitForPlayerGoal()
            invalidate()
        }
        catch (e:IllegalArgumentException) {
            Logger.getLogger("BasicChessEndgamesTrainer").severe("Position $startFen is invalid and could not be load.")
        }
    }

    fun isWhiteToPlay() : Boolean {
        return _relatedPosition.isWhiteToMove()
    }

    fun makeComputerPlay(){
        val isComputerToMove = _playerHasWhite != isWhiteToPlay()
        if (isComputerToMove) {
            EngineInteraction.evaluate(_relatedPosition.toFEN())
        }
    }

    fun checkIfGameFinished() {
        fun checkIfGameFinished(finishMessageId: Int, finishTest: IPosition.() -> Boolean) : Boolean {
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

        if (!checkIfGameFinished(R.string.checkmate){ _relatedPosition.isMate() })
            if (!checkIfGameFinished(R.string.missing_material_draw){ _relatedPosition.isDrawByMissingMatingMaterial() })
                if (!checkIfGameFinished(R.string.position_repetitions_draw){ _relatedPosition.isDrawByRepetitions() })
                    if (!checkIfGameFinished(R.string.stalemate){ _relatedPosition.isStaleMate() })
                        if (!checkIfGameFinished(R.string.fiftyMoveDraw){ _relatedPosition.isDrawByFiftyMovesCount() }){}
    }

    private fun playMoveAndAddItToList(move: ChessMove, moveFan: String, promotionPieceType: PromotionPieceType = PromotionPieceType.Queen) {
        when (context) {
            is PlayingActivity -> {

                val isWhiteTurnBeforeMove = _relatedPosition.isWhiteToMove()

                val moveNumberBeforeMoveCommit = getMoveNumber()

                _relatedPosition.modifyPositionByDoingMove(move, promotionPieceType = promotionPieceType)
                val fenAfterMove = _relatedPosition.toFEN()

                val moveNumberAfterMoveCommit = getMoveNumber()

                val moveToHighlight = MoveToHighlight(
                        startFile = move.from.file,
                        startRank = move.from.rank,
                        endFile = move.to.file,
                        endRank = move.to.rank
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
                        if (isWhiteTurnBeforeMove) {
                            val moveNumberToAdd = if (_blackStartedTheGame) {
                                moveNumberAfterMoveCommit
                            } else {
                                moveNumberBeforeMoveCommit
                            }
                            addPositionInMovesList(moveNumberToAdd.toString(), "",
                                    MoveToHighlight(-1,-1,-1,-1))
                        }
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

    fun validatePromotionMove(givenPromotionPiece: PromotionPieceType) {
        when(_pendingPromotionInfo) {
            null -> {}
            else -> {

                val startSquare = ChessCell(file = _pendingPromotionInfo!!.startFile,
                        rank = _pendingPromotionInfo!!.startRank)
                val endSquare = ChessCell(file = _pendingPromotionInfo!!.endFile,
                        rank = _pendingPromotionInfo!!.endRank)
                val move = ChessMove(from = startSquare, to = endSquare, promotionPiece = givenPromotionPiece)

                if ( ! _relatedPosition.isLegalMove(move) ) Logger.getLogger("BasicChessEndgamesTrainer").severe("Illegal move : $move ! (When validating promotion)")
                else {
                    playMoveAndAddItToList(move, moveFan = _relatedPosition.getMoveFAN(move, promotionPieceType = givenPromotionPiece)!!,
                            promotionPieceType = givenPromotionPiece)
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
       return _relatedPosition.getMoveNumber()
    }

    override fun setHighlightedMove(fromFile: Int, fromRank: Int, toFile: Int, toRank: Int) {
        if (fromFile !in 0..7) throw IllegalArgumentException()
        if (fromRank !in 0..7) throw IllegalArgumentException()
        if (toFile !in 0..7) throw IllegalArgumentException()
        if (toRank !in 0..7) throw IllegalArgumentException()

        super.setHighlightedMove(fromFile, fromRank, toFile, toRank)
        _moveToHighlightFrom = ChessCell(file = fromFile, rank = fromRank)
        _moveToHighlightTo = ChessCell(file = toFile, rank = toRank)
    }

    fun getMoveToHighlightFromFile():Int? = _moveToHighlightFrom?.file

    fun getMoveToHighlightFromRank():Int? = _moveToHighlightFrom?.rank

    fun getMoveToHighlightToFile():Int? = _moveToHighlightTo?.file

    fun getMoveToHighlightToRank():Int? = _moveToHighlightTo?.rank

    private fun updateHighlightedMove(){
        val from = _moveToHighlightFrom
        val to = _moveToHighlightTo
        if (from != null && to != null){
            setHighlightedMove(
                    from.file, from.rank,
                    to.file, to.rank
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
    private var _moveToHighlightFrom:ChessCell? = null
    private var _moveToHighlightTo:ChessCell? = null
    private var _blackStartedTheGame = false
}