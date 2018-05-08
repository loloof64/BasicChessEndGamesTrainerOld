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
import android.content.DialogInterface
import android.support.v7.widget.ThemedSpinnerAdapter
import android.content.res.Resources.Theme
import android.support.v7.app.AlertDialog
import android.widget.EditText
import android.widget.Toast
import com.loloof64.android.basicchessendgamestrainer.position_generator_editor.*
import com.loloof64.android.basicchessendgamestrainer.utils.FilesManager
import com.loloof64.android.basicchessendgamestrainer.utils.RxEventBus
import com.nightonke.boommenu.BoomButtons.OnBMClickListener
import com.nightonke.boommenu.BoomButtons.TextOutsideCircleButton
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer

import kotlinx.android.synthetic.main.activity_position_generator_editor.*
import kotlinx.android.synthetic.main.position_generator_editor_list_item.view.*
import java.io.IOException
import java.lang.ref.WeakReference


object PositionGeneratorValuesHolder {
    var playerKingConstraintScript = ""
    var computerKingConstraintScript = ""
    var kingsMutualConstraintScript = ""
    val otherPiecesCount = mutableListOf<PieceKindCount>()
    var otherPiecesGlobalConstraintScript = ""
    var otherPiecesMutualConstraintScript = ""
    var otherPiecesIndexedConstraintScript = ""
    var resultShouldBeDraw = false
}

data class MessageToShowInDialogEvent(val title: String, val message: String)

data class BoomButtonParameters(val textId: Int, val iconId: Int, val colorId: Int, val listener: OnBMClickListener)

class PositionGeneratorEditorObserver(parentActivity: PositionGeneratorEditorActivity) : Consumer<Any> {
    override fun accept(message: Any) {
        when (message) {
            is MessageToShowInDialogEvent -> parentRef.get()?.showAlertDialog(
                    title = message.title, message = message.message)
        }
    }

    private val parentRef = WeakReference(parentActivity)
}

class PositionGeneratorEditorActivity : AppCompatActivity() {

    private var isEditingAnExistingFile = false
    private var currentEditedFileName = ""
    private lateinit var busSubscription:Disposable

    companion object {
        val PlayerKingConstraintEditorFragmentIndex = 0
        val ComputerKingConstraintEditorFragmentIndex = 1
        val KingsMutualConstraintEditorFragmentIndex = 2
        val OtherPiecesCountConstraintEditorFragmentIndex = 3
        val OtherPiecesGlobalConstraintEditorFragmentIndex = 4
        val OtherPiecesMutualConstraintEditorFragmentIndex = 5
        val OtherPiecesIndexedConstraintEditorFragmentIndex = 6

        val allFragments = arrayOf(
                PlayerKingConstraintEditorFragment.newInstance(),
                ComputerKingConstraintEditorFragment.newInstance(),
                KingsMutualConstraintEditorFragment.newInstance(),
                OtherPiecesCountConstraintEditorFragment.newInstance(),
                OtherPiecesGlobalConstraintEditorFragment.newInstance(),
                OtherPiecesMutualConstraintEditorFragment.newInstance(),
                OtherPiecesIndexedConstraintEditorFragment.newInstance(),
                SetIfResultShouldBeDrawFragment.newInstance()
        )

        const val isEditingAnExistingFileKey = "EditingExistingFile"
        const val resultShouldBeDrawKey = "resultShouldBeDraw"
        const val playerKingConstraintScriptKey = "playerKingConstraintScript"
        const val computerKingConstraintScriptKey = "computerKingConstraintScript"
        const val kingsMutualConstraintScriptKey = "kingsMutualConstraintScript"
        const val otherPiecesCountKey = "otherPiecesCount"
        const val otherPiecesGlobalConstraintScriptKey = "otherPiecesGlobalConstraintScript"
        const val otherPiecesMutualConstraintScriptKey = "otherPiecesMutualConstraintScript"
        const val otherPiecesIndexedConstraintScriptKey = "otherPiecesIndexedConstraintScript"
        const val editedFileNameKey = "editedFileName"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_position_generator_editor)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)


            if (intent.extras?.getBoolean(isEditingAnExistingFileKey) == true) {
                PositionGeneratorValuesHolder.resultShouldBeDraw = intent.extras?.getBoolean(resultShouldBeDrawKey) ?: false
                PositionGeneratorValuesHolder.playerKingConstraintScript = intent.extras?.getString(playerKingConstraintScriptKey) ?: ""
                PositionGeneratorValuesHolder.computerKingConstraintScript = intent.extras?.getString(computerKingConstraintScriptKey) ?: ""
                PositionGeneratorValuesHolder.kingsMutualConstraintScript = intent.extras?.getString(kingsMutualConstraintScriptKey) ?: ""
                PositionGeneratorValuesHolder.otherPiecesCount.clear()
                PositionGeneratorValuesHolder.otherPiecesCount.addAll(OtherPiecesKindCountListArrayAdapter.getPiecesCountFromString(
                        intent.extras?.getString(otherPiecesCountKey) ?: ""))
                PositionGeneratorValuesHolder.otherPiecesGlobalConstraintScript = intent.extras?.getString(otherPiecesGlobalConstraintScriptKey) ?: ""
                PositionGeneratorValuesHolder.otherPiecesMutualConstraintScript = intent.extras?.getString(otherPiecesMutualConstraintScriptKey) ?: ""
                PositionGeneratorValuesHolder.otherPiecesIndexedConstraintScript = intent.extras?.getString(otherPiecesIndexedConstraintScriptKey) ?: ""
                isEditingAnExistingFile = true
                currentEditedFileName = intent.extras?.getString(editedFileNameKey)!!
            } else {
                PositionGeneratorValuesHolder.resultShouldBeDraw = false
                PositionGeneratorValuesHolder.playerKingConstraintScript = ""
                PositionGeneratorValuesHolder.computerKingConstraintScript = ""
                PositionGeneratorValuesHolder.kingsMutualConstraintScript = ""
                PositionGeneratorValuesHolder.otherPiecesCount.clear()
                PositionGeneratorValuesHolder.otherPiecesGlobalConstraintScript = ""
                PositionGeneratorValuesHolder.otherPiecesMutualConstraintScript = ""
                PositionGeneratorValuesHolder.otherPiecesIndexedConstraintScript = ""
            }

        val boomMenuButtonPiecesNumber = position_generator_activity_boom_menu_button.piecePlaceEnum.pieceNumber()
        (0 until boomMenuButtonPiecesNumber).forEach {pieceIndex ->
            val buttonParameters = when(pieceIndex) {
                0 -> BoomButtonParameters(
                        textId = R.string.action_save_generator,
                        iconId = R.mipmap.ic_action_save,
                        colorId = R.color.position_generator_activity_boom_menu_action_save,
                        listener = PositionGeneratorEditorActivityBoomButtonListener(allFragments, {
                            val playerKingFragment = it[0] as PlayerKingConstraintEditorFragment
                            val allScriptsAreGood =
                                    playerKingFragment.checkIsScriptIsValidAndShowEventualError()
                            if (allScriptsAreGood){
                                if (isEditingAnExistingFile){
                                    askIfFileShouldBeOverwritten(currentEditedFileName)
                                }
                                else {
                                    promptForFileName()
                                }
                            }
                            else {
                                val title = resources.getString(R.string.script_errors)
                                val message = resources.getString(R.string.correct_scripts_errors_first)
                                showAlertDialog(title, message)
                            }
                        })
                )
                1 -> BoomButtonParameters(
                        textId = R.string.action_cancel_generator,
                        iconId = R.mipmap.ic_action_cancel,
                        colorId = R.color.position_generator_activity_boom_menu_action_cancel,
                        listener = PositionGeneratorEditorActivityBoomButtonListener(allFragments, {
                            askForCancelConfirmation()
                        })
                )
                else -> throw RuntimeException("Unexpected pieceIndex value $pieceIndex")
            }
            val builder = TextOutsideCircleButton.Builder().
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
                        R.string.other_pieces_indexed_constraints,
                        R.string.theoretic_result_option).map {
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

    override fun onBackPressed() {
        askForCancelConfirmation()
    }

    override fun onStart() {
        super.onStart()
        busSubscription = RxEventBus.toSubject().subscribe(PositionGeneratorEditorObserver(parentActivity = this))
    }

    override fun onStop() {
        busSubscription.dispose()
        super.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putBoolean(isEditingAnExistingFileKey, isEditingAnExistingFile)
        outState?.putString(editedFileNameKey, currentEditedFileName)

        outState?.putBoolean(resultShouldBeDrawKey, PositionGeneratorValuesHolder.resultShouldBeDraw)
        outState?.putString(playerKingConstraintScriptKey, PositionGeneratorValuesHolder.playerKingConstraintScript)
        outState?.putString(computerKingConstraintScriptKey, PositionGeneratorValuesHolder.computerKingConstraintScript)
        outState?.putString(kingsMutualConstraintScriptKey, PositionGeneratorValuesHolder.kingsMutualConstraintScript)
        outState?.putString(otherPiecesCountKey,
                OtherPiecesKindCountListArrayAdapter.stringFromPiecesCount(PositionGeneratorValuesHolder.otherPiecesCount)
        )
        outState?.putString(otherPiecesGlobalConstraintScriptKey, PositionGeneratorValuesHolder.otherPiecesGlobalConstraintScript)
        outState?.putString(otherPiecesMutualConstraintScriptKey, PositionGeneratorValuesHolder.otherPiecesMutualConstraintScript)
        outState?.putString(otherPiecesIndexedConstraintScriptKey, PositionGeneratorValuesHolder.otherPiecesIndexedConstraintScript)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)

        isEditingAnExistingFile = savedInstanceState?.getBoolean(isEditingAnExistingFileKey) ?: false
        currentEditedFileName = savedInstanceState?.getString(editedFileNameKey) ?: ""

        PositionGeneratorValuesHolder.resultShouldBeDraw = savedInstanceState?.getBoolean(resultShouldBeDrawKey) ?: false
        PositionGeneratorValuesHolder.playerKingConstraintScript = savedInstanceState?.getString(playerKingConstraintScriptKey) ?: ""
        PositionGeneratorValuesHolder.computerKingConstraintScript = savedInstanceState?.getString(computerKingConstraintScriptKey) ?: ""
        PositionGeneratorValuesHolder.kingsMutualConstraintScript = savedInstanceState?.getString(kingsMutualConstraintScriptKey) ?: ""
        PositionGeneratorValuesHolder.otherPiecesCount.clear()
        PositionGeneratorValuesHolder.otherPiecesCount.addAll(
                OtherPiecesKindCountListArrayAdapter.getPiecesCountFromString(savedInstanceState?.getString(otherPiecesCountKey) ?: "")
        )
        PositionGeneratorValuesHolder.otherPiecesGlobalConstraintScript = savedInstanceState?.getString(otherPiecesGlobalConstraintScriptKey) ?: ""
        PositionGeneratorValuesHolder.otherPiecesMutualConstraintScript = savedInstanceState?.getString(otherPiecesMutualConstraintScriptKey) ?: ""
        PositionGeneratorValuesHolder.otherPiecesIndexedConstraintScript = savedInstanceState?.getString(otherPiecesIndexedConstraintScriptKey) ?: ""
    }



    fun showAlertDialog(title : String, message: String) {
        val dialog = AlertDialog.Builder(this).create()
        dialog.setTitle(title)
        dialog.setMessage(message)
        val buttonText = this.resources.getString(R.string.OK)
        dialog.setButton(AlertDialog.BUTTON_NEUTRAL, buttonText) { currDialog, _ -> currDialog?.dismiss() }

        dialog.show()
    }

    fun deleteOtherPieceGlobalConstraintScriptAssociatedWithPieceKind(pieceKind: PieceKind) {
        val otherPiecesGlobalConstraintFrag = allFragments[OtherPiecesGlobalConstraintEditorFragmentIndex] as
                OtherPiecesGlobalConstraintEditorFragment
        otherPiecesGlobalConstraintFrag.deleteScriptAssociatedWithPieceKind(pieceKind)
    }

    private fun buildFileContent(): String {
        val contentBuilder = StringBuilder()

        contentBuilder.append(if (PositionGeneratorValuesHolder.resultShouldBeDraw) "1" else "0")
        contentBuilder.append(FilesManager.newLine)
        contentBuilder.append(FilesManager.newLine)

        contentBuilder.append(FilesManager.playerKingHeader)
        contentBuilder.append(FilesManager.newLine)
        contentBuilder.append(FilesManager.newLine)
        contentBuilder.append(PositionGeneratorValuesHolder.playerKingConstraintScript)
        contentBuilder.append(FilesManager.newLine)
        contentBuilder.append(FilesManager.newLine)

        contentBuilder.append(FilesManager.computerKingHeader)
        contentBuilder.append(FilesManager.newLine)
        contentBuilder.append(FilesManager.newLine)
        contentBuilder.append(PositionGeneratorValuesHolder.computerKingConstraintScript)
        contentBuilder.append(FilesManager.newLine)
        contentBuilder.append(FilesManager.newLine)

        contentBuilder.append(FilesManager.mutualKingsHeader)
        contentBuilder.append(FilesManager.newLine)
        contentBuilder.append(FilesManager.newLine)
        contentBuilder.append(PositionGeneratorValuesHolder.kingsMutualConstraintScript)
        contentBuilder.append(FilesManager.newLine)
        contentBuilder.append(FilesManager.newLine)

        contentBuilder.append(FilesManager.otherPiecesCountHeader)
        contentBuilder.append(FilesManager.newLine)
        contentBuilder.append(FilesManager.newLine)
        contentBuilder.append(OtherPiecesKindCountListArrayAdapter.stringFromPiecesCount(
                PositionGeneratorValuesHolder.otherPiecesCount))
        contentBuilder.append(FilesManager.newLine)
        contentBuilder.append(FilesManager.newLine)

        return contentBuilder.toString()
    }

    private fun promptForFileName() {
        val inputField = EditText(this)
        AlertDialog.Builder(this)
            .setTitle(R.string.title_output_script_file_name)
            .setMessage(R.string.message_output_script_file_name)
            .setView(inputField)
            .setPositiveButton(R.string.OK, { _: DialogInterface?, _: Int ->
                val fileName = inputField.text.toString()
                if (fileName.isNotEmpty()){
                    val fileNameWithExtension = if (fileName.endsWith(".txt")) fileName else "$fileName.txt"

                    if (FilesManager.alreadyFileOrFolderInCurrentDirectory(fileNameWithExtension)) {
                        askIfFileShouldBeOverwritten(fileNameWithExtension)
                    }
                    else {
                        saveFile(fileNameWithExtension)
                    }
                }
                else {
                    Toast.makeText(this, R.string.no_filename_given, Toast.LENGTH_SHORT).show()
                }
            })
            .setNegativeButton(R.string.cancel, { dialog: DialogInterface?, _: Int ->
                dialog?.dismiss()
            })
            .show()
    }

    private fun saveFile(name: String) {
        val content = buildFileContent()
        try {
            FilesManager.saveTextFileInCurrentDirectory(name, content)
            finish()
        }
        catch (ex: IOException) {
            Toast.makeText(this, R.string.could_not_save_file, Toast.LENGTH_SHORT).show()
        }
    }

    private fun askIfFileShouldBeOverwritten(name: String) {
        AlertDialog.Builder(this)
                .setTitle(R.string.overwrite_file_prompt_title)
                .setMessage(String.format(resources.getString(R.string.overwrite_file_prompt_message), name))
                .setPositiveButton(R.string.OK, {_: DialogInterface?, _: Int ->
                    saveFile(name)
                })
                .setNegativeButton(R.string.cancel, {dialog: DialogInterface?, _: Int ->
                    dialog?.dismiss()
                })
                .show()
    }

    private fun askForCancelConfirmation() {
        AlertDialog.Builder(this)
                .setTitle(R.string.confirm_cancel_script_editing_title)
                .setMessage(R.string.confirm_cancel_script_editing_message)
                .setPositiveButton(R.string.OK, {dialog: DialogInterface?, _: Int ->
                    dialog?.dismiss()
                    finish()
                })
                .setNegativeButton(R.string.cancel, {dialog: DialogInterface?, _: Int ->
                    dialog?.dismiss()
                })
                .show()
    }


    private class MyAdapter(context: Context, objects: Array<String>) : ArrayAdapter<String>(context, R.layout.position_generator_editor_list_item, objects), ThemedSpinnerAdapter {
        private val mDropDownHelper: ThemedSpinnerAdapter.Helper = ThemedSpinnerAdapter.Helper(context)

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = if (convertView == null) {
                // Inflate the drop down using the helper's LayoutInflater
                val inflater = mDropDownHelper.dropDownViewInflater
                inflater.inflate(R.layout.position_generator_editor_list_item, parent, false)
            } else {
                convertView
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

class PositionGeneratorEditorActivityBoomButtonListener(internalFragments: Array<Fragment>,
                                                        private val action: (internalFragments: Array<Fragment>) -> Unit) : OnBMClickListener {
    private val internalFragmentsRef = WeakReference(internalFragments)

    override fun onBoomButtonClick(index: Int) {
        if (internalFragmentsRef.get() != null) action(internalFragmentsRef.get()!!)
    }
}
