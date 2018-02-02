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
package com.loloof64.android.basicchessendgamestrainer.position_generator_editor.antlr4.single_king_constraint


data class SingleKingConstraintRange(val lowerBound: SingleKingConstraintNumericExpr,
                                     val upperBound: SingleKingConstraintNumericExpr)

sealed class SingleKingConstraintBooleanExpr
data class ParenthesisSingleKingConstraintBooleanExpr(val expr: SingleKingConstraintBooleanExpr) : SingleKingConstraintBooleanExpr()
data class VariableSingleKingConstraintBooleanExpr(val name: String) : SingleKingConstraintBooleanExpr()
data class RangeCheckSingleKingConstraintBooleanExpr(val numericExpr: SingleKingConstraintNumericExpr, val range: SingleKingConstraintRange)
data class NumericRelationalSingleKingConstraintBooleanExpr(val expr1: SingleKingConstraintNumericExpr,
                                                            val op: String,
                                                            val expr2: SingleKingConstraintNumericExpr): SingleKingConstraintBooleanExpr()
data class NumericEqualitySingleKingConstraintBooleanExpr(val expr1: SingleKingConstraintNumericExpr,
                                                          val isEqualOp: Boolean,
                                                          val expr2: SingleKingConstraintNumericExpr) : SingleKingConstraintBooleanExpr()
data class AndComparisonSingleKingConstraintBooleanExpr(val expr1: SingleKingConstraintBooleanExpr,
                                                        val expr2: SingleKingConstraintBooleanExpr): SingleKingConstraintBooleanExpr()
data class OrComparisonSingleKingConstraintBooleanExpr(val expr1: SingleKingConstraintBooleanExpr,
                                                       val expr2: SingleKingConstraintBooleanExpr): SingleKingConstraintBooleanExpr()


enum class FileConstant {
    FILE_A,
    FILE_B,
    FILE_C,
    FILE_D,
    FILE_E,
    FILE_F,
    FILE_G,
    FILE_H
}

enum class RankConstant {
    RANK_1,
    RANK_2,
    RANK_3,
    RANK_4,
    RANK_5,
    RANK_6,
    RANK_7,
    RANK_8
}

sealed class SingleKingConstraintNumericExpr
data class ParenthesisSingleKingConstraintNumericExpr(val expr: SingleKingConstraintNumericExpr) : SingleKingConstraintNumericExpr()
data class AbsoluteSingleKingConstraintNumericExpr(val expr: SingleKingConstraintNumericExpr): SingleKingConstraintNumericExpr()
data class LiteralSingleKingConstraintNumericExpr(val value: Int): SingleKingConstraintNumericExpr()
data class VariableSingleKingConstraintNumericExpr(val name: String): SingleKingConstraintNumericExpr()
data class FileConstantSingleKingConstraintNumericExpr(val file: FileConstant): SingleKingConstraintNumericExpr()
data class RankConstantSingleKingConstraintNumericExpr(val rank: RankConstant): SingleKingConstraintNumericExpr()
data class PlusMinusSingleKingConstraintNumericExpr(val expr1: SingleKingConstraintNumericExpr,
                                                    val op: Char,
                                                    val expr2: SingleKingConstraintNumericExpr): SingleKingConstraintNumericExpr()