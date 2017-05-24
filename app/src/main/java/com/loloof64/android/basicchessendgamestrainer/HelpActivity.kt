package com.loloof64.android.basicchessendgamestrainer

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_help.*

class HelpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)
        help_text_view.text = resources.getString(R.string.help)
                .replace("[CR]", System.getProperty("line.separator"))
                .replace("[TAB]", "    ")
    }
}
