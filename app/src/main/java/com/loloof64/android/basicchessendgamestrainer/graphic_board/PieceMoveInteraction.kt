package com.loloof64.android.basicchessendgamestrainer.graphic_board

import ictk.boardgame.IllegalMoveException
import ictk.boardgame.OutOfTurnException
import ictk.boardgame.chess.ChessBoard
import ictk.boardgame.chess.ChessMove
import java.util.logging.Logger

data class PromotionInfo(val startFile: Int, val startRank: Int,
                         val endFile: Int, val endRank: Int)

interface PieceMoveInteraction {

    var _highlightedCell: Pair<Int, Int>?

    var _relatedBoard: ChessBoard
    var _pendingPromotionInfo: PromotionInfo?

    fun reactForIllegalMove() : Unit
    fun askForPromotionPiece() : Unit
    fun invalidate()

    fun reactOnClick(cellFileIndex: Int, cellRankIndex: Int) {
        require(cellFileIndex >= 0 && cellFileIndex < 8)
        require(cellRankIndex >= 0 && cellRankIndex < 8)

        val realFile = cellFileIndex + 1
        val realRank = cellRankIndex + 1

        if (_highlightedCell != null) {
            // move validation
            val startFile = _highlightedCell!!.first
            val startRank = _highlightedCell!!.second

            val realStartFile = startFile + 1
            val realStartRank = startRank + 1

            val pieceAtStartCellIsPawn = _relatedBoard.toCharArray()[startFile][startRank].toLowerCase() == 'p'
            val isWhiteTurn = _relatedBoard.playerToMove == 0
            val isPromotionMove = pieceAtStartCellIsPawn && (if (isWhiteTurn) cellRankIndex == 7 else cellRankIndex == 0)

            val pieceAtStartCellIsKing = _relatedBoard.toCharArray()[startFile][startRank].toLowerCase() == 'k'

            val deltaFile = cellFileIndex - startFile
            val deltaRank = cellRankIndex - startRank
            val isKingSideCastlingMove = pieceAtStartCellIsKing && deltaFile == 2 && deltaRank == 0
            val isQueenSideCastlingMove = pieceAtStartCellIsKing && deltaFile == -2 && deltaRank == 0

            if (isPromotionMove) {
                _pendingPromotionInfo = PromotionInfo(realStartFile, realStartRank, realFile, realRank)
                askForPromotionPiece()
            }
            else {
                try {
                    val move = if (isKingSideCastlingMove) ChessMove(_relatedBoard, ChessMove.CASTLE_KINGSIDE)
                    else if(isQueenSideCastlingMove) ChessMove(_relatedBoard, ChessMove.CASTLE_QUEENSIDE)
                    else ChessMove(_relatedBoard, realStartFile, realStartRank, realFile, realRank)
                    _relatedBoard.playMove(move)
                }
                catch (e: IllegalMoveException) {
                    if (realStartFile != realFile || realStartRank != realRank) reactForIllegalMove()
                }
                catch (e: OutOfTurnException) {
                    if (realStartFile != realFile || realStartRank != realRank) reactForIllegalMove()
                } finally {
                    _highlightedCell = null
                }
            }
        } else {
            // move start
            val NO_PIECE = null
            val movedPiece = _relatedBoard.toCharArray()[cellFileIndex][cellRankIndex]
            val isOccupiedSquare = movedPiece != NO_PIECE
            val isWhiteTurn = _relatedBoard.playerToMove == 0
            val isBlackTurn = _relatedBoard.playerToMove > 0
            val isWhitePiece = movedPiece.isUpperCase()
            val isBlackPiece = movedPiece.isLowerCase()
            val isOneOfOurPiece = (isWhiteTurn && isWhitePiece) || (isBlackTurn && isBlackPiece)

            _highlightedCell = if (isOccupiedSquare && isOneOfOurPiece) Pair(cellFileIndex, cellRankIndex) else null
        }

        invalidate()
    }

    fun validatePromotionMove(promotedPieceType: Int) {
        when(_pendingPromotionInfo) {
            null -> {}
            else -> {
                try {
                    val move = ChessMove(_relatedBoard,
                            _pendingPromotionInfo!!.startFile, _pendingPromotionInfo!!.startRank,
                            _pendingPromotionInfo!!.endFile, _pendingPromotionInfo!!.endRank,
                            promotedPieceType)
                    _relatedBoard.playMove(move)
                } catch (e: IllegalMoveException) {
                    // should not happen
                    Logger.getLogger("ChessExercisesManager").severe("Illegal move ! (When validating promotion)")
                } catch (e: OutOfTurnException) {
                    // should not happen
                    Logger.getLogger("ChessExercisesManager").severe("Out of turn ! (When validating promotion)")
                } finally {
                    _pendingPromotionInfo = null
                    _highlightedCell = null
                    invalidate()
                }
            }
        }
    }

}