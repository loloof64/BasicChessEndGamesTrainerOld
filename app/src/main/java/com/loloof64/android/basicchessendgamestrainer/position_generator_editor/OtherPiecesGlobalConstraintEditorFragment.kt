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
import android.widget.ArrayAdapter
import com.loloof64.android.basicchessendgamestrainer.PositionGeneratorValuesHolder
import com.loloof64.android.basicchessendgamestrainer.R
import kotlinx.android.synthetic.main.fragment_editing_other_pieces_global_constraint.*

class OtherPiecesGlobalConstraintEditorFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_editing_other_pieces_global_constraint, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadSpinnerTitles()
    }

    companion object {
        fun newInstance(): OtherPiecesGlobalConstraintEditorFragment {
            return OtherPiecesGlobalConstraintEditorFragment()
        }
    }

    private fun loadSpinnerTitles() {
        val pieceTypesStrings = resources.getStringArray(R.array.piece_type_spinner)
        val sideStrings = resources.getStringArray(R.array.player_computer_spinner)
        val otherPiecesKinds = PositionGeneratorValuesHolder.otherPiecesCount.map { it.pieceKind }.map {
            "${pieceTypesStrings[it.pieceType.ordinal]} ${sideStrings[it.side.ordinal]}"
        }.toTypedArray()

        val spinnerAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, otherPiecesKinds)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        generator_editor_spinner_other_piece_global_constraint.adapter = spinnerAdapter
    }

}