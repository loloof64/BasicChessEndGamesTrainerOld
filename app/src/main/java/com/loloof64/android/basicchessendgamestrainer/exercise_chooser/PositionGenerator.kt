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

package com.loloof64.android.basicchessendgamestrainer.exercise_chooser

import com.loloof64.android.basicchessendgamestrainer.utils.*
import java.util.*
import java.util.logging.Logger

class PositionGenerationLoopException(message: String = "") : Exception(message)

private const val maxLoopsIterations = 250
const val empty_board_fen = "8/8/8/8/8/8/8/8 w - - 0 1"

class PositionGenerator(private val constraints : PositionConstraints) {

    private fun generateCell() = ChessCell(
            file = _random.nextInt(8),
            rank = _random.nextInt(8)
    )

    private fun buildPositionOrNullIfCellAlreadyOccupiedOrResultingPositionIllegal(startFen: String, pieceToAdd: ChessPiece, pieceCell: ChessCell, checkIfResultPositionLegal: Boolean = true): IPosition? {
        val builtPosition : IPosition = ICTKChessLib.buildPositionFromString(startFen)

        val wantedCellOccupied = builtPosition.getPieceAtCell(pieceCell) != null
        if (wantedCellOccupied) return null

        builtPosition.modifyPositionByAddingPieceAt(piece = pieceToAdd, cell = pieceCell)
        if (checkIfResultPositionLegal && !ICTKChessLib.isLegalPositionString(builtPosition.toFEN())) return null
        return builtPosition
    }

    fun generatePosition(playerHasWhite: Boolean = true): String {
        _position = ICTKChessLib.buildPositionFromString(positionFEN = "8/8/8/8/8/8/8/8 ${if (playerHasWhite) 'w' else 'b'} - - 0 1")

        placeKings(playerHasWhite)
        placeOtherPieces(playerHasWhite)

        Logger.getLogger("BasicChessEndgamesTrainer").info("Generated position is '${_position.toFEN()}'")

        return _position.toFEN()
    }

    private var playerKingCell = ChessCell(file = 0, rank = 0)
    private var oppositeKingCell = ChessCell(file = 0, rank = 0)

    private fun placeKings(playerHasWhite: Boolean){
        var loopSuccess = false
        for (iters in 0..maxLoopsIterations){ // setting up player king

            val kingCell = generateCell()
            val tempPosition = buildPositionOrNullIfCellAlreadyOccupiedOrResultingPositionIllegal(
                    startFen = _position.toFEN(),
                    pieceToAdd = if (playerHasWhite) ChessPiece(ChessPieceType.King, whiteOwner = true) else ChessPiece(ChessPieceType.King, whiteOwner = false),
                    pieceCell = kingCell,
                    checkIfResultPositionLegal = false
            ) ?: continue

            val playerKingConstraintRespected = constraints.checkPlayerKingConstraint(
                    file = kingCell.file, rank = kingCell.rank,
                    playerHasWhite = playerHasWhite)
            if (playerKingConstraintRespected) {
                _position = ICTKChessLib.buildPositionFromString(tempPosition.toFEN(), checkIfResultPositionLegal = false)
                playerKingCell = kingCell
                loopSuccess = true
                break
            }
        }
        if (!loopSuccess) throw PositionGenerationLoopException()

        loopSuccess = false
        for (iters in 0..maxLoopsIterations){  // setting up enemy king

            val kingCell = generateCell()

            val tempPosition = buildPositionOrNullIfCellAlreadyOccupiedOrResultingPositionIllegal(
                    startFen = _position.toFEN(),
                    pieceToAdd = if (playerHasWhite) ChessPiece(ChessPieceType.King, whiteOwner = false) else ChessPiece(ChessPieceType.King, whiteOwner = true),
                    pieceCell = kingCell,
                    checkIfResultPositionLegal = true
            ) ?: continue

            if (!ICTKChessLib.isLegalPositionString(tempPosition.toFEN())) continue

            // validate position if enemy king constraint and kings mutual constraint are respected
            val computerKingConstraintRespected = constraints.checkComputerKingConstraint(file = kingCell.file, rank = kingCell.rank, playerHasWhite = playerHasWhite)
                    && constraints.checkKingsMutualConstraint(
                    playerKingFile = playerKingCell.file, playerKingRank = playerKingCell.rank,
                    computerKingFile = kingCell.file, computerKingRank = kingCell.rank,
                    playerHasWhite = playerHasWhite
            )

            if (computerKingConstraintRespected) {
                oppositeKingCell = kingCell
                _position = ICTKChessLib.buildPositionFromString(tempPosition.toFEN())
                loopSuccess = true
                break
            }
        }
        if (!loopSuccess) throw PositionGenerationLoopException()
    }

    private fun placeOtherPieces(playerHasWhite: Boolean){

        fun Int.loops(callback : (Int) -> Unit) {
            for (i in 0 until this) callback(i)
        }

        fun pieceKindToChessPiece(kind: PieceKind, whitePiece: Boolean): ChessPiece =
                when(kind.pieceType){
                    PieceType.pawn -> if (whitePiece) ChessPiece(ChessPieceType.Pawn, whiteOwner = true) else ChessPiece(ChessPieceType.Pawn, whiteOwner = false)
                    PieceType.knight -> if (whitePiece) ChessPiece(ChessPieceType.Knight, whiteOwner = true) else ChessPiece(ChessPieceType.Knight, whiteOwner = false)
                    PieceType.bishop -> if (whitePiece) ChessPiece(ChessPieceType.Bishop, whiteOwner = true) else ChessPiece(ChessPieceType.Bishop, whiteOwner = false)
                    PieceType.rook -> if (whitePiece) ChessPiece(ChessPieceType.Rook, whiteOwner = true) else ChessPiece(ChessPieceType.Rook, whiteOwner = false)
                    PieceType.queen -> if (whitePiece) ChessPiece(ChessPieceType.Queen, whiteOwner = true) else ChessPiece(ChessPieceType.Queen, whiteOwner = false)
                    PieceType.king -> if (whitePiece) ChessPiece(ChessPieceType.King, whiteOwner = true) else ChessPiece(ChessPieceType.King, whiteOwner = false)
                }

        constraints.otherPiecesCountsConstraint.forEach { (kind, count) ->

            val savedCoordinates = arrayListOf<ChessCell>()
            count.loops { index ->
                var loopSuccess = false
                for (loopIter in 0..maxLoopsIterations) {
                    val isAPieceOfPlayer = kind.side == Side.player
                    val isWhitePiece = (isAPieceOfPlayer && playerHasWhite)
                            || (!isAPieceOfPlayer && !playerHasWhite)

                    val pieceCell = generateCell()
                    val tempPosition = buildPositionOrNullIfCellAlreadyOccupiedOrResultingPositionIllegal(
                            startFen = _position.toFEN(),
                            pieceToAdd = pieceKindToChessPiece(kind, isWhitePiece),
                            pieceCell = pieceCell
                    ) ?: continue

                    if (!ICTKChessLib.isLegalPositionString(tempPosition.toFEN())) continue

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
                        _position = ICTKChessLib.buildPositionFromString(tempPosition.toFEN())
                        savedCoordinates += pieceCell
                        loopSuccess = true
                        break
                    }
                }
                if (!loopSuccess) throw PositionGenerationLoopException()
            }
        }
    }

    private var _position:IPosition = ICTKChessLib.buildPositionFromString(empty_board_fen, checkIfResultPositionLegal = false)
    private val _random = Random()
}

