package com.loloof64.android.basicchessendgamestrainer.exercise_chooser

import com.loloof64.android.basicchessendgamestrainer.R
import com.loloof64.android.basicchessendgamestrainer.chess_abstraction.*
import com.loloof64.android.basicchessendgamestrainer.karballo_chess_implementation.Position
import java.util.*
import java.util.logging.Logger

class PositionGenerationLoopException : Exception()

data class PieceKind(val pieceType: PieceType, val ofPlayerSide: Boolean)
data class PieceKindCount(val pieceKind: PieceKind, val count: Int)

/*
 * Constraint based on the piece coordinate and general position consideration
 */
typealias KingConstraint = (coord: Square, playerHasWhite: Boolean, position: IPosition) -> Boolean

/*
 * Constraint based on the piece coordinate, both kings positions and general position consideration
 */
typealias OtherPieceConstraint = (coord: Square, playerHasWhite: Boolean, position: IPosition,
                                  playerKingCoord : Square, oppositeKingCoord: Square) -> Boolean

/**
 * Constraint based on the piece kind, its generation index (is it the first, the second, ... ?),
 * and general constraint consideration
 */
typealias IndexedConstraint = (pieceKind: PieceKind, pieceIndex: Int, coord: Square,
                               playerHasWhite: Boolean, position: IPosition) -> Boolean

/*
 * Constraint based on 2 pieces of the same kind (so taking into account the piece class and
 * the fact that they belong or not to player) and general position consideration
 */
typealias SameColorPiecesMutualConstraint = (firstBoardCoord: Square,
                                             secondBoardCoord: Square,
                                             playerHasWhite: Boolean,
                                             position: IPosition) -> Boolean
/**
 * Constraint between both kings
 */
typealias KingsMutualConstraint = (playerKingCoordinate: Square,
                                   oppositeKingCoordinate: Square,
                                   playerHasWhite: Boolean, position: IPosition) -> Boolean

fun NO_CONSTRAINT(coord: Square, playerHasWhite: Boolean, position: IPosition) = true

fun NO_KINGS_MUTUAL_CONSTRAINT(playerKingCoordinate: Square,
                               oppositeKingCoordinate: Square,
                               playerHasWhite: Boolean, position: IPosition) = true

val NO_OTHER_PIECES_GLOBAL_CONSTRAINT = mapOf<PieceKind, OtherPieceConstraint>()
val NO_OTHER_PIECES_MUTUAL_CONSTRAINT = mapOf<PieceKind, SameColorPiecesMutualConstraint>()
val NO_OTHER_PIECE_INDEXED_CONSTRAINT = mapOf<PieceKind, IndexedConstraint>()

private val maxLoopsIterations = 250

fun Int.loops(callback : (Int) -> Unit) {
    for (i in 0 until this) callback(i)
}

/**
 * Builds a PieceKind of player from 'P', 'N', 'B', 'R', 'Q' or 'K'.
 * Other character will throw exception !
 */
val Char.p: PieceKind
        get() = when (this){
            'P' -> PieceKind(pieceType = PieceType.PAWN, ofPlayerSide = true)
            'N' -> PieceKind(pieceType = PieceType.KNIGHT, ofPlayerSide = true)
            'B' -> PieceKind(pieceType = PieceType.BISHOP, ofPlayerSide = true)
            'R' -> PieceKind(pieceType = PieceType.ROOK, ofPlayerSide = true)
            'Q' -> PieceKind(pieceType = PieceType.QUEEN, ofPlayerSide = true)
            'K' -> PieceKind(pieceType = PieceType.KING, ofPlayerSide = true)
            else -> throw IllegalArgumentException("p() not usable for char $this.")
        }

/**
 * Builds a PieceKind of enemy from 'P', 'N', 'B', 'R', 'Q' or 'K'.
 * Other character will throw exception !
 */
val Char.e : PieceKind
        get() = when (this){
            'P' -> PieceKind(pieceType = PieceType.PAWN, ofPlayerSide = false)
            'N' -> PieceKind(pieceType = PieceType.KNIGHT, ofPlayerSide = false)
            'B' -> PieceKind(pieceType = PieceType.BISHOP, ofPlayerSide = false)
            'R' -> PieceKind(pieceType = PieceType.ROOK, ofPlayerSide = false)
            'Q' -> PieceKind(pieceType = PieceType.QUEEN, ofPlayerSide = false)
            'K' -> PieceKind(pieceType = PieceType.KING, ofPlayerSide = false)
            else -> throw IllegalArgumentException("e() not usable for char $this.")
        }

class PositionGenerator(val playerKingConstraint: KingConstraint,
                        val oppositeKingConstraint: KingConstraint,
                        val kingsMutualConstraint: KingsMutualConstraint,
                        val otherPiecesCount: Array<PieceKindCount>,
                        val otherPiecesGlobalConstraint: Map<PieceKind, OtherPieceConstraint>,
                        val otherPiecesMutualConstraint: Map<PieceKind, SameColorPiecesMutualConstraint>,
                        val otherPiecesIndexedConstraint: Map<PieceKind, IndexedConstraint>) {

    fun generatePosition(playerHasWhite: Boolean = true): String {
        _position = Position("8/8/8/8/8/8/8/8 ${if (playerHasWhite) 'w' else 'b'} - - 0 1")

        try {
            placeKings(playerHasWhite)
            placeOtherPieces(playerHasWhite)
        } catch (e: PositionGenerationLoopException){
            return ""
        }

        Logger.getLogger("BasicChessEndgamesTrainer").info("Generated position is '${_position.toFen()}'")

        return _position.toFen()
    }

    private var playerKingCoords: Square? = null
    private var oppositeKingCoords: Square? = null

    private fun placeKings(playerHasWhite: Boolean){
        playerKingCoords = null
        oppositeKingCoords = null
        var loopSuccess = false
        for (iters in 0..maxLoopsIterations){ // setting up player king
            val kingFile = _random.nextInt(8)
            val kingRank = _random.nextInt(8)

            val tempPosition = Position(_position.toFen())
            tempPosition.setPieceAtSquare(Square(kingFile, kingRank),
                    if (playerHasWhite) Piece.WHITE_KING else Piece.BLACK_KING)
            if (playerKingConstraint(Square(kingFile, kingRank), playerHasWhite, tempPosition)) {
                _position = Position(tempPosition.toFen())
                playerKingCoords = Square(kingFile, kingRank)
                loopSuccess = true
                break
            }
        }
        if (!loopSuccess) throw PositionGenerationLoopException()

        loopSuccess = false
        for (iters in 0..maxLoopsIterations){  // setting up enemy king
            val kingFile = _random.nextInt(8)
            val kingRank = _random.nextInt(8)

            val tempPosition = Position(_position.toFen())
            val cellNotEmpty = tempPosition.getPieceAtSquare(Square(kingFile, kingRank)) != null
            if (cellNotEmpty) continue
            tempPosition.setPieceAtSquare(Square(kingFile, kingRank),
                    if (playerHasWhite) Piece.BLACK_KING else Piece.WHITE_KING)

            val enemyKingInChess = tempPosition.kingInChess(if (playerHasWhite) Color.BLACK else Color.WHITE)
            if (enemyKingInChess) continue

            if (oppositeKingConstraint(Square(kingFile, kingRank), playerHasWhite, tempPosition)
                    && kingsMutualConstraint(playerKingCoords!!, Square(kingFile, kingRank),
                    playerHasWhite, tempPosition)) {
                oppositeKingCoords = Square(kingFile, kingRank)
                _position = Position(tempPosition.toFen())
                loopSuccess = true
                break
            }
        }
        if (!loopSuccess) throw PositionGenerationLoopException()
    }

    private fun placeOtherPieces(playerHasWhite: Boolean){
        otherPiecesCount.forEach { pieceKindCount ->
            val savedCoordinates = arrayListOf<Square>()
            val pieceKindConstraint = otherPiecesGlobalConstraint[pieceKindCount.pieceKind]
            val pieceKindMutualConstraint = otherPiecesMutualConstraint[pieceKindCount.pieceKind]
            pieceKindCount.count.loops { index ->
                var loopSuccess = false
                for (loopIter in 0..maxLoopsIterations) {
                    val pieceFile = _random.nextInt(8)
                    val pieceRank = _random.nextInt(8)
                    val currentCoordinate = Square(pieceFile, pieceRank)

                    val tempPosition = Position(_position.toFen())
                    val cellNotEmpty = tempPosition.getPieceAtSquare(Square(pieceFile, pieceRank)) != null
                    if (cellNotEmpty) continue
                    val isWhitePiece = (pieceKindCount.pieceKind.ofPlayerSide && playerHasWhite)
                    || (!pieceKindCount.pieceKind.ofPlayerSide && !playerHasWhite)
                    tempPosition.setPieceAtSquare(Square(pieceFile, pieceRank), Piece(pieceType = pieceKindCount.pieceKind.pieceType, color = if (isWhitePiece) Color.WHITE else Color.BLACK))
                    
                    val enemyKingInChess = tempPosition.kingInChess(if (playerHasWhite) Color.BLACK else Color.WHITE)
                    if (enemyKingInChess) continue

                    // If for any previous piece of same kind, mutual constraint is not respected, will go into another try
                    if (pieceKindMutualConstraint != null &&
                            savedCoordinates.any { !pieceKindMutualConstraint(it, currentCoordinate, playerHasWhite, tempPosition) }) continue

                    val pieceKindIndexConstraint = otherPiecesIndexedConstraint[pieceKindCount.pieceKind]
                    if (pieceKindIndexConstraint != null && !pieceKindIndexConstraint(pieceKindCount.pieceKind, index,
                            Square(pieceFile, pieceRank),
                            playerHasWhite, tempPosition)) continue

                    if (pieceKindConstraint == null || pieceKindConstraint(currentCoordinate, playerHasWhite, tempPosition, playerKingCoords!!, oppositeKingCoords!!)){
                        _position = Position(tempPosition.toFen())
                        savedCoordinates += Square(pieceFile, pieceRank)
                        loopSuccess = true
                        break
                    }
                }
                if (!loopSuccess) throw PositionGenerationLoopException()
            }
        }
    }

    private var _position : IPosition = Position()
    private val _random = Random()
}

val KRRvK_PositionGenerator = PositionGenerator(
     playerKingConstraint = ::NO_CONSTRAINT,
     oppositeKingConstraint = {(file, rank), _, position ->
         (file in Square.FILE_C..Square.FILE_F) && (rank in Square.RANK_3..Square.RANK_6)
     },
     kingsMutualConstraint = ::NO_KINGS_MUTUAL_CONSTRAINT,
     otherPiecesCount = arrayOf(PieceKindCount('R'.p, 2)),
     otherPiecesGlobalConstraint = NO_OTHER_PIECES_GLOBAL_CONSTRAINT,
     otherPiecesMutualConstraint = NO_OTHER_PIECES_MUTUAL_CONSTRAINT,
     otherPiecesIndexedConstraint = NO_OTHER_PIECE_INDEXED_CONSTRAINT
)

val KQvK_PositionGenerator = PositionGenerator(
        playerKingConstraint = ::NO_CONSTRAINT,
        oppositeKingConstraint = {(file, rank), _, position ->
            (file in Square.FILE_C..Square.FILE_F) && (rank in Square.RANK_3..Square.RANK_6)
        },
        kingsMutualConstraint = ::NO_KINGS_MUTUAL_CONSTRAINT,
        otherPiecesCount = arrayOf(PieceKindCount('Q'.p, 1)),
        otherPiecesGlobalConstraint = NO_OTHER_PIECES_GLOBAL_CONSTRAINT,
        otherPiecesMutualConstraint = NO_OTHER_PIECES_MUTUAL_CONSTRAINT,
        otherPiecesIndexedConstraint = NO_OTHER_PIECE_INDEXED_CONSTRAINT
)

val KRvK_PositionGenerator = PositionGenerator(
        playerKingConstraint = ::NO_CONSTRAINT,
        oppositeKingConstraint = {(file, rank), _, position ->
            (file in Square.FILE_C..Square.FILE_F) && (rank in Square.RANK_3..Square.RANK_6)
        },
        kingsMutualConstraint = ::NO_KINGS_MUTUAL_CONSTRAINT,
        otherPiecesCount = arrayOf(PieceKindCount('R'.p, 1)),
        otherPiecesGlobalConstraint = NO_OTHER_PIECES_GLOBAL_CONSTRAINT,
        otherPiecesMutualConstraint = NO_OTHER_PIECES_MUTUAL_CONSTRAINT,
        otherPiecesIndexedConstraint = NO_OTHER_PIECE_INDEXED_CONSTRAINT
)


val KBBvK_PositionGenerator = PositionGenerator(
        playerKingConstraint = ::NO_CONSTRAINT,
        oppositeKingConstraint = {(file, rank), _, position ->
            (file in Square.FILE_C..Square.FILE_F) && (rank in Square.RANK_3..Square.RANK_6)
        },
        kingsMutualConstraint = ::NO_KINGS_MUTUAL_CONSTRAINT,
        otherPiecesCount = arrayOf(PieceKindCount('B'.p, 2)),
        otherPiecesGlobalConstraint = NO_OTHER_PIECES_GLOBAL_CONSTRAINT,
        otherPiecesMutualConstraint = mapOf('B'.p to {first, second, _, position ->
            val firstSquareIsBlack = (first.file + first.rank) % 2 > 0
            val secondSquareIsBlack = (second.file + second.rank) % 2 > 0
            firstSquareIsBlack != secondSquareIsBlack
        }),
        otherPiecesIndexedConstraint = NO_OTHER_PIECE_INDEXED_CONSTRAINT
)

val KPvK_I_PositionGenerator = PositionGenerator(
        playerKingConstraint = {(file, rank), playerHasWhite, _ ->
            (rank == if (playerHasWhite) Square.RANK_6 else Square.RANK_3)
            && (file in Square.FILE_B..Square.FILE_G)
        },
        oppositeKingConstraint = {(_, rank), playerHasWhite, _ ->
            rank == if (playerHasWhite) Square.RANK_8 else Square.RANK_1
        },
        kingsMutualConstraint = {(playerKingFile, _), (oppositeKingFile, _), playerHasWhite, position ->
            playerKingFile == oppositeKingFile
        },
        otherPiecesCount = arrayOf(PieceKindCount('P'.p, 1)),
        otherPiecesGlobalConstraint = mapOf('P'.p to {(pieceFile, pieceRank), playerHasWhite, position, (playerKingFile, _), _ ->
            (pieceRank == if (playerHasWhite) Square.RANK_5 else Square.RANK_4)
            && (pieceFile == playerKingFile)
        }),
        otherPiecesMutualConstraint = NO_OTHER_PIECES_MUTUAL_CONSTRAINT,
        otherPiecesIndexedConstraint = NO_OTHER_PIECE_INDEXED_CONSTRAINT
)

val KPvK_II_PositionGenerator = PositionGenerator(
        playerKingConstraint = {(_, rank), playerHasWhite, _ ->
            rank == if (playerHasWhite) Square.RANK_1 else Square.RANK_8
        },
        oppositeKingConstraint = {(_, rank), playerHasWhite, _ ->
            rank == if (playerHasWhite) Square.RANK_4 else Square.RANK_5
        },
        kingsMutualConstraint = {(playerKingFile, _), (oppositeKingFile, _), playerHasWhite, position ->
            Math.abs(playerKingFile - oppositeKingFile) <= 1
        },
        otherPiecesCount = arrayOf(PieceKindCount('P'.e, 1)),
        otherPiecesGlobalConstraint = mapOf('P'.e to {(pieceFile, pieceRank), playerHasWhite, position, (playerKingFile, _), _ ->
            (pieceRank == if (playerHasWhite) Square.RANK_5 else Square.RANK_4)
            && (pieceFile == playerKingFile)
        }),
        otherPiecesMutualConstraint = NO_OTHER_PIECES_MUTUAL_CONSTRAINT,
        otherPiecesIndexedConstraint = NO_OTHER_PIECE_INDEXED_CONSTRAINT
)

val availableGenerators = arrayOf(
    R.string.exercise_krr_k to KRRvK_PositionGenerator,
    R.string.exercise_kq_k to KQvK_PositionGenerator,
    R.string.exercise_kr_k to KRvK_PositionGenerator,
    R.string.exercise_kbb_k to KBBvK_PositionGenerator,
    R.string.exercise_kp_k_I to KPvK_I_PositionGenerator,
    R.string.exercise_kp_k_II to KPvK_II_PositionGenerator
)

