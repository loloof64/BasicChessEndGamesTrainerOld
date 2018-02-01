package com.loloof64.android.basicchessendgamestrainer

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.loloof64.android.basicchessendgamestrainer.exercise_chooser.CustomExercisesListAdapter
import com.loloof64.android.basicchessendgamestrainer.exercise_chooser.ItemClickListener
import com.loloof64.android.basicchessendgamestrainer.exercise_chooser.ItemLongClickListener
import kotlinx.android.synthetic.main.fragment_custom_exercise_chooser.*

class CustomExerciseChooserFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_custom_exercise_chooser, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        customExercisesListView.layoutManager = LinearLayoutManager(activity)
        customExercisesListView.adapter = CustomExercisesListAdapter(object : ItemClickListener {
            override fun onClick(position: Int) {
                //TODO launch exercise from the definition
            }

        },
                object : ItemLongClickListener {
                    override fun onLongClick(position: Int) {
                        //TODO launch item menu
                    }

                })
    }

    companion object {
        fun newInstance(): CustomExerciseChooserFragment {
            return CustomExerciseChooserFragment()
        }
    }
}