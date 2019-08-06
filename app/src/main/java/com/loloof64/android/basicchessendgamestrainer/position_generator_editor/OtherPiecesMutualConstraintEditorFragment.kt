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
import androidx.fragment.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.loloof64.android.basicchessendgamestrainer.MyApplication
import com.loloof64.android.basicchessendgamestrainer.PositionGeneratorEditorActivity
import com.loloof64.android.basicchessendgamestrainer.PositionGeneratorValuesHolder
import kotlinx.android.synthetic.main.fragment_editing_other_pieces_mutual_constraint.*
import com.loloof64.android.basicchessendgamestrainer.R
import com.loloof64.android.basicchessendgamestrainer.exercise_chooser.PositionConstraints
import com.loloof64.android.basicchessendgamestrainer.position_generator_editor.script_language.ScriptLanguageBuilder
import java.lang.ref.WeakReference

class OtherPiecesMutualConstraintEditorFragment : Fragment() {

    private var spinnerPiecesKindValues = listOf<PieceKind>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_editing_other_pieces_mutual_constraint, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        generator_editor_spinner_other_piece_mutual_constraint.onItemSelectedListener =
                OtherPiecesMutualConstraintEditorSpinnerSelectionListener(parent = this)

        generator_editor_field_other_piece_mutual_constraint.text.clear()
        generator_editor_field_other_piece_mutual_constraint.isEnabled = false

        generator_editor_field_other_piece_mutual_constraint.addTextChangedListener(
                OtherPiecesMutualConstraintEditorFieldTextWatcher(parent = this)
        )

        button_check_other_piece_mutual_constraint.setOnClickListener(CheckScriptButtonOnClickListener(activity as PositionGeneratorEditorActivity,
                {parentActivity ->
                    if (checkIfAllScriptAreValidAndShowEventualError()) {
                        val message = parentActivity.resources.getString(R.string.script_valid)
                        parentActivity.showAlertDialog(title = "", message = message)
                    }
                }))

        updatePieceKindsSpinnerAndLoadFirstScriptIfAny()
    }

    fun checkIfAllScriptAreValidAndShowEventualError(): Boolean {
        val samplesIntValues = mapOf(
                "firstPieceFile" to PositionConstraints.FileB,
                "firstPieceRank" to PositionConstraints.Rank1,
                "secondPieceFile" to PositionConstraints.FileD,
                "secondPieceRank" to PositionConstraints.Rank1
        )
        val sampleBooleanValues = mapOf<String, Boolean>()

        var allScriptsAreValid = true
        PositionGeneratorValuesHolder.otherPiecesMutualConstraintScripts.keys.forEach {
            allScriptsAreValid = allScriptsAreValid && ScriptLanguageBuilder.checkIfScriptIsValidAndShowFirstEventualError(
                    script = PositionGeneratorValuesHolder.otherPiecesMutualConstraintScripts[it]!!,
                    scriptSectionTitle = MyApplication.appContext.getString(R.string.other_pieces_mutual_constraints_script_error_title, it.toLocalString()) ?: "#[TitleFetchingError]",
                    sampleIntValues = samplesIntValues,
                    sampleBooleanValues = sampleBooleanValues
            )
        }

        return allScriptsAreValid
    }

    private fun updatePieceKindsSpinnerAndLoadFirstScriptIfAny() {
        loadSpinnerTitles()
        if (PositionGeneratorValuesHolder.otherPiecesMutualConstraintScripts.isNotEmpty()) {
            generator_editor_spinner_other_piece_mutual_constraint.setSelection(0)
        }
    }

    private fun loadSpinnerTitles() {
        val pieceTypesStrings = resources.getStringArray(R.array.piece_type_spinner)
        val sideStrings = resources.getStringArray(R.array.player_computer_spinner)
        spinnerPiecesKindValues = PositionGeneratorValuesHolder.otherPiecesCount.map { it.pieceKind }
        val otherPiecesKinds = spinnerPiecesKindValues.map {
            "${pieceTypesStrings[it.pieceType.ordinal]} ${sideStrings[it.side.ordinal]}"
        }.toTypedArray()

        val spinnerAdapter = ArrayAdapter(activity!!, android.R.layout.simple_spinner_item, otherPiecesKinds)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        generator_editor_spinner_other_piece_mutual_constraint.adapter = spinnerAdapter
    }

    fun loadScriptMatchingSpinnerSelection() {
        if (spinnerPiecesKindValues.isNotEmpty()) {
            val selectedItemPosition = generator_editor_spinner_other_piece_mutual_constraint.selectedItemPosition
            val selectedPieceKind = spinnerPiecesKindValues[selectedItemPosition]
            val associatedScript = PositionGeneratorValuesHolder.otherPiecesMutualConstraintScripts[selectedPieceKind] ?: ""

            generator_editor_field_other_piece_mutual_constraint.isEnabled = true
            generator_editor_field_other_piece_mutual_constraint.setText(associatedScript)
        }
    }

    fun getEditedPieceKind() : PieceKind? {
        if (generator_editor_spinner_other_piece_mutual_constraint.selectedItemPosition == Spinner.INVALID_POSITION) return null
        return spinnerPiecesKindValues[generator_editor_spinner_other_piece_mutual_constraint.selectedItemPosition]
    }

    fun clearScriptField() {
        generator_editor_field_other_piece_mutual_constraint.text.clear()
    }

    companion object {
        fun newInstance(): OtherPiecesMutualConstraintEditorFragment {
            return OtherPiecesMutualConstraintEditorFragment()
        }
    }

}

class OtherPiecesMutualConstraintEditorSpinnerSelectionListener(parent: OtherPiecesMutualConstraintEditorFragment):
        AdapterView.OnItemSelectedListener {
    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        setScriptToCurrent()
    }

    private fun setScriptToCurrent(){
        parentRef.get()?.loadScriptMatchingSpinnerSelection()
    }

    private val parentRef = WeakReference(parent)
}

class OtherPiecesMutualConstraintEditorFieldTextWatcher(parent: OtherPiecesMutualConstraintEditorFragment) : TextWatcher {


    override fun afterTextChanged(s: Editable?) {
        val editedPieceKind = parentRef.get()?.getEditedPieceKind()
        if (editedPieceKind != null){
            PositionGeneratorValuesHolder.otherPiecesMutualConstraintScripts[editedPieceKind] = s?.toString()
                    ?: "<Internal error : could not read updated other piece global constraint field>"
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        // not needed
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        // not needed
    }

    private val parentRef = WeakReference(parent)
}
