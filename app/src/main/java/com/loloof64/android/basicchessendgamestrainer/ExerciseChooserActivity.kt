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

        exercisesListView.adapter = ExercisesListAdapter(this, generateExercises())
        exercisesListView.onItemClickListener = AdapterView.OnItemClickListener { adapter, _, position, _ ->
            val intent = Intent(this, PlayingActivity::class.java)
            val item = adapter.getItemAtPosition(position) as ExerciseRow

            val bundle = Bundle()
            val generatedPosition = item.positionGenerator.generatePosition(_random.nextBoolean())

            if (generatedPosition.isNotEmpty()) {
                bundle.putString(PlayingActivity.positionToSetupKey, generatedPosition)
                intent.putExtras(bundle)

                startActivity(intent)
            } else {
                Toast.makeText(this@ExerciseChooserActivity, R.string.position_generation_error,
                        Toast.LENGTH_LONG).show()
            }
        }

    }

    override fun onStop() {
        super.onStop()
        if (isFinishing) (application as MyApplication).uciEnd()
    }

    private fun generateExercises() : List<ExerciseRow>{
        return listOf(
                ExerciseRow(R.string.exercise_krr_k, KRRvK_PositionGenerator),
                ExerciseRow(R.string.exercise_kq_k, KQvK_PositionGenerator),
                ExerciseRow(R.string.exercise_kr_k, KRvK_PositionGenerator),
                ExerciseRow(R.string.exercise_kbb_k, KBBvK_PositionGenerator)
        )
    }

    private val _random = Random()

}
