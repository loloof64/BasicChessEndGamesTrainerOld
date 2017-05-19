package com.loloof64.android.basicchessendgamestrainer.exercise_chooser

import ictk.boardgame.chess.ChessBoard
import ictk.boardgame.chess.ChessPiece
import ictk.boardgame.chess.Square
import ictk.boardgame.chess.io.FEN
import java.util.*

data class PieceKind(val pieceClass:Class<out ChessPiece>, val ofPlayerSide: Boolean)
typealias Constraint = (file: Int, rank: Int, board: ChessBoard) -> Boolean

fun NO_CONSTRAINT(file: Int, rank: Int, board: ChessBoard): Boolean {
    return true
}

val FILE_A = 0
val FILE_B = 1
val FILE_C = 2
val FILE_D = 3
val FILE_E = 4
val FILE_F = 5
val FILE_G = 6
val FILE_H = 7

val RANK_1 = 0
val RANK_2 = 1
val RANK_3 = 2
val RANK_4 = 3
val RANK_5 = 4
val RANK_6 = 5
val RANK_7 = 6
val RANK_8 = 7

class PieceConstraint(val pieceKind: PieceKind, val constraint: Constraint)

class PositionGenerator(val playerKingConstraint: Constraint,
                        val oppositeKingConstraint: Constraint,
                        val otherConstraint: Array<Constraint>) {

    fun generatePosition(playerHasWhite: Boolean = true): String {
        _boardPieces = Array(8, {_ -> kotlin.CharArray(8, {_ -> ' '})})
        placeKings(playerHasWhite)
        placeOtherPieces(playerHasWhite)
        val board = ChessBoard()
        board.setPosition(_boardPieces)
        return FEN().boardToString(board)
    }

    private fun cloneBoardPieces(refToClone: Array<CharArray>):Array<CharArray> {
        val arrayToReturn = Array(8, {_ -> kotlin.CharArray(8,  {_ -> ' '})})
        refToClone.forEachIndexed{index, chars -> arrayToReturn[index] = chars.clone()}
        return arrayToReturn
    }

    private fun placeKings(playerHasWhite: Boolean){
        val board = ChessBoard()
        while (true){ // setting up player king
            val kingFile = _random.nextInt(8)
            val kingRank = _random.nextInt(8)

            val boardPiecesClone = cloneBoardPieces(_boardPieces)
            if (boardPiecesClone[kingFile][kingRank] in "PNBRQKpnbrqk") continue
            boardPiecesClone[kingFile][kingRank] = if (playerHasWhite) 'K' else 'k'
            board.setPosition(boardPiecesClone)
            if (playerKingConstraint(kingFile, kingRank, board)) {
                _boardPieces = cloneBoardPieces(boardPiecesClone)
                break
            }
        }
        while (true){ // setting up enemy king
            val kingFile = _random.nextInt(8)
            val kingRank = _random.nextInt(8)

            val boardPiecesClone = cloneBoardPieces(_boardPieces)
            if (boardPiecesClone[kingFile][kingRank] in "PNBRQKpnbrqk") continue
            boardPiecesClone[kingFile][kingRank] = if (!playerHasWhite) 'K' else 'k'
            board.setPosition(boardPiecesClone)
            // If the new placed king is threatened by the other
            if (board.isThreatened(Square((kingFile+1).toByte(),
                    (kingRank+1).toByte()), playerHasWhite)) continue
            if (oppositeKingConstraint(kingFile, kingRank, board)) {
                _boardPieces = cloneBoardPieces(boardPiecesClone)
                break
            }
        }
    }

    private fun placeOtherPieces(playerHasWhite: Boolean){

    }

    private var _boardPieces:Array<CharArray> = Array(8, {_ -> kotlin.CharArray(8,  {_ -> ' '})})
    private val _random = Random()
}

val KRRvK_PositionGenerator = PositionGenerator(
     playerKingConstraint = ::NO_CONSTRAINT,
     oppositeKingConstraint = {file, rank, board ->
         (file >= FILE_C && file <= FILE_F) && (rank >= RANK_3 && rank <= RANK_6)
     },
     otherConstraint = arrayOf()
)

val KQvK_PositionGenerator = PositionGenerator(
        playerKingConstraint = ::NO_CONSTRAINT,
        oppositeKingConstraint = {file, rank, board ->
            (file >= FILE_C && file <= FILE_F) && (rank >= RANK_3 && rank <= RANK_6)
        },
        otherConstraint = arrayOf()
)


val KBBvK_PositionGenerator = PositionGenerator(
        playerKingConstraint = ::NO_CONSTRAINT,
        oppositeKingConstraint = {file, rank, board ->
            (file >= FILE_C && file <= FILE_F) && (rank >= RANK_3 && rank <= RANK_6)
        },
        otherConstraint = arrayOf()
)

