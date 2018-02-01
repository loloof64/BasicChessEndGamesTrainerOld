package com.loloof64.android.basicchessendgamestrainer

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import com.loloof64.android.basicchessendgamestrainer.exercise_chooser.PredefinedExercisesListAdapter
import com.loloof64.android.basicchessendgamestrainer.exercise_chooser.ItemClickListener
import com.loloof64.android.basicchessendgamestrainer.exercise_chooser.availableGenerators
import com.loloof64.android.basicchessendgamestrainer.playing_activity.EngineInteraction
import kotlinx.android.synthetic.main.fragment_predefined_exercise_chooser.*

class PredefinedExerciseChooserFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_predefined_exercise_chooser, container, false)
        EngineInteraction.copyStockfishIntoInternalMemoryIfNecessary()
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        predefinedExercisesListView.layoutManager = LinearLayoutManager(activity)
        predefinedExercisesListView.adapter = PredefinedExercisesListAdapter(availableGenerators, object : ItemClickListener {
            override fun onClick(position: Int) {
                val intent = Intent(activity, PlayingActivity::class.java)
                val bundle = Bundle()
                bundle.putInt(PlayingActivity.generatorIndexKey, position)
                intent.putExtras(bundle)
                startActivity(intent)
            }
        })

    }

    companion object {
        fun newInstance(): PredefinedExerciseChooserFragment {
            return PredefinedExerciseChooserFragment()
        }
    }

}
