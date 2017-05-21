package com.loloof64.android.basicchessendgamestrainer

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.AdapterView
import android.widget.Toast
import com.loloof64.android.basicchessendgamestrainer.exercise_chooser.*
import kotlinx.android.synthetic.main.activity_exercise_chooser.*
import java.util.*

class ExerciseChooserActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_exercise_chooser)

        exercisesListView.adapter = ExercisesListAdapter(this, generateExercisesList())
        exercisesListView.onItemClickListener = AdapterView.OnItemClickListener { adapter, _, position, _ ->
            val intent = Intent(this, PlayingActivity::class.java)
            val bundle = Bundle()
            bundle.putInt(PlayingActivity.generatorIndexKey, position)
            intent.putExtras(bundle)
            startActivity(intent)
        }

    }

    private fun generateExercisesList() : List<ExerciseRow> {
        return listOf(R.string.exercise_krr_k,
                R.string.exercise_kq_k,
                R.string.exercise_kr_k,
                R.string.exercise_kbb_k).map { ExerciseRow(it) }
    }

    override fun onStop() {
        super.onStop()
        if (isFinishing) (application as MyApplication).uciEnd()
    }

    private val _random = Random()

}
