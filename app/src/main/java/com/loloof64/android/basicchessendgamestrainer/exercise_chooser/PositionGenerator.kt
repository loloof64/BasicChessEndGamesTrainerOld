package com.loloof64.android.basicchessendgamestrainer.exercise_chooser

import chesspresso.Chess
import chesspresso.position.FEN
import chesspresso.position.Position
import java.util.*
import java.util.logging.Logger

data class PieceKind(val pieceType: Short, val ofPlayerSide: Boolean)
data class PieceKindCount(val pieceKind: PieceKind, val count: Int)
data class BoardCoordinate(val file: Int, val rank: Int)
/*
 * Constraint based on the piece coordinate and general position consideration
 */
typealias Constraint = (coord: BoardCoordinate, position: Position) -> Boolean

/*
 * Constraint based on 2 pieces of the same kind (so taking into account the piece class and
 * the fact that they belong or not to player) and general position consideration
 */
typealias MutualConstraint = (firstBoardCoord: BoardCoordinate,
                              secondBoardCoord: BoardCoordinate,
                              position: Position) -> Boolean

fun NO_CONSTRAINT(coord: BoardCoordinate, position: Position): Boolean {
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

fun Int.loops(callback : (Int) -> Unit) {
    for (i in 0 until this) callback(i)
}

/**
 * Builds a PieceKind of player from 'P', 'N', 'B', 'R', 'Q' or 'K'.
 * Other character will throw exception !
 */
val Char.p: PieceKind
        get() = when (this){
            'P' -> PieceKind(pieceType = Chess.PAWN, ofPlayerSide = true)
            'N' -> PieceKind(pieceType = Chess.KNIGHT, ofPlayerSide = true)
            'B' -> PieceKind(pieceType = Chess.BISHOP, ofPlayerSide = true)
            'R' -> PieceKind(pieceType = Chess.ROOK, ofPlayerSide = true)
            'Q' -> PieceKind(pieceType = Chess.QUEEN, ofPlayerSide = true)
            'K' -> PieceKind(pieceType = Chess.KING, ofPlayerSide = true)
            else -> throw IllegalArgumentException("p() not usable for char $this.")
        }

/**
 * Builds a PieceKind of enemy from 'P', 'N', 'B', 'R', 'Q' or 'K'.
 * Other character will throw exception !
 */
val Char.e : PieceKind
        get() = when (this){
            'P' -> PieceKind(pieceType = Chess.PAWN, ofPlayerSide = false)
            'N' -> PieceKind(pieceType = Chess.KNIGHT, ofPlayerSide = false)
            'B' -> PieceKind(pieceType = Chess.BISHOP, ofPlayerSide = false)
            'R' -> PieceKind(pieceType = Chess.ROOK, ofPlayerSide = false)
            'Q' -> PieceKind(pieceType = Chess.QUEEN, ofPlayerSide = false)
            'K' -> PieceKind(pieceType = Chess.KING, ofPlayerSide = false)
            else -> throw IllegalArgumentException("e() not usable for char $this.")
        }

class PositionGenerator(val playerKingConstraint: Constraint,
                        val oppositeKingConstraint: Constraint,
                        val otherPiecesCount: Array<PieceKindCount>,
                        val otherPiecesGlobalConstraint: Map<PieceKind, Constraint>,
                        val otherPiecesMutualConstraint: Map<PieceKind, MutualConstraint>) {

    fun generatePosition(playerHasWhite: Boolean = true): String {
        _position = Position()
        placeKings(playerHasWhite)
        placeOtherPieces(playerHasWhite)
        _position.plyNumber = 1
        Logger.getLogger("BasicChessEndgamesTrainer").info("Generated position is '${FEN.getFEN(_position)}'")
        return FEN.getFEN(_position)
    }

    private fun placeKings(playerHasWhite: Boolean){
        while (true){ // setting up player king
            val kingFile = _random.nextInt(8)
            val kingRank = _random.nextInt(8)

            val tempPosition = Position(_position)
            tempPosition.setStone(kingFile + 8*kingRank,
                    (if (playerHasWhite) Chess.WHITE_KING else Chess.BLACK_KING).toInt())
            if (playerKingConstraint(BoardCoordinate(kingFile, kingRank), tempPosition)) {
                _position = Position(tempPosition)
                break
            }
        }
        while (true){ // setting up enemy king
            val kingFile = _random.nextInt(8)
            val kingRank = _random.nextInt(8)

            val tempPosition = Position(_position)
            val cellNotEmpty = tempPosition.getStone(kingFile + 8*kingRank).toShort() != Chess.NO_STONE
            if (cellNotEmpty) continue
            tempPosition.setStone(kingFile + 8*kingRank,
                    (if (playerHasWhite) Chess.BLACK_KING else Chess.WHITE_KING).toInt())
            // If the enemy king is threatened, will go into another try
            tempPosition.toPlay = if (playerHasWhite) Chess.WHITE else Chess.BLACK
            val enemyKingInChess = tempPosition.isCheck
            tempPosition.toPlay = if (playerHasWhite) Chess.BLACK else Chess.WHITE
            if (enemyKingInChess) continue

            if (oppositeKingConstraint(BoardCoordinate(kingFile, kingRank), tempPosition)) {
                _position = Position(tempPosition)
                break
            }
        }
    }

    private fun placeOtherPieces(playerHasWhite: Boolean){
        otherPiecesCount.forEach { pieceKindCount ->
            val savedCoordinates = arrayListOf<BoardCoordinate>()
            val pieceKindConstraint = otherPiecesGlobalConstraint[pieceKindCount.pieceKind]
            val pieceKindMutualConstraint = otherPiecesMutualConstraint[pieceKindCount.pieceKind]
            pieceKindCount.count.loops { i ->
                while (true) {
                    val pieceFile = _random.nextInt(8)
                    val pieceRank = _random.nextInt(8)
                    val currentCoordinate = BoardCoordinate(pieceFile, pieceRank)

                    val tempPosition = _position.clone
                    val cellNotEmpty = tempPosition.getStone(pieceFile + 8*pieceRank).toShort() != Chess.NO_STONE
                    if (cellNotEmpty) continue
                    val isWhitePiece = (pieceKindCount.pieceKind.ofPlayerSide && playerHasWhite)
                    || (!pieceKindCount.pieceKind.ofPlayerSide && !playerHasWhite)
                    tempPosition.setStone(pieceFile + 8*pieceRank, pieceToStone(pieceKindCount.pieceKind, isWhitePiece))
                    // If the enemy king is threatened, will go into another try
                    tempPosition.toPlay = if (playerHasWhite) Chess.BLACK else Chess.WHITE
                    val enemyKingInChess = tempPosition.isCheck
                    tempPosition.toPlay = if (playerHasWhite) Chess.WHITE else Chess.BLACK
                    if (enemyKingInChess) continue

                    // If for any previous piece of same kind, mutual constraint is not respected, will go into another try
                    if (pieceKindMutualConstraint != null &&
                            savedCoordinates.any { !pieceKindMutualConstraint(it, currentCoordinate, tempPosition) }) continue

                    if (pieceKindConstraint == null || pieceKindConstraint(currentCoordinate, tempPosition)){
                        _position = tempPosition.clone
                        savedCoordinates += BoardCoordinate(pieceFile, pieceRank)
                        break
                    }
                }
            }
        }
    }

    private fun pieceToStone(pieceKind: PieceKind, whitePiece: Boolean): Int {
        return when(pieceKind.pieceType){
            Chess.PAWN -> if (whitePiece) Chess.WHITE_PAWN else Chess.BLACK_PAWN
            Chess.KNIGHT -> if (whitePiece) Chess.WHITE_KNIGHT else Chess.BLACK_KNIGHT
            Chess.BISHOP -> if (whitePiece) Chess.WHITE_BISHOP else Chess.BLACK_BISHOP
            Chess.ROOK -> if (whitePiece) Chess.WHITE_ROOK else Chess.BLACK_ROOK
            Chess.QUEEN -> if (whitePiece) Chess.WHITE_QUEEN else Chess.BLACK_QUEEN
            Chess.KING -> if (whitePiece) Chess.WHITE_KING else Chess.BLACK_KING
            else -> Chess.NO_STONE
        }.toInt()
    }

    private var _position = Position()
    private val _random = Random()
}

val KRRvK_PositionGenerator = PositionGenerator(
     playerKingConstraint = ::NO_CONSTRAINT,
     oppositeKingConstraint = {(file, rank), position ->
         (file in FILE_C..FILE_F) && (rank in RANK_3..RANK_6)
     },
     otherPiecesCount = arrayOf(PieceKindCount('R'.p, 2)),
     otherPiecesGlobalConstraint = mapOf<PieceKind, Constraint>(),
     otherPiecesMutualConstraint = mapOf()
)

val KQvK_PositionGenerator = PositionGenerator(
        playerKingConstraint = ::NO_CONSTRAINT,
        oppositeKingConstraint = {(file, rank), position ->
            (file in FILE_C..FILE_F) && (rank in RANK_3..RANK_6)
        },
        otherPiecesCount = arrayOf(PieceKindCount('Q'.p, 1)),
        otherPiecesGlobalConstraint = mapOf<PieceKind, Constraint>(),
        otherPiecesMutualConstraint = mapOf()
)


val KBBvK_PositionGenerator = PositionGenerator(
        playerKingConstraint = ::NO_CONSTRAINT,
        oppositeKingConstraint = {(file, rank), position ->
            (file in FILE_C..FILE_F) && (rank in RANK_3..RANK_6)
        },
        otherPiecesCount = arrayOf(PieceKindCount('B'.p, 2)),
        otherPiecesGlobalConstraint = mapOf<PieceKind, Constraint>(),
        otherPiecesMutualConstraint = mapOf('B'.p to {first, second, position ->
            val firstSquareIsBlack = (first.file + first.rank) % 2 > 0
            val secondSquareIsBlack = (second.file + second.rank) % 2 > 0
            firstSquareIsBlack != secondSquareIsBlack
        })
)

