package com.loloof64.android.basicchessendgamestrainer.exercise_chooser

import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.loloof64.android.basicchessendgamestrainer.MyApplication
import com.loloof64.android.basicchessendgamestrainer.R

interface ItemLongClickListener {
    fun onLongClick(position: Int)
}

class CustomExercisesListAdapter(private val itemClickListener: ItemClickListener,
                                 private val itemLongClickListener: ItemLongClickListener)
    : RecyclerView.Adapter<CustomExercisesListAdapter.Companion.ViewHolder>(){

    private var exercisesList: List<ExerciseInfo> = listOf()

    companion object {
        class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val layout = LayoutInflater.from(parent?.context).inflate(R.layout.exercises_list_row, parent, false) as LinearLayout
        val textView = layout.findViewById<TextView>(R.id.exercise_list_row_value)
        layout.removeView(textView)
        return CustomExercisesListAdapter.Companion.ViewHolder(textView)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        fun getColor(colorId: Int) : Int {
            val context = MyApplication.getApplicationContext()
            return ResourcesCompat.getColor(context.resources, colorId, null)
        }

        holder?.textView?.text = MyApplication.getApplicationContext().getString(exercisesList[position].textId)
        holder?.textView?.setOnClickListener{ itemClickListener.onClick(position) }
        holder?.textView?.setOnLongClickListener { itemLongClickListener.onLongClick(position); true }
        holder?.textView?.setBackgroundColor(
                if (exercisesList[position].mustWin) getColor(R.color.exercise_chooser_winning_color)
                else getColor(R.color.exercise_chooser_nullifying_color)
        )
    }

    override fun getItemCount(): Int {
        return exercisesList.size
    }

}