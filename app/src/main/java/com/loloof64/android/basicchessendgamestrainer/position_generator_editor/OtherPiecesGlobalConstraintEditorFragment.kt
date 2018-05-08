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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.loloof64.android.basicchessendgamestrainer.PositionGeneratorValuesHolder
import com.loloof64.android.basicchessendgamestrainer.R
import kotlinx.android.synthetic.main.fragment_editing_other_pieces_global_constraint.*
import java.lang.ref.WeakReference
import java.util.logging.Logger

class OtherPiecesGlobalConstraintEditorFragment : Fragment() {

    private var spinnerPiecesKindValues = listOf<PieceKind>()
    private var lastSpinnerSelectedItem : PieceKind? = null
    private val scriptsByPieceKind = mutableMapOf<PieceKind, String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_editing_other_pieces_global_constraint, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        generator_editor_spinner_other_piece_global_constraint.onItemSelectedListener =
                OtherPiecesGlobalConstraintEditorSpinnerSelectionListener(this)
        generator_editor_field_other_piece_global_constraint.onFocusChangeListener =
                OtherPiecesGlobalConstraintEditorScriptFieldFocusListener(this)

        updatePieceKindsSpinnerAndLoadFirstScriptIfAny()
    }

    private fun updatePieceKindsSpinnerAndLoadFirstScriptIfAny() {
        loadSpinnerTitles()
        loadScriptMatchingSpinnerSelectionOrDisableAndClearField()
        if (scriptsByPieceKind.isNotEmpty()) {
            generator_editor_spinner_other_piece_global_constraint.setSelection(0)
        }
    }

    companion object {
        fun newInstance(): OtherPiecesGlobalConstraintEditorFragment {
            return OtherPiecesGlobalConstraintEditorFragment()
        }
    }

    fun deleteScriptAssociatedWithPieceKind(kind: PieceKind){
        scriptsByPieceKind.remove(kind)
    }

    private fun loadSpinnerTitles() {
        val pieceTypesStrings = resources.getStringArray(R.array.piece_type_spinner)
        val sideStrings = resources.getStringArray(R.array.player_computer_spinner)
        spinnerPiecesKindValues = PositionGeneratorValuesHolder.otherPiecesCount.map { it.pieceKind }
        val otherPiecesKinds = spinnerPiecesKindValues.map {
            "${pieceTypesStrings[it.pieceType.ordinal]} ${sideStrings[it.side.ordinal]}"
        }.toTypedArray()

        val spinnerAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, otherPiecesKinds)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        generator_editor_spinner_other_piece_global_constraint.adapter = spinnerAdapter

        setEmptyScriptsWheneverScriptMissingForASpinnerKey()
    }

    private fun setEmptyScriptsWheneverScriptMissingForASpinnerKey(){
        for (currentSpinnerKey in spinnerPiecesKindValues){
            if ( ! scriptsByPieceKind.containsKey(currentSpinnerKey) ){
                scriptsByPieceKind[currentSpinnerKey] = ""
            }
        }
    }

    fun loadScriptMatchingSpinnerSelectionOrDisableAndClearField(){
        if (spinnerPiecesKindValues.isEmpty()){
            generator_editor_field_other_piece_global_constraint.text.clear()
            generator_editor_field_other_piece_global_constraint.isEnabled = false
        }
        else {
            val selectedItemPosition = generator_editor_spinner_other_piece_global_constraint.
                    selectedItemPosition
            val selectedPieceKind = spinnerPiecesKindValues[selectedItemPosition]
            val associatedScript = scriptsByPieceKind[selectedPieceKind]

            generator_editor_field_other_piece_global_constraint.isEnabled = true
            generator_editor_field_other_piece_global_constraint.setText(associatedScript)

            /////////////////////////////////////////////////
            Logger.getLogger("loloof64").info("selected piece kind : $selectedPieceKind")
            Logger.getLogger("loloof64").info("associated script : $associatedScript")
            Logger.getLogger("loloof64").info("current script field value : ${generator_editor_field_other_piece_global_constraint.text}")
            /////////////////////////////////////////////////
        }
    }

    fun retainCurrentScript() {
        if (lastSpinnerSelectedItem != null){
            scriptsByPieceKind[lastSpinnerSelectedItem!!] = generator_editor_field_other_piece_global_constraint.text.toString()
        }
    }

    fun removeScriptFieldFocus() {
        generator_editor_field_other_piece_global_constraint.clearFocus()
    }

    fun updateLastSelectedItem() {
        val spinnerSelectedItemPosition = generator_editor_spinner_other_piece_global_constraint.selectedItemPosition
        lastSpinnerSelectedItem =
                if (spinnerSelectedItemPosition == Spinner.INVALID_POSITION) null
                else spinnerPiecesKindValues[spinnerSelectedItemPosition]
    }

}

class OtherPiecesGlobalConstraintEditorSpinnerSelectionListener(parent: OtherPiecesGlobalConstraintEditorFragment):
        AdapterView.OnItemSelectedListener {
    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        saveLastScriptAndSetToCurrent()
    }

    private fun saveLastScriptAndSetToCurrent(){
        parentRef.get()?.removeScriptFieldFocus()
        parentRef.get()?.updateLastSelectedItem()
        parentRef.get()?.loadScriptMatchingSpinnerSelectionOrDisableAndClearField()
    }

    private val parentRef = WeakReference(parent)
}

class OtherPiecesGlobalConstraintEditorScriptFieldFocusListener(parent: OtherPiecesGlobalConstraintEditorFragment):
    View.OnFocusChangeListener
{
    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (!hasFocus){
            parentRef.get()?.retainCurrentScript()
        }
    }

    private val parentRef = WeakReference(parent)
}
