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
package com.loloof64.android.basicchessendgamestrainer.position_generator_editor.single_king_constraint


sealed class SingleKingConstraintGenericExpr
object UnitSingleKingConstraintGenericExpr: SingleKingConstraintGenericExpr()

sealed class SingleKingConstraintBooleanExpr: SingleKingConstraintGenericExpr()
data class ParenthesisSingleKingConstraintBooleanExpr(val expr: SingleKingConstraintBooleanExpr) : SingleKingConstraintBooleanExpr()
data class VariableSingleKingConstraintBooleanExpr(val name: String) : SingleKingConstraintBooleanExpr()
data class LT_SingleKingConstraintBooleanExpr(val expr1: SingleKingConstraintNumericExpr, val expr2: SingleKingConstraintNumericExpr) : SingleKingConstraintBooleanExpr()
data class GT_SingleKingConstraintBooleanExpr(val expr1: SingleKingConstraintNumericExpr, val expr2: SingleKingConstraintNumericExpr) : SingleKingConstraintBooleanExpr()
data class LEQ_SingleKingConstraintBooleanExpr(val expr1: SingleKingConstraintNumericExpr, val expr2: SingleKingConstraintNumericExpr) : SingleKingConstraintBooleanExpr()
data class GEQ_SingleKingConstraintBooleanExpr(val expr1: SingleKingConstraintNumericExpr, val expr2: SingleKingConstraintNumericExpr) : SingleKingConstraintBooleanExpr()
data class EQ_SingleKingConstraintBooleanExpr(val expr1: SingleKingConstraintNumericExpr, val expr2: SingleKingConstraintNumericExpr) : SingleKingConstraintBooleanExpr()
data class NEQ_SingleKingConstraintBooleanExpr(val expr1: SingleKingConstraintNumericExpr, val expr2: SingleKingConstraintNumericExpr) : SingleKingConstraintBooleanExpr()
data class AndComparisonSingleKingConstraintBooleanExpr(val expr1: SingleKingConstraintBooleanExpr,
                                                        val expr2: SingleKingConstraintBooleanExpr): SingleKingConstraintBooleanExpr()
data class OrComparisonSingleKingConstraintBooleanExpr(val expr1: SingleKingConstraintBooleanExpr,
                                                       val expr2: SingleKingConstraintBooleanExpr): SingleKingConstraintBooleanExpr()
data class LiteralSingleKingConstraintBooleanExpr(val value: Boolean) : SingleKingConstraintBooleanExpr()

sealed class SingleKingConstraintNumericExpr: SingleKingConstraintGenericExpr()
data class ParenthesisSingleKingConstraintNumericExpr(val expr: SingleKingConstraintNumericExpr) : SingleKingConstraintNumericExpr()
data class AbsoluteSingleKingConstraintNumericExpr(val expr: SingleKingConstraintNumericExpr): SingleKingConstraintNumericExpr()
data class LiteralSingleKingConstraintNumericExpr(val value: Int): SingleKingConstraintNumericExpr()
data class VariableSingleKingConstraintNumericExpr(val name: String): SingleKingConstraintNumericExpr()
data class Plus_SingleKingConstraintNumericExpr(val expr1: SingleKingConstraintNumericExpr, val expr2: SingleKingConstraintNumericExpr) : SingleKingConstraintNumericExpr()
data class Minus_SingleKingConstraintNumericExpr(val expr1: SingleKingConstraintNumericExpr, val expr2: SingleKingConstraintNumericExpr) : SingleKingConstraintNumericExpr()

fun eval(expr: SingleKingConstraintNumericExpr, intVariables: Map<String, Int>) : Int {
    return when (expr) {
        is ParenthesisSingleKingConstraintNumericExpr -> eval(expr.expr, intVariables)
        is AbsoluteSingleKingConstraintNumericExpr -> Math.abs(eval(expr.expr, intVariables))
        is LiteralSingleKingConstraintNumericExpr -> expr.value
        is VariableSingleKingConstraintNumericExpr -> intVariables[expr.name]!!
        is Plus_SingleKingConstraintNumericExpr -> eval(expr.expr1, intVariables) + eval(expr.expr2, intVariables)
        is Minus_SingleKingConstraintNumericExpr -> eval(expr.expr1, intVariables) - eval(expr.expr2, intVariables)
    }
}

fun eval(expr: SingleKingConstraintBooleanExpr, intVariables: Map<String, Int>, booleanVariables: Map<String, Boolean>) : Boolean {
    return when (expr) {
        is ParenthesisSingleKingConstraintBooleanExpr -> eval(expr.expr, intVariables, booleanVariables)
        is VariableSingleKingConstraintBooleanExpr -> booleanVariables[expr.name]!!
        is LT_SingleKingConstraintBooleanExpr -> eval(expr.expr1, intVariables) < eval(expr.expr2, intVariables)
        is GT_SingleKingConstraintBooleanExpr -> eval(expr.expr1, intVariables) > eval(expr.expr2, intVariables)
        is LEQ_SingleKingConstraintBooleanExpr -> eval(expr.expr1, intVariables) <= eval(expr.expr2, intVariables)
        is GEQ_SingleKingConstraintBooleanExpr -> eval(expr.expr1, intVariables) >= eval(expr.expr2, intVariables)
        is EQ_SingleKingConstraintBooleanExpr -> eval(expr.expr1, intVariables) == eval(expr.expr2, intVariables)
        is NEQ_SingleKingConstraintBooleanExpr -> eval(expr.expr1, intVariables) != eval(expr.expr2, intVariables)
        is AndComparisonSingleKingConstraintBooleanExpr -> eval(expr.expr1, intVariables, booleanVariables) && eval(expr.expr2, intVariables, booleanVariables)
        is OrComparisonSingleKingConstraintBooleanExpr -> eval(expr.expr1, intVariables, booleanVariables) || eval(expr.expr2, intVariables, booleanVariables)
        is LiteralSingleKingConstraintBooleanExpr -> expr.value
    }
}