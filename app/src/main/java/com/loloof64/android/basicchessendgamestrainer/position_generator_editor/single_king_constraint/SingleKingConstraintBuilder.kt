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

import com.loloof64.android.basicchessendgamestrainer.MyApplication
import com.loloof64.android.basicchessendgamestrainer.R
import com.loloof64.android.basicchessendgamestrainer.exercise_chooser.PositionConstraints
import com.loloof64.android.basicchessendgamestrainer.position_generator_editor.single_king_constraint.antlr4.SingleKingConstraintBaseVisitor
import com.loloof64.android.basicchessendgamestrainer.position_generator_editor.single_king_constraint.antlr4.SingleKingConstraintParser
import org.antlr.v4.runtime.misc.ParseCancellationException

data class GenericExprVariable(val name: String, val value: SingleKingConstraintGenericExpr)

object SingleKingConstraintBuilder : SingleKingConstraintBaseVisitor<SingleKingConstraintGenericExpr>() {

    private val builtVariables = mutableListOf<GenericExprVariable>()

    private fun List<GenericExprVariable>.containsKey(key: String): Boolean {
        return this.any { it.name == key }
    }

    private operator fun List<GenericExprVariable>.get(key: String) : SingleKingConstraintGenericExpr {
        val expr = this.find { it.name == key }?.value
        if (expr == null) {
            val errorFormat = MyApplication.appContext.resources.getString(R.string.parser_variable_not_affected)
            throw ParseCancellationException(String.format(errorFormat, key))
        }
        return expr
    }

    fun clearVariables() {
        builtVariables.clear()
    }

    fun getVariables() = builtVariables.toList()

    override fun visitTerminalExpr(ctx: SingleKingConstraintParser.TerminalExprContext?): SingleKingConstraintGenericExpr {
        return visit(ctx?.booleanExpr())
    }

    override fun visitNumericAssign(ctx: SingleKingConstraintParser.NumericAssignContext?): SingleKingConstraintGenericExpr {
        val variableName = ctx?.ID()?.text.toString()
        val assignedValue = visit(ctx?.numericExpr()) as SingleKingConstraintNumericExpr
        builtVariables.add(GenericExprVariable(variableName, assignedValue))
        return UnitSingleKingConstraintGenericExpr
    }

    override fun visitBooleanAssign(ctx: SingleKingConstraintParser.BooleanAssignContext?): SingleKingConstraintGenericExpr {
        val variableName = ctx?.ID()?.text.toString()
        val assignedValue = visit(ctx?.booleanExpr()) as SingleKingConstraintBooleanExpr
        builtVariables.add(GenericExprVariable(variableName, assignedValue))
        return UnitSingleKingConstraintGenericExpr
    }

    override fun visitParenthesisBooleanExpr(ctx: SingleKingConstraintParser.ParenthesisBooleanExprContext?): SingleKingConstraintGenericExpr {
        val internalExpr = visit(ctx?.booleanExpr()) as SingleKingConstraintBooleanExpr
        return ParenthesisSingleKingConstraintBooleanExpr(expr = internalExpr)
    }

    override fun visitConditionalBooleanExpr(ctx: SingleKingConstraintParser.ConditionalBooleanExprContext?): SingleKingConstraintGenericExpr {
        val conditionalExpr = visit(ctx?.booleanExpr(0)) as SingleKingConstraintBooleanExpr
        val successExpr = visit(ctx?.booleanExpr(1)) as SingleKingConstraintBooleanExpr
        val failureExpr = visit(ctx?.booleanExpr(2)) as SingleKingConstraintBooleanExpr

        return ConditionalSingleKingConstraintBooleanExpr(condition = conditionalExpr, successExpr = successExpr, failureExpr = failureExpr)
    }

    override fun visitBooleanVariable(ctx: SingleKingConstraintParser.BooleanVariableContext?): SingleKingConstraintGenericExpr {
        val variableName = ctx?.ID()?.text.toString()
        return if (builtVariables.containsKey(variableName)) builtVariables[variableName]
                            else VariableSingleKingConstraintBooleanExpr(name = variableName)
    }

    override fun visitNumericRelational(ctx: SingleKingConstraintParser.NumericRelationalContext?): SingleKingConstraintGenericExpr {
        val expr1 = visit(ctx?.numericExpr(0)) as SingleKingConstraintNumericExpr
        val expr2 = visit(ctx?.numericExpr(1)) as SingleKingConstraintNumericExpr
        val op = ctx?.op?.text.toString()

        return when(op) {
            "<" -> LT_SingleKingConstraintBooleanExpr(expr1 = expr1, expr2 = expr2)
            ">" -> GT_SingleKingConstraintBooleanExpr(expr1 = expr1, expr2 = expr2)
            "<=" -> LEQ_SingleKingConstraintBooleanExpr(expr1 = expr1, expr2 = expr2)
            ">=" -> GEQ_SingleKingConstraintBooleanExpr(expr1 = expr1, expr2 = expr2)
            else -> throw IllegalArgumentException("Unknown operator $op")
        }
    }

    override fun visitNumericEquality(ctx: SingleKingConstraintParser.NumericEqualityContext?): SingleKingConstraintGenericExpr {
        val expr1 = visit(ctx?.numericExpr(0)) as SingleKingConstraintNumericExpr
        val expr2 = visit(ctx?.numericExpr(1)) as SingleKingConstraintNumericExpr
        val op = ctx?.op?.text.toString()

        return when(op) {
            "=" -> EQ_SingleKingConstraintBooleanExpr(expr1 = expr1, expr2 = expr2)
            "<>" -> NEQ_SingleKingConstraintBooleanExpr(expr1 = expr1, expr2 = expr2)
            else -> throw IllegalArgumentException("Unknown operator $op")
        }
    }

    override fun visitAndComparison(ctx: SingleKingConstraintParser.AndComparisonContext?): SingleKingConstraintGenericExpr {
        val expr1 = visit(ctx?.booleanExpr(0)) as SingleKingConstraintBooleanExpr
        val expr2 = visit(ctx?.booleanExpr(1)) as SingleKingConstraintBooleanExpr

        return AndComparisonSingleKingConstraintBooleanExpr(expr1 = expr1, expr2 = expr2)
    }

    override fun visitOrComparison(ctx: SingleKingConstraintParser.OrComparisonContext?): SingleKingConstraintGenericExpr {
        val expr1 = visit(ctx?.booleanExpr(0)) as SingleKingConstraintBooleanExpr
        val expr2 = visit(ctx?.booleanExpr(1)) as SingleKingConstraintBooleanExpr

        return OrComparisonSingleKingConstraintBooleanExpr(expr1 = expr1, expr2 = expr2)
    }

    override fun visitParenthesisNumericExpr(ctx: SingleKingConstraintParser.ParenthesisNumericExprContext?): SingleKingConstraintGenericExpr {
        val internalExpr = visit(ctx?.numericExpr()) as SingleKingConstraintNumericExpr
        return ParenthesisSingleKingConstraintNumericExpr(expr = internalExpr)
    }

    override fun visitConditionalNumericExpr(ctx: SingleKingConstraintParser.ConditionalNumericExprContext?): SingleKingConstraintGenericExpr {
        val conditionalExpr = visit(ctx?.booleanExpr()) as SingleKingConstraintBooleanExpr
        val successExpr = visit(ctx?.numericExpr(0)) as SingleKingConstraintNumericExpr
        val failureExpr = visit(ctx?.numericExpr(1)) as SingleKingConstraintNumericExpr

        return ConditionalSingleKingConstraintNumericExpr(condition = conditionalExpr, successExpr = successExpr, failureExpr = failureExpr)
    }

    override fun visitAbsoluteNumericExpr(ctx: SingleKingConstraintParser.AbsoluteNumericExprContext?): SingleKingConstraintGenericExpr {
        val internalExpr = visit(ctx?.numericExpr()) as SingleKingConstraintNumericExpr
        return AbsoluteSingleKingConstraintNumericExpr(expr = internalExpr)
    }

    override fun visitLitteralNumericExpr(ctx: SingleKingConstraintParser.LitteralNumericExprContext?): SingleKingConstraintGenericExpr {
        val internalValue = Integer.parseInt(ctx?.NumericLitteral()?.text.toString())
        return LiteralSingleKingConstraintNumericExpr(value = internalValue)
    }

    override fun visitNumericVariable(ctx: SingleKingConstraintParser.NumericVariableContext?): SingleKingConstraintGenericExpr {
        val variableName = ctx?.ID()?.text.toString()
        return if (builtVariables.containsKey(variableName)) builtVariables[variableName]
        else VariableSingleKingConstraintNumericExpr(name = variableName)
    }

    override fun visitFileConstantNumericExpr(ctx: SingleKingConstraintParser.FileConstantNumericExprContext?): SingleKingConstraintGenericExpr {
        val constantText = ctx?.fileConstant()?.text.toString()
        val literalValue = when (constantText) {
            "FileA" -> PositionConstraints.FileA
            "FileB" -> PositionConstraints.FileB
            "FileC" -> PositionConstraints.FileC
            "FileD" -> PositionConstraints.FileD
            "FileE" -> PositionConstraints.FileE
            "FileF" -> PositionConstraints.FileF
            "FileG" -> PositionConstraints.FileG
            "FileH" -> PositionConstraints.FileH
            else -> throw IllegalArgumentException("Constant $constantText is not a file constant.")
        }
        return LiteralSingleKingConstraintNumericExpr(value = literalValue)
    }

    override fun visitRankConstantNumericExpr(ctx: SingleKingConstraintParser.RankConstantNumericExprContext?): SingleKingConstraintGenericExpr {
        val constantText = ctx?.rankConstant()?.text.toString()
        val literalValue = when(constantText) {
            "Rank1" -> PositionConstraints.Rank1
            "Rank2" -> PositionConstraints.Rank2
            "Rank3" -> PositionConstraints.Rank3
            "Rank4" -> PositionConstraints.Rank4
            "Rank5" -> PositionConstraints.Rank5
            "Rank6" -> PositionConstraints.Rank6
            "Rank7" -> PositionConstraints.Rank7
            "Rank8" -> PositionConstraints.Rank8
            else -> throw IllegalArgumentException("Constant $constantText is not a rank constant.")
        }
        return LiteralSingleKingConstraintNumericExpr(value = literalValue)
    }

    override fun visitPlusMinusNumericExpr(ctx: SingleKingConstraintParser.PlusMinusNumericExprContext?): SingleKingConstraintGenericExpr {
        val expr1 = visit(ctx?.numericExpr(0)) as SingleKingConstraintNumericExpr
        val expr2 = visit(ctx?.numericExpr(1)) as SingleKingConstraintNumericExpr
        val op = ctx?.op?.text.toString()

        return when(op){
            "+" -> Plus_SingleKingConstraintNumericExpr(expr1 = expr1, expr2 = expr2)
            "-" -> Minus_SingleKingConstraintNumericExpr(expr1 = expr1, expr2 = expr2)
            else -> throw IllegalArgumentException("Unknown operator $op")
        }
    }

}