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

    private var playerKingFile = -1
    private var playerKingRank = -1
    private var oppositeKingFile = -1
    private var oppositeKingRank = -1

    private fun placeKings(playerHasWhite: Boolean){
        playerKingFile = -1
        playerKingRank = -1
        oppositeKingFile = -1
        oppositeKingRank = -1
        var loopSuccess = false
        for (iters in 0..maxLoopsIterations){ // setting up player king
            val kingFile = _random.nextInt(8)
            val kingRank = _random.nextInt(8)

            val tempPosition = Board()
            tempPosition.loadFromFEN(_position.fen)
            tempPosition.setPiece(if (playerHasWhite) Piece.WHITE_KING else Piece.BLACK_KING,
                    Square.encode(Rank.values()[kingRank], File.values()[kingFile]))

            if (constraints.checkPlayerKingConstraint(kingFile, kingRank, playerHasWhite)) {
                _position.loadFromFEN(tempPosition.fen)
                playerKingFile = kingFile
                playerKingRank = kingRank
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
            tempPosition.loadFromFEN(_position.fen)
            val cellNotEmpty = tempPosition.getPiece(buildSquare(kingRank, kingFile)) != Piece.NONE
            if (cellNotEmpty) continue
            tempPosition.setPiece(if (playerHasWhite) Piece.BLACK_KING else Piece.WHITE_KING, buildSquare(kingRank, kingFile))

            tempPosition.sideToMove = if (playerHasWhite) LibSide.BLACK else LibSide.WHITE
            val enemyKingInChess = tempPosition.isKingAttacked()
            tempPosition.sideToMove = if (playerHasWhite) LibSide.WHITE else LibSide.BLACK
            if (enemyKingInChess) continue

            if (constraints.checkComputerKingConstraint(kingFile, kingRank, playerHasWhite)
                    && constraints.checkKingsMutualConstraint(
                    playerKingFile, playerKingRank,
                    kingFile, kingRank,
                    playerHasWhite
            )) {
                oppositeKingFile = kingFile
                oppositeKingRank = kingRank
                _position.loadFromFEN(tempPosition.fen)
                loopSuccess = true
                break
            }
        }
        if (!loopSuccess) throw PositionGenerationLoopException()
    }

    private fun placeOtherPieces(playerHasWhite: Boolean){
        constraints.otherPiecesCountsConstraint.forEach { (kind, count) ->

            val savedCoordinates = arrayListOf<Pair<Int, Int>>()
            count.loops { index ->
                var loopSuccess = false
                for (loopIter in 0..maxLoopsIterations) {
                    val pieceFile = _random.nextInt(8)
                    val pieceRank = _random.nextInt(8)
                    val currentPieceCoordinate = pieceFile to pieceRank

                    val tempPosition = Board()
                    tempPosition.loadFromFEN(_position.fen)
                    val cellNotEmpty = tempPosition.getPiece(buildSquare(pieceRank, pieceFile)) != Piece.NONE
                    if (cellNotEmpty) continue
                    val isAPieceOfPlayer = kind.side == Side.player
                    val isWhitePiece = (isAPieceOfPlayer && playerHasWhite)
                        || (!isAPieceOfPlayer && !playerHasWhite)
                    tempPosition.setPiece(pieceKindToPiece(kind, isWhitePiece), buildSquare(pieceRank, pieceFile))
                    
                    tempPosition.sideToMove = if (playerHasWhite) LibSide.BLACK else LibSide.WHITE
                    val enemyKingInChess = tempPosition.isKingAttacked()
                    tempPosition.sideToMove = if (playerHasWhite) LibSide.WHITE else LibSide.BLACK
                    if (enemyKingInChess) continue

                    // If for any previous piece of same kind, mutual constraint is not respected, will go into another try
                    if (savedCoordinates.any { !constraints.checkOtherPieceMutualConstraint(kind, it.first, it.second,
                            currentPieceCoordinate.first, currentPieceCoordinate.second, playerHasWhite) }) continue

                    if (!constraints.checkOtherPieceIndexedConstraint(kind, index,
                            pieceFile, pieceRank,
                            playerHasWhite)) continue

                    if (constraints.checkOtherPieceGlobalConstraint(kind, currentPieceCoordinate.first,
                            currentPieceCoordinate.second, playerHasWhite, playerKingFile, playerKingRank,
                            oppositeKingFile, oppositeKingRank)){
                        _position.loadFromFEN(tempPosition.fen)
                        savedCoordinates += pieceFile to pieceRank
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

