package com.loloof64.android.basicchessendgamestrainer.exercise_chooser

import com.loloof64.android.basicchessendgamestrainer.R
import com.loloof64.android.basicchessendgamestrainer.playing_activity.coordinatesToSquare
import karballo.Board
import karballo.Piece
import java.util.*
import java.util.logging.Logger

class PositionGenerationLoopException : Exception()

data class PieceKind(val pieceType: Int, val ofPlayerSide: Boolean)
data class PieceKindCount(val pieceKind: PieceKind, val count: Int)
data class BoardCoordinate(val file: Int, val rank: Int)

/*
 * Constraint based on the piece coordinate and general position consideration
 */
typealias KingConstraint = (coord: BoardCoordinate, playerHasWhite: Boolean, position: Board) -> Boolean

/*
 * Constraint based on the piece coordinate, both kings positions and general position consideration
 */
typealias OtherPieceConstraint = (coord: BoardCoordinate, playerHasWhite: Boolean, position: Board,
                                  playerKingCoord : BoardCoordinate, oppositeKingCoord: BoardCoordinate) -> Boolean

/**
 * Constraint based on the piece kind, its generation index (is it the first, the second, ... ?),
 * and general constraint consideration
 */
typealias IndexedConstraint = (pieceKind: PieceKind, pieceIndex: Int, coord: BoardCoordinate,
                               playerHasWhite: Boolean, position: Board) -> Boolean

/*
 * Constraint based on 2 pieces of the same kind (so taking into account the piece class and
 * the fact that they belong or not to player) and general position consideration
 */
typealias SameColorPiecesMutualConstraint = (firstBoardCoord: BoardCoordinate,
                                             secondBoardCoord: BoardCoordinate,
                                             playerHasWhite: Boolean,
                                             position: Board) -> Boolean
/**
 * Constraint between both kings
 */
typealias KingsMutualConstraint = (playerKingCoordinate: BoardCoordinate,
                                   oppositeKingCoordinate: BoardCoordinate,
                                   playerHasWhite: Boolean, position: Board) -> Boolean

fun NO_CONSTRAINT(coord: BoardCoordinate, playerHasWhite: Boolean, position: Board) = true

fun NO_KINGS_MUTUAL_CONSTRAINT(playerKingCoordinate: BoardCoordinate,
                               oppositeKingCoordinate: BoardCoordinate,
                               playerHasWhite: Boolean, position: Board) = true

val NO_OTHER_PIECES_GLOBAL_CONSTRAINT = mapOf<PieceKind, OtherPieceConstraint>()
val NO_OTHER_PIECES_MUTUAL_CONSTRAINT = mapOf<PieceKind, SameColorPiecesMutualConstraint>()
val NO_OTHER_PIECE_INDEXED_CONSTRAINT = mapOf<PieceKind, IndexedConstraint>()


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

private val maxLoopsIterations = 150

fun Int.loops(callback : (Int) -> Unit) {
    for (i in 0 until this) callback(i)
}

/**
 * Builds a PieceKind of player from 'P', 'N', 'B', 'R', 'Q' or 'K'.
 * Other character will throw exception !
 */
val Char.p: PieceKind
        get() = when (this){
            'P' -> PieceKind(pieceType = Piece.PAWN, ofPlayerSide = true)
            'N' -> PieceKind(pieceType = Piece.KNIGHT, ofPlayerSide = true)
            'B' -> PieceKind(pieceType = Piece.BISHOP, ofPlayerSide = true)
            'R' -> PieceKind(pieceType = Piece.ROOK, ofPlayerSide = true)
            'Q' -> PieceKind(pieceType = Piece.QUEEN, ofPlayerSide = true)
            'K' -> PieceKind(pieceType = Piece.KING, ofPlayerSide = true)
            else -> throw IllegalArgumentException("p() not usable for char $this.")
        }

/**
 * Builds a PieceKind of enemy from 'P', 'N', 'B', 'R', 'Q' or 'K'.
 * Other character will throw exception !
 */
val Char.e : PieceKind
        get() = when (this){
            'P' -> PieceKind(pieceType = Piece.PAWN, ofPlayerSide = false)
            'N' -> PieceKind(pieceType = Piece.KNIGHT, ofPlayerSide = false)
            'B' -> PieceKind(pieceType = Piece.BISHOP, ofPlayerSide = false)
            'R' -> PieceKind(pieceType = Piece.ROOK, ofPlayerSide = false)
            'Q' -> PieceKind(pieceType = Piece.QUEEN, ofPlayerSide = false)
            'K' -> PieceKind(pieceType = Piece.KING, ofPlayerSide = false)
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
        _position.fen = "8/8/8/8/8/8/8/8 ${if (playerHasWhite) 'w' else 'b'} - - 0 1"
        _position.moveNumber = 1

        try {
            placeKings(playerHasWhite)
            placeOtherPieces(playerHasWhite)
        } catch (e: PositionGenerationLoopException){
            return ""
        }

        Logger.getLogger("BasicChessEndgamesTrainer").info("Generated position is '${_position.fen}'")

        return _position.fen
    }

    private var playerKingCoords = BoardCoordinate(-1,-1)
    private var oppositeKingCoords = BoardCoordinate(-1,-1)

    private fun placeKings(playerHasWhite: Boolean){
        playerKingCoords = BoardCoordinate(-1,-1)
        oppositeKingCoords = BoardCoordinate(-1,-1)
        var loopSuccess = false
        for (iters in 0..maxLoopsIterations){ // setting up player king
            val kingFile = _random.nextInt(8)
            val kingRank = _random.nextInt(8)

            val tempPosition = Board()
            tempPosition.fen = _position.fen
            tempPosition.setPieceAt(coordinatesToSquare(kingFile, kingRank),
                    if (playerHasWhite) 'K' else 'k')
            if (playerKingConstraint(BoardCoordinate(kingFile, kingRank), playerHasWhite, tempPosition)) {
                _position.fen = tempPosition.fen
                playerKingCoords = BoardCoordinate(file = kingFile, rank = kingRank)
                loopSuccess = true
                break
            }
        }
        if (!loopSuccess) throw PositionGenerationLoopException()

        loopSuccess = false
        for (iters in 0..maxLoopsIterations){  // setting up enemy king
            val kingFile = _random.nextInt(8)
            val kingRank = _random.nextInt(8)

            val tempPosition = Board()
            tempPosition.fen = _position.fen
            val cellNotEmpty = tempPosition.getPieceAt(coordinatesToSquare(kingFile, kingRank)) != '.'
            if (cellNotEmpty) continue
            tempPosition.setPieceAt(coordinatesToSquare(kingFile, kingRank),
                    if (playerHasWhite) 'k' else 'K')
            // If the enemy king is threatened, will go into another try
            tempPosition.turn = !playerHasWhite
            val enemyKingInChess = tempPosition.check
            tempPosition.turn = playerHasWhite
            if (enemyKingInChess) continue

            if (oppositeKingConstraint(BoardCoordinate(kingFile, kingRank), playerHasWhite, tempPosition)
                    && kingsMutualConstraint(playerKingCoords, BoardCoordinate(file = kingFile, rank = kingRank),
                    playerHasWhite, tempPosition)) {
                oppositeKingCoords = BoardCoordinate(kingFile, kingRank)
                _position.fen = tempPosition.fen
                loopSuccess = true
                break
            }
        }
        if (!loopSuccess) throw PositionGenerationLoopException()
    }

    private fun placeOtherPieces(playerHasWhite: Boolean){
        otherPiecesCount.forEach { pieceKindCount ->
            val savedCoordinates = arrayListOf<BoardCoordinate>()
            val pieceKindConstraint = otherPiecesGlobalConstraint[pieceKindCount.pieceKind]
            val pieceKindMutualConstraint = otherPiecesMutualConstraint[pieceKindCount.pieceKind]
            pieceKindCount.count.loops { index ->
                var loopSuccess = false
                for (loopIter in 0..maxLoopsIterations) {
                    val pieceFile = _random.nextInt(8)
                    val pieceRank = _random.nextInt(8)
                    val currentCoordinate = BoardCoordinate(pieceFile, pieceRank)

                    val tempPosition = Board()
                    tempPosition.fen = _position.fen
                    val cellNotEmpty = tempPosition.getPieceAt(coordinatesToSquare(pieceFile, pieceRank)) != '.'
                    if (cellNotEmpty) continue
                    val isWhitePiece = (pieceKindCount.pieceKind.ofPlayerSide && playerHasWhite)
                    || (!pieceKindCount.pieceKind.ofPlayerSide && !playerHasWhite)
                    tempPosition.setPieceAt(coordinatesToSquare(pieceFile, pieceRank), pieceToStone(pieceKindCount.pieceKind, isWhitePiece))
                    // If the enemy king is threatened, will go into another try
                    tempPosition.turn = !playerHasWhite
                    val enemyKingInChess = tempPosition.check
                    tempPosition.turn = playerHasWhite
                    if (enemyKingInChess) continue

                    // If for any previous piece of same kind, mutual constraint is not respected, will go into another try
                    if (pieceKindMutualConstraint != null &&
                            savedCoordinates.any { !pieceKindMutualConstraint(it, currentCoordinate, playerHasWhite, tempPosition) }) continue

                    val pieceKindIndexConstraint = otherPiecesIndexedConstraint[pieceKindCount.pieceKind]
                    if (pieceKindIndexConstraint != null && !pieceKindIndexConstraint(pieceKindCount.pieceKind, index,
                            BoardCoordinate(pieceFile, pieceRank),
                            playerHasWhite, tempPosition)) continue

                    if (pieceKindConstraint == null || pieceKindConstraint(currentCoordinate, playerHasWhite, tempPosition, playerKingCoords, oppositeKingCoords)){
                        _position.fen = tempPosition.fen
                        savedCoordinates += BoardCoordinate(pieceFile, pieceRank)
                        loopSuccess = true
                        break
                    }
                }
                if (!loopSuccess) throw PositionGenerationLoopException()
            }
        }
    }

    private fun pieceToStone(pieceKind: PieceKind, whitePiece: Boolean): Char {
        return when(pieceKind.pieceType){
            Piece.PAWN -> if (whitePiece) 'P' else 'p'
            Piece.KNIGHT -> if (whitePiece) 'N' else 'n'
            Piece.BISHOP -> if (whitePiece) 'B' else 'b'
            Piece.ROOK -> if (whitePiece) 'R' else 'r'
            Piece.QUEEN -> if (whitePiece) 'Q' else 'q'
            Piece.KING -> if (whitePiece) 'K' else 'k'
            else -> '.'
        }
    }

    private val _position = Board()
    private val _random = Random()
}

val KRRvK_PositionGenerator = PositionGenerator(
     playerKingConstraint = ::NO_CONSTRAINT,
     oppositeKingConstraint = {(file, rank), _, position ->
         (file in FILE_C..FILE_F) && (rank in RANK_3..RANK_6)
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
            (file in FILE_C..FILE_F) && (rank in RANK_3..RANK_6)
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
            (file in FILE_C..FILE_F) && (rank in RANK_3..RANK_6)
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
            (file in FILE_C..FILE_F) && (rank in RANK_3..RANK_6)
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
            (rank == if (playerHasWhite) RANK_6 else RANK_3)
            && (file in FILE_B..FILE_G)
        },
        oppositeKingConstraint = {(_, rank), playerHasWhite, _ ->
            rank == if (playerHasWhite) RANK_8 else RANK_1
        },
        kingsMutualConstraint = {(playerKingFile, _), (oppositeKingFile, _), playerHasWhite, position ->
            playerKingFile == oppositeKingFile
        },
        otherPiecesCount = arrayOf(PieceKindCount('P'.p, 1)),
        otherPiecesGlobalConstraint = mapOf('P'.p to {(pieceFile, pieceRank), playerHasWhite, position, (playerKingFile, _), _ ->
            (pieceRank == if (playerHasWhite) RANK_5 else RANK_4)
            && (pieceFile == playerKingFile)
        }),
        otherPiecesMutualConstraint = NO_OTHER_PIECES_MUTUAL_CONSTRAINT,
        otherPiecesIndexedConstraint = NO_OTHER_PIECE_INDEXED_CONSTRAINT
)

val KPvK_II_PositionGenerator = PositionGenerator(
        playerKingConstraint = {(_, rank), playerHasWhite, _ ->
            rank == if (playerHasWhite) RANK_1 else RANK_8
        },
        oppositeKingConstraint = {(_, rank), playerHasWhite, _ ->
            rank == if (playerHasWhite) RANK_4 else RANK_5
        },
        kingsMutualConstraint = {(playerKingFile, _), (oppositeKingFile, _), playerHasWhite, position ->
            Math.abs(playerKingFile - oppositeKingFile) <= 1
        },
        otherPiecesCount = arrayOf(PieceKindCount('P'.e, 1)),
        otherPiecesGlobalConstraint = mapOf('P'.e to {(pieceFile, pieceRank), playerHasWhite, position, (playerKingFile, _), _ ->
            (pieceRank == if (playerHasWhite) RANK_5 else RANK_4)
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

