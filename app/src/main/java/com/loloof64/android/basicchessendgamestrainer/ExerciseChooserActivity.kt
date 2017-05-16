package com.loloof64.android.basicchessendgamestrainer

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.loloof64.android.basicchessendgamestrainer.exercise_chooser.ExerciseRow
import com.loloof64.android.basicchessendgamestrainer.exercise_chooser.ExercisesListAdapter
import java.util.logging.Logger
import kotlinx.android.synthetic.main.activity_exercise_chooser.*

class MyUCICommandAnswerCallback : UCICommandAnswerCallback {
    override fun execute(answer: String) {
        answer.split("\n").filter{ it.isNotEmpty() }.forEachIndexed { index, s ->  Logger.getLogger("loloof64").info("Late uci result line is ($index) => $s") }
    }
}

class ExerciseChooserActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_exercise_chooser)

        (application as MyApplication).uciSetup()
        (application as MyApplication).uciSetAnswerCallback(MyUCICommandAnswerCallback())
        (application as MyApplication).uciInteract("go")

        exercisesListView.adapter = ExercisesListAdapter(this, generateExercises())

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
