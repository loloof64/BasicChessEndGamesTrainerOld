package com.loloof64.android.basicchessendgamestrainer.playing_activity

import android.os.Build
import com.github.bhlangonijr.chesslib.move.Move
import com.loloof64.android.basicchessendgamestrainer.MyApplication
import com.loloof64.android.basicchessendgamestrainer.exercise_chooser.buildSquare
import java.io.File
import java.io.FileOutputStream
import java.io.PrintWriter
import java.util.*

fun String.asMove(): Move {
    fun Char.toFile() = this.toInt() - 'a'.toInt()
    fun Char.toRank() = this.toInt() - '1'.toInt()

    return Move(
            buildSquare(this[1].toRank(), this[0].toFile()),
            buildSquare(this[3].toRank(), this[2].toFile())
    )
}

object EngineInteraction {

    private fun sendOutput(outputString: String) {
        val bestMoveLineRegex = Regex("""^bestmove ([a-h][1-8][a-h][1-8])""")
        val scoreRegex = Regex("""score (cp|mate) (\d+)""")
        if (scoreRegex.containsMatchIn(outputString)) {
                val scoreMatcher = scoreRegex.find(outputString)
                val scoreType = scoreMatcher?.groups?.get(1)?.value
                val scoreValue = scoreMatcher?.groups?.get(2)?.value
                when (scoreType){
                    "cp" -> uciObserver.consumeScore(Integer.parseInt(scoreValue))
                    "mate" -> uciObserver.consumeScore(MIN_MATE_SCORE)
                }
        }
        else if (bestMoveLineRegex.containsMatchIn(outputString)) {
                val moveStr = bestMoveLineRegex.find(outputString)?.groups?.get(1)?.value
                uciObserver.consumeMove(moveStr!!.asMove())
        }
        else {
        }
    }

    fun startNewGame(){
        sendCommandToStockfishProcess("ucinewgame")
    }

    fun evaluate(positionFen: String) {
        sendCommandToStockfishProcess("position fen $positionFen")
        sendCommandToStockfishProcess("go")
    }

    private val copyingThread = Thread {
        val inStream = MyApplication.appContext.assets.open(stockfishName)
        val outStream = FileOutputStream(localStockfishPath)
        val buffer = ByteArray(4096)
        var read: Int
        while (true) {
            read = inStream.read(buffer)
            if (read <= 0) break
            outStream.write(buffer, 0, read)
        }
        outStream.close()
        inStream.close()

        // Giving executable right
        Runtime.getRuntime().exec("/system/bin/chmod 744 $localStockfishPath")
    }

    private lateinit var processWriter: PrintWriter
    private lateinit var uciObserver: SimpleUciObserver

    private val stockfishName by lazy {
        @Suppress("DEPRECATION")
        val suffix = when(Build.CPU_ABI){
            "armeabi-v7a" -> "arm7"
            "arm64-v8a" -> "arm8"
            "x86" -> "x86"
            "x86_64" -> "x86_64"
            else -> throw IllegalArgumentException("Unsupported cpu !")
        }
        "Stockfish.$suffix"
    }

    private val localStockfishPath by lazy {
        "${MyApplication.appContext.filesDir.path}/stockfish"
    }

    fun setUciObserver(observer: SimpleUciObserver){
        this.uciObserver = observer
    }

    fun initStockfishProcess() : Boolean {
        if (copyingThread.isAlive) return false
        val process = ProcessBuilder(localStockfishPath).start()
        val processInput = Scanner(process.inputStream)
        processWriter = PrintWriter(process.outputStream)

        Thread {
            while(processInput.hasNextLine()) {
                val line = processInput.nextLine()
                sendOutput(line)
            }
        }.start()
        return true
    }

    fun copyStockfishIntoInternalMemoryIfNecessary(){
        if (!File(localStockfishPath).exists()) {
            copyingThread.start()
        }
    }

    operator fun Regex.contains(text: CharSequence): Boolean = this.matches(text)

    private fun sendCommandToStockfishProcess(command: String){
        processWriter.println(command)
        processWriter.flush()
    }

    fun closeProcess(){
        processWriter.close()
    }


}

interface SimpleUciObserver {
    fun consumeMove(move: Move)
    fun consumeScore(score: Int)
}