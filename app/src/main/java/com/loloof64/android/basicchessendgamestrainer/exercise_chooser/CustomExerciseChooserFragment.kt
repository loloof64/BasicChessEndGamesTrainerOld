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

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.view.menu.MenuBuilder
import android.support.v7.view.menu.MenuPopupHelper
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PopupMenu
import android.view.*
import android.widget.EditText
import com.loloof64.android.basicchessendgamestrainer.*
import com.loloof64.android.basicchessendgamestrainer.position_generator_editor.OtherPiecesKindCountListArrayAdapter
import com.loloof64.android.basicchessendgamestrainer.position_generator_editor.PositionConstraintBailErrorStrategy
import com.loloof64.android.basicchessendgamestrainer.position_generator_editor.script_language.BailScriptLanguageLexer
import com.loloof64.android.basicchessendgamestrainer.position_generator_editor.script_language.ScriptLanguageBooleanExpr
import com.loloof64.android.basicchessendgamestrainer.position_generator_editor.script_language.ScriptLanguageBuilder
import com.loloof64.android.basicchessendgamestrainer.position_generator_editor.script_language.antlr4.ScriptLanguageParser
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
    private lateinit var layoutManager: LinearLayoutManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_custom_exercise_chooser, container, false)
    }

    @SuppressLint("RestrictedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        layoutManager = LinearLayoutManager(activity)
        customExercisesListView.layoutManager = layoutManager
        adapter = CustomExercisesListAdapter(itemClickListener = CustomExerciseChooserFragmentItemClickListener(
                parentFragment = this,
                action = {
                    parentFragment: CustomExerciseChooserFragment, position: Int ->
                // TODO If it is an exercise run it, otherwise if parent folder, try to go up, else go into folder
                    val clickedElement = parentFragment.adapter.getElementAtPosition(position)
                    when {
                        clickedElement.isFolder -> {

                        }
                        else -> {
                            parentFragment.launchExercise(clickedElement.name)
                        }
                    }
                }),
                itemLongClickListener = CustomExerciseChooserFragmentItemLongClickListener(
                        parentFragment = this,
                        action = {
                            parentFragment: CustomExerciseChooserFragment, position: Int ->
                            /*
                             TODO if it is an exercise show popup menu with edit/rename/delete options
                             else it is not parent folder show popup menu with rename/delete options
                              */

                            val clickedElement = parentFragment.adapter.getElementAtPosition(position)
                            when {
                                clickedElement.isFolder -> {

                                }
                                else -> {
                                    val concernedView = parentFragment.layoutManager.findViewByPosition(position)
                                    val popupMenu = PopupMenu(parentFragment.activity!!, concernedView)
                                    popupMenu.menuInflater.inflate(R.menu.custom_exercise_item_popum_menu, popupMenu.menu)
                                    popupMenu.setOnMenuItemClickListener(CustomExerciseChooserFragmentPopupMenuItemListener(
                                            parentFragment = parentFragment,
                                            position = position,
                                            action = {popUpParentFragment, menuItemId ->
                                                when(menuItemId){
                                                    R.id.custom_exercise_edit -> {
                                                        val intent = Intent(popUpParentFragment.activity, PositionGeneratorEditorActivity::class.java)
                                                        val fileName = popUpParentFragment.adapter.getElementAtPosition(position).name
                                                        val constraints = popUpParentFragment.loadConstraintsScriptsFromFile(fileName)

                                                        intent.putExtra(PositionGeneratorEditorActivity.isEditingAnExistingFileKey, true)
                                                        intent.putExtra(PositionGeneratorEditorActivity.editedFileNameKey, fileName)
                                                        intent.putExtra(PositionGeneratorEditorActivity.resultShouldBeDrawKey, constraints?.resultShouldBeDraw)
                                                        intent.putExtra(PositionGeneratorEditorActivity.playerKingConstraintScriptKey, constraints?.playerKingConstraint ?: "")
                                                        intent.putExtra(PositionGeneratorEditorActivity.computerKingConstraintScriptKey, constraints?.computerKingConstraint ?: "")
                                                        intent.putExtra(PositionGeneratorEditorActivity.kingsMutualConstraintScriptKey, constraints?.kingsMutualConstraint ?: "")
                                                        intent.putExtra(PositionGeneratorEditorActivity.otherPiecesCountKey, constraints?.otherPiecesCountConstraint ?: "")
                                                        startActivity(intent)
                                                    }
                                                }
                                            }
                                    ))

                                    val menuHelper = MenuPopupHelper(parentFragment.activity!!, popupMenu.menu as MenuBuilder, concernedView)
                                    menuHelper.setForceShowIcon(true)
                                    menuHelper.show()
                                }
                            }
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
                            intent.putExtra(PositionGeneratorEditorActivity.isEditingAnExistingFileKey, false)
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
                                            if (FilesManager.alreadyFileOrFolderInCurrentDirectory(folderName)){
                                                val title = resources.getString(R.string.folder_already_exists_title)
                                                val message = resources.getString(R.string.folder_already_exists_message, folderName)
                                                it.showAlertDialog(title, message)
                                            }
                                            else {
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

    private fun launchExercise(exerciseNameWithExtension: String) {
        val constraints = loadConstraintsExprFromFile(exerciseNameWithExtension) ?: return
        PositionGeneratorFromANTLR.setConstraints(constraints)

        val intent = Intent(activity, PlayingActivity::class.java)
        intent.putExtra(PlayingActivity.usingCustomGeneratorConstraintsKey, true)
        startActivity(intent)
    }

    private fun loadConstraintsScriptsFromFile(exerciseNameWithExtension: String): PositionGeneratorConstraintsScripts? {
        fun String?.doesNotStartPlayerKingConstraintOrEndFile(): Boolean =
                this != null && this != FilesManager.playerKingHeader

        fun String?.doesNotStartComputerKingConstraintOrEndFile(): Boolean =
                this != null && this != FilesManager.computerKingHeader

        fun String?.doesNotStartKingsMutualConstraintOrEndFile(): Boolean =
                this != null && this != FilesManager.mutualKingsHeader

        fun String?.doesNotStartOtherPiecesCountConstraintOrEndFile(): Boolean =
                this != null && this != FilesManager.otherPiecesCountHeader

        fun String?.doesNotStartOtherPiecesGlobalConstraintOrEndFile(): Boolean =
                this != null && this != FilesManager.otherPiecesGlobalHeader

        val exerciseFile = FilesManager.getCurrentDirectoryFiles().find { !it.isDirectory && it.name == exerciseNameWithExtension }
        if (exerciseFile != null){
            val playerKingConstraintBuilder = StringBuilder()
            val computerKingConstraintBuilder = StringBuilder()
            val kingsMutualConstraintBuilder = StringBuilder()
            val otherPiecesCountBuilder = StringBuilder()
            var resultShouldBeDraw = false

            BufferedReader(FileReader(exerciseFile)).use {
                var currentLine: String?

                //First line is draw result
                currentLine = it.readLine()
                resultShouldBeDraw = if (currentLine == null) false else currentLine == FilesManager.DRAW_LINE_VALUE

                // skipping lines before player king constraint section
                do {
                    currentLine = it.readLine()
                } while(currentLine.doesNotStartPlayerKingConstraintOrEndFile())

                // filling player king constraint string
                do {
                    currentLine = it.readLine()
                    if (currentLine.doesNotStartComputerKingConstraintOrEndFile()) playerKingConstraintBuilder.append(currentLine)
                } while (currentLine.doesNotStartComputerKingConstraintOrEndFile())

                // filling computer king constraint string
                do {
                    currentLine = it.readLine()
                    if (currentLine.doesNotStartKingsMutualConstraintOrEndFile()) computerKingConstraintBuilder.append(currentLine)
                } while(currentLine.doesNotStartKingsMutualConstraintOrEndFile())

                //filling kings mutual constraint string
                do {
                    currentLine = it.readLine()
                    if (currentLine.doesNotStartOtherPiecesCountConstraintOrEndFile()) kingsMutualConstraintBuilder.append(currentLine)
                } while (currentLine.doesNotStartOtherPiecesCountConstraintOrEndFile())

                //filling other pieces count string
                do {
                    currentLine = it.readLine()
                    if (currentLine.doesNotStartOtherPiecesGlobalConstraintOrEndFile()) otherPiecesCountBuilder.append(currentLine)
                } while (currentLine.doesNotStartOtherPiecesGlobalConstraintOrEndFile())
            }

            return PositionGeneratorConstraintsScripts(
                    resultShouldBeDraw = resultShouldBeDraw,
                    playerKingConstraint = playerKingConstraintBuilder.toString(),
                    computerKingConstraint = computerKingConstraintBuilder.toString(),
                    kingsMutualConstraint = kingsMutualConstraintBuilder.toString(),
                    otherPiecesCountConstraint = otherPiecesCountBuilder.toString()
            )
        }
        else {
            val title = resources.getString(R.string.exercise_loading_error)
            val message = resources.getString(R.string.could_not_load_file, exerciseNameWithExtension)
            showAlertDialog(title, message)
            return null
        }
    }

    private fun loadConstraintsExprFromFile(exerciseNameWithExtension: String): PositionGeneratorConstraintsExpr? {
            val constraintsScripts = loadConstraintsScriptsFromFile((exerciseNameWithExtension)) ?: return null

            return PositionGeneratorConstraintsExpr(
                    playerKingConstraint = buildScriptExprFromString(constraintsScripts.playerKingConstraint),
                    computerKingConstraint = buildScriptExprFromString(constraintsScripts.computerKingConstraint),
                    kingsMutualConstraint = buildScriptExprFromString(constraintsScripts.kingsMutualConstraint),
                    otherPiecesCountConstraint = OtherPiecesKindCountListArrayAdapter.getPiecesCountFromString(constraintsScripts.otherPiecesCountConstraint)
            )
    }

    private fun buildScriptExprFromString(constraintStr: String) : ScriptLanguageBooleanExpr? {
        if (constraintStr.isEmpty()) return null


        val inputStream = CharStreams.fromString(constraintStr)
        val lexer = BailScriptLanguageLexer(inputStream)
        val tokens = CommonTokenStream(lexer)
        val parser = ScriptLanguageParser(tokens)
        parser.errorHandler = PositionConstraintBailErrorStrategy()
        val tree = parser.scriptLanguage()
        ScriptLanguageBuilder.clearVariables()
        return ScriptLanguageBuilder.visit(tree) as ScriptLanguageBooleanExpr
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

class CustomExerciseChooserFragmentPopupMenuItemListener(
        parentFragment: CustomExerciseChooserFragment,
        val position: Int,
        private val action: (CustomExerciseChooserFragment, menuItemId: Int) -> Unit
) : PopupMenu.OnMenuItemClickListener {

    private val parentFragmentRef = WeakReference(parentFragment)

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        if (parentFragmentRef.get() != null && item != null) action(parentFragmentRef.get()!!, item.itemId)
        return true
    }

}