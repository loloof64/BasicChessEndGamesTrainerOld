package com.loloof64.android.basicchessendgamestrainer.karballo_chess_implementation

import com.loloof64.android.basicchessendgamestrainer.chess_abstraction.IMove
import com.loloof64.android.basicchessendgamestrainer.chess_abstraction.Square
import com.loloof64.android.basicchessendgamestrainer.chess_abstraction.PieceType

import karballo.Move as KMove
import karballo.Piece as KPiece

class Move(private val from: Square, private val to: Square, private val promotionPiece: PieceType? = null) : IMove {
    override fun getFrom(): Square = from
    override fun getTo(): Square = to
    override fun getPromotionPieceType(): PieceType? = promotionPiece

    companion object {
        fun getMoveFromIndex(index: Int): Move {
            val fromSquare = longToSquare(KMove.getFromSquare(index))
            val toSquare = longToSquare(KMove.getToSquare(index))
            val karballoPromotionPiece = KMove.getPiecePromoted(index)
            val promotedPiece = when (karballoPromotionPiece){
                KPiece.QUEEN -> PieceType.QUEEN
                KPiece.ROOK -> PieceType.ROOK
                KPiece.BISHOP -> PieceType.BISHOP
                KPiece.KNIGHT -> PieceType.KNIGHT
                else -> null
            }

            return Move(fromSquare, toSquare, promotedPiece)
        }
    }
}