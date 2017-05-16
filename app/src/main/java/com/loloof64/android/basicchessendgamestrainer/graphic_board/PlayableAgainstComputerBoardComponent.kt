package com.loloof64.android.basicchessendgamestrainer.graphic_board

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import com.loloof64.android.basicchessendgamestrainer.PlayingActivity
import ictk.boardgame.chess.ChessBoard

class PlayableAgainstComputerBoardComponent(context: Context, override val attrs: AttributeSet?,
                             defStyleAttr: Int) : BoardComponent(context, attrs, defStyleAttr), PieceMoveInteraction {

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null, 0)

    override fun relatedBoard(): ChessBoard {
        return _relatedBoard
    }

    override fun replaceBoardWith(board: ChessBoard) {
        _relatedBoard = board
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
    override var _relatedBoard = ChessBoard()
    override var _highlightedCell:Pair<Int, Int>? = null
    override var _pendingPromotionInfo:PromotionInfo? = null

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        val action = event.action

        val cellSize = (measuredWidth.min(measuredHeight)) / 9
        val file = ((x-cellSize*0.5) / cellSize).toInt()
        val rank = 7 - ((y-cellSize*0.5) / cellSize).toInt()

        if (action == MotionEvent.ACTION_DOWN && file >= 0 && file < 8 && rank >= 0 && rank < 8) {
            if (reversed) reactOnClick(7-file, 7-rank) else reactOnClick(file, rank)
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

    fun new_game(startFen: String = startPosition) {
        try {
            _relatedBoard = FEN.stringToBoard(startFen) as ChessBoard
            invalidate()
        }
        catch (e:IllegalArgumentException) {
            java.util.logging.Logger.getLogger("ChessExercisesTool").severe("Position $startFen is invalid and could not be load.")
        }
    }

    fun isWhiteToPlay() : Boolean {
        val WHITE_PLAYER = 0
        return _relatedBoard.playerToMove == WHITE_PLAYER
    }

    companion object {
        val startPosition = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"
    }

}