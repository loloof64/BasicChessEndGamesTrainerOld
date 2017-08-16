package com.loloof64.android.basicchessendgamestrainer.exercise_chooser

import com.loloof64.android.basicchessendgamestrainer.playing_activity.coordinatesToSquare
import karballo.Board
import java.util.*
import java.util.logging.Logger

class PositionGenerationLoopException : Exception()

private val maxLoopsIterations = 250

fun Int.loops(callback : (Int) -> Unit) {
    for (i in 0 until this) callback(i)
}

class PositionGenerator(private val constraints : PositionConstraints) {

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
            if (constraints.checkPlayerKingConstraint(BoardCoordinate(kingFile, kingRank), playerHasWhite)) {
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

            tempPosition.turn = !playerHasWhite
            tempPosition.setCheckFlags()
            val enemyKingInChess = tempPosition.check
            tempPosition.turn = playerHasWhite
            if (enemyKingInChess) continue

            if (constraints.checkComputerKingConstraint(BoardCoordinate(kingFile, kingRank), playerHasWhite)
                    && constraints.checkKingsMutualConstraint(
                    playerKingCoords,
                    BoardCoordinate(file = kingFile, rank = kingRank),
                    playerHasWhite
            )) {
                oppositeKingCoords = BoardCoordinate(kingFile, kingRank)
                _position.fen = tempPosition.fen
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
                    val pieceFile = _random.nextInt(8)
                    val pieceRank = _random.nextInt(8)
                    val currentCoordinate = BoardCoordinate(pieceFile, pieceRank)

                    val tempPosition = Board()
                    tempPosition.fen = _position.fen
                    val cellNotEmpty = tempPosition.getPieceAt(coordinatesToSquare(pieceFile, pieceRank)) != '.'
                    if (cellNotEmpty) continue
                    val isAPieceOfPlayer = kind.side == Side.player
                    val isWhitePiece = (isAPieceOfPlayer && playerHasWhite)
                        || (!isAPieceOfPlayer && !playerHasWhite)
                    tempPosition.setPieceAt(coordinatesToSquare(pieceFile, pieceRank), pieceToStone(kind, isWhitePiece))
                    
                    tempPosition.turn = !playerHasWhite
                    tempPosition.setCheckFlags()
                    val enemyKingInChess = tempPosition.check
                    tempPosition.turn = playerHasWhite
                    if (enemyKingInChess) continue

                    // If for any previous piece of same kind, mutual constraint is not respected, will go into another try
                    if (savedCoordinates.any { !constraints.checkOtherPieceMutualConstraint(kind, it, currentCoordinate, playerHasWhite) }) continue

                    if (!constraints.checkOtherPieceIndexedConstraint(kind, index,
                            BoardCoordinate(pieceFile, pieceRank),
                            playerHasWhite)) continue

                    if (constraints.checkOtherPieceGlobalConstraint(kind, currentCoordinate, playerHasWhite, playerKingCoords, oppositeKingCoords)){
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
            PieceType.pawn -> if (whitePiece) 'P' else 'p'
            PieceType.knight -> if (whitePiece) 'N' else 'n'
            PieceType.bishop -> if (whitePiece) 'B' else 'b'
            PieceType.rook -> if (whitePiece) 'R' else 'r'
            PieceType.queen -> if (whitePiece) 'Q' else 'q'
            PieceType.king -> if (whitePiece) 'K' else 'k'
        }
    }

    private val _position = Board()
    private val _random = Random()
}

