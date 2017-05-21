package com.loloof64.android.basicchessendgamestrainer

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.Toast
import com.loloof64.android.basicchessendgamestrainer.exercise_chooser.availableGenerators
import com.loloof64.android.basicchessendgamestrainer.graphic_board.PromotionPieceChooserDialogFragment
import kotlinx.android.synthetic.main.activity_playing.*
import java.util.*

class PlayingActivity : AppCompatActivity(), PromotionPieceChooserDialogFragment.Companion.Listener {

    companion object {
        val currentPositionkey = "CurrentPosition"
        val playerHasWhiteKey = "PlayerHasWhite"
        val gameFinishedKey = "GameFinished"
        val lastExerciseKey = "LastExercise"
        val playerGoalKey = "PlayerGoal"
        val waitingForPlayerGoalKey = "WaitingForPlayerGoal"
        val generatorIndexKey = "GeneratorIndex"

        val standardFEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"
    }

    override fun reactToPromotionPieceSelection(piece: Short) {
        playingBoard.validatePromotionMove(piece)
        playingBoard.checkIfGameFinished()
        if (!playingBoard.gameFinished()) playingBoard.makeComputerPlay()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playing)

        generatorIndex = intent.extras?.getInt(generatorIndexKey) ?: 0
        val generatedPosition = availableGenerators[generatorIndex].second.generatePosition(random.nextBoolean())
        if (generatedPosition.isNotEmpty()) {
            newGame(generatedPosition)
        }
        else {
            Toast.makeText(this, R.string.position_generation_error, Toast.LENGTH_LONG).show()
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putString(currentPositionkey, playingBoard.toFEN())
        outState?.putBoolean(playerHasWhiteKey, playingBoard.playerHasWhite())
        outState?.putBoolean(gameFinishedKey, playingBoard.gameFinished())
        outState?.putString(lastExerciseKey, lastExercise)
        outState?.putString(playerGoalKey, playingBoard.playerGoal())
        outState?.putBoolean(waitingForPlayerGoalKey, playingBoard.isWaitingForPlayerGoal())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        if (savedInstanceState != null) {
            playingBoard.reloadPosition(fen = savedInstanceState.getString(currentPositionkey),
                    playerHasWhite = savedInstanceState.getBoolean(playerHasWhiteKey),
                    gameFinished = savedInstanceState.getBoolean(gameFinishedKey),
                    waitingForPlayerGoal = savedInstanceState.getBoolean(waitingForPlayerGoalKey)
            )
            lastExercise = savedInstanceState.getString(lastExerciseKey)
            label_player_goal.text = savedInstanceState.getString(playerGoalKey)
        }
    }

    fun askForPromotionPiece() {
        val title = getString(R.string.promotion_chooser_title)
        val dialog = PromotionPieceChooserDialogFragment.newInstance(title, playingBoard.isWhiteToPlay())
        dialog.show(supportFragmentManager, "promotionPieceChooser")
    }

    fun reactForIllegalMove() {
        Toast.makeText(this, R.string.illegal_move, Toast.LENGTH_SHORT).show()
    }

    fun reverseBoard(view: View) {
        playingBoard.reverse()
    }

    /**
     * If playerHasWhite is given null, it will be set to the turn of the given fen
     */
    fun newGame(fen: String = standardFEN){
        lastExercise = fen
        playingBoard.new_game(fen)
    }

    fun restartLastExercise(view: View){
        AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.restarting_exercise_alert_title)
                .setMessage(R.string.restarting_exercise_alert_message)
                .setPositiveButton(R.string.yes, {_, _ -> playingBoard.new_game(lastExercise)})
                .setNegativeButton(R.string.no, null)
                .show()
    }

    fun newExercise(view: View){
        AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.new_exercise_alert_title)
                .setMessage(R.string.new_exercise_alert_message)
                .setPositiveButton(R.string.yes, {_, _ ->
                    val generatedPosition = availableGenerators[generatorIndex].second.generatePosition(random.nextBoolean())
                    newGame(generatedPosition)
                })
                .setNegativeButton(R.string.no, null)
                .show()
    }

    private lateinit var lastExercise:String
    private var generatorIndex: Int = 0
    private var random = Random()
}
