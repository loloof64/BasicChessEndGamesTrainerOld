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

import com.loloof64.android.basicchessendgamestrainer.position_generator_editor.PieceKindCount as EditorPieceKindCount
import com.loloof64.android.basicchessendgamestrainer.position_generator_editor.PieceKind as EditorPieceKind
import com.loloof64.android.basicchessendgamestrainer.position_generator_editor.PieceType as EditorPieceType
import com.loloof64.android.basicchessendgamestrainer.position_generator_editor.Side as EditorSide
import com.loloof64.android.basicchessendgamestrainer.position_generator_editor.script_language.ScriptLanguageBooleanExpr
import com.loloof64.android.basicchessendgamestrainer.position_generator_editor.script_language.eval
import com.loloof64.android.basicchessendgamestrainer.utils.*
import java.util.*

class PositionGeneratorConstraintsExpr(
        val playerKingConstraint: ScriptLanguageBooleanExpr?,
        val computerKingConstraint: ScriptLanguageBooleanExpr?,
        val kingsMutualConstraint: ScriptLanguageBooleanExpr?,
        val otherPiecesCountConstraint: List<EditorPieceKindCount>
)

data class PositionGeneratorConstraintsScripts(
        val resultShouldBeDraw: Boolean,
        val playerKingConstraint: String,
        val computerKingConstraint: String,
        val kingsMutualConstraint: String,
        val otherPiecesCountConstraint: String
)

object PositionGeneratorFromANTLR {

    const val DEFAULT_POSITION = "k1K5/8/8/8/8/8/8/8 w - - 0 1"

    private data class BoardCoordinate(val file: Int, val rank : Int){
        init {
            require(file in (0 until 8))
            require(rank in (0 until 8))
        }
    }

    private fun generateCell() = BoardCoordinate(
            file = random.nextInt(8),
            rank = random.nextInt(8)
    )

    private fun coordinatesToChessCell(coord: BoardCoordinate) : ChessCell {
        return ChessCell(file = coord.file, rank = coord.rank)
    }

    private const val maxLoopsIterations = 300

    private val NO_CONSTRAINT = PositionGeneratorConstraintsExpr(
            playerKingConstraint = null,
            computerKingConstraint = null,
            kingsMutualConstraint = null,
            otherPiecesCountConstraint = listOf()
    )

    private val random = Random()

    private var allConstraints: PositionGeneratorConstraintsExpr = NO_CONSTRAINT

    private var playerKingCell = BoardCoordinate(file = 0, rank = 0)
    private var computerKingCell = BoardCoordinate(file = 0, rank = 0)

    fun setConstraints(constraints: PositionGeneratorConstraintsExpr) {
        allConstraints = constraints
    }

    fun generatePosition(): String {
        playerKingCell = BoardCoordinate(file = 0, rank = 0)
        computerKingCell = BoardCoordinate(file = 0, rank = 0)

        val playerHasWhite = random.nextBoolean()
        val startFen = "8/8/8/8/8/8/8/8 ${if (playerHasWhite) 'w' else 'b'} - - 0 1"

        val positionWithPlayerKing = placePlayerKingInPosition(startFen = startFen, playerHasWhite = playerHasWhite)
        val positionWithBothKings = placeComputerKingInPosition(startFen = positionWithPlayerKing, playerHasWhite = playerHasWhite)
        return placeOtherPiecesInPosition(startFen = positionWithBothKings, playerHasWhite = playerHasWhite)
    }

    private fun addPieceToPositionOrReturnNullIfCellAlreadyOccupied(startFen: String, pieceToAdd: ChessPiece, pieceCell: ChessCell): String? {
        val builtPosition = ICTKChessLib.buildPositionFromString(startFen)

        val wantedCellOccupied = builtPosition.getPieceAtCell(pieceCell) != null
        if (wantedCellOccupied) return null

        builtPosition.modifyPositionByAddingPieceAt(pieceToAdd, pieceCell)
        return builtPosition.toFEN()
    }

    private fun placePlayerKingInPosition(startFen: String, playerHasWhite: Boolean): String {
        val kingPiece = ChessPiece(ChessPieceType.King, whiteOwner = playerHasWhite)

        for (tryNumber in 0..maxLoopsIterations){

            val kingCell = generateCell()
            val builtPosition = addPieceToPositionOrReturnNullIfCellAlreadyOccupied(
                    startFen = startFen,
                    pieceToAdd = kingPiece,
                    pieceCell = coordinatesToChessCell(kingCell)
            )

            if (builtPosition != null) {
                if (allConstraints.playerKingConstraint != null){
                    val intValues = mapOf("file" to kingCell.file, "rank" to kingCell.rank)
                    val booleanValues = mapOf("playerHasWhite" to playerHasWhite)
                    val playerKingConstraintRespected = eval(expr = allConstraints.playerKingConstraint!!,
                            intValues = intValues, booleanValues = booleanValues
                    )
                    if (playerKingConstraintRespected) {
                        playerKingCell = kingCell
                        return builtPosition
                    }
                }
                else {
                    playerKingCell = kingCell
                    return builtPosition
                }
            }

        }

        throw PositionGenerationLoopException("Failed to place player king !")
    }

    private fun placeComputerKingInPosition(startFen: String, playerHasWhite: Boolean): String {
        val kingPiece =  ChessPiece(ChessPieceType.King, whiteOwner = !playerHasWhite)

        for (tryNumber in 0..maxLoopsIterations){

            val kingCell = generateCell()
            val builtPosition = addPieceToPositionOrReturnNullIfCellAlreadyOccupied(
                    startFen = startFen,
                    pieceToAdd = kingPiece,
                    pieceCell = coordinatesToChessCell(kingCell)
            )

            val builtPositionIsConformAndLegal = builtPosition != null && !computerKingInChessForPosition(
                    positionFEN = builtPosition, playerHasWhite = playerHasWhite) && ICTKChessLib.isLegalPositionString(builtPosition)

            if (builtPositionIsConformAndLegal) {
                if (allConstraints.computerKingConstraint != null){
                    val computerKingCstrIntValues = mapOf("file" to kingCell.file, "rank" to kingCell.rank)
                    val computerKingCstrBooleanValues = mapOf("playerHasWhite" to playerHasWhite)
                    val computerKingConstraintRespected = eval(expr = allConstraints.computerKingConstraint!!,
                            intValues = computerKingCstrIntValues, booleanValues = computerKingCstrBooleanValues
                    )
                    if (computerKingConstraintRespected) {
                        if (allConstraints.kingsMutualConstraint != null) {
                            val kingsMutualCstrIntValues = mapOf(
                                    "playerKingFile" to playerKingCell.file,
                                    "playerKingRank" to playerKingCell.rank,
                                    "computerKingFile" to kingCell.file,
                                    "computerKingRank" to kingCell.rank
                            )
                            val kingsMutualCstrBooleanValues = mapOf("playerHasWhite" to playerHasWhite)
                            val kingsMutualConstraintRespected = eval(expr = allConstraints.kingsMutualConstraint!!,
                                    intValues = kingsMutualCstrIntValues, booleanValues = kingsMutualCstrBooleanValues)
                            if (kingsMutualConstraintRespected){
                                computerKingCell = kingCell
                                return builtPosition!!
                            }
                        }
                        else {
                            computerKingCell = kingCell
                            return builtPosition!!
                        }
                    }
                }
                else {
                    computerKingCell = kingCell
                    return builtPosition!!
                }
            }

        }

        throw PositionGenerationLoopException("Failed to place computer king !")
    }

    private fun placeOtherPiecesInPosition(startFen: String, playerHasWhite: Boolean): String {

        fun Int.loops(callback : (Int) -> Unit) {
            for (i in 0 until this) callback(i)
        }

        fun pieceKindToChessPiece(kind: EditorPieceKind, whitePiece: Boolean): ChessPiece =
                when(kind.pieceType){
                    EditorPieceType.Pawn -> ChessPiece(ChessPieceType.Pawn, whiteOwner = whitePiece)
                    EditorPieceType.Knight -> ChessPiece(ChessPieceType.Knight, whiteOwner = whitePiece)
                    EditorPieceType.Bishop -> ChessPiece(ChessPieceType.Bishop, whiteOwner = whitePiece)
                    EditorPieceType.Rook ->  ChessPiece(ChessPieceType.Rook, whiteOwner = whitePiece)
                    EditorPieceType.Queen ->  ChessPiece(ChessPieceType.Queen, whiteOwner = whitePiece)
                    EditorPieceType.King ->  ChessPiece(ChessPieceType.King, whiteOwner = whitePiece)
                }


        var currentPosition = ICTKChessLib.buildPositionFromString(startFen)

        allConstraints.otherPiecesCountConstraint.forEach { (kind, count) ->
            val savedCoordinates = arrayListOf<BoardCoordinate>()
            count.loops { _ ->
                var loopSuccess = false
                for (loopIter in 0..maxLoopsIterations) {
                    val isAPieceOfPlayer = kind.side == EditorSide.Player
                    val isAPieceOfComputer = !isAPieceOfPlayer
                    val computerHasWhite = !playerHasWhite
                    val mustBeWhitePiece = (isAPieceOfPlayer && playerHasWhite)
                            || (isAPieceOfComputer && computerHasWhite)

                    val pieceCoordinates = generateCell()

                    val tempPosition = addPieceToPositionOrReturnNullIfCellAlreadyOccupied(
                            startFen = currentPosition.toFEN(),
                            pieceToAdd = pieceKindToChessPiece(kind = kind, whitePiece = mustBeWhitePiece),
                            pieceCell = coordinatesToChessCell(pieceCoordinates)
                    )

                    val forbiddenPosition = tempPosition == null ||
                            computerKingInChessForPosition(tempPosition, playerHasWhite) ||
                            ! ICTKChessLib.isLegalPositionString(tempPosition)
                    if (forbiddenPosition) continue

                    currentPosition = ICTKChessLib.buildPositionFromString(tempPosition!!)
                    savedCoordinates += pieceCoordinates
                    loopSuccess = true
                    break
                }
                if (!loopSuccess) throw PositionGenerationLoopException()
            }
        }

        return currentPosition.toFEN()
    }

    private fun computerKingInChessForPosition(positionFEN: String, playerHasWhite: Boolean) : Boolean {
        val currentPositionWithTurnReversed = ICTKChessLib.buildPositionFromString(positionFEN)
        currentPositionWithTurnReversed.modifyPositionBySettingIfWhiteToMove(! playerHasWhite)
        return currentPositionWithTurnReversed.kingInChess()
    }

}