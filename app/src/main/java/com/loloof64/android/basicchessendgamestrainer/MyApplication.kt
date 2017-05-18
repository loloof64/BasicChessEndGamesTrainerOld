package com.loloof64.android.basicchessendgamestrainer

import android.app.Application
import android.net.Uri
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
        uciSetup()
    }

    fun uciSetup(){
        uciSetAnswerCallback(object : UCICommandAnswerCallback{
            override fun execute(answer: String) {
                answer.split("\n").forEach { Logger.getLogger("loloof64").info("syzygy info: $it") }
            }
        })

        val uciCmdLastResult = uciInteract("uci").split("\n").filter{ it.isNotEmpty() }.last()
        if (uciCmdLastResult != "uciok") throw RuntimeException("Could not set up engine !")
        val syzygyPath = Uri.parse("file:///android_asset/raw/syzygy").toString()
        val uciSyzygyCmdResult = uciInteract("setoption name SyzygyPath value $syzygyPath")
        Logger.getLogger("loloof64").info("syzygy result : $uciSyzygyCmdResult")
        //uciInteract("setoption name Minimum Thinking Time value 1200")
    }

    fun uciNewGame(positionFEN:String){
        uciInteract("ucinewgame")
        val uciCmdIsReadyLastResult = uciInteract("isready").split("\n").filter{ it.isNotEmpty() }.last()
        if (uciCmdIsReadyLastResult != "readyok") throw RuntimeException("Could not set up engine !")
        uciInteract("position fen $positionFEN")
    }

    fun setCallbackForUciCommandAnswer(callback: UCICommandAnswerCallback){
        uciSetAnswerCallback(callback)
    }

}
