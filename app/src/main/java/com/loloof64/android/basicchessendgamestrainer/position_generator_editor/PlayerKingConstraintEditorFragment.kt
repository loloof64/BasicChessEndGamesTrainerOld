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
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.loloof64.android.basicchessendgamestrainer.R
import com.loloof64.android.basicchessendgamestrainer.position_generator_editor.single_king_constraint.BailSingleKingConstraintLexer
import com.loloof64.android.basicchessendgamestrainer.position_generator_editor.single_king_constraint.antlr4.SingleKingConstraintParser
import org.antlr.v4.runtime.CharStreams
import kotlinx.android.synthetic.main.fragment_editing_player_king_constraint.*
import org.antlr.v4.runtime.CommonTokenStream

class PlayerKingConstraintEditorFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_editing_player_king_constraint, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        button_check_player_king_constraint.setOnClickListener {
            if (checkIsScriptIsValidAndShowEventualError()) Toast.makeText(activity, R.string.script_valid, Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkIsScriptIsValidAndShowEventualError(): Boolean {
       return try {
           val inputStream = CharStreams.fromString(generator_editor_field_player_king_constraint.text.toString())
           val lexer = BailSingleKingConstraintLexer(inputStream)
           val tokens = CommonTokenStream(lexer)
           val parser = SingleKingConstraintParser(tokens)
           parser.errorHandler = PositionConstraintBailErrorStrategy()
           parser.singleKingConstraint()
           true
       }
       catch (ex: Exception){
           val dialog = AlertDialog.Builder(activity!!).create()
           dialog.setTitle(R.string.parse_error_dialog_title)
           dialog.setMessage(ex.message)
           val buttonText = activity?.resources?.getString(R.string.OK)
           dialog.setButton(AlertDialog.BUTTON_NEUTRAL, buttonText) { currDialog, _ -> currDialog?.dismiss() }

           dialog.show()
           false
       }
    }

    companion object {
        fun newInstance(): PlayerKingConstraintEditorFragment {
            return PlayerKingConstraintEditorFragment()
        }
    }
}