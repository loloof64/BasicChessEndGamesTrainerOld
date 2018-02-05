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

package com.loloof64.android.basicchessendgamestrainer

import android.support.v7.app.AppCompatActivity

import android.support.v4.app.Fragment
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.content.Context
import android.support.v7.widget.ThemedSpinnerAdapter
import android.content.res.Resources.Theme
import com.loloof64.android.basicchessendgamestrainer.position_generator_editor.*
import com.nightonke.boommenu.BoomButtons.OnBMClickListener
import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton

import kotlinx.android.synthetic.main.activity_position_generator_editor.*
import kotlinx.android.synthetic.main.position_generator_editor_list_item.view.*


data class BoomButtonParameters(val textId: Int, val iconId: Int, val colorId: Int, val listener: OnBMClickListener)

class PositionGeneratorEditorActivity : AppCompatActivity() {

    companion object {
        val allFragments = arrayOf(
                PlayerKingConstraintEditorFragment.newInstance(),
                ComputerKingConstraintEditorFragment.newInstance(),
                KingsMutualConstraintEditorFragment.newInstance(),
                OtherPiecesCountConstraintEditorFragment.newInstance(),
                OtherPiecesGlobalConstraintEditorFragment.newInstance(),
                OtherPiecesMutualConstraintEditorFragment.newInstance(),
                OtherPiecesIndexedConstraintEditorFragment.newInstance()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_position_generator_editor)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val boomMenuButtonPiecesNumber = position_generator_activity_boom_menu_button.piecePlaceEnum.pieceNumber()
        (0 until boomMenuButtonPiecesNumber).forEach {pieceIndex ->
            val buttonParameters = when(pieceIndex) {
                0 -> BoomButtonParameters(
                        textId = R.string.action_save_generator,
                        iconId = R.mipmap.ic_action_save,
                        colorId = R.color.position_generator_activity_boom_menu_action_save,
                        listener = OnBMClickListener {
                            /*
                                                TODO check all scripts and
                                                create file and exit if success
                                                or inform user of errors
                                                */
                        }
                )
                1 -> BoomButtonParameters(
                        textId = R.string.action_cancel_generator,
                        iconId = R.mipmap.ic_action_cancel,
                        colorId = R.color.position_generator_activity_boom_menu_action_cancel,
                        listener = OnBMClickListener {
                            /*
                                                TODO ask for confirmation and proceed/stay accordingly
                                                 */
                        }
                )
                else -> throw RuntimeException("Unexpected pieceIndex value $pieceIndex")
            }
            val builder = TextInsideCircleButton.Builder().
                    listener(buttonParameters.listener).
                    normalImageRes(buttonParameters.iconId).
                    normalTextRes(buttonParameters.textId).
                    normalColorRes(buttonParameters.colorId).
                    textSize(20)
            position_generator_activity_boom_menu_button.addBuilder(builder)
        }

        spinner.adapter = MyAdapter(
                toolbar.context,
                arrayOf(R.string.player_king_constraints,
                        R.string.computer_king_constraints,
                        R.string.kings_mutual_constraints,
                        R.string.other_pieces_count_constraints,
                        R.string.other_pieces_global_constraints,
                        R.string.other_pieces_mutual_constraints,
                        R.string.other_pieces_indexed_constraints).map {
                    resources.getString(it)
                }.toTypedArray()
        )

        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val fragment: Fragment = if (position < allFragments.size) allFragments[position]
                    else throw IllegalArgumentException("No fragment defined for position $position !")
                supportFragmentManager.beginTransaction()
                        .replace(R.id.container, fragment)
                        .commit()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

    }

    private class MyAdapter(context: Context, objects: Array<String>) : ArrayAdapter<String>(context, R.layout.position_generator_editor_list_item, objects), ThemedSpinnerAdapter {
        private val mDropDownHelper: ThemedSpinnerAdapter.Helper = ThemedSpinnerAdapter.Helper(context)

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view: View

            if (convertView == null) {
                // Inflate the drop down using the helper's LayoutInflater
                val inflater = mDropDownHelper.dropDownViewInflater
                view = inflater.inflate(R.layout.position_generator_editor_list_item, parent, false)
            } else {
                view = convertView
            }

            view.text1.text = getItem(position)

            return view
        }

        override fun getDropDownViewTheme(): Theme? {
            return mDropDownHelper.dropDownViewTheme
        }

        override fun setDropDownViewTheme(theme: Theme?) {
            mDropDownHelper.dropDownViewTheme = theme
        }
    }
}
