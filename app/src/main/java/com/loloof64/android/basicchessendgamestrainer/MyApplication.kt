package com.loloof64.android.basicchessendgamestrainer

import android.app.Application
import android.content.Context
import android.content.res.AssetManager
import java.io.*
import java.util.logging.Logger

interface UCICommandAnswerCallback {
    fun execute(answer: String): Unit
}

class MyApplication: Application(){

    companion object {
        init {
            System.loadLibrary("stockfish_8")
        }

        fun getApplicationContext() = appContext
        fun setApplicationContext(ctx: Context){
            appContext = ctx
        }

        lateinit var appContext: Context
    }

    private external fun uciStart()
    external fun uciInteract(uciCmd: String): String
    external fun uciSetAnswerCallback(callback: UCICommandAnswerCallback)
    external fun uciEnd()

    override fun onCreate() {
        super.onCreate()
        setApplicationContext(this)
        uciStart()
        uciSetup()
    }

    fun uciSetup(){
        copySyzygyAssetsToDataFolder()

        val uciCmdLastResult = uciInteract("uci").split("\n").filter{ it.isNotEmpty() }.last()
        if (uciCmdLastResult != "uciok") throw RuntimeException("Could not set up engine !")
        val syzygyPath = File(filesDir, "syzygy").absolutePath
        uciInteract("setoption name SyzygyPath value $syzygyPath")
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

    private fun copySyzygyAssetsToDataFolder(){
        val assetsManager = assets
        val files = assetsManager.list("syzygy")
        files.forEach {
            try {
                val inStream = assetsManager.open("syzygy/$it", AssetManager.ACCESS_BUFFER)
                val outDir = File(filesDir, "syzygy")
                outDir.mkdir()
                val outFile = File(outDir, it)

                val outStream = FileOutputStream(outFile)
                copyFile(inStream, outStream)
                inStream.close()
                outStream.flush()
                outStream.close()
            } catch(e: IOException){
                Logger.getLogger("BasicChessEndgamesTrainer").info("Failed to copy asset file syzygy/$it !")
                Logger.getLogger("BasicChessEndgamesTrainer").info(e.message)
            }
        }
    }

    private fun copyFile(inStream: InputStream, outStream: OutputStream){
        val buffer = ByteArray(1024, { _ -> 0})
        var read:Int
        while (true){
            read = inStream.read(buffer)
            if (read == -1) break
            outStream.write(buffer, 0, read)
        }
    }

}
