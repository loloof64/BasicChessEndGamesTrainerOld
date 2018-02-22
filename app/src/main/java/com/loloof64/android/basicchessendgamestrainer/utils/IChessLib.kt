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

interface IChessLib {
    fun isLegalPositionString(positionFEN: String) : Boolean
    fun buildPositionFromString(positionFEN: String,
                                checkIfResultPositionLegal: Boolean = true): IPosition
}

class IllegalMoveException : Exception()

enum class ChessFile {
    FileA, FileB, FileC, FileD, FileE, FileG, FileH
}

enum class ChessRank {
    Rank1, Rank2, Rank3, Rank4, Rank5, Rank6, Rank7, Rank8
}

data class ChessCell(val file: Int, val rank: Int){
    init {
        require(file in (0 until 8))
        require(rank in (0 until 8))
    }
}

data class CastleRights(val whiteShortCastle: Boolean,
                        val whiteLongCastle: Boolean,
                        val blackShortCastle: Boolean,
                        val blackLongCastle: Boolean)

enum class ChessPieceType {
    Pawn, Knight, Bishop, Rook, Queen, King
}

enum class PromotionPieceType {
    Knight, Bishop, Rook, Queen
}

data class ChessPiece(val type: ChessPieceType, val whiteOwner: Boolean)

data class ChessMove(val from: ChessCell, val to: ChessCell, val promotionPiece: PromotionPieceType? = null)

interface IPosition {
    fun toFEN():String
    fun isLegalMove(move: ChessMove) : Boolean
    fun modifyPositionByDoingMove(move: ChessMove, promotionPieceType: PromotionPieceType = PromotionPieceType.Queen)

    fun modifyPositionByAddingPieceAt(piece: ChessPiece, cell: ChessCell)
    fun modifyPositionBySettingIfWhiteToMove(whiteToMove: Boolean)
    fun modifyPositionBySettingCastleRights(castleRights: CastleRights,
                                            checkIfResultPositionLegal: Boolean)
    fun modifyPositionBySettingEnPassantFile(file: Int?,
                                             checkIfResultPositionLegal: Boolean)
    fun modifyPositionBySettingHalfMovesCountForDraw(count: Int)
    fun modifyPositionBySettingMoveNumber(moveNumber: Int)

    fun getPieceAtCell(cell: ChessCell): ChessPiece?
    fun isWhiteToMove(): Boolean
    fun getCastleRights(): CastleRights
    fun getEnPassantFile(): Int?
    fun getHalfMovesCountForDraw(): Int
    fun getMoveNumber(): Int

    fun kingInChess(): Boolean
    fun getMoveSAN(move: ChessMove, promotionPieceType: PromotionPieceType = PromotionPieceType.Queen): String?
    fun getMoveFAN(move: ChessMove, promotionPieceType: PromotionPieceType = PromotionPieceType.Queen): String?

    fun isMate(): Boolean
    fun isStaleMate() : Boolean
    fun isDrawByRepetitions(): Boolean
    fun isDrawByMissingMatingMaterial(): Boolean
    fun isDrawByFiftyMovesCount(): Boolean

    fun isPromotionMove(move: ChessMove) : Boolean
}