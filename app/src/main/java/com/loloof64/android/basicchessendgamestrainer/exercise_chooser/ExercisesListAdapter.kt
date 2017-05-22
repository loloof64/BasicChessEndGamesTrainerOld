package com.loloof64.android.basicchessendgamestrainer.exercise_chooser

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.loloof64.android.basicchessendgamestrainer.MyApplication
import com.loloof64.android.basicchessendgamestrainer.R

class ExerciseRow(val textId: Int, val mustWin: Boolean)

interface ItemClickListener {
    fun onClick(position: Int):Unit
}

class ExercisesListAdapter(val exercisesList: List<ExerciseRow>,
                           val itemClickListener: ItemClickListener) :
        RecyclerView.Adapter<ExercisesListAdapter.Companion.ViewHolder>(){

    companion object {
        class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val layout = LayoutInflater.from(parent?.context).inflate(R.layout.exercises_list_row, parent, false) as LinearLayout
        val textView = layout.findViewById(R.id.exercise_list_row_value) as TextView
        layout.removeView(textView)
        return ViewHolder(textView)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.textView?.text = MyApplication.getApplicationContext().getString(exercisesList[position].textId)
        holder?.textView?.setOnClickListener{ itemClickListener.onClick(position) }
        holder?.textView?.setBackgroundColor(
                if (exercisesList[position].mustWin) Color.parseColor("#50DF50")
                else Color.parseColor("#DF5050")
        )
    }

    override fun getItemCount(): Int {
        return exercisesList.size
    }
}
