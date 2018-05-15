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
import kotlinx.android.synthetic.main.fragment_editing_computer_king_constraint.*

class ComputerKingConstraintEditorFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_editing_computer_king_constraint, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        button_check_computer_king_constraint.setOnClickListener(CheckScriptButtonOnClickListener(
                activity as PositionGeneratorEditorActivity,
                {parentActivity ->
                    if (checkIfScriptIsValidAndShowEventualError()) {
                        val message = parentActivity.resources.getString(R.string.script_valid)
                        parentActivity.showAlertDialog(title = "", message = message)
                    }
                }))

        generator_editor_field_computer_king_constraint.setText(PositionGeneratorValuesHolder.computerKingConstraintScript)
        generator_editor_field_computer_king_constraint.addTextChangedListener(ComputerKingConstraintFragmentTextWatcher())
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

     fun checkIfScriptIsValidAndShowEventualError(): Boolean {

        val samplesIntValues = mapOf(
                "file" to PositionConstraints.FileA,
                "rank" to PositionConstraints.Rank7
        )
        val sampleBooleanValues = mapOf("playerHasWhite" to true)

        return ScriptLanguageBuilder.checkIfScriptIsValidAndShowFirstEventualError(
                script = PositionGeneratorValuesHolder.computerKingConstraintScript,
                scriptSectionTitle = MyApplication.appContext.getString(R.string.computer_king_constraints) ?: "#[TitleFetchingError]",
                sampleIntValues = samplesIntValues,
                sampleBooleanValues = sampleBooleanValues
        )
    }

    fun clearScriptField() {
        generator_editor_field_computer_king_constraint.text.clear()
    }

    companion object {
        fun newInstance(): ComputerKingConstraintEditorFragment {
            return ComputerKingConstraintEditorFragment()
        }

        const val SCRIPT_KEY = "Script"
    }
}

class ComputerKingConstraintFragmentTextWatcher : TextWatcher {
    override fun afterTextChanged(str: Editable?) {
        PositionGeneratorValuesHolder.computerKingConstraintScript = str?.toString() ?:
                "<Internal error : could not read updated computer king constraint field>"
    }

    override fun beforeTextChanged(str: CharSequence?, start: Int, count: Int, after: Int) {
        // not needed
    }

    override fun onTextChanged(str: CharSequence?, start: Int, before: Int, count: Int) {
        // not needed
    }
}