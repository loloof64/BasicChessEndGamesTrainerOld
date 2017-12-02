package com.loloof64.android.basicchessendgamestrainer.exercise_chooser

import com.github.bhlangonijr.chesslib.*
import java.util.*
import java.util.logging.Logger
import com.github.bhlangonijr.chesslib.Side as LibSide

class PositionGenerationLoopException : Exception()

private val maxLoopsIterations = 250

fun Int.loops(callback : (Int) -> Unit) {
    for (i in 0 until this) callback(i)
}

fun buildSquare(rank: Int, file: Int) =
        Square.encode(Rank.values()[rank], File.values()[file])

fun pieceKindToPiece(kind: PieceKind, whitePiece: Boolean): Piece =
        when(kind.pieceType){
            PieceType.pawn -> if (whitePiece) Piece.WHITE_PAWN else Piece.BLACK_PAWN
            PieceType.knight -> if (whitePiece) Piece.WHITE_KNIGHT else Piece.BLACK_KNIGHT
            PieceType.bishop -> if (whitePiece) Piece.WHITE_BISHOP else Piece.BLACK_BISHOP
            PieceType.rook -> if (whitePiece) Piece.WHITE_ROOK else Piece.BLACK_ROOK
            PieceType.queen -> if (whitePiece) Piece.WHITE_QUEEN else Piece.BLACK_QUEEN
            PieceType.king -> if (whitePiece) Piece.WHITE_KING else Piece.BLACK_KING
        }

class PositionGenerator(private val constraints : PositionConstraints) {

    private data class BoardCoordinate(val file: Int, val rank : Int){
        init {
            require(file in (0 until 8))
            require(rank in (0 until 8))
        }
    }

    private fun generateCell() = BoardCoordinate(
            file = _random.nextInt(8),
            rank = _random.nextInt(8)
    )

    private fun buildPositionOrNullIfCellAlreadyOccupied(startFen: String, pieceToAdd: Piece, pieceCell: Square): Board? {
        val builtPosition = Board()
        builtPosition.loadFromFEN(startFen)

        val wantedCellOccupied = builtPosition.getPiece(pieceCell) != Piece.NONE
        if (wantedCellOccupied) return null

        builtPosition.setPiece(pieceToAdd, pieceCell)
        return builtPosition
    }

    private fun enemyKingInChessFor(position: Board, playerHasWhite: Boolean) : Boolean =
        Board().apply {
            loadFromFEN(position.fen)
            sideToMove = if (playerHasWhite) LibSide.BLACK else LibSide.WHITE
        }.isKingAttacked

    fun generatePosition(playerHasWhite: Boolean = true): String {
        _position.loadFromFEN( "8/8/8/8/8/8/8/8 ${if (playerHasWhite) 'w' else 'b'} - - 0 1")
        _position.halfMoveCounter = 0
        _position.moveCounter = 1

        try {
            placeKings(playerHasWhite)
            placeOtherPieces(playerHasWhite)
        } catch (e: PositionGenerationLoopException){
            return ""
        }

        Logger.getLogger("BasicChessEndgamesTrainer").info("Generated position is '${_position.fen}'")

        return _position.fen
    }

    private var playerKingCell = BoardCoordinate(file = 0, rank = 0)
    private var oppositeKingCell = BoardCoordinate(file = 0, rank = 0)

    private fun placeKings(playerHasWhite: Boolean){
        var loopSuccess = false
        for (iters in 0..maxLoopsIterations){ // setting up player king

            val kingCell = generateCell()
            val tempPosition = buildPositionOrNullIfCellAlreadyOccupied(
                    startFen = _position.fen,
                    pieceToAdd = if (playerHasWhite) Piece.WHITE_KING else Piece.BLACK_KING,
                    pieceCell = buildSquare(rank = kingCell.rank, file = kingCell.file)
            )
            if (tempPosition == null) continue


            if (constraints.checkPlayerKingConstraint(
                    file = kingCell.file, rank = kingCell.rank,
                    playerHasWhite = playerHasWhite)) {
                _position.loadFromFEN(tempPosition.fen)
                playerKingCell = kingCell
                loopSuccess = true
                break
            }
        }
        if (!loopSuccess) throw PositionGenerationLoopException()

        loopSuccess = false
        for (iters in 0..maxLoopsIterations){  // setting up enemy king

            val kingCell = generateCell()

            val tempPosition = buildPositionOrNullIfCellAlreadyOccupied(
                    startFen = _position.fen,
                    pieceToAdd = if (playerHasWhite) Piece.BLACK_KING else Piece.WHITE_KING,
                    pieceCell = buildSquare(
                            rank = kingCell.rank, file = kingCell.file
                    )
            )

            if (tempPosition == null) continue
            if (enemyKingInChessFor(tempPosition, playerHasWhite)) continue

            // validate position if enemy king constraint and kings mutual constraint are respected
            if (constraints.checkComputerKingConstraint(file = kingCell.file, rank = kingCell.rank, playerHasWhite = playerHasWhite)
                    && constraints.checkKingsMutualConstraint(
                    playerKingFile = playerKingCell.file, playerKingRank = playerKingCell.rank,
                    computerKingFile = kingCell.file, computerKingRank = kingCell.rank,
                    playerHasWhite = playerHasWhite
            )) {
                oppositeKingCell = kingCell
                _position.loadFromFEN(tempPosition.fen)
                loopSuccess = true
                break
            }
        }
        if (!loopSuccess) throw PositionGenerationLoopException()
    }

    private fun placeOtherPieces(playerHasWhite: Boolean){
        constraints.otherPiecesCountsConstraint.forEach { (kind, count) ->

            val savedCoordinates = arrayListOf<BoardCoordinate>()
            count.loops { index ->
                var loopSuccess = false
                for (loopIter in 0..maxLoopsIterations) {
                    val isAPieceOfPlayer = kind.side == Side.player
                    val isWhitePiece = (isAPieceOfPlayer && playerHasWhite)
                            || (!isAPieceOfPlayer && !playerHasWhite)

                    val pieceCell = generateCell()
                    val tempPosition = buildPositionOrNullIfCellAlreadyOccupied(
                            startFen = _position.fen,
                            pieceToAdd = pieceKindToPiece(kind, isWhitePiece),
                            pieceCell = buildSquare(
                                    rank = pieceCell.rank, file = pieceCell.file
                            )
                    )
                    if (tempPosition == null) continue
                    if (enemyKingInChessFor(tempPosition, playerHasWhite)) continue

                    // If for any previous piece of same kind, mutual constraint is not respected, will loop another time
                    if (savedCoordinates.any { !constraints.checkOtherPieceMutualConstraint(
                            pieceKind = kind, firstPieceFile = it.file, firstPieceRank = it.rank,
                            secondPieceFile = pieceCell.file, secondPieceRank = pieceCell.rank,
                            playerHasWhite = playerHasWhite) }) continue

                    if (!constraints.checkOtherPieceIndexedConstraint(pieceKind = kind, apparitionIndex = index,
                            file = pieceCell.file, rank = pieceCell.rank,
                            playerHasWhite = playerHasWhite)) continue

                    if (constraints.checkOtherPieceGlobalConstraint(
                            pieceKind = kind,
                            file = pieceCell.file,
                            rank = pieceCell.rank,
                            playerHasWhite = playerHasWhite,
                            playerKingFile = playerKingCell.file, playerKingRank = playerKingCell.rank,
                            computerKingFile = oppositeKingCell.file, computerKingRank = playerKingCell.rank)){
                        _position.loadFromFEN(tempPosition.fen)
                        savedCoordinates += pieceCell
                        loopSuccess = true
                        break
                    }
                }
                if (!loopSuccess) throw PositionGenerationLoopException()
            }
        }
    }

    private val _position = Board()
    private val _random = Random()
}

