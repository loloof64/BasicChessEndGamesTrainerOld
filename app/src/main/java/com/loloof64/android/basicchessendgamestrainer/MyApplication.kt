package com.loloof64.android.basicchessendgamestrainer

import android.app.Application
import java.util.logging.Logger

interface UCICommandAnswerCallback {
    fun execute(answer: String): Unit
}

class MyApplication: Application(){

    companion object {
        init {
            System.loadLibrary("stockfish_8")
        }
    }

    private external fun uciStart()
    external fun uciInteract(uciCmd: String): String
    external fun uciSetAnswerCallback(callback: UCICommandAnswerCallback)
    external fun uciEnd()

    override fun onCreate() {
        super.onCreate()
        uciStart()
    }

    fun uciSetup(){
        val uciCmdLastResult = uciInteract("uci").split("\n").filter{ it.isNotEmpty() }.last()
        if (uciCmdLastResult != "uciok") throw RuntimeException("Could not set up engine !")
        uciInteract("ucinewgame")
        val uciCmdIsReadyLastResult = uciInteract("isready").split("\n").filter{ it.isNotEmpty() }.last()
        if (uciCmdIsReadyLastResult != "readyok") throw RuntimeException("Could not set up engine !")
    }

    fun setCallbackForUciCommandAnswer(callback: UCICommandAnswerCallback){
        uciSetAnswerCallback(callback)
    }

}
