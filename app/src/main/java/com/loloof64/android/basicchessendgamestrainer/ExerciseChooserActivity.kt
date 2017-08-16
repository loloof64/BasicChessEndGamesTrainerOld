package com.loloof64.android.basicchessendgamestrainer

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.loloof64.android.basicchessendgamestrainer.exercise_chooser.ExerciseRow
import com.loloof64.android.basicchessendgamestrainer.exercise_chooser.ExercisesListAdapter
import com.loloof64.android.basicchessendgamestrainer.exercise_chooser.ItemClickListener
import kotlinx.android.synthetic.main.activity_exercise_chooser.*

class ExerciseChooserActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_chooser)

        exercisesListView.layoutManager = LinearLayoutManager(this)
        exercisesListView.adapter = ExercisesListAdapter(generateExercisesList(), object : ItemClickListener {
            override fun onClick(position: Int) {
                val intent = Intent(this@ExerciseChooserActivity, PlayingActivity::class.java)
                val bundle = Bundle()
                bundle.putInt(PlayingActivity.generatorIndexKey, position)
                intent.putExtras(bundle)
                startActivity(intent)
            }
        })

    }

    private fun generateExercisesList() : List<ExerciseRow> {
        //TODO remove code duplication (as in ExercisesList)
        return listOf(R.string.exercise_krr_k to true,
                R.string.exercise_kq_k to true,
                R.string.exercise_kr_k to true,
                R.string.exercise_kbb_k to true,
                R.string.exercise_kp_k_I to true,
                R.string.exercise_kp_k_II to false,
                R.string.exercise_kppp_kppp to true).map { ExerciseRow(it.first, it.second) }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_exercise_chooser, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when(item?.itemId) {
            R.id.action_help -> {
                val intent = Intent(this, HelpActivity::class.java)
                startActivity(intent)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onStop(){
        super.onStop()
    }

}
