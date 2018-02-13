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
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.loloof64.android.basicchessendgamestrainer.*
import com.loloof64.android.basicchessendgamestrainer.exercise_chooser.PositionConstraints
import com.loloof64.android.basicchessendgamestrainer.position_generator_editor.script_language.ScriptLanguageBuilder
import com.loloof64.android.basicchessendgamestrainer.position_generator_editor.script_language.VariableIsNotAffectedException
import kotlinx.android.synthetic.main.fragment_editing_kings_mutual_constraint.*
import org.antlr.v4.runtime.misc.ParseCancellationException
import org.greenrobot.eventbus.EventBus

class KingsMutualConstraintEditorFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_editing_kings_mutual_constraint, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        button_check_kings_mutual_constraint.setOnClickListener(CheckScriptButtonOnClickListener(
                activity as PositionGeneratorEditorActivity,
                {
                    parentActivity ->
                    if (checkIsScriptIsValidAndShowEventualError()) {
                        val message = parentActivity.resources.getString(R.string.script_valid)
                        parentActivity.showAlertDialog(title = "", message = message)
                    }
                }
        ))

        generator_editor_field_kings_mutual_constraint.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(str: Editable?) {
                PositionGeneratorValuesHolder.kingsMutualConstraintScript = str?.toString() ?:
                        "<Internal error : could not read updated player king constraint field>"
            }

            override fun beforeTextChanged(str: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(str: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        generator_editor_field_kings_mutual_constraint.setText(PositionGeneratorValuesHolder.kingsMutualConstraintScript)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KingsMutualConstraintEditorFragment.SCRIPT_KEY, generator_editor_field_kings_mutual_constraint.text.toString())
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState != null) {
            generator_editor_field_kings_mutual_constraint.text.clear()
            generator_editor_field_kings_mutual_constraint.text.append(savedInstanceState.getString(KingsMutualConstraintEditorFragment.SCRIPT_KEY))
        }
    }

    fun checkIsScriptIsValidAndShowEventualError(): Boolean {
        // TODO refactor into a single method point
        val scriptIsEmpty = PositionGeneratorValuesHolder.kingsMutualConstraintScript.isEmpty()
        if (scriptIsEmpty){
            val resources = MyApplication.appContext.resources
            val title = resources.getString(R.string.kings_mutual_constraints)
            val errorMessage = resources.getString(R.string.empty_script_error)

            EventBus.getDefault().post(OnMessageToShowInDialogEvent(title, errorMessage))
            return true
        }

        val samplesIntValues = mapOf(
                "playerKingFile" to PositionConstraints.FileA,
                "playerKingRank" to PositionConstraints.Rank7,
                "computerKingFile" to PositionConstraints.FileD,
                "computerKingRank" to PositionConstraints.Rank5
        )
        val sampleBooleanValues = mapOf("playerHasWhite" to true)

        return try {
            ScriptLanguageBuilder.checkIsScriptStringIsValid(
                    scriptString = PositionGeneratorValuesHolder.kingsMutualConstraintScript,
                    sampleIntValues = samplesIntValues,
                    sampleBooleanValues = sampleBooleanValues
            )
            true
        }
        catch (ex: VariableIsNotAffectedException) {
            val resources = MyApplication.appContext.resources
            val messageFormat = resources.getString(R.string.parser_variable_not_affected)
            val message = String.format(messageFormat ?: "<Internal error : could not open format string !>", ex.name)

            val constraintTypeStr = resources.getString(R.string.kings_mutual_constraints)

            val titleFormat = resources.getString(R.string.parse_error_dialog_title)
            val title = String.format(titleFormat ?: "<Internal error : could not open localized title string !>", constraintTypeStr)
            EventBus.getDefault().post(OnMessageToShowInDialogEvent(title, message))

            false
        }
        catch (ex: ParseCancellationException){
            val message = ex.message ?: "<Internal error : could not get ParseCancellationException message !>"
            val resources = MyApplication.appContext.resources
            val constraintTypeStr = resources.getString(R.string.kings_mutual_constraints)
            val titleFormat = resources.getString(R.string.parse_error_dialog_title)
            val title = String.format(titleFormat ?: "<Internal error : could not open localized title string !>", constraintTypeStr)
            EventBus.getDefault().post(OnMessageToShowInDialogEvent(title, message))
            false
        }
    }

    companion object {
        fun newInstance(): KingsMutualConstraintEditorFragment {
            return KingsMutualConstraintEditorFragment()
        }

        const val SCRIPT_KEY = "Script"
    }

}
