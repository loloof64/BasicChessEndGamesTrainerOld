package com.loloof64.android.basicchessendgamestrainer

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.loloof64.android.basicchessendgamestrainer.exercise_chooser.ExercisesListAdapter
import com.loloof64.android.basicchessendgamestrainer.exercise_chooser.ItemClickListener
import com.loloof64.android.basicchessendgamestrainer.exercise_chooser.availableGenerators
import kotlinx.android.synthetic.main.activity_exercise_chooser.*

class ExerciseChooserActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_chooser)

        exercisesListView.layoutManager = LinearLayoutManager(this)
        exercisesListView.adapter = ExercisesListAdapter(availableGenerators, object : ItemClickListener {
            override fun onClick(position: Int) {
                val intent = Intent(this@ExerciseChooserActivity, PlayingActivity::class.java)
                val bundle = Bundle()
                bundle.putInt(PlayingActivity.generatorIndexKey, position)
                intent.putExtras(bundle)
                startActivity(intent)
            }
        })

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
