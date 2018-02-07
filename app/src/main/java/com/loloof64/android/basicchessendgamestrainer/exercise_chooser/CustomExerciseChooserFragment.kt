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

package com.loloof64.android.basicchessendgamestrainer.exercise_chooser

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.loloof64.android.basicchessendgamestrainer.PositionGeneratorEditorActivity
import com.loloof64.android.basicchessendgamestrainer.R
import kotlinx.android.synthetic.main.fragment_custom_exercise_chooser.*

class CustomExerciseChooserFragment : Fragment() {
    private lateinit var adapter: CustomExercisesListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_custom_exercise_chooser, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        customExercisesListView.layoutManager = LinearLayoutManager(activity)
        adapter = CustomExercisesListAdapter(CustomExerciseChooserFragmentItemClickListener(),
                CustomExerciseChooserFragmentItemLongClickListener())
        customExercisesListView.adapter = adapter

        fab_add_custom_exercise.setOnClickListener {
            val intent = Intent(activity, PositionGeneratorEditorActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        adapter.loadScriptFilesList()
    }

    companion object {
        fun newInstance(): CustomExerciseChooserFragment {
            return CustomExerciseChooserFragment()
        }
    }
}

class CustomExerciseChooserFragmentItemClickListener : ItemClickListener {
    override fun onClick(position: Int) {
        //TODO launch exercise from the definition
    }
}

class CustomExerciseChooserFragmentItemLongClickListener: ItemLongClickListener {
    override fun onLongClick(position: Int) {
        //TODO launch item menu
    }
}