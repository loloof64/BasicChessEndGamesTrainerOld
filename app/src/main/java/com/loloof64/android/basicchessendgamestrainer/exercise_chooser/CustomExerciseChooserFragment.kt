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

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.loloof64.android.basicchessendgamestrainer.BoomButtonParameters
import com.loloof64.android.basicchessendgamestrainer.MyApplication
import com.loloof64.android.basicchessendgamestrainer.PositionGeneratorEditorActivity
import com.loloof64.android.basicchessendgamestrainer.R
import com.loloof64.android.basicchessendgamestrainer.utils.FilesManager
import com.nightonke.boommenu.BoomButtons.OnBMClickListener
import com.nightonke.boommenu.BoomButtons.TextOutsideCircleButton
import kotlinx.android.synthetic.main.fragment_custom_exercise_chooser.*
import java.lang.ref.WeakReference

class CustomExerciseChooserFragment : Fragment() {
    private lateinit var adapter: CustomExercisesListAdapter

    fun getExercisesListAdapter(): CustomExercisesListAdapter = adapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_custom_exercise_chooser, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        customExercisesListView.layoutManager = LinearLayoutManager(activity)
        adapter = CustomExercisesListAdapter(CustomExerciseChooserFragmentItemClickListener(
                this, {
            fragment: Fragment, position: Int ->
                // TODO If it is an exercise run it, otherwise if parent folder, try to go up, else go into folder
        }),
                CustomExerciseChooserFragmentItemLongClickListener(this, {
                    fragment: Fragment, position: Int ->
                    /*
                     TODO if it is an exercise show popup menu with edit/rename/delete options
                     else it is not parent folder show popup menu with rename/delete options
                      */
                }))
        customExercisesListView.adapter = adapter

        (0 until fab_custom_exercise_menu.piecePlaceEnum.pieceNumber()).forEach { pieceIndex ->
            val buttonParameters = when(pieceIndex) {
                0 -> BoomButtonParameters(
                        textId = R.string.action_menu_add_exercise,
                        iconId = R.mipmap.ic_file_type,
                        colorId = R.color.exercise_chooser_activity_boom_menu_action_add_exercise,
                        listener = CustomExerciseChooserFragmentBoomButtonListener(this, {
                            val intent = Intent(it.activity, PositionGeneratorEditorActivity::class.java)
                            startActivity(intent)
                        })
                )
                1 -> BoomButtonParameters(
                        textId = R.string.action_menu_add_folder,
                        iconId = R.mipmap.ic_folder_type,
                        colorId = R.color.exercise_chooser_activity_boom_menu_action_add_folder,
                        listener = CustomExerciseChooserFragmentBoomButtonListener(this, {
                            val dialogBuilder = AlertDialog.Builder(it.activity!!)
                            val input = EditText(it.activity!!)
                            dialogBuilder.setTitle(R.string.new_folder_creation)
                                    .setMessage(R.string.folder_name_prompt)
                                    .setView(input)
                                    .setPositiveButton(R.string.OK, {dialog: DialogInterface?, _: Int ->
                                        val folderName = input.text.toString()
                                        if (folderName.isNotEmpty()) {
                                            try {
                                                FilesManager.createFolderInCurrentDirectory(folderName)
                                                it.adapter.loadFilesAndFoldersList()
                                            }
                                            catch (ex: SecurityException) {
                                                val resources = MyApplication.appContext.resources
                                                val title = resources.getString(R.string.folder_creation_error)
                                                val message = resources.getString(R.string.faced_folder_creation_restriction)
                                                it.showAlertDialog(title, message)
                                            }
                                        }
                                        else {
                                            val resources = MyApplication.appContext.resources
                                            val title = resources.getString(R.string.folder_creation_error)
                                            val message = resources.getString(R.string.empty_folder_name)
                                            it.showAlertDialog(title, message)
                                        }
                                        dialog?.dismiss()
                                    })
                                    .setNegativeButton(R.string.cancel, {dialog: DialogInterface?, _: Int ->
                                        dialog?.dismiss()
                                    })
                            dialogBuilder.show()
                        })
                )
                else -> throw RuntimeException("Unexpected pieceIndex value $pieceIndex")
            }
            val builder = TextOutsideCircleButton.Builder()
            builder.normalImageRes(buttonParameters.iconId).
                    normalTextRes(buttonParameters.textId).
                    normalColorRes(buttonParameters.colorId).
                    textSize(20).
                    listener(buttonParameters.listener)
            fab_custom_exercise_menu.addBuilder(builder)
        }
    }

    override fun onStart() {
        super.onStart()
        adapter.loadFilesAndFoldersList()
    }

    companion object {
        fun newInstance(): CustomExerciseChooserFragment {
            return CustomExerciseChooserFragment()
        }
    }

    private fun showAlertDialog(title : String, message: String) {
        val dialog = AlertDialog.Builder(this.activity!!).create()
        dialog.setTitle(title)
        dialog.setMessage(message)
        val buttonText = this.resources.getString(R.string.OK)
        dialog.setButton(AlertDialog.BUTTON_NEUTRAL, buttonText) { currDialog, _ -> currDialog?.dismiss() }

        dialog.show()
    }
}

class CustomExerciseChooserFragmentItemClickListener(
        parentFragment: CustomExerciseChooserFragment,
        private val action : (Fragment, Int) -> Unit
) : ItemClickListener {

    private var parentFragmentRef = WeakReference(parentFragment)

    override fun onClick(position: Int) {
        if (parentFragmentRef.get() != null)
            action(parentFragmentRef.get()!!, position)
    }
}

class CustomExerciseChooserFragmentItemLongClickListener(
        parentFragment: CustomExerciseChooserFragment,
        private val action : (Fragment, Int) -> Unit
): ItemLongClickListener {

    private var parentFragmentRef = WeakReference(parentFragment)

    override fun onLongClick(position: Int) {
        if (parentFragmentRef.get() != null)
            action(parentFragmentRef.get()!!, position)
    }
}

class CustomExerciseChooserFragmentBoomButtonListener(parentFragment: CustomExerciseChooserFragment,
                                                      private val action : (CustomExerciseChooserFragment) -> Unit) : OnBMClickListener {
    private val parentFragmentRef = WeakReference(parentFragment)

    override fun onBoomButtonClick(index: Int) {
        if (parentFragmentRef.get() != null) action(parentFragmentRef.get()!!)
    }
}