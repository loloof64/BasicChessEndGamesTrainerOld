package com.loloof64.android.basicchessendgamestrainer

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.loloof64.android.basicchessendgamestrainer.graphic_board.PromotionPieceChooserDialogFragment
import kotlinx.android.synthetic.main.activity_playing.*

class PlayingActivity : AppCompatActivity(), PromotionPieceChooserDialogFragment.Companion.Listener {

    companion object {
        val currentPositionkey = "CurrentPosition"
        val playerHasWhiteKey = "PlayerHasWhite"
        val gameFinishedKey = "GameFinished"
        val positionToSetupKey = "PositionToSetup"
        val lastExerciseKey = "LastExercise"

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

        newGame(intent.extras?.getString(positionToSetupKey) ?: standardFEN)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putString(currentPositionkey, playingBoard.toFEN())
        outState?.putBoolean(playerHasWhiteKey, playingBoard.playerHasWhite())
        outState?.putBoolean(gameFinishedKey, playingBoard.gameFinished())
        outState?.putString(lastExerciseKey, lastExercise)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        if (savedInstanceState != null) {
            playingBoard.reloadPosition(fen = savedInstanceState.getString(currentPositionkey),
                    playerHasWhite = savedInstanceState.getBoolean(playerHasWhiteKey),
                    gameFinished = savedInstanceState.getBoolean(gameFinishedKey))
            lastExercise = savedInstanceState.getString(lastExerciseKey)
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
        (applicationContext as MyApplication).uciNewGame(fen)
        (applicationContext as MyApplication).uciInteract("go")
    }

    fun restartLastExercise(view: View){
        playingBoard.new_game(lastExercise)
    }

    private lateinit var lastExercise:String
}
