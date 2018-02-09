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

import com.github.bhlangonijr.chesslib.*
import com.github.bhlangonijr.chesslib.Side
import com.loloof64.android.basicchessendgamestrainer.position_generator_editor.single_king_constraint.SingleKingConstraintBooleanExpr
import com.loloof64.android.basicchessendgamestrainer.position_generator_editor.single_king_constraint.SingleKingConstraintNumericExpr
import com.loloof64.android.basicchessendgamestrainer.position_generator_editor.single_king_constraint.eval
import java.util.*
import java.util.logging.Logger

class PositionGeneratorConstraints(
        val playerKingConstraint: SingleKingConstraintBooleanExpr?,
        val computerKingConstraint: SingleKingConstraintBooleanExpr?
)

object PositionGeneratorFromANTLR {

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

    private fun coordinatesToSquare(coord: BoardCoordinate) : Square {
        val rank = Rank.values()[coord.rank]
        val file = File.values()[coord.file]
        return Square.encode(rank, file)
    }

    private val maxLoopsIterations = 250

    private val NO_CONSTRAINT = PositionGeneratorConstraints(
            playerKingConstraint = null,
            computerKingConstraint = null
    )

    private val random = Random()

    var allConstraints: PositionGeneratorConstraints = NO_CONSTRAINT

    fun setConstraints(constraints: PositionGeneratorConstraints) {
        allConstraints = constraints
    }

    fun generatePosition(): String {

        val playerHasWhite = random.nextBoolean()
        val startFen = "8/8/8/8/8/8/8/8 ${if (playerHasWhite) 'w' else 'b'} - - 0 1"

        val positionWithPlayerKing = placePlayerKingInPosition(startFen = startFen, playerHasWhite = playerHasWhite)
        return placeComputerKingInPosition(startFen = positionWithPlayerKing, playerHasWhite = playerHasWhite)
    }

    private fun addPieceToPositionOrReturnNullIfCellAlreadyOccupied(startFen: String, pieceToAdd: Piece, pieceCell: Square): String? {
        val builtPosition = Board()
        builtPosition.loadFromFEN(startFen)

        val wantedCellOccupied = builtPosition.getPiece(pieceCell) != Piece.NONE
        if (wantedCellOccupied) return null

        builtPosition.setPiece(pieceToAdd, pieceCell)
        return builtPosition.fen
    }

    private fun placePlayerKingInPosition(startFen: String, playerHasWhite: Boolean): String {
        val kingPiece = if (playerHasWhite) Piece.WHITE_KING else Piece.BLACK_KING

        for (tryNumber in 0..maxLoopsIterations){

            val kingCell = generateCell()
            val builtPosition = addPieceToPositionOrReturnNullIfCellAlreadyOccupied(
                    startFen = startFen,
                    pieceToAdd = kingPiece,
                    pieceCell = coordinatesToSquare(kingCell)
            )

            if (builtPosition != null) {
                if (allConstraints.playerKingConstraint != null){
                    val intValues = mapOf("file" to kingCell.file, "rank" to kingCell.rank)
                    val booleanValues = mapOf("playerHasWhite" to playerHasWhite)
                    val buildSuccess = eval(expr = allConstraints.playerKingConstraint!!,
                            intValues = intValues, booleanValues = booleanValues
                    )
                    if (buildSuccess) return builtPosition
                }
                else {
                    return builtPosition
                }
            }

        }

        throw PositionGenerationLoopException("Failed to place player king !")
    }

    private fun placeComputerKingInPosition(startFen: String, playerHasWhite: Boolean): String {
        val kingPiece = if (playerHasWhite) Piece.BLACK_KING else Piece.WHITE_KING

        for (tryNumber in 0..maxLoopsIterations){

            val kingCell = generateCell()
            val builtPosition = addPieceToPositionOrReturnNullIfCellAlreadyOccupied(
                    startFen = startFen,
                    pieceToAdd = kingPiece,
                    pieceCell = coordinatesToSquare(kingCell)
            )

            val builtPositionIsLegal = builtPosition != null && !computerKingInChessForPosition(
                    positionFEN = builtPosition, playerHasWhite = playerHasWhite)

            if (builtPositionIsLegal) {
                if (allConstraints.computerKingConstraint != null){
                    val intValues = mapOf("file" to kingCell.file, "rank" to kingCell.rank)
                    val booleanValues = mapOf("playerHasWhite" to playerHasWhite)
                    val buildSuccess = eval(expr = allConstraints.computerKingConstraint!!,
                            intValues = intValues, booleanValues = booleanValues
                    )
                    if (buildSuccess) return builtPosition!!
                }
                else {
                    return builtPosition!!
                }
            }

        }

        throw PositionGenerationLoopException("Failed to place computer king !")
    }

    private fun computerKingInChessForPosition(positionFEN: String, playerHasWhite: Boolean) : Boolean =
    Board().apply {
        loadFromFEN(positionFEN)
        sideToMove = if (playerHasWhite) Side.BLACK else Side.WHITE
    }.isKingAttacked

}