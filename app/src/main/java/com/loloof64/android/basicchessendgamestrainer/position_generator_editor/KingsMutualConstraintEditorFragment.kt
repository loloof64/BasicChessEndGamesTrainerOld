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
import kotlinx.android.synthetic.main.fragment_editing_kings_mutual_constraint.*

class KingsMutualConstraintEditorFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_editing_kings_mutual_constraint, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        button_check_kings_mutual_constraint.setOnClickListener(CheckScriptButtonOnClickListener(
                activity as PositionGeneratorEditorActivity,
                {
                    parentActivity ->
                    if (checkIfScriptIsValidAndShowEventualError()) {
                        val message = parentActivity.resources.getString(R.string.script_valid)
                        parentActivity.showAlertDialog(title = "", message = message)
                    }
                }
        ))

        generator_editor_field_kings_mutual_constraint.setText(PositionGeneratorValuesHolder.kingsMutualConstraintScript)
        generator_editor_field_kings_mutual_constraint.addTextChangedListener(KingsMutualConstraintEditorTextWatcher())
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

    fun checkIfScriptIsValidAndShowEventualError(): Boolean {

        val samplesIntValues = mapOf(
                "playerKingFile" to PositionConstraints.FileA,
                "playerKingRank" to PositionConstraints.Rank7,
                "computerKingFile" to PositionConstraints.FileD,
                "computerKingRank" to PositionConstraints.Rank5
        )
        val sampleBooleanValues = mapOf("playerHasWhite" to true)

        return ScriptLanguageBuilder.checkIfScriptIsValidAndShowFirstEventualError(
                script = PositionGeneratorValuesHolder.kingsMutualConstraintScript,
                scriptSectionTitle = MyApplication.appContext.getString(R.string.kings_mutual_constraints) ?: "#[TitleFetchingError]",
                sampleIntValues = samplesIntValues,
                sampleBooleanValues = sampleBooleanValues
        )

    }

    companion object {
        fun newInstance(): KingsMutualConstraintEditorFragment {
            return KingsMutualConstraintEditorFragment()
        }

        const val SCRIPT_KEY = "Script"
    }

}

class KingsMutualConstraintEditorTextWatcher : TextWatcher {
    override fun afterTextChanged(str: Editable?) {
        PositionGeneratorValuesHolder.kingsMutualConstraintScript = str?.toString() ?:
                "<Internal error : could not read updated player king constraint field>"
    }

    override fun beforeTextChanged(str: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(str: CharSequence?, start: Int, before: Int, count: Int) {

    }
}
