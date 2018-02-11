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
package com.loloof64.android.basicchessendgamestrainer.position_generator_editor.script_language

class VariableIsNotAffectedException(val name: String): Exception()

sealed class ScriptLanguageGenericExpr
object UnitScriptLanguageGenericExpr: ScriptLanguageGenericExpr()

sealed class ScriptLanguageBooleanExpr: ScriptLanguageGenericExpr()
data class ParenthesisScriptLanguageBooleanExpr(val expr: ScriptLanguageBooleanExpr) : ScriptLanguageBooleanExpr()
data class ConditionalScriptLanguageBooleanExpr(val condition: ScriptLanguageBooleanExpr,
                                                      val successExpr: ScriptLanguageBooleanExpr,
                                                      val failureExpr: ScriptLanguageBooleanExpr) : ScriptLanguageBooleanExpr()
data class VariableScriptLanguageBooleanExpr(val name: String) : ScriptLanguageBooleanExpr()
data class LT_ScriptLanguageBooleanExpr(val expr1: ScriptLanguageNumericExpr, val expr2: ScriptLanguageNumericExpr) : ScriptLanguageBooleanExpr()
data class GT_ScriptLanguageBooleanExpr(val expr1: ScriptLanguageNumericExpr, val expr2: ScriptLanguageNumericExpr) : ScriptLanguageBooleanExpr()
data class LEQ_ScriptLanguageBooleanExpr(val expr1: ScriptLanguageNumericExpr, val expr2: ScriptLanguageNumericExpr) : ScriptLanguageBooleanExpr()
data class GEQ_ScriptLanguageBooleanExpr(val expr1: ScriptLanguageNumericExpr, val expr2: ScriptLanguageNumericExpr) : ScriptLanguageBooleanExpr()
data class EQ_ScriptLanguageBooleanExpr(val expr1: ScriptLanguageNumericExpr, val expr2: ScriptLanguageNumericExpr) : ScriptLanguageBooleanExpr()
data class NEQ_ScriptLanguageBooleanExpr(val expr1: ScriptLanguageNumericExpr, val expr2: ScriptLanguageNumericExpr) : ScriptLanguageBooleanExpr()
data class AndComparisonScriptLanguageBooleanExpr(val expr1: ScriptLanguageBooleanExpr,
                                                        val expr2: ScriptLanguageBooleanExpr): ScriptLanguageBooleanExpr()
data class OrComparisonScriptLanguageBooleanExpr(val expr1: ScriptLanguageBooleanExpr,
                                                       val expr2: ScriptLanguageBooleanExpr): ScriptLanguageBooleanExpr()
data class LiteralScriptLanguageBooleanExpr(val value: Boolean) : ScriptLanguageBooleanExpr()

sealed class ScriptLanguageNumericExpr: ScriptLanguageGenericExpr()
data class ParenthesisScriptLanguageNumericExpr(val expr: ScriptLanguageNumericExpr) : ScriptLanguageNumericExpr()
data class ConditionalScriptLanguageNumericExpr(val condition: ScriptLanguageBooleanExpr,
                                                      val successExpr: ScriptLanguageNumericExpr,
                                                      val failureExpr: ScriptLanguageNumericExpr) : ScriptLanguageNumericExpr()
data class AbsoluteScriptLanguageNumericExpr(val expr: ScriptLanguageNumericExpr): ScriptLanguageNumericExpr()
data class LiteralScriptLanguageNumericExpr(val value: Int): ScriptLanguageNumericExpr()
data class VariableScriptLanguageNumericExpr(val name: String): ScriptLanguageNumericExpr()
data class Plus_ScriptLanguageNumericExpr(val expr1: ScriptLanguageNumericExpr, val expr2: ScriptLanguageNumericExpr) : ScriptLanguageNumericExpr()
data class Minus_ScriptLanguageNumericExpr(val expr1: ScriptLanguageNumericExpr, val expr2: ScriptLanguageNumericExpr) : ScriptLanguageNumericExpr()

fun eval(expr: ScriptLanguageNumericExpr, intValues: Map<String, Int>, booleanValues: Map<String, Boolean>) : Int {
    return when (expr) {
        is ParenthesisScriptLanguageNumericExpr -> eval(expr.expr, intValues, booleanValues)
        is ConditionalScriptLanguageNumericExpr -> {
            val condition = eval(expr.condition, intValues, booleanValues)
            val successBranch = eval(expr.successExpr, intValues, booleanValues)
            val failureBranch = eval(expr.failureExpr, intValues, booleanValues)

            if (condition) successBranch else failureBranch
        }
        is AbsoluteScriptLanguageNumericExpr -> Math.abs(eval(expr.expr, intValues, booleanValues))
        is LiteralScriptLanguageNumericExpr -> expr.value
        is VariableScriptLanguageNumericExpr -> {
            if (intValues.containsKey(expr.name)) intValues[expr.name]!!
            else throw VariableIsNotAffectedException(expr.name)
        }
        is Plus_ScriptLanguageNumericExpr -> eval(expr.expr1, intValues, booleanValues) +
                eval(expr.expr2, intValues, booleanValues)
        is Minus_ScriptLanguageNumericExpr -> eval(expr.expr1, intValues, booleanValues) -
                eval(expr.expr2, intValues, booleanValues)
    }
}

fun eval(expr: ScriptLanguageBooleanExpr, intValues: Map<String, Int>, booleanValues: Map<String, Boolean>) : Boolean {
    return when (expr) {
        is ParenthesisScriptLanguageBooleanExpr -> eval(expr.expr, intValues, booleanValues)
        is ConditionalScriptLanguageBooleanExpr -> {
            val condition = eval(expr.condition, intValues, booleanValues)
            val successBranch = eval(expr.successExpr, intValues, booleanValues)
            val failureBranch = eval(expr.failureExpr, intValues, booleanValues)

            if (condition) successBranch else failureBranch
        }
        is VariableScriptLanguageBooleanExpr -> {
            if (booleanValues.containsKey(expr.name)) booleanValues[expr.name]!!
            else throw VariableIsNotAffectedException(expr.name)
        }
        is LT_ScriptLanguageBooleanExpr -> eval(expr.expr1, intValues, booleanValues) <
                eval(expr.expr2, intValues, booleanValues)
        is GT_ScriptLanguageBooleanExpr -> eval(expr.expr1, intValues, booleanValues) >
                eval(expr.expr2, intValues, booleanValues)
        is LEQ_ScriptLanguageBooleanExpr -> eval(expr.expr1, intValues, booleanValues) <=
                eval(expr.expr2, intValues, booleanValues)
        is GEQ_ScriptLanguageBooleanExpr -> eval(expr.expr1, intValues, booleanValues) >=
                eval(expr.expr2, intValues, booleanValues)
        is EQ_ScriptLanguageBooleanExpr -> eval(expr.expr1, intValues, booleanValues) ==
                eval(expr.expr2, intValues, booleanValues)
        is NEQ_ScriptLanguageBooleanExpr -> eval(expr.expr1, intValues, booleanValues) !=
                eval(expr.expr2, intValues, booleanValues)
        is AndComparisonScriptLanguageBooleanExpr -> eval(expr.expr1, intValues, booleanValues) &&
                eval(expr.expr2, intValues, booleanValues)
        is OrComparisonScriptLanguageBooleanExpr -> eval(expr.expr1, intValues, booleanValues) ||
                eval(expr.expr2, intValues, booleanValues)
        is LiteralScriptLanguageBooleanExpr -> expr.value
    }
}