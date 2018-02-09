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

import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.loloof64.android.basicchessendgamestrainer.utils.FilesManager
import com.loloof64.android.basicchessendgamestrainer.MyApplication
import com.loloof64.android.basicchessendgamestrainer.R
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

interface ItemLongClickListener {
    fun onLongClick(position: Int)
}

data class CustomExerciseInfo(val isFolder: Boolean, val name: String, val mustDraw: Boolean)

class CustomExercisesListAdapter(private val itemClickListener: ItemClickListener,
                                 private val itemLongClickListener: ItemLongClickListener)
    : RecyclerView.Adapter<CustomExercisesListAdapter.Companion.ViewHolder>(){

    private var exercisesList: List<CustomExerciseInfo> = listOf()

    init {
        loadFilesAndFoldersList()
    }

    companion object {
        class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
            val nameView: TextView = view.findViewById(R.id.custom_exercise_fragment_file_name)
            val imageView: ImageView = view.findViewById(R.id.custom_exercise_fragment_file_type)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val layout = LayoutInflater.from(parent?.context).inflate(R.layout.exercises_list_row, parent, false) as LinearLayout
        return CustomExercisesListAdapter.Companion.ViewHolder(layout)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        fun getColorFromId(colorId: Int) : Int {
            val context = MyApplication.getApplicationContext()
            return ResourcesCompat.getColor(context.resources, colorId, null)
        }

        holder?.nameView?.text = exercisesList[position].name
        holder?.nameView?.setOnClickListener{ itemClickListener.onClick(position) }
        holder?.nameView?.setOnLongClickListener { itemLongClickListener.onLongClick(position); true }
        holder?.nameView?.setBackgroundColor(
                when {
                    exercisesList[position].isFolder -> getColorFromId(R.color.exercise_chooser_folder_color)
                    exercisesList[position].mustDraw -> getColorFromId(R.color.exercise_chooser_nullifying_color)
                    else -> getColorFromId(R.color.exercise_chooser_winning_color)
                }
        )

        holder?.imageView?.setImageResource(if (exercisesList[position].isFolder) R.mipmap.ic_folder_type else R.mipmap.ic_file_type)
    }

    override fun getItemCount(): Int {
        return exercisesList.size
    }

    fun loadFilesAndFoldersList() {
        exercisesList = FilesManager.getCurrentDirectoryFiles()
                .filter { it.isDirectory || it.name.endsWith(".txt") }
                .map{
                    val mustDraw = if (it.isDirectory) false else readFirstLine(it) == "1"
                    CustomExerciseInfo(isFolder = it.isDirectory, name = it.nameWithoutExtension, mustDraw = mustDraw)
                }.sortedWith(compareBy({!it.isFolder}, {it.name}))
        notifyDataSetChanged()
    }

    fun getElementAtPosition(position: Int) : CustomExerciseInfo {
        return exercisesList[position]
    }

    private fun readFirstLine(file: File) : String {
        lateinit var line: String
        BufferedReader(FileReader(file)).use {
            line = it.readLine()
        }
        return line
    }

}