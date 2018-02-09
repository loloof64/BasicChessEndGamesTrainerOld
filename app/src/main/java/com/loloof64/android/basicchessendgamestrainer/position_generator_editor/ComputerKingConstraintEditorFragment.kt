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
import com.loloof64.android.basicchessendgamestrainer.position_generator_editor.single_king_constraint.SingleKingConstraintBuilder
import com.loloof64.android.basicchessendgamestrainer.position_generator_editor.single_king_constraint.VariableIsNotAffectedException
import kotlinx.android.synthetic.main.fragment_editing_computer_king_constraint.*
import org.antlr.v4.runtime.misc.ParseCancellationException
import org.greenrobot.eventbus.EventBus

class ComputerKingConstraintEditorFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_editing_computer_king_constraint, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        button_check_computer_king_constraint.setOnClickListener {
            if (checkIsScriptIsValidAndShowEventualError()) {
                val parentActivity = activity as PositionGeneratorEditorActivity
                val message = parentActivity.resources.getString(R.string.script_valid)
                parentActivity.showAlertDialog(title = "", message = message)
            }
        }

        generator_editor_field_computer_king_constraint.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(str: Editable?) {
                PositionGeneratorValuesHolder.computerKingConstraintScript = str?.toString() ?:
                        "<Internal error : could not read updated computer king constraint field>"
            }

            override fun beforeTextChanged(str: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(str: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(ComputerKingConstraintEditorFragment.SCRIPT_KEY, generator_editor_field_computer_king_constraint.text.toString())
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState != null) {
            generator_editor_field_computer_king_constraint.text.clear()
            generator_editor_field_computer_king_constraint.text.append(savedInstanceState.getString(ComputerKingConstraintEditorFragment.SCRIPT_KEY))
        }
    }

    fun checkIsScriptIsValidAndShowEventualError(): Boolean {
        val scriptIsEmpty = PositionGeneratorValuesHolder.computerKingConstraintScript.isEmpty()
        if (scriptIsEmpty){
            val resources = MyApplication.appContext.resources
            val title = resources.getString(R.string.computer_king_constraints)
            val errorMessage = resources.getString(R.string.empty_script_error)

            EventBus.getDefault().post(onMessageToShowInDialogEvent(title, errorMessage))
            return true
        }

        val samplesIntValues = mapOf(
                "file" to PositionConstraints.FileA,
                "rank" to PositionConstraints.Rank7
        )
        val sampleBooleanValues = mapOf("playerHasWhite" to true)

        return try {
            SingleKingConstraintBuilder.checkIsScriptStringIsValid(
                    scriptString = PositionGeneratorValuesHolder.computerKingConstraintScript,
                    sampleIntValues = samplesIntValues,
                    sampleBooleanValues = sampleBooleanValues
            )
            true
        }
        catch (ex: VariableIsNotAffectedException) {
            val resources = MyApplication.appContext.resources
            val messageFormat = resources.getString(R.string.parser_variable_not_affected)
            val message = String.format(messageFormat ?: "<Internal error : could not open format string !>", ex.name)

            val constraintTypeStr = resources.getString(R.string.computer_king_constraints)

            val titleFormat = resources.getString(R.string.parse_error_dialog_title)
            val title = String.format(titleFormat ?: "<Internal error : could not open localized title string !>", constraintTypeStr)
            EventBus.getDefault().post(onMessageToShowInDialogEvent(title, message))

            false
        }
        catch (ex: ParseCancellationException){
            val message = ex.message ?: "<Internal error : could not get ParseCancellationException message !>"
            val resources = MyApplication.appContext.resources
            val constraintTypeStr = resources.getString(R.string.computer_king_constraints)
            val titleFormat = resources.getString(R.string.parse_error_dialog_title)
            val title = String.format(titleFormat ?: "<Internal error : could not open localized title string !>", constraintTypeStr)
            EventBus.getDefault().post(onMessageToShowInDialogEvent(title, message))
            false
        }
    }

    companion object {
        fun newInstance(): ComputerKingConstraintEditorFragment {
            return ComputerKingConstraintEditorFragment()
        }

        const val SCRIPT_KEY = "Script"
    }
}