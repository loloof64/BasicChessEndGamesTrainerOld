package com.loloof64.android.basicchessendgamestrainer.position_generator_editor

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.loloof64.android.basicchessendgamestrainer.MyApplication
import com.loloof64.android.basicchessendgamestrainer.PositionGeneratorEditorActivity
import com.loloof64.android.basicchessendgamestrainer.PositionGeneratorValuesHolder
import com.loloof64.android.basicchessendgamestrainer.R
import com.loloof64.android.basicchessendgamestrainer.exercise_chooser.PositionConstraints
import com.loloof64.android.basicchessendgamestrainer.position_generator_editor.script_language.ScriptLanguageBuilder
import kotlinx.android.synthetic.main.fragment_editing_other_pieces_indexed_constraint.*
import java.lang.ref.WeakReference

class OtherPiecesIndexedConstraintEditorFragment : Fragment() {

    private var spinnerPiecesKindValues = listOf<PieceKind>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_editing_other_pieces_indexed_constraint, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        generator_editor_spinner_other_piece_indexed_constraint.onItemSelectedListener =
                OtherPiecesIndexedConstraintEditorSpinnerSelectionListener(parent = this)

        generator_editor_field_other_piece_indexed_constraint.text.clear()
        generator_editor_field_other_piece_indexed_constraint.isEnabled = false

        generator_editor_field_other_piece_indexed_constraint.addTextChangedListener(
                OtherPiecesIndexedConstraintEditorFieldTextWatcher(parent = this)
        )

        button_check_other_piece_indexed_constraint.setOnClickListener(CheckScriptButtonOnClickListener(activity as PositionGeneratorEditorActivity,
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
                "apparitionIndex" to 0,
                "file" to PositionConstraints.FileA,
                "rank" to PositionConstraints.Rank2
        )
        val sampleBooleanValues = mapOf(
                "playerHasWhite" to true
        )

        var allScriptsAreValid = true
        PositionGeneratorValuesHolder.otherPiecesIndexedConstraintScripts.keys.forEach {
            allScriptsAreValid = allScriptsAreValid && ScriptLanguageBuilder.checkIfScriptIsValidAndShowFirstEventualError(
                    script = PositionGeneratorValuesHolder.otherPiecesIndexedConstraintScripts[it]!!,
                    scriptSectionTitle = MyApplication.appContext.getString(R.string.other_pieces_indexed_constraints_script_error_title, it.toLocalString()) ?: "#[TitleFetchingError]",
                    sampleIntValues = samplesIntValues,
                    sampleBooleanValues = sampleBooleanValues
            )
        }

        return allScriptsAreValid
    }

    private fun updatePieceKindsSpinnerAndLoadFirstScriptIfAny() {
        loadSpinnerTitles()
        if (PositionGeneratorValuesHolder.otherPiecesIndexedConstraintScripts.isNotEmpty()) {
            generator_editor_spinner_other_piece_indexed_constraint.setSelection(0)
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

        generator_editor_spinner_other_piece_indexed_constraint.adapter = spinnerAdapter
    }

    fun loadScriptMatchingSpinnerSelection() {
        if (spinnerPiecesKindValues.isNotEmpty()) {
            val selectedItemPosition = generator_editor_spinner_other_piece_indexed_constraint.selectedItemPosition
            val selectedPieceKind = spinnerPiecesKindValues[selectedItemPosition]
            val associatedScript = PositionGeneratorValuesHolder.otherPiecesIndexedConstraintScripts[selectedPieceKind] ?: ""

            generator_editor_field_other_piece_indexed_constraint.isEnabled = true
            generator_editor_field_other_piece_indexed_constraint.setText(associatedScript)
        }
    }

    fun getEditedPieceKind() : PieceKind? {
        if (generator_editor_spinner_other_piece_indexed_constraint.selectedItemPosition == Spinner.INVALID_POSITION) return null
        return spinnerPiecesKindValues[generator_editor_spinner_other_piece_indexed_constraint.selectedItemPosition]
    }

    fun clearScriptField() {
        generator_editor_field_other_piece_indexed_constraint.text.clear()
    }

    companion object {
        fun newInstance(): OtherPiecesIndexedConstraintEditorFragment {
            return OtherPiecesIndexedConstraintEditorFragment()
        }
    }

}

class OtherPiecesIndexedConstraintEditorSpinnerSelectionListener(parent: OtherPiecesIndexedConstraintEditorFragment):
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

class OtherPiecesIndexedConstraintEditorFieldTextWatcher(parent: OtherPiecesIndexedConstraintEditorFragment) : TextWatcher {


    override fun afterTextChanged(s: Editable?) {
        val editedPieceKind = parentRef.get()?.getEditedPieceKind()
        if (editedPieceKind != null){
            PositionGeneratorValuesHolder.otherPiecesIndexedConstraintScripts[editedPieceKind] = s?.toString()
                    ?: "<Internal error : could not read updated other piece indexed constraint field>"
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