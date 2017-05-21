package com.loloof64.android.basicchessendgamestrainer.graphic_board

import chesspresso.Chess
import chesspresso.move.Move
import chesspresso.position.Position
import java.util.logging.Logger

data class PromotionInfo(val startFile: Int, val startRank: Int,
                         val endFile: Int, val endRank: Int)

interface PieceMoveInteraction {

    var _highlightedCell: Pair<Int, Int>?

    var _relatedPosition: Position
    var _pendingPromotionInfo: PromotionInfo?

    fun reactForIllegalMove() : Unit
    fun askForPromotionPiece() : Unit
    fun invalidate()

    fun reactOnClick(cellFileIndex: Int, cellRankIndex: Int) {
        require(cellFileIndex >= 0 && cellFileIndex < 8)
        require(cellRankIndex >= 0 && cellRankIndex < 8)

        if (_highlightedCell != null) {
            // move validation
            val startFile = _highlightedCell!!.first
            val startRank = _highlightedCell!!.second

            val pieceAtStartCellIsPawn = _relatedPosition.getPiece(Chess.coorToSqi(startFile, startRank)).toShort() == Chess.PAWN
            val isWhiteTurn = _relatedPosition.toPlay == Chess.WHITE
            val isPromotionMove = pieceAtStartCellIsPawn && (if (isWhiteTurn) cellRankIndex == 7 else cellRankIndex == 0)

            if (isPromotionMove) {
                _pendingPromotionInfo = PromotionInfo(startFile, startRank, cellFileIndex, cellRankIndex)
                askForPromotionPiece()
            }
            else {
                val matchMoves = _relatedPosition.allMoves.filter { currentMove ->
                    val currentMoveFrom = Move.getFromSqi(currentMove)
                    val currentMoveTo = Move.getToSqi(currentMove)

                    val commandMoveFrom = Chess.coorToSqi(startFile, startRank)
                    val commandMoveTo = Chess.coorToSqi(cellFileIndex, cellRankIndex)

                    (commandMoveFrom == currentMoveFrom)
                            && (commandMoveTo == currentMoveTo)
                }
                if (matchMoves.isEmpty()) reactForIllegalMove()
                else {
                    _relatedPosition.doMove(matchMoves.first())
                }
                _highlightedCell = null
            }
        } else {
            // move start
            val movedPiece = _relatedPosition.getStone(Chess.coorToSqi(cellFileIndex, cellRankIndex)).toShort()
            val isOccupiedSquare = movedPiece != Chess.NO_PIECE
            val isWhiteTurn = _relatedPosition.toPlay == Chess.WHITE
            val isBlackTurn = _relatedPosition.toPlay != Chess.WHITE
            val isWhitePiece = movedPiece < 0
            val isBlackPiece = movedPiece > 0
            val isOneOfOurPiece = (isWhiteTurn && isWhitePiece) || (isBlackTurn && isBlackPiece)

            _highlightedCell = if (isOccupiedSquare && isOneOfOurPiece) Pair(cellFileIndex, cellRankIndex) else null
        }

        invalidate()
    }

    fun validatePromotionMove(promotedPieceType: Short) {
        when(_pendingPromotionInfo) {
            null -> {}
            else -> {
                val move = _relatedPosition.getPawnMove(_pendingPromotionInfo!!.startFile,
                        Chess.coorToSqi(_pendingPromotionInfo!!.endFile, _pendingPromotionInfo!!.endRank),
                        promotedPieceType.toInt())
                if (move == Move.ILLEGAL_MOVE) Logger.getLogger("BasicChessEndgamesTrainer").severe("Illegal move ! (When validating promotion)")
                else _relatedPosition.doMove(move)
                _pendingPromotionInfo = null
                _highlightedCell = null
                invalidate()
            }
        }
    }

}