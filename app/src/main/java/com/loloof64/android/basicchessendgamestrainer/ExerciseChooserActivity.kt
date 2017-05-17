package com.loloof64.android.basicchessendgamestrainer

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.AdapterView
import com.loloof64.android.basicchessendgamestrainer.exercise_chooser.ExerciseRow
import com.loloof64.android.basicchessendgamestrainer.exercise_chooser.ExercisesListAdapter
import kotlinx.android.synthetic.main.activity_exercise_chooser.*

class ExerciseChooserActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_exercise_chooser)

        exercisesListView.adapter = ExercisesListAdapter(this, generateExercises())
        exercisesListView.onItemClickListener = AdapterView.OnItemClickListener { _, _, _, _ ->
            val intent = Intent(this, PlayingActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onStop() {
        super.onStop()
        if (isFinishing) (application as MyApplication).uciEnd()
    }

    private fun generateExercises() : List<ExerciseRow>{
        return listOf(
                ExerciseRow(R.string.exercise_kq_k),
                ExerciseRow(R.string.exercise_krr_k),
                ExerciseRow(R.string.exercise_kbb_k)
        )
    }

}
