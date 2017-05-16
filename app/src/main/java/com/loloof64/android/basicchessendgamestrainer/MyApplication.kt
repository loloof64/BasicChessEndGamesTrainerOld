package com.loloof64.android.basicchessendgamestrainer

import android.app.Application
import java.util.logging.Logger

class MyApplication: Application(){

    companion object {
        init {
            System.loadLibrary("stockfish_8")
        }
    }

    external fun uciStart()
    external fun uciInteract(uciCmd: String): String
    external fun uciEnd()

    override fun onCreate() {
        super.onCreate()
        uciStart()
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

}
