package com.loloof64.android.basicchessendgamestrainer.playing_activity

import android.os.Build
import android.os.Handler
import android.os.Looper
import com.github.bhlangonijr.chesslib.move.Move
import com.loloof64.android.basicchessendgamestrainer.MyApplication
import com.loloof64.android.basicchessendgamestrainer.exercise_chooser.buildSquare
import java.io.*

fun String.asMove(): Move {
    fun Char.toFile() = this.toInt() - 'a'.toInt()
    fun Char.toRank() = this.toInt() - '1'.toInt()

    return Move(
            buildSquare(this[1].toRank(), this[0].toFile()),
            buildSquare(this[3].toRank(), this[2].toFile())
    )
}

fun runOnUI(block : () -> Unit){
    Handler(Looper.getMainLooper()).post(block)
}

class ProcessCommunicator(process: Process) : Runnable {

    override fun run(){
        while(!mustStop) {
            val line = processInput.readLine()
            if (line != null) EngineInteraction.processOutput(line)
        }
    }

    fun sendCommand(command: String){
        processWriter.println(command)
        processWriter.flush()
    }

    fun stop(){
        mustStop = true
        processWriter.close()
        processInput.close()
    }

    private var mustStop = false
    private val processWriter: PrintWriter = PrintWriter(process.outputStream)
    private val processInput: BufferedReader = BufferedReader(InputStreamReader(process.inputStream))
}

object EngineInteraction {

    fun processOutput(outputString: String) {
        val bestMoveLineRegex = Regex("""^bestmove ([a-h][1-8][a-h][1-8])""")
        val scoreRegex = Regex("""score (cp|mate) (\d+)""")
        if (scoreRegex.containsMatchIn(outputString)) {
                val scoreMatcher = scoreRegex.find(outputString)
                val scoreType = scoreMatcher?.groups?.get(1)?.value
                val scoreValue = scoreMatcher?.groups?.get(2)?.value
                runOnUI{
                    when (scoreType){
                        "cp" -> uciObserver.consumeScore(Integer.parseInt(scoreValue))
                        "mate" -> uciObserver.consumeScore(MIN_MATE_SCORE)
                    }
                }
        }
        else if (bestMoveLineRegex.containsMatchIn(outputString)) {
                val moveStr = bestMoveLineRegex.find(outputString)?.groups?.get(1)?.value
                runOnUI { uciObserver.consumeMove(moveStr!!.asMove()) }
        }
        else {
            println("Unrecognized uci output '$outputString'")
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

    private lateinit var uciObserver: SimpleUciObserver
    private lateinit var processCommunicator: ProcessCommunicator

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

    fun initStockfishProcessIfNotDoneYet() : Boolean {
        if (copyingThread.isAlive) return false
        val process = ProcessBuilder(localStockfishPath).start()
        processCommunicator = ProcessCommunicator(process)
        Thread(processCommunicator).apply {
            isDaemon = true
            start()
        }

        return true
    }

    fun copyStockfishIntoInternalMemoryIfNecessary(){
        if (!File(localStockfishPath).exists()) {
            copyingThread.start()
        }
    }

    operator fun Regex.contains(text: CharSequence): Boolean = this.matches(text)

    private fun sendCommandToStockfishProcess(command: String){
        processCommunicator.sendCommand(command)
    }

    fun closeStockfishProcess(){
        processCommunicator.stop()
    }


}

interface SimpleUciObserver {
    fun consumeMove(move: Move)
    fun consumeScore(score: Int)
}