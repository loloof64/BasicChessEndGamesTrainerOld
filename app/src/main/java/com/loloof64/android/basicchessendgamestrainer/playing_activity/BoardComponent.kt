package com.loloof64.android.basicchessendgamestrainer.playing_activity

import android.content.Context
import android.graphics.*
import android.support.graphics.drawable.VectorDrawableCompat
import android.util.AttributeSet
import android.view.View
import com.loloof64.android.basicchessendgamestrainer.R
import karballo.Board
import karballo.Color as KColor

infix fun Int.min(other : Int) = if (this < other) this else other
infix fun Int.max(other : Int) = if (this > other) this else other

data class SquareCoordinates(val file: Int, val rank: Int)

fun coordinatesToSquare(file: Int, rank: Int) : Long {
    return 1L shl ((7-file) + (8*rank))
}

fun squareToCoordinates(squareIndex: Long) : Pair<Int, Int> {
    val powerOf2 = (Math.log(squareIndex.toDouble()) / Math.log(2.0)).toInt()
    return Pair(7-(powerOf2%8), powerOf2/8)
}

abstract class BoardComponent(context: Context, open val attrs: AttributeSet?, defStyleAttr: Int) : View(context, attrs, defStyleAttr) {

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null, 0)

    data class ColorARGB(val alpha: Int, val red: Int, val green: Int, val blue: Int)

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

    protected abstract fun relatedPosition() : Board
    protected abstract fun replacePositionWith(positionFEN : String): Unit

    protected var reversed = false
    private val rectPaint = Paint()
    private val fontPaint = Paint()

    abstract fun highlightedStartCell() : SquareCoordinates?
    abstract fun highlightedTargetCell() : SquareCoordinates?

    fun reverse() {
        reversed = !reversed
        invalidate()
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
                val piece = relatedPosition().getPieceAt(coordinatesToSquare(file = cellFile, rank = cellRank))
                if (piece != '.') {
                    val imageRes = when (piece) {
                        'P' -> R.drawable.chess_pl
                        'p' -> R.drawable.chess_pd
                        'N' -> R.drawable.chess_nl
                        'n' -> R.drawable.chess_nd
                        'B' -> R.drawable.chess_bl
                        'b' -> R.drawable.chess_bd
                        'R' -> R.drawable.chess_rl
                        'r' -> R.drawable.chess_rd
                        'Q' -> R.drawable.chess_ql
                        'q' -> R.drawable.chess_qd
                        'K' -> R.drawable.chess_kl
                        'k' -> R.drawable.chess_kd
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
        val color = if (relatedPosition().turn) ColorARGB(255, 255, 255, 255) else ColorARGB(255, 0, 0, 0)
        val location = (8.5 * cellSize).toFloat()
        val locationEnd = (location + cellSize * 0.5).toFloat()
        rectPaint.setARGB(color.alpha, color.red, color.green, color.blue)
        canvas.drawRect(location, location, locationEnd, locationEnd, rectPaint)
    }

    private fun drawHighlightedCells(canvas: Canvas, cellSize: Int) {
        val startCellToHighlight = highlightedStartCell()
        if (startCellToHighlight != null){
            val fileIndex = startCellToHighlight.file
            val rankIndex = startCellToHighlight.rank

            val x = (cellSize * (0.5 + (if (reversed) 7 - fileIndex else fileIndex))).toFloat()
            val y = (cellSize * (0.5 + (if (reversed) rankIndex else 7 - rankIndex))).toFloat()
            rectPaint.setARGB(185, 255, 0, 0)
            canvas.drawRect(x, y, x + cellSize, y + cellSize, rectPaint)
        }

        val targetCellToHighlight = highlightedTargetCell()
        if (targetCellToHighlight != null) {
            val fileIndex = targetCellToHighlight.file
            val rankIndex = targetCellToHighlight.rank

            val x = (cellSize * (0.5 + (if (reversed) 7 - fileIndex else fileIndex))).toFloat()
            val y = (cellSize * (0.5 + (if (reversed) rankIndex else 7 - rankIndex))).toFloat()
            rectPaint.setARGB(185, 0, 255, 0)
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
            rectPaint.setARGB(185, 0, 255, 0)
            rectPaint.strokeWidth = cellSize * 0.1f

            canvas.drawLine(0f, y, width.toFloat(), y, rectPaint)
            canvas.drawLine(x, 0f, x, height.toFloat(), rectPaint)
        }
    }

    private fun drawHighlightedMove(canvas: Canvas, cellSize: Int){
        if (_highlightedMoveFrom.file !in 0..7) return
        if (_highlightedMoveFrom.rank !in 0..7) return
        if (_highlightedMoveTo.file !in 0..7) return
        if (_highlightedMoveTo.rank !in 0..7) return

        val paint = Paint()

        val fromPointX = (cellSize * if (reversed) (8 - _highlightedMoveFrom.file) else (_highlightedMoveFrom.file+1)).toFloat()
        val fromPointY = (cellSize * if (reversed) (_highlightedMoveFrom.rank+1) else (8 - _highlightedMoveFrom.rank)).toFloat()
        val toPointX = (cellSize * if (reversed) (8 - _highlightedMoveTo.file) else (_highlightedMoveTo.file+1)).toFloat()
        val toPointY = (cellSize * if (reversed) (_highlightedMoveTo.rank+1) else (8 - _highlightedMoveTo.rank)).toFloat()

        val angleDegrees = Math.toDegrees(Math.atan2(toPointY.toDouble() - fromPointY.toDouble(),
                toPointX.toDouble() - fromPointX.toDouble())).toFloat()

        val distance = Math.sqrt(Math.pow((toPointX - fromPointX).toDouble(), 2.0) +
                Math.pow((toPointY - fromPointY).toDouble(), 2.0)).toFloat()

        val arrowLength = distance * 0.15f

        paint.color = Color.parseColor("#FEAC22")
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

    fun toFEN(): String = relatedPosition().fen

    fun setFromFen(boardFen: String) {
        replacePositionWith(boardFen)
        invalidate()
    }

    private var _highlightedMoveFrom = SquareCoordinates(file = -1, rank = -1)
    private var _highlightedMoveTo = SquareCoordinates(file = -1, rank = -1)


}