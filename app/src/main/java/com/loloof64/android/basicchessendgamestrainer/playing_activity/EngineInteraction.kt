/*
 * Basic Chess Endgames : generates a position of the endgame you want, then play it against computer.
    Copyright (C) 2017-2018  Laurent Bernabe <laurent.bernabe@gmail.com>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.loloof64.android.basicchessendgamestrainer.playing_activity

import android.os.Build
import android.os.Handler
import android.os.Looper
import com.loloof64.android.basicchessendgamestrainer.MyApplication
import com.loloof64.android.basicchessendgamestrainer.utils.ChessCell
import com.loloof64.android.basicchessendgamestrainer.utils.ChessMove
import com.loloof64.android.basicchessendgamestrainer.utils.PromotionPieceType
import java.io.*

fun String.asMove(): ChessMove {
    fun Char.toFile() = this.toInt() - 'a'.toInt()
    fun Char.toRank() = this.toInt() - '1'.toInt()

    return ChessMove(
            ChessCell(rank = this[1].toRank(), file = this[0].toFile()),
            ChessCell(rank = this[3].toRank(), file = this[2].toFile())
    )
}

fun String.getPromotionPiece(): PromotionPieceType {
    val lastChar = this.last()
    return when (lastChar){
        'Q' -> PromotionPieceType.Queen
        'R' -> PromotionPieceType.Rook
        'B' -> PromotionPieceType.Bishop
        'N' -> PromotionPieceType.Knight
        else -> PromotionPieceType.Queen
    }
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
                runOnUI { uciObserver.consumeMove(moveStr!!.asMove(), moveStr.getPromotionPiece()) }
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
    fun consumeMove(move: ChessMove, promotionPieceType: PromotionPieceType = PromotionPieceType.Queen)
    fun consumeScore(score: Int)
}