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

package com.loloof64.android.basicchessendgamestrainer.position_generator_editor

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.loloof64.android.basicchessendgamestrainer.PositionGeneratorValuesHolder
import com.loloof64.android.basicchessendgamestrainer.R
import com.loloof64.android.basicchessendgamestrainer.exercise_chooser.PositionConstraints
import com.loloof64.android.basicchessendgamestrainer.position_generator_editor.single_king_constraint.*
import com.loloof64.android.basicchessendgamestrainer.position_generator_editor.single_king_constraint.antlr4.SingleKingConstraintParser
import org.antlr.v4.runtime.CharStreams
import kotlinx.android.synthetic.main.fragment_editing_player_king_constraint.*
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.misc.ParseCancellationException

class PlayerKingConstraintEditorFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_editing_player_king_constraint, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        button_check_player_king_constraint.setOnClickListener {
            if (checkIsScriptIsValidAndShowEventualError()) Toast.makeText(activity, R.string.script_valid, Toast.LENGTH_SHORT).show()
        }

        generator_editor_field_player_king_constraint.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(str: Editable?) {
                PositionGeneratorValuesHolder.playerKingConstraintScript = str?.toString() ?:
                        "<Internal error : could not read updated player king constraint field>"
            }

            override fun beforeTextChanged(str: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(str: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SCRIPT_KEY, generator_editor_field_player_king_constraint.text.toString())
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState != null) {
            generator_editor_field_player_king_constraint.text.clear()
            generator_editor_field_player_king_constraint.text.append(savedInstanceState.getString(SCRIPT_KEY))
        }
    }

    fun checkIsScriptIsValidAndShowEventualError(): Boolean {
       val scriptIsEmpty = PositionGeneratorValuesHolder.playerKingConstraintScript.isEmpty()
        if (scriptIsEmpty){
            val title = activity?.resources?.getString(R.string.player_king_constraints) ?:
            "<Internal error : could not get empty script localized constraint type !>"
            val errorMessage = activity?.resources?.getString(R.string.empty_script_error) ?:
                "<Internal error : could not get empty script localized error message !>"
            showAlertDialog(title, errorMessage)
            return true
        }

       return try {
           val resultExpr = buildExprObjectFromScript()
           return testCanEvaluateExpressionWithDefaultVariablesSetAndShowEventualError(resultExpr)
       }
       catch (ex: ParseCancellationException){
           val message = ex.message ?: "<Internal error : could not get ParseCancellationException message !>"
           val constraintTypeStr = activity?.resources?.getString(R.string.player_king_constraints) ?:
           "<Internal error : could not get empty script localized constraint type !>"
           val titleFormat = activity?.resources?.getString(R.string.parse_error_dialog_title)
           val title = String.format(titleFormat ?: "<Internal error : could not open localized title string !>", constraintTypeStr)
           showAlertDialog(title, message)
           false
       }
    }

    private fun buildExprObjectFromScript() : SingleKingConstraintBooleanExpr {
        val inputStream = CharStreams.fromString(PositionGeneratorValuesHolder.playerKingConstraintScript)
        val lexer = BailSingleKingConstraintLexer(inputStream)
        val tokens = CommonTokenStream(lexer)
        val parser = SingleKingConstraintParser(tokens)
        parser.errorHandler = PositionConstraintBailErrorStrategy()
        val tree = parser.singleKingConstraint()
        SingleKingConstraintBuilder.clearVariables()
        return SingleKingConstraintBuilder.visit(tree) as SingleKingConstraintBooleanExpr
    }

    private fun testCanEvaluateExpressionWithDefaultVariablesSetAndShowEventualError(expr: SingleKingConstraintBooleanExpr): Boolean {
        return try {
            val samplesIntValues = mapOf(
                    "file" to PositionConstraints.FileA,
                    "rank" to PositionConstraints.Rank7
            )
            val sampleBooleanValues = mapOf("playerHasWhite" to true)

            val intValues = samplesIntValues.toMutableMap()
            val booleanValues = sampleBooleanValues.toMutableMap()

            val variables = SingleKingConstraintBuilder.getVariables()
            // We must evaluate all variables before evaluating the final script expression
            variables.forEach {
                when (it.value) {
                    is SingleKingConstraintNumericExpr -> {
                        if (samplesIntValues.containsKey(it.name)) {
                            val errorFormat = resources.getString(R.string.parser_overriding_predefined_variable)
                            throw ParseCancellationException(String.format(errorFormat, it.name))
                        }
                        else intValues[it.name] = eval(expr = it.value, intValues = intValues, booleanValues = booleanValues)
                    }
                    is SingleKingConstraintBooleanExpr -> {
                        if (sampleBooleanValues.containsKey(it.name)) {
                            val errorFormat = resources.getString(R.string.parser_overriding_predefined_variable)
                            throw ParseCancellationException(String.format(errorFormat, it.name))
                        }
                        else booleanValues[it.name] = eval(expr = it.value, intValues = intValues, booleanValues = booleanValues)
                    }
                }

            }


            eval(expr = expr, intValues = intValues, booleanValues = booleanValues)
            true
        }
        catch (ex: VariableIsNotAffectedException) {
            val messageFormat = activity?.resources?.getString(R.string.parser_variable_not_affected)
            val message = String.format(messageFormat ?: "<Internal error : could not open format string !>", ex.name)

            val constraintTypeStr = activity?.resources?.getString(R.string.player_king_constraints)

            val titleFormat = activity?.resources?.getString(R.string.parse_error_dialog_title)
            val title = String.format(titleFormat ?: "<Internal error : could not open localized title string !>", constraintTypeStr)
            showAlertDialog(title, message)

            false
        }
    }

    private fun showAlertDialog(title : String, message: String) {
        val dialog = AlertDialog.Builder(activity!!).create()
        dialog.setTitle(title)
        dialog.setMessage(message)
        val buttonText = activity?.resources?.getString(R.string.OK)
        dialog.setButton(AlertDialog.BUTTON_NEUTRAL, buttonText) { currDialog, _ -> currDialog?.dismiss() }

        dialog.show()
    }

    companion object {
        fun newInstance(): PlayerKingConstraintEditorFragment {
            return PlayerKingConstraintEditorFragment()
        }

        const val SCRIPT_KEY = "Script"
    }
}