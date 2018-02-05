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

class VariableIsNotAffectedException(val name: String): Exception()

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

fun eval(expr: SingleKingConstraintNumericExpr, intValues: Map<String, Int>,
         numericVariables: Map<String, SingleKingConstraintNumericExpr>) : Int {
    return when (expr) {
        is ParenthesisSingleKingConstraintNumericExpr -> eval(expr.expr, intValues, numericVariables)
        is AbsoluteSingleKingConstraintNumericExpr -> Math.abs(eval(expr.expr, intValues, numericVariables))
        is LiteralSingleKingConstraintNumericExpr -> expr.value
        is VariableSingleKingConstraintNumericExpr -> {
            if (intValues.containsKey(expr.name)) intValues[expr.name]!!
            else if (numericVariables.containsKey(expr.name)) eval(numericVariables[expr.name]!!, intValues, numericVariables)
            else throw VariableIsNotAffectedException(expr.name)
        }
        is Plus_SingleKingConstraintNumericExpr -> eval(expr.expr1, intValues, numericVariables) +
                eval(expr.expr2, intValues, numericVariables)
        is Minus_SingleKingConstraintNumericExpr -> eval(expr.expr1, intValues, numericVariables) -
                eval(expr.expr2, intValues, numericVariables)
    }
}

fun eval(expr: SingleKingConstraintBooleanExpr, intValues: Map<String, Int>, booleanValues: Map<String, Boolean>,
         numericVariables: Map<String, SingleKingConstraintNumericExpr>,
         booleanVariables: Map<String, SingleKingConstraintBooleanExpr>) : Boolean {
    return when (expr) {
        is ParenthesisSingleKingConstraintBooleanExpr -> eval(expr.expr, intValues, booleanValues, numericVariables, booleanVariables)
        is VariableSingleKingConstraintBooleanExpr -> {
            if (booleanValues.containsKey(expr.name)) booleanValues[expr.name]!!
            else if (booleanVariables.containsKey(expr.name)) eval(booleanVariables[expr.name]!!, intValues, booleanValues, numericVariables, booleanVariables)
            else throw VariableIsNotAffectedException(expr.name)
        }
        is LT_SingleKingConstraintBooleanExpr -> eval(expr.expr1, intValues, numericVariables) < eval(expr.expr2, intValues, numericVariables)
        is GT_SingleKingConstraintBooleanExpr -> eval(expr.expr1, intValues, numericVariables) > eval(expr.expr2, intValues, numericVariables)
        is LEQ_SingleKingConstraintBooleanExpr -> eval(expr.expr1, intValues, numericVariables) <= eval(expr.expr2, intValues, numericVariables)
        is GEQ_SingleKingConstraintBooleanExpr -> eval(expr.expr1, intValues, numericVariables) >= eval(expr.expr2, intValues, numericVariables)
        is EQ_SingleKingConstraintBooleanExpr -> eval(expr.expr1, intValues, numericVariables) == eval(expr.expr2, intValues, numericVariables)
        is NEQ_SingleKingConstraintBooleanExpr -> eval(expr.expr1, intValues, numericVariables) != eval(expr.expr2, intValues, numericVariables)
        is AndComparisonSingleKingConstraintBooleanExpr -> eval(expr.expr1, intValues, booleanValues, numericVariables, booleanVariables) &&
                eval(expr.expr2, intValues, booleanValues, numericVariables, booleanVariables)
        is OrComparisonSingleKingConstraintBooleanExpr -> eval(expr.expr1, intValues, booleanValues, numericVariables, booleanVariables) ||
                eval(expr.expr2, intValues, booleanValues, numericVariables, booleanVariables)
        is LiteralSingleKingConstraintBooleanExpr -> expr.value
    }
}