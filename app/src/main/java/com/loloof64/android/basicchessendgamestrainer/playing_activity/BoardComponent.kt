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
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.support.graphics.drawable.VectorDrawableCompat
import android.util.AttributeSet
import android.view.View
import com.loloof64.android.basicchessendgamestrainer.R
import com.loloof64.android.basicchessendgamestrainer.utils.ChessCell
import com.loloof64.android.basicchessendgamestrainer.utils.ChessPiece
import com.loloof64.android.basicchessendgamestrainer.utils.ChessPieceType
import com.loloof64.android.basicchessendgamestrainer.utils.IPosition

infix fun Int.min(other : Int) = if (this < other) this else other
infix fun Int.max(other : Int) = if (this > other) this else other

data class SquareCoordinates(val file: Int, val rank: Int)

abstract class BoardComponent(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : View(context, attrs, defStyleAttr) {

    @Suppress("DEPRECATION")
    fun getColor(colorResId: Int): Int = resources.getColor(colorResId)

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null, 0)

    private val minAvailableSpacePercentage:Int

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.BoardComponent)
        val computed = typedArray.getInt(R.styleable.BoardComponent_min_dimension_percentage, 100)
        typedArray.recycle()
        minAvailableSpacePercentage = computed
    }

    open fun computeMinAvailableSpacePercentage():Int {
        return minAvailableSpacePercentage
    }

    protected abstract fun relatedPosition() : IPosition
    protected abstract fun replacePositionWith(positionFEN : String)

    protected var reversed = false
    private val rectPaint = Paint()
    private val fontPaint = Paint()

    abstract fun highlightedStartCell() : SquareCoordinates?
    abstract fun highlightedTargetCell() : SquareCoordinates?

    fun reverse() {
        reversed = !reversed
        invalidate()
    }

    fun areBlackDown():Boolean = reversed

    fun setBlackDown(yes: Boolean) {
        reversed = yes
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int){
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val minSpacePercentage = computeMinAvailableSpacePercentage()

        val widthAdjusted = (widthSize * minSpacePercentage / 100).max(suggestedMinimumWidth)
        val heightAdjusted = (heightSize * minSpacePercentage / 100).max(suggestedMinimumHeight)

        val desiredWidth = widthAdjusted - (widthAdjusted % 9)
        val desiredHeight = heightAdjusted - (heightAdjusted % 9)

        val computedSize = desiredWidth.min(desiredHeight)
        setMeasuredDimension(computedSize, computedSize)
    }

    private fun drawBackground(canvas: Canvas) {
        rectPaint.color = getColor(R.color.chess_board_background_color)
        canvas.drawRect(0.toFloat(), 0.toFloat(), measuredWidth.toFloat(), measuredHeight.toFloat(), rectPaint)
    }

    private fun drawCells(canvas: Canvas, cellSize: Int) {

        for (row in 0 until 8) {
            for (col in 0 until 8) {
                val color = if ((row + col) % 2 == 0) R.color.chess_board_white_cells_color else R.color.chess_board_black_cells_color
                val x = (cellSize / 2 + col * cellSize).toFloat()
                val y = (cellSize / 2 + row * cellSize).toFloat()
                rectPaint.color = getColor(color)
                canvas.drawRect(x, y, x + cellSize, y + cellSize, rectPaint)
            }
        }

    }

    private fun drawLetters(canvas: Canvas, cellSize: Int) {
        val fileCoords = if (reversed) "HGFEDCBA" else "ABCDEFGH"
        val rankCoords = if (reversed) "12345678" else "87654321"

        for ((file, letter) in fileCoords.withIndex()) {
            val y1 = (cellSize * 0.4).toFloat()
            val y2 = (cellSize * 8.9).toFloat()
            val x = (cellSize * 0.9 + file * cellSize).toFloat()

            fontPaint.color = getColor(R.color.chess_board_font_color)

            canvas.drawText("$letter", x, y1, fontPaint)
            canvas.drawText("$letter", x, y2, fontPaint)
        }

        for ((rank, digit) in rankCoords.withIndex()) {
            val x1 = (cellSize * 0.15).toFloat()
            val x2 = (cellSize * 8.65).toFloat()
            val y = (cellSize * 1.2 + rank * cellSize).toFloat()

            fontPaint.color = getColor(R.color.chess_board_font_color)

            canvas.drawText("$digit", x1, y, fontPaint)
            canvas.drawText("$digit", x2, y, fontPaint)
        }
    }

    private fun drawPieces(canvas: Canvas, cellSize: Int) {

        for (cellRank in (0 until 8)) {
            for (cellFile in (0 until 8)) {
                val piece = relatedPosition().getPieceAtCell(ChessCell(file = cellFile, rank = cellRank))
                if (piece != null) {
                    val imageRes = when (piece) {
                        ChessPiece(ChessPieceType.Pawn, whiteOwner = true) -> R.drawable.chess_pl
                        ChessPiece(ChessPieceType.Pawn, whiteOwner = false) -> R.drawable.chess_pd
                        ChessPiece(ChessPieceType.Knight, whiteOwner = true) -> R.drawable.chess_nl
                        ChessPiece(ChessPieceType.Knight, whiteOwner = false) -> R.drawable.chess_nd
                        ChessPiece(ChessPieceType.Bishop, whiteOwner = true) -> R.drawable.chess_bl
                        ChessPiece(ChessPieceType.Bishop, whiteOwner = false) -> R.drawable.chess_bd
                        ChessPiece(ChessPieceType.Rook, whiteOwner = true) -> R.drawable.chess_rl
                        ChessPiece(ChessPieceType.Rook, whiteOwner = false) -> R.drawable.chess_rd
                        ChessPiece(ChessPieceType.Queen, whiteOwner = true) -> R.drawable.chess_ql
                        ChessPiece(ChessPieceType.Queen, whiteOwner = false) -> R.drawable.chess_qd
                        ChessPiece(ChessPieceType.King, whiteOwner = true) -> R.drawable.chess_kl
                        ChessPiece(ChessPieceType.King, whiteOwner = false) -> R.drawable.chess_kd
                        else -> throw IllegalArgumentException("Unrecognized piece fen $piece !")
                    }
                    val x = (cellSize * (0.5 + (if (reversed) 7 - cellFile else cellFile))).toInt()
                    val y = (cellSize * (0.5 + (if (reversed) cellRank else 7 - cellRank))).toInt()

                    val drawable = VectorDrawableCompat.create(context.resources, imageRes, null)
                    drawable?.bounds = Rect(x, y, x + cellSize, y + cellSize)
                    drawable?.draw(canvas)
                }
            }
        }
    }

    private fun drawPlayerTurn(canvas: Canvas, cellSize: Int) {
        val color = if (relatedPosition().isWhiteToMove()) R.color.chess_board_white_player_turn_color else R.color.chess_board_black_player_turn_color
        val location = (8.5 * cellSize).toFloat()
        val locationEnd = (location + cellSize * 0.5).toFloat()
        rectPaint.color = getColor(color)
        canvas.drawRect(location, location, locationEnd, locationEnd, rectPaint)
    }

    private fun drawHighlightedCells(canvas: Canvas, cellSize: Int) {
        val startCellToHighlight = highlightedStartCell()
        if (startCellToHighlight != null){
            val fileIndex = startCellToHighlight.file
            val rankIndex = startCellToHighlight.rank

            val x = (cellSize * (0.5 + (if (reversed) 7 - fileIndex else fileIndex))).toFloat()
            val y = (cellSize * (0.5 + (if (reversed) rankIndex else 7 - rankIndex))).toFloat()
            rectPaint.color = getColor(R.color.chess_board_move_start_cell_highlighting)
            canvas.drawRect(x, y, x + cellSize, y + cellSize, rectPaint)
        }

        val targetCellToHighlight = highlightedTargetCell()
        if (targetCellToHighlight != null) {
            val fileIndex = targetCellToHighlight.file
            val rankIndex = targetCellToHighlight.rank

            val x = (cellSize * (0.5 + (if (reversed) 7 - fileIndex else fileIndex))).toFloat()
            val y = (cellSize * (0.5 + (if (reversed) rankIndex else 7 - rankIndex))).toFloat()
            rectPaint.color = getColor(R.color.chess_board_move_current_cell_highlighting)
            canvas.drawRect(x, y, x + cellSize, y + cellSize, rectPaint)
        }
    }

    private fun drawCurrentTargetCellGuidingAxis(canvas: Canvas, cellSize: Int){
        val targetCellToHighlight = highlightedTargetCell()
        if (targetCellToHighlight != null) {
            val fileIndex = targetCellToHighlight.file
            val rankIndex = targetCellToHighlight.rank

            val x = (cellSize * (1 + (if (reversed) 7 - fileIndex else fileIndex))).toFloat()
            val y = (cellSize * (1 + (if (reversed) rankIndex else 7 - rankIndex))).toFloat()
            rectPaint.color = getColor(R.color.chess_board_move_current_cell_highlighting)
            rectPaint.strokeWidth = cellSize * 0.1f

            canvas.drawLine(0f, y, width.toFloat(), y, rectPaint)
            canvas.drawLine(x, 0f, x, height.toFloat(), rectPaint)
        }
    }

    private fun drawHighlightedMove(canvas: Canvas, cellSize: Int){
        val from = _highlightedMoveFrom
        val to = _highlightedMoveTo
        if (from == null || to == null) return
        if (from.file !in 0..7) return
        if (from.rank !in 0..7) return
        if (to.file !in 0..7) return
        if (to.rank !in 0..7) return

        val paint = Paint()

        val fromPointX = (cellSize * if (reversed) (8 - from.file) else (from.file+1)).toFloat()
        val fromPointY = (cellSize * if (reversed) (from.rank+1) else (8 - from.rank)).toFloat()
        val toPointX = (cellSize * if (reversed) (8 - to.file) else (to.file+1)).toFloat()
        val toPointY = (cellSize * if (reversed) (to.rank+1) else (8 - to.rank)).toFloat()

        val angleDegrees = Math.toDegrees(Math.atan2(toPointY.toDouble() - fromPointY.toDouble(),
                toPointX.toDouble() - fromPointX.toDouble())).toFloat()

        val distance = Math.sqrt(Math.pow((toPointX - fromPointX).toDouble(), 2.0) +
                Math.pow((toPointY - fromPointY).toDouble(), 2.0)).toFloat()

        val arrowLength = distance * 0.15f

        paint.color = getColor(R.color.chess_board_highlighted_move_arrow_color)
        paint.strokeWidth = cellSize * 0.1f

        canvas.save()
        canvas.translate(fromPointX, fromPointY)
        canvas.rotate(angleDegrees)
        canvas.drawLine(0f, 0f, distance, 0f, paint)
        canvas.translate(distance, 0f)
        canvas.rotate(180f)
        canvas.save()
        canvas.drawLine(0f, 0f, arrowLength, arrowLength, paint)
        canvas.restore()
        canvas.drawLine(0f, 0f, arrowLength, -arrowLength, paint)
        canvas.restore()
    }


    override fun onDraw(canvas: Canvas) {
        val cellSize = measuredWidth.min(measuredHeight) / 9
        fontPaint.textSize = (cellSize * 0.4).toFloat()
        fontPaint.typeface = Typeface.defaultFromStyle(Typeface.BOLD)

        drawBackground(canvas)
        drawCells(canvas, cellSize)
        drawLetters(canvas, cellSize)
        if (highlightedTargetCell() != null) drawHighlightedCells(canvas, cellSize)
        drawPieces(canvas, cellSize)
        drawPlayerTurn(canvas, cellSize)
        drawHighlightedMove(canvas, cellSize)
        drawCurrentTargetCellGuidingAxis(canvas, cellSize)
    }

    open fun setHighlightedMove(fromFile: Int, fromRank: Int,
                           toFile: Int, toRank: Int){
        _highlightedMoveFrom = SquareCoordinates(file =  if (fromFile in 0..7) fromFile else -1,
                rank = if (fromRank in 0..7) fromRank else -1)
        _highlightedMoveTo = SquareCoordinates(file = if (toFile in 0..7) toFile else -1,
                rank = if (toRank in 0..7) toRank else -1)
        invalidate()
    }

    fun clearHighlightedMove(){
        _highlightedMoveFrom = null
        _highlightedMoveTo = null
        invalidate()
    }

    fun toFEN(): String = relatedPosition().toFEN()

    fun setFromFen(boardFen: String) {
        replacePositionWith(boardFen)
        invalidate()
    }

    private var _highlightedMoveFrom:SquareCoordinates? = null
    private var _highlightedMoveTo:SquareCoordinates? = null


}