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
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.loloof64.android.basicchessendgamestrainer.R
import kotlinx.android.synthetic.main.fragment_editing_other_pieces_count_constraint.*
import java.lang.ref.WeakReference

class OtherPiecesCountConstraintEditorFragment: Fragment() {

    private lateinit var listViewAdapter:OtherPiecesKindCountListArrayAdapter

    fun getListViewAdapter(): OtherPiecesKindCountListArrayAdapter = listViewAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_editing_other_pieces_count_constraint, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        listViewAdapter = OtherPiecesKindCountListArrayAdapter(activity!!)
        recycler_view_other_pieces_count.layoutManager = LinearLayoutManager(activity!!)
        recycler_view_other_pieces_count.adapter = listViewAdapter

        button_add_piece_kind_count.setOnClickListener(ButtonAddPieceKindCountOnClickListener(
                parentFragment = this
        ))
    }

    fun getCurrentDefinedPieceCount(): PieceKindCount {
        return PieceKindCount(
                pieceKind = PieceKind(
                        pieceType = PieceType.values()[spinner_add_piece_kind_type.selectedItemPosition],
                        side = Side.values()[spinner_add_piece_kind_owner.selectedItemPosition]
                ),
                count = 1  + spinner_add_piece_kind_count.selectedItemPosition
        )
    }

    companion object {
        fun newInstance(): OtherPiecesCountConstraintEditorFragment {
            return OtherPiecesCountConstraintEditorFragment()
        }
    }

}

class ButtonAddPieceKindCountOnClickListener(parentFragment : OtherPiecesCountConstraintEditorFragment) : View.OnClickListener {

    private val parentFragmentRef = WeakReference(parentFragment)

    override fun onClick(v: View?) {
        if (parentFragmentRef.get() != null) {
            parentFragmentRef.get()!!.getListViewAdapter().tryToAddPieceCount(parentFragmentRef.get()!!.getCurrentDefinedPieceCount())
        }
    }
}