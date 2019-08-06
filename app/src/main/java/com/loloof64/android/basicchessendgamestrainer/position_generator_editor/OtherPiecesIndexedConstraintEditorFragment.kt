package com.loloof64.android.basicchessendgamestrainer.position_generator_editor

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.loloof64.android.basicchessendgamestrainer.R

class OtherPiecesIndexedConstraintEditorFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_editing_other_pieces_indexed_constraint, container, false)
    }

    companion object {
        fun newInstance(): OtherPiecesIndexedConstraintEditorFragment {
            return OtherPiecesIndexedConstraintEditorFragment()
        }
    }

}