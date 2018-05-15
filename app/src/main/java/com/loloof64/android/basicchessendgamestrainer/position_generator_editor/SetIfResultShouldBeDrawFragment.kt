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
import com.loloof64.android.basicchessendgamestrainer.PositionGeneratorValuesHolder
import com.loloof64.android.basicchessendgamestrainer.R
import kotlinx.android.synthetic.main.fragment_set_if_result_should_be_draw.*

class SetIfResultShouldBeDrawFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_set_if_result_should_be_draw, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        result_should_be_draw_button.setOnClickListener {
            PositionGeneratorValuesHolder.resultShouldBeDraw = true
        }

        result_should_not_be_draw_button.setOnClickListener {
            PositionGeneratorValuesHolder.resultShouldBeDraw = false
        }

        should_result_be_draw_buttons_group.clearCheck()
        if (PositionGeneratorValuesHolder.resultShouldBeDraw) {
            result_should_be_draw_button.isChecked = true
        }
        else {
            result_should_not_be_draw_button.isChecked = true
        }
    }

    fun resetChoice() {
        result_should_be_draw_button.isChecked = false
    }

    companion object {
        fun newInstance(): SetIfResultShouldBeDrawFragment {
            return SetIfResultShouldBeDrawFragment()
        }
    }
}