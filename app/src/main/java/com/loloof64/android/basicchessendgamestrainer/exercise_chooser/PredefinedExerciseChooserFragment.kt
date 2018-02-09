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
import android.view.*
import com.loloof64.android.basicchessendgamestrainer.PlayingActivity
import com.loloof64.android.basicchessendgamestrainer.R
import com.loloof64.android.basicchessendgamestrainer.playing_activity.EngineInteraction
import kotlinx.android.synthetic.main.fragment_predefined_exercise_chooser.*

class PredefinedExerciseChooserFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_predefined_exercise_chooser, container, false)
        EngineInteraction.copyStockfishIntoInternalMemoryIfNecessary()
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        predefinedExercisesListView.layoutManager = LinearLayoutManager(activity)
        predefinedExercisesListView.adapter = PredefinedExercisesListAdapter(availableGenerators, object : ItemClickListener {
            override fun onClick(position: Int) {
                val intent = Intent(activity, PlayingActivity::class.java)
                intent.putExtra(PlayingActivity.usingCustomGeneratorConstraintsKey, false)
                intent.putExtra(PlayingActivity.predefinedGeneratorIndexKey, position)
                startActivity(intent)
            }
        })

    }

    companion object {
        fun newInstance(): PredefinedExerciseChooserFragment {
            return PredefinedExerciseChooserFragment()
        }
    }

}
