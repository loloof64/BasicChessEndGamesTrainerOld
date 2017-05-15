package com.loloof64.android.basicchessendgamestrainer

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import java.util.logging.Logger

class ExerciseChooserActivity : AppCompatActivity() {

    companion object {
        init {
            System.loadLibrary("stockfish_8")
            uciStart()
        }

        external fun uciStart()
    }

    fun uciSetup(){
        var uciCmdResult = uciInteract("uci")
        uciCmdResult.split("\n").filter{ it.isNotEmpty() }.forEachIndexed { index, s ->  Logger.getLogger("loloof64").info("uci result line is ($index) => $s") }
        uciCmdResult = uciInteract("ucinewgame")
        uciCmdResult.split("\n").filter{ it.isNotEmpty() }.forEachIndexed { index, s ->  Logger.getLogger("loloof64").info("uci result line is ($index) => $s") }
        uciCmdResult = uciInteract("isready")
        uciCmdResult.split("\n").filter{ it.isNotEmpty() }.forEachIndexed { index, s ->  Logger.getLogger("loloof64").info("uci result line is ($index) => $s") }
        uciCmdResult = uciInteract("position fen 4k3/8/8/2K5/8/8/6q1/8 b - - 0 1")
        uciCmdResult.split("\n").filter{ it.isNotEmpty() }.forEachIndexed { index, s ->  Logger.getLogger("loloof64").info("uci result line is ($index) => $s") }
        uciCmdResult = uciInteract("go")
        uciCmdResult.split("\n").filter{ it.isNotEmpty() }.forEachIndexed { index, s ->  Logger.getLogger("loloof64").info("uci result line is ($index) => $s") }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_exercise_chooser)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        uciSetup()
    }

    override fun onDestroy() {
        super.onDestroy()
        uciEnd()
    }

    external fun uciInteract(uciCmd: String): String
    external fun uciEnd()
}
