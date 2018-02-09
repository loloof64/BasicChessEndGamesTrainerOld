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
import com.loloof64.android.basicchessendgamestrainer.*
import com.loloof64.android.basicchessendgamestrainer.position_generator_editor.PositionConstraintBailErrorStrategy
import com.loloof64.android.basicchessendgamestrainer.position_generator_editor.single_king_constraint.BailSingleKingConstraintLexer
import com.loloof64.android.basicchessendgamestrainer.position_generator_editor.single_king_constraint.SingleKingConstraintBooleanExpr
import com.loloof64.android.basicchessendgamestrainer.position_generator_editor.single_king_constraint.SingleKingConstraintBuilder
import com.loloof64.android.basicchessendgamestrainer.position_generator_editor.single_king_constraint.antlr4.SingleKingConstraintParser
import com.loloof64.android.basicchessendgamestrainer.utils.FilesManager
import com.nightonke.boommenu.BoomButtons.OnBMClickListener
import com.nightonke.boommenu.BoomButtons.TextOutsideCircleButton
import kotlinx.android.synthetic.main.fragment_custom_exercise_chooser.*
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import java.io.BufferedReader
import java.io.FileReader
import java.lang.ref.WeakReference

class CustomExerciseChooserFragment : Fragment() {
    private lateinit var adapter: CustomExercisesListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_custom_exercise_chooser, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        customExercisesListView.layoutManager = LinearLayoutManager(activity)
        adapter = CustomExercisesListAdapter(itemClickListener = CustomExerciseChooserFragmentItemClickListener(
                parentFragment = this,
                action = {
                    fragment: CustomExerciseChooserFragment, position: Int ->
                // TODO If it is an exercise run it, otherwise if parent folder, try to go up, else go into folder
                    val clickedElement = fragment.adapter.getElementAtPosition(position)
                    when {
                        clickedElement.isFolder -> {

                        }
                        else -> {
                            fragment.launchExercise(clickedElement.name)
                        }
                    }
                }),
                itemLongClickListener = CustomExerciseChooserFragmentItemLongClickListener(
                        parentFragment = this,
                        action = {
                            fragment: CustomExerciseChooserFragment, position: Int ->
                            /*
                             TODO if it is an exercise show popup menu with edit/rename/delete options
                             else it is not parent folder show popup menu with rename/delete options
                              */
                        }
                ))
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

    private fun launchExercise(exerciseNameWithoutExtension: String) {
        val constraints = loadConstraintsFromFile(exerciseNameWithoutExtension) ?: return
        PositionGeneratorFromANTLR.setConstraints(constraints)

        val intent = Intent(activity, PlayingActivity::class.java)
        intent.putExtra(PlayingActivity.usingCustomGeneratorConstraintsKey, true)
        startActivity(intent)
    }

    private fun loadConstraintsFromFile(exerciseNameWithoutExtension: String): PositionGeneratorConstraints? {
        fun String?.doesNotStartPlayerKingConstraint(): Boolean =
                this != null && this != FilesManager.playerKingHeader

        fun String?.doesNotStartComputerKingConstraint(): Boolean =
                this != null && this != FilesManager.computerKingHeader

        fun String?.doesNotStartKingsMutualConstraint(): Boolean =
                this != null && this != FilesManager.mutualKingsHeader

        val exerciseFile = FilesManager.getCurrentDirectoryFiles().find { !it.isDirectory && it.nameWithoutExtension == exerciseNameWithoutExtension }
        if (exerciseFile != null){
            val playerKingConstraintBuilder = StringBuilder()
            val computerKingConstraintBuilder = StringBuilder()

            BufferedReader(FileReader(exerciseFile)).use {
                var currentLine: String?

                // skipping lines before player king constraint section
                do {
                    currentLine = it.readLine()
                } while(currentLine.doesNotStartPlayerKingConstraint())

                // filling player king constraint string
                do {
                    currentLine = it.readLine()
                    if (currentLine.doesNotStartComputerKingConstraint()) playerKingConstraintBuilder.append(currentLine)
                } while (currentLine.doesNotStartComputerKingConstraint())

                // filling computer king constraint string
                do {
                    currentLine = it.readLine()
                    if (currentLine.doesNotStartKingsMutualConstraint()) computerKingConstraintBuilder.append(currentLine)
                } while(currentLine.doesNotStartKingsMutualConstraint())
            }

            return PositionGeneratorConstraints(
                    playerKingConstraint = buildSingleKingConstraintFromString(playerKingConstraintBuilder.toString()),
                    computerKingConstraint = buildSingleKingConstraintFromString(computerKingConstraintBuilder.toString())
            )
        }
        else {
            val title = resources.getString(R.string.exercise_loading_error)
            val message = resources.getString(R.string.could_not_load_file, exerciseNameWithoutExtension)
            showAlertDialog(title, message)
            return null
        }
    }

    private fun buildSingleKingConstraintFromString(constraintStr: String) : SingleKingConstraintBooleanExpr? {
        if (constraintStr.isEmpty()) return null


        val inputStream = CharStreams.fromString(constraintStr)
        val lexer = BailSingleKingConstraintLexer(inputStream)
        val tokens = CommonTokenStream(lexer)
        val parser = SingleKingConstraintParser(tokens)
        parser.errorHandler = PositionConstraintBailErrorStrategy()
        val tree = parser.singleKingConstraint()
        SingleKingConstraintBuilder.clearVariables()
        return SingleKingConstraintBuilder.visit(tree) as SingleKingConstraintBooleanExpr
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
        private val action : (CustomExerciseChooserFragment, Int) -> Unit
) : ItemClickListener {

    private var parentFragmentRef = WeakReference(parentFragment)

    override fun onClick(position: Int) {
        if (parentFragmentRef.get() != null)
            action(parentFragmentRef.get()!!, position)
    }
}

class CustomExerciseChooserFragmentItemLongClickListener(
        parentFragment: CustomExerciseChooserFragment,
        private val action : (CustomExerciseChooserFragment, Int) -> Unit
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