package com.loloof64.android.basicchessendgamestrainer.exercise_chooser

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.loloof64.android.basicchessendgamestrainer.R

class ExerciseRowViewHolder(val textView: TextView)
class ExerciseRow(val textId: Int, val positionGenerator: PositionGenerator)

class ExercisesListAdapter(context: Context, exercisesList: List<ExerciseRow>) :
        ArrayAdapter<ExerciseRow>(context, 0, exercisesList){


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val viewToReturn = convertView ?: LayoutInflater.from(context)
                .inflate(R.layout.exercises_list_row, parent, false)

        var viewHolder = viewToReturn.tag as ExerciseRowViewHolder?
        if (viewHolder == null){
            viewHolder = ExerciseRowViewHolder(viewToReturn
                    .findViewById(R.id.exercise_list_row_value) as TextView)
            viewToReturn.setTag(viewHolder)
        }

        val exercise = getItem(position)
        viewHolder.textView.text = context.resources.getString(exercise.textId)

        return viewToReturn
    }
}
