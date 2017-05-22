package com.loloof64.android.basicchessendgamestrainer.playing_activity

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.support.graphics.drawable.VectorDrawableCompat
import android.util.AttributeSet
import android.view.View
import chesspresso.Chess
import chesspresso.position.Position
import com.loloof64.android.basicchessendgamestrainer.R

fun Int.min(other : Int) = if (this < other) this else other
fun Int.max(other : Int) = if (this > other) this else other

abstract class BoardComponent(context: Context, open val attrs: AttributeSet?, defStyleAttr: Int) : View(context, attrs, defStyleAttr) {

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null, 0)

    data class ColorARGB(val alpha: Int, val red: Int, val green: Int, val blue: Int)

    private val typedArray by lazy {
        context.obtainStyledAttributes(attrs, R.styleable.board_component)
    }
    private val minAvailableSpacePercentage by lazy {
        val computed = typedArray.getInt(R.styleable.board_component_available_space_min_dimension_percentage, 100)
        typedArray.recycle()
        computed
    }

    protected abstract fun relatedPosition() : Position
    protected abstract fun replacePositionWith(board: Position): Unit

    protected var reversed = false
    private val rectPaint = Paint()
    private val fontPaint = Paint()

    abstract fun highlightedCell() : Pair<Int, Int>?

    fun reverse() {
        reversed = !reversed
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int){
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val widthAdjusted = widthSize * minAvailableSpacePercentage / 100.max(suggestedMinimumWidth)
        val heightAdjusted = heightSize * minAvailableSpacePercentage / 100.max(suggestedMinimumHeight)

        val desiredWidth = widthAdjusted - (widthAdjusted % 9)
        val desiredHeight = heightAdjusted - (heightAdjusted % 9)

        val computedSize = desiredWidth.min(desiredHeight)
        setMeasuredDimension(computedSize, computedSize)
    }

    private fun drawBackground(canvas: Canvas) {
        rectPaint.setARGB(255, 102, 51, 0)
        canvas.drawRect(0.toFloat(), 0.toFloat(), measuredWidth.toFloat(), measuredHeight.toFloat(), rectPaint)
    }

    private fun drawCells(canvas: Canvas, cellSize: Int) {

        for (row in 0 until 8) {
            for (col in 0 until 8) {
                val color = if ((row + col) % 2 == 0) ColorARGB(255, 255, 204, 102) else ColorARGB(255, 153, 102, 51)
                val x = (cellSize / 2 + col * cellSize).toFloat()
                val y = (cellSize / 2 + row * cellSize).toFloat()
                rectPaint.setARGB(color.alpha, color.red, color.green, color.blue)
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

            fontPaint.setARGB(255, 255, 255, 255)

            canvas.drawText("$letter", x, y1, fontPaint)
            canvas.drawText("$letter", x, y2, fontPaint)
        }

        for ((rank, digit) in rankCoords.withIndex()) {
            val x1 = (cellSize * 0.15).toFloat()
            val x2 = (cellSize * 8.65).toFloat()
            val y = (cellSize * 1.2 + rank * cellSize).toFloat()

            fontPaint.setARGB(255, 255, 255, 255)

            canvas.drawText("$digit", x1, y, fontPaint)
            canvas.drawText("$digit", x2, y, fontPaint)
        }
    }

    private fun drawPieces(canvas: Canvas, cellSize: Int) {
        for (cellRank in (0 until 8)) {
            for (cellFile in (0 until 8)) {
                val piece = relatedPosition().getStone(cellFile + 8*cellRank).toShort()
                if (piece != Chess.NO_STONE) {
                    val imageRes = when (piece) {
                        Chess.WHITE_PAWN -> R.drawable.chess_pl
                        Chess.BLACK_PAWN -> R.drawable.chess_pd
                        Chess.WHITE_KNIGHT -> R.drawable.chess_nl
                        Chess.BLACK_KNIGHT -> R.drawable.chess_nd
                        Chess.WHITE_BISHOP -> R.drawable.chess_bl
                        Chess.BLACK_BISHOP -> R.drawable.chess_bd
                        Chess.WHITE_ROOK -> R.drawable.chess_rl
                        Chess.BLACK_ROOK -> R.drawable.chess_rd
                        Chess.WHITE_QUEEN -> R.drawable.chess_ql
                        Chess.BLACK_QUEEN -> R.drawable.chess_qd
                        Chess.WHITE_KING -> R.drawable.chess_kl
                        Chess.BLACK_KING -> R.drawable.chess_kd
                        else -> R.drawable.red_cross
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
        val color = if (relatedPosition().toPlay == Chess.WHITE) ColorARGB(255, 255, 255, 255) else ColorARGB(255, 0, 0, 0)
        val location = (8.5 * cellSize).toFloat()
        val locationEnd = (location + cellSize * 0.5).toFloat()
        rectPaint.setARGB(color.alpha, color.red, color.green, color.blue)
        canvas.drawRect(location, location, locationEnd, locationEnd, rectPaint)
    }

    private fun drawHighlightedCell(canvas: Canvas, cellSize: Int) {
        val cellToHighlight = highlightedCell()
        if (cellToHighlight != null) {
            val fileIndex = cellToHighlight.first
            val rankIndex = cellToHighlight.second

            val x = (cellSize * (0.5 + (if (reversed) 7 - fileIndex else fileIndex))).toFloat()
            val y = (cellSize * (0.5 + (if (reversed) rankIndex else 7 - rankIndex))).toFloat()
            rectPaint.setARGB(185, 0, 255, 0)
            canvas.drawRect(x, y, x + cellSize, y + cellSize, rectPaint)
        }
    }


    override fun onDraw(canvas: Canvas) {
        val cellSize = measuredWidth.min(measuredHeight) / 9
        fontPaint.textSize = (cellSize * 0.4).toFloat()
        fontPaint.typeface = Typeface.defaultFromStyle(Typeface.BOLD)

        drawBackground(canvas)
        drawCells(canvas, cellSize)
        drawLetters(canvas, cellSize)
        if (highlightedCell() != null) drawHighlightedCell(canvas, cellSize)
        drawPieces(canvas, cellSize)
        drawPlayerTurn(canvas, cellSize)
    }

    fun toFEN(): String = relatedPosition().fen

    fun setFromFen(boardFen: String) {
        val newPosition = Position(boardFen)
        replacePositionWith(newPosition)
    }

}