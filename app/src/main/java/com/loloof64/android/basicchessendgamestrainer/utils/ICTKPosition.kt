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

package com.loloof64.android.basicchessendgamestrainer.utils

import ictk.boardgame.Move
import ictk.boardgame.Piece
import ictk.boardgame.chess.*
import ictk.boardgame.chess.io.FEN
import ictk.boardgame.chess.io.SAN
import java.util.*

class ICTKPosition(startPositionFen: String) : IPosition {

    companion object {

        fun ictkPieceToChessPiece(piece: Piece?): ChessPiece? {
            return if (piece == null) null
            else {
                val pieceToConvert = piece as ictk.boardgame.chess.ChessPiece
                when {
                    pieceToConvert.isPawn -> ChessPiece(type = ChessPieceType.Pawn, whiteOwner = ! pieceToConvert.isBlack)
                    pieceToConvert.isKnight -> ChessPiece(type = ChessPieceType.Knight, whiteOwner = ! pieceToConvert.isBlack)
                    pieceToConvert.isBishop -> ChessPiece(type = ChessPieceType.Bishop, whiteOwner = ! pieceToConvert.isBlack)
                    pieceToConvert.isRook -> ChessPiece(type = ChessPieceType.Rook, whiteOwner = ! pieceToConvert.isBlack)
                    pieceToConvert.isQueen -> ChessPiece(type = ChessPieceType.Queen, whiteOwner = ! pieceToConvert.isBlack)
                    pieceToConvert.isKing -> ChessPiece(type = ChessPieceType.King, whiteOwner = ! pieceToConvert.isBlack)
                    else -> throw RuntimeException("Unrecognized piece $pieceToConvert !")
                }
            }
        }

        fun String?.isSamePositionAs(otherPosition: String?): Boolean {
            if (this == null || otherPosition == null) return false
            val thisParts = this.split(" ")
            val otherParts = otherPosition.split(" ")
            val boardsAreSame = thisParts[0] == otherParts[0]
            val playerTurnIsSame = thisParts[1] == otherParts[1]
            val castlingsAreSame = thisParts[2] == otherParts[2]
            val enPassantAreSame = thisParts[3] == otherParts[3]

            return boardsAreSame and playerTurnIsSame and castlingsAreSame and enPassantAreSame
        }
    }

    private val fenInstance = FEN()
    private val sanInstance = SAN()
    private var internalGame = ChessGame(ChessGameInfo(), fenInstance.stringToBoard(startPositionFen) as ChessBoard)

    override fun toFEN(): String {
        return fenInstance.boardToString(internalGame.board)
    }

    override fun isLegalMove(move: ChessMove): Boolean {
        return try {
            internalGame.board.verifyIsLegalMove(chessMoveToICTKMove(move))
            true
        }
        catch (ex: Exception){
            false
        }
    }

    override fun modifyPositionByDoingMove(move: ChessMove, promotionPieceType: PromotionPieceType) {
        internalGame.history.add(chessMoveToICTKMove(move, promotionPieceType))
    }

    override fun modifyPositionByAddingPieceAt(piece: ChessPiece, cell: ChessCell) {
        val internalBoard = internalGame.board as ChessBoard
        when (piece.type) {
            ChessPieceType.Pawn -> internalBoard.addPawn(cell.file + 1, cell.rank + 1, ! piece.whiteOwner)
            ChessPieceType.Knight -> internalBoard.addKnight(cell.file + 1, cell.rank + 1, ! piece.whiteOwner)
            ChessPieceType.Bishop -> internalBoard.addBishop(cell.file + 1, cell.rank + 1, ! piece.whiteOwner)
            ChessPieceType.Rook -> internalBoard.addRook(cell.file + 1, cell.rank + 1, ! piece.whiteOwner)
            ChessPieceType.Queen -> internalBoard.addQueen(cell.file + 1, cell.rank + 1, ! piece.whiteOwner)
            ChessPieceType.King -> internalBoard.addKing(cell.file + 1, cell.rank + 1, ! piece.whiteOwner)
        }
    }

    override fun modifyPositionBySettingIfWhiteToMove(whiteToMove: Boolean) {
        (internalGame.board as ChessBoard).isBlackMove = ! whiteToMove
    }

    override fun modifyPositionBySettingCastleRights(castleRights: CastleRights, checkIfResultPositionLegal: Boolean) {
        val internalBoard = internalGame.board as ChessBoard
        internalBoard.isWhiteCastleableKingside = castleRights.whiteShortCastle
        internalBoard.isWhiteCastleableQueenside = castleRights.whiteLongCastle
        internalBoard.isBlackCastleableKingside = castleRights.blackShortCastle
        internalBoard.isBlackCastleableQueenside = castleRights.blackLongCastle
    }

    override fun modifyPositionBySettingEnPassantFile(file: Int?, checkIfResultPositionLegal: Boolean) {
        (internalGame.board as ChessBoard).setEnPassantFile(file ?: ChessBoard.NO_ENPASSANT.toInt())
    }

    override fun modifyPositionBySettingHalfMovesCountForDraw(count: Int) {
        (internalGame.board as ChessBoard).set50MoveRulePlyCount(count)
    }

    override fun modifyPositionBySettingMoveNumber(moveNumber: Int) {
        (internalGame.board as ChessBoard).currentMoveNumber = moveNumber
    }

    override fun getPieceAtCell(cell: ChessCell): ChessPiece? {
        val square = (internalGame.board as ChessBoard).getSquare(cell.file + 1, cell.rank + 1)
        return ictkPieceToChessPiece(square.piece)
    }

    override fun isWhiteToMove(): Boolean {
        return ! (internalGame.board as ChessBoard).isBlackMove
    }

    override fun getCastleRights(): CastleRights {
        val internalBoard = internalGame.board as ChessBoard
        return CastleRights(
                whiteShortCastle = internalBoard.isWhiteCastleableKingside,
                whiteLongCastle = internalBoard.isWhiteCastleableQueenside,
                blackShortCastle = internalBoard.isBlackCastleableKingside,
                blackLongCastle = internalBoard.isBlackCastleableQueenside
        )
    }

    override fun getEnPassantFile(): Int? {
        val file = (internalGame.board as ChessBoard).enPassantFile
        return if (file == ChessBoard.NO_ENPASSANT) null else file.toInt()
    }

    override fun getHalfMovesCountForDraw(): Int {
        return (internalGame.board as ChessBoard).get50MoveRulePlyCount()
    }

    override fun getMoveNumber(): Int {
        return (internalGame.board as ChessBoard).currentMoveNumber
    }

    override fun kingInChess(): Boolean {
        return (internalGame.board as ChessBoard).isCheck
    }

    override fun getMoveSAN(move: ChessMove, promotionPieceType: PromotionPieceType): String? {
        return sanInstance.moveToString(chessMoveToICTKMove(move, promotionPieceType))
    }

    override fun getMoveFAN(move: ChessMove, promotionPieceType: PromotionPieceType): String? {
        var moveFAN = getMoveSAN(move, promotionPieceType)
        val movedPiece = getPieceAtCell(move.from)
        val movedPieceOwnerIsWhite = movedPiece!!.whiteOwner

        moveFAN = moveFAN?.replace("P", if (movedPieceOwnerIsWhite) "\u2659" else "\u265f")
        moveFAN = moveFAN?.replace("N", if (movedPieceOwnerIsWhite) "\u2658" else "\u265e")
        moveFAN = moveFAN?.replace("B", if (movedPieceOwnerIsWhite) "\u2657" else "\u265d")
        moveFAN = moveFAN?.replace("R", if (movedPieceOwnerIsWhite) "\u2656" else "\u265c")
        moveFAN = moveFAN?.replace("Q", if (movedPieceOwnerIsWhite) "\u2655" else "\u265b")
        moveFAN = moveFAN?.replace("K", if (movedPieceOwnerIsWhite) "\u2654" else "\u265a")

        return moveFAN
    }

    override fun isMate(): Boolean {
        return (internalGame.board as ChessBoard).isCheckmate
    }

    override fun isStaleMate(): Boolean {
        return (internalGame.board as ChessBoard).isStalemate
    }

    override fun isDrawByRepetitions(): Boolean {
        val history = internalGame.history
        val internalBoard = internalGame.board
        val historyMovesStack = Stack<Move>()
        val historyPositionsStack = Stack<String>()
        val currentPosition = fenInstance.boardToString(internalBoard)

        // Getting the line positions and moves
        historyPositionsStack.push(currentPosition)
        var currentHistoryMove = history.prev()
        while (currentHistoryMove != null) {
            historyMovesStack.push(currentHistoryMove)
            historyPositionsStack.push(fenInstance.boardToString(internalBoard))
            currentHistoryMove = history.prev()
        }

        // Playing the history back
        try {
            while (true) {
                val currentMove = historyMovesStack.pop()
                history.next(currentMove)
            }
        } catch (ex: EmptyStackException) {}

        // Count
        var currentPositionRepetitions = 0
        try {
            while (true) {
                val soughtPosition = historyPositionsStack.pop()
                if (soughtPosition.isSamePositionAs(currentPosition)) currentPositionRepetitions += 1
            }
        } catch (ex: EmptyStackException) {}

        return currentPositionRepetitions >= 3
    }

    override fun isDrawByMissingMatingMaterial(): Boolean {
        val pawnsCount = getPieceCount(ChessPiece(
                type = ChessPieceType.Pawn,
                whiteOwner = true
        )) + getPieceCount(ChessPiece(
                type= ChessPieceType.Pawn,
                whiteOwner = false
        ))

        val rooksCount = getPieceCount(ChessPiece(
                type = ChessPieceType.Rook,
                whiteOwner = true
        )) + getPieceCount(ChessPiece(
                type= ChessPieceType.Rook,
                whiteOwner = false
        ))

        val queensCount = getPieceCount(ChessPiece(
                type = ChessPieceType.Queen,
                whiteOwner = true
        )) + getPieceCount(ChessPiece(
                type= ChessPieceType.Queen,
                whiteOwner = false
        ))

        val whitePieces = (internalGame.board as ChessBoard).getMaterialCount(false)
        val blackPieces = (internalGame.board as ChessBoard).getMaterialCount(true)

        val whiteKnightsCount = getPieceCount(ChessPiece(
                type = ChessPieceType.Knight,
                whiteOwner = true
        ))
        val blackKnightsCount = getPieceCount(ChessPiece(
                type = ChessPieceType.Knight,
                whiteOwner = false
        ))

        val allPiecesCount = whitePieces + blackPieces


        return if (pawnsCount > 0) false
        else if (queensCount + rooksCount > 0) false
        else if (allPiecesCount == 4) {
            if (whitePieces > 1 && blackPieces > 1) true
            else whiteKnightsCount == 2 || blackKnightsCount == 2
        }
        else allPiecesCount < 4
    }

    override fun isDrawByFiftyMovesCount(): Boolean {
        return (internalGame.board as ChessBoard).is50MoveRuleApplicible
    }

    override fun isPromotionMove(move: ChessMove): Boolean {
        val ictkMove = chessMoveToICTKMove(move) as ictk.boardgame.chess.ChessMove
        return ictkMove.promotion != null
    }

    fun checkThatExactlyOneKingForEachSide() {
        val whiteKingsCount = getPieceCount(ChessPiece(ChessPieceType.King, whiteOwner = true))
        val blackKingsCount = getPieceCount(ChessPiece(ChessPieceType.King, whiteOwner = false))

        if (whiteKingsCount != 1) throw RuntimeException("There must be exactly one white king (given: $whiteKingsCount) !")
        if (blackKingsCount != 1) throw RuntimeException("There must be exactly one black king (given: $blackKingsCount) !")
    }

    fun checkThatNoPawnOnRank1Or8() {
        val internalBoard = internalGame.board as ChessBoard
        val ranks = arrayOf(ChessRank.Rank1.ordinal, ChessRank.Rank8.ordinal)
        ranks.forEach { soughtRank ->
            (0 until 8).forEach { soughtFile ->
                val currentSquare = internalBoard.getSquare(soughtFile + 1, soughtRank + 1)
                if (currentSquare.piece != null && currentSquare.piece.isPawn) throw RuntimeException(
                        "There must not be any pawn on rank 1 and 8 !")
            }
        }
    }

    fun checkThatSideNotToMoveHasNotKingInChess() {
        val internalBoard = internalGame.board as ChessBoard

        fun findFirstSquareForPiece(pieceToSearch: ChessPiece): Square? {
            var soughtSquare:Square? = null
            (0 until 8).forEach { soughtRank ->
                (0 until 8).forEach { soughtFile ->
                    val currentSquare = internalBoard.getSquare(soughtFile + 1, soughtRank + 1)
                    val pieceAtCurrentSquare = currentSquare.piece
                    if (ictkPieceToChessPiece(pieceAtCurrentSquare) == pieceToSearch) {
                        soughtSquare = currentSquare
                    }
                }
            }
            return soughtSquare
        }

        val enemyKingSquare = findFirstSquareForPiece(ChessPiece(type=ChessPieceType.King, whiteOwner =  internalBoard.isBlackMove ))
        val playerKingSquare = findFirstSquareForPiece(ChessPiece(type=ChessPieceType.King, whiteOwner =  ! internalBoard.isBlackMove ))

        val couldNotFindEnemyKingSquare = enemyKingSquare == null
        if (couldNotFindEnemyKingSquare) {
            val enemyKingSide = if (internalBoard.isBlackMove) "white" else "black"
            throw RuntimeException("Missing king for $enemyKingSide !")
        }

        val enemyKingInChess = internalBoard.isThreatened(enemyKingSquare, internalBoard.isBlackMove )
        if (enemyKingInChess) throw RuntimeException("" +
                "King of side without turn must not be in chess !")

        val bothKingsAreNeighbours = if (playerKingSquare == null || enemyKingSquare == null) false else
            (Math.abs(playerKingSquare.file - enemyKingSquare.file) <= 1) and (Math.abs(playerKingSquare.rank - enemyKingSquare.rank) <= 1)
        if (bothKingsAreNeighbours) throw RuntimeException("Both kings can't be neighbours !")
    }

    fun checkThatOtherPiecesCountIsGood() {
        val whiteQueensCount = getPieceCount(ChessPiece(ChessPieceType.Queen, whiteOwner = true))
        val blackQueensCount = getPieceCount(ChessPiece(ChessPieceType.Queen, whiteOwner = false))

        if (whiteQueensCount > 9) throw RuntimeException("There must be at most 9 white queens on board !")
        if (blackQueensCount > 9) throw RuntimeException("There must be at most 9 black queens on board !")

        val whitePawnsCount = getPieceCount(ChessPiece(ChessPieceType.Pawn, whiteOwner = true))
        val blackPawnsCount = getPieceCount(ChessPiece(ChessPieceType.Pawn, whiteOwner = false))

        if (whitePawnsCount > 8) throw RuntimeException("There must be at most 8 white pawns on board !")
        if (blackPawnsCount > 8) throw RuntimeException("There must be at most 8 black pawns on board !")

        val whiteKnightsCount = getPieceCount(ChessPiece(ChessPieceType.Knight, whiteOwner = true))
        val blackKnightsCount = getPieceCount(ChessPiece(ChessPieceType.Knight, whiteOwner = false))

        if (whiteKnightsCount > 10) throw RuntimeException("There must be at most 10 white knights on board !")
        if (blackKnightsCount > 10) throw RuntimeException("There must be at most 10 black knights on board !")

        val whiteBishopCount = getPieceCount(ChessPiece(ChessPieceType.Bishop, whiteOwner = true))
        val blackBishopCount = getPieceCount(ChessPiece(ChessPieceType.Bishop, whiteOwner = false))

        if (whiteBishopCount > 10) throw RuntimeException("There must be at most 10 white bishops on board !")
        if (blackBishopCount > 10) throw RuntimeException("There must be at most 10 black bishops on board !")

        val whiteRookCount = getPieceCount(ChessPiece(ChessPieceType.Rook, whiteOwner = true))
        val blackRookCount = getPieceCount(ChessPiece(ChessPieceType.Rook, whiteOwner = false))

        if (whiteRookCount > 10) throw RuntimeException("There must be at most 10 white rooks on board !")
        if (blackRookCount > 10) throw RuntimeException("There must be at most 10 black rooks on board !")

        val internalBoard = internalGame.board as ChessBoard
        val whitePiecesCount = internalBoard.getMaterialCount(false)
        val blackPiecesCount = internalBoard.getMaterialCount(true)

        if (whitePiecesCount > 16) throw RuntimeException("There must be at most 16 pieces for white on board !")
        if (blackPiecesCount > 16) throw RuntimeException("There must be at most 16 pieces for black on board !")
    }

    private fun getPieceCount(piece: ChessPiece): Int {
        val internalBoard = internalGame.board as ChessBoard
        return (0 until 8).map {rank ->
            (0 until 8).map {file ->
                val square = internalBoard.getSquare(file + 1, rank + 1)
                ictkPieceToChessPiece(square.piece) == piece
            }
        }.flatten().filter{ it }.count()
    }

    private fun chessMoveToICTKMove(move: ChessMove, promotionPieceType: PromotionPieceType = PromotionPieceType.Queen) : Move {
        val internalBoard = internalGame.board as ChessBoard
        return ChessMove(
            internalBoard,
            internalBoard.getSquare(move.from.file + 1, move.from.rank + 1),
            internalBoard.getSquare(move.to.file + 1, move.to.rank + 1),
            promotionPieceToICTKChessPiece(promotionPieceType) as ictk.boardgame.chess.ChessPiece
        )
    }

    private fun promotionPieceToICTKChessPiece(promotionPieceType: PromotionPieceType) : Piece {
        return when (promotionPieceType) {
            PromotionPieceType.Queen -> Queen(! isWhiteToMove())
            PromotionPieceType.Rook -> Rook(! isWhiteToMove())
            PromotionPieceType.Bishop -> Bishop(! isWhiteToMove())
            PromotionPieceType.Knight -> Knight(! isWhiteToMove())
        }
    }
}