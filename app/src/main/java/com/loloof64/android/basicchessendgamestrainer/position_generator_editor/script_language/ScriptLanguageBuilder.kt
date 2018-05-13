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

import android.widget.Toast
import com.loloof64.android.basicchessendgamestrainer.MyApplication
import com.loloof64.android.basicchessendgamestrainer.MessageToShowInDialogEvent
import com.loloof64.android.basicchessendgamestrainer.R
import com.loloof64.android.basicchessendgamestrainer.exercise_chooser.PositionConstraints
import com.loloof64.android.basicchessendgamestrainer.position_generator_editor.PositionConstraintBailErrorStrategy
import com.loloof64.android.basicchessendgamestrainer.position_generator_editor.script_language.antlr4.ScriptLanguageBaseVisitor
import com.loloof64.android.basicchessendgamestrainer.position_generator_editor.script_language.antlr4.ScriptLanguageParser
import com.loloof64.android.basicchessendgamestrainer.utils.RxEventBus
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.misc.ParseCancellationException

data class GenericExprVariable(val name: String, val value: ScriptLanguageGenericExpr)

object ScriptLanguageBuilder : ScriptLanguageBaseVisitor<ScriptLanguageGenericExpr>() {

    private val builtVariables = mutableListOf<GenericExprVariable>()

    private fun List<GenericExprVariable>.containsKey(key: String): Boolean {
        return this.any { it.name == key }
    }

    private operator fun List<GenericExprVariable>.get(key: String) : ScriptLanguageGenericExpr {
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

    private fun getVariables(): List<GenericExprVariable> = builtVariables.toList()

    private fun checkIfScriptStringIsValid(scriptString: String,
                                   sampleIntValues : Map<String, Int>,
                                   sampleBooleanValues: Map<String, Boolean>) {
        val resultExpr = buildExprObjectFromScript(scriptString)
        testCanEvaluateExpressionWithDefaultVariablesSetAndShowEventualError(
                expr = resultExpr,
                sampleIntValues = sampleIntValues,
                sampleBooleanValues = sampleBooleanValues
        )
    }

    private fun buildExprObjectFromScript(scriptString: String) : ScriptLanguageBooleanExpr {
        val inputStream = CharStreams.fromString(scriptString)
        val lexer = BailScriptLanguageLexer(inputStream)
        val tokens = CommonTokenStream(lexer)
        val parser = ScriptLanguageParser(tokens)
        parser.errorHandler = PositionConstraintBailErrorStrategy()
        val tree = parser.scriptLanguage()
        ScriptLanguageBuilder.clearVariables()
        return ScriptLanguageBuilder.visit(tree) as ScriptLanguageBooleanExpr
    }

    private fun testCanEvaluateExpressionWithDefaultVariablesSetAndShowEventualError(
            expr: ScriptLanguageBooleanExpr,
            sampleIntValues : Map<String, Int>,
            sampleBooleanValues: Map<String, Boolean>
    ) {
            val intValues = sampleIntValues.toMutableMap()
            val booleanValues = sampleBooleanValues.toMutableMap()

            val resources = MyApplication.appContext.resources

            val variables = ScriptLanguageBuilder.getVariables()
            // We must evaluate all variables before evaluating the final script expression
            variables.forEach {
                when (it.value) {
                    is ScriptLanguageNumericExpr -> {
                        if (sampleIntValues.containsKey(it.name)) {
                            val error = resources.getString(R.string.parser_overriding_predefined_variable, it.name)
                            throw ParseCancellationException(error)
                        }
                        else intValues[it.name] = eval(expr = it.value, intValues = intValues, booleanValues = booleanValues)
                    }
                    is ScriptLanguageBooleanExpr -> {
                        if (sampleBooleanValues.containsKey(it.name)) {
                            val error = resources.getString(R.string.parser_overriding_predefined_variable, it.name)
                            throw ParseCancellationException(error)
                        }
                        else booleanValues[it.name] = eval(expr = it.value, intValues = intValues, booleanValues = booleanValues)
                    }
                }

            }

            eval(expr = expr, intValues = intValues, booleanValues = booleanValues)
    }

    override fun visitTerminalExpr(ctx: ScriptLanguageParser.TerminalExprContext?): ScriptLanguageGenericExpr {
        return visit(ctx?.booleanExpr())
    }

    override fun visitNumericAssign(ctx: ScriptLanguageParser.NumericAssignContext?): ScriptLanguageGenericExpr {
        val variableName = ctx?.ID()?.text.toString()
        val assignedValue = visit(ctx?.numericExpr()) as ScriptLanguageNumericExpr
        builtVariables.add(GenericExprVariable(variableName, assignedValue))
        return UnitScriptLanguageGenericExpr
    }

    override fun visitBooleanAssign(ctx: ScriptLanguageParser.BooleanAssignContext?): ScriptLanguageGenericExpr {
        val variableName = ctx?.ID()?.text.toString()
        val assignedValue = visit(ctx?.booleanExpr()) as ScriptLanguageBooleanExpr
        builtVariables.add(GenericExprVariable(variableName, assignedValue))
        return UnitScriptLanguageGenericExpr
    }

    override fun visitParenthesisBooleanExpr(ctx: ScriptLanguageParser.ParenthesisBooleanExprContext?): ScriptLanguageGenericExpr {
        val internalExpr = visit(ctx?.booleanExpr()) as ScriptLanguageBooleanExpr
        return ParenthesisScriptLanguageBooleanExpr(expr = internalExpr)
    }

    override fun visitConditionalBooleanExpr(ctx: ScriptLanguageParser.ConditionalBooleanExprContext?): ScriptLanguageGenericExpr {
        val conditionalExpr = visit(ctx?.booleanExpr(0)) as ScriptLanguageBooleanExpr
        val successExpr = visit(ctx?.booleanExpr(1)) as ScriptLanguageBooleanExpr
        val failureExpr = visit(ctx?.booleanExpr(2)) as ScriptLanguageBooleanExpr

        return ConditionalScriptLanguageBooleanExpr(condition = conditionalExpr, successExpr = successExpr, failureExpr = failureExpr)
    }

    override fun visitBooleanVariable(ctx: ScriptLanguageParser.BooleanVariableContext?): ScriptLanguageGenericExpr {
        val variableName = ctx?.ID()?.text.toString()
        return if (builtVariables.containsKey(variableName)) builtVariables[variableName]
                            else VariableScriptLanguageBooleanExpr(name = variableName)
    }

    override fun visitNumericRelational(ctx: ScriptLanguageParser.NumericRelationalContext?): ScriptLanguageGenericExpr {
        val expr1 = visit(ctx?.numericExpr(0)) as ScriptLanguageNumericExpr
        val expr2 = visit(ctx?.numericExpr(1)) as ScriptLanguageNumericExpr
        val op = ctx?.op?.text.toString()

        return when(op) {
            "<" -> LT_ScriptLanguageBooleanExpr(expr1 = expr1, expr2 = expr2)
            ">" -> GT_ScriptLanguageBooleanExpr(expr1 = expr1, expr2 = expr2)
            "<=" -> LEQ_ScriptLanguageBooleanExpr(expr1 = expr1, expr2 = expr2)
            ">=" -> GEQ_ScriptLanguageBooleanExpr(expr1 = expr1, expr2 = expr2)
            else -> throw IllegalArgumentException("Unknown operator $op")
        }
    }

    override fun visitNumericEquality(ctx: ScriptLanguageParser.NumericEqualityContext?): ScriptLanguageGenericExpr {
        val expr1 = visit(ctx?.numericExpr(0)) as ScriptLanguageNumericExpr
        val expr2 = visit(ctx?.numericExpr(1)) as ScriptLanguageNumericExpr
        val op = ctx?.op?.text.toString()

        return when(op) {
            "=" -> EQ_ScriptLanguageBooleanExpr(expr1 = expr1, expr2 = expr2)
            "<>" -> NEQ_ScriptLanguageBooleanExpr(expr1 = expr1, expr2 = expr2)
            else -> throw IllegalArgumentException("Unknown operator $op")
        }
    }

    override fun visitAndComparison(ctx: ScriptLanguageParser.AndComparisonContext?): ScriptLanguageGenericExpr {
        val expr1 = visit(ctx?.booleanExpr(0)) as ScriptLanguageBooleanExpr
        val expr2 = visit(ctx?.booleanExpr(1)) as ScriptLanguageBooleanExpr

        return AndComparisonScriptLanguageBooleanExpr(expr1 = expr1, expr2 = expr2)
    }

    override fun visitOrComparison(ctx: ScriptLanguageParser.OrComparisonContext?): ScriptLanguageGenericExpr {
        val expr1 = visit(ctx?.booleanExpr(0)) as ScriptLanguageBooleanExpr
        val expr2 = visit(ctx?.booleanExpr(1)) as ScriptLanguageBooleanExpr

        return OrComparisonScriptLanguageBooleanExpr(expr1 = expr1, expr2 = expr2)
    }

    override fun visitParenthesisNumericExpr(ctx: ScriptLanguageParser.ParenthesisNumericExprContext?): ScriptLanguageGenericExpr {
        val internalExpr = visit(ctx?.numericExpr()) as ScriptLanguageNumericExpr
        return ParenthesisScriptLanguageNumericExpr(expr = internalExpr)
    }

    override fun visitConditionalNumericExpr(ctx: ScriptLanguageParser.ConditionalNumericExprContext?): ScriptLanguageGenericExpr {
        val conditionalExpr = visit(ctx?.booleanExpr()) as ScriptLanguageBooleanExpr
        val successExpr = visit(ctx?.numericExpr(0)) as ScriptLanguageNumericExpr
        val failureExpr = visit(ctx?.numericExpr(1)) as ScriptLanguageNumericExpr

        return ConditionalScriptLanguageNumericExpr(condition = conditionalExpr, successExpr = successExpr, failureExpr = failureExpr)
    }

    override fun visitAbsoluteNumericExpr(ctx: ScriptLanguageParser.AbsoluteNumericExprContext?): ScriptLanguageGenericExpr {
        val internalExpr = visit(ctx?.numericExpr()) as ScriptLanguageNumericExpr
        return AbsoluteScriptLanguageNumericExpr(expr = internalExpr)
    }

    override fun visitLitteralNumericExpr(ctx: ScriptLanguageParser.LitteralNumericExprContext?): ScriptLanguageGenericExpr {
        val internalValue = Integer.parseInt(ctx?.NumericLitteral()?.text.toString())
        return LiteralScriptLanguageNumericExpr(value = internalValue)
    }

    override fun visitNumericVariable(ctx: ScriptLanguageParser.NumericVariableContext?): ScriptLanguageGenericExpr {
        val variableName = ctx?.ID()?.text.toString()
        return if (builtVariables.containsKey(variableName)) builtVariables[variableName]
        else VariableScriptLanguageNumericExpr(name = variableName)
    }

    override fun visitFileConstantNumericExpr(ctx: ScriptLanguageParser.FileConstantNumericExprContext?): ScriptLanguageGenericExpr {
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
        return LiteralScriptLanguageNumericExpr(value = literalValue)
    }

    override fun visitRankConstantNumericExpr(ctx: ScriptLanguageParser.RankConstantNumericExprContext?): ScriptLanguageGenericExpr {
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
        return LiteralScriptLanguageNumericExpr(value = literalValue)
    }

    override fun visitModuloNumericExpr(ctx: ScriptLanguageParser.ModuloNumericExprContext?): ScriptLanguageGenericExpr {
        val expr1 = visit(ctx?.numericExpr(0)) as ScriptLanguageNumericExpr
        val expr2 = visit(ctx?.numericExpr(1)) as ScriptLanguageNumericExpr

        return Modulo_ScriptLanguageNumericExpr(expr1 = expr1, expr2 = expr2)
    }

    override fun visitPlusMinusNumericExpr(ctx: ScriptLanguageParser.PlusMinusNumericExprContext?): ScriptLanguageGenericExpr {
        val expr1 = visit(ctx?.numericExpr(0)) as ScriptLanguageNumericExpr
        val expr2 = visit(ctx?.numericExpr(1)) as ScriptLanguageNumericExpr
        val op = ctx?.op?.text.toString()

        return when(op){
            "+" -> Plus_ScriptLanguageNumericExpr(expr1 = expr1, expr2 = expr2)
            "-" -> Minus_ScriptLanguageNumericExpr(expr1 = expr1, expr2 = expr2)
            else -> throw IllegalArgumentException("Unknown operator $op")
        }
    }

    fun checkIfScriptIsValidAndShowFirstEventualError(script: String, scriptSectionTitle: String,
                                                      sampleIntValues: Map<String, Int>,
                                                      sampleBooleanValues: Map<String, Boolean>): Boolean {
        val scriptIsEmpty = script.isEmpty()
        if (scriptIsEmpty){
            val resources = MyApplication.appContext.resources
            val scriptIgnoredMessage = resources.getString(R.string.empty_script_error)

            Toast.makeText(MyApplication.appContext, scriptIgnoredMessage, Toast.LENGTH_LONG).show()
            return true
        }

        return try {
            ScriptLanguageBuilder.checkIfScriptStringIsValid(
                    scriptString = script,
                    sampleIntValues = sampleIntValues,
                    sampleBooleanValues = sampleBooleanValues
            )
            true
        }
        catch (ex: VariableIsNotAffectedException) {
            val resources = MyApplication.appContext.resources
            val messageFormat = resources.getString(R.string.parser_variable_not_affected)
            val message = String.format(messageFormat ?: "<Internal error : could not open format string !>", ex.name)

            val titleFormat = resources.getString(R.string.parse_error_dialog_title)
            val title = String.format(titleFormat ?: "<Internal error : could not open localized title string !>", scriptSectionTitle)
            RxEventBus.send(MessageToShowInDialogEvent(title, message))

            false
        }
        catch (ex: ParseCancellationException){
            val message = ex.message ?: "<Internal error : could not get ParseCancellationException message !>"
            val resources = MyApplication.appContext.resources
            val titleFormat = resources.getString(R.string.parse_error_dialog_title)
            val title = String.format(titleFormat ?: "<Internal error : could not open localized title string !>", scriptSectionTitle)
            RxEventBus.send(MessageToShowInDialogEvent(title, message))
            false
        }
    }

}