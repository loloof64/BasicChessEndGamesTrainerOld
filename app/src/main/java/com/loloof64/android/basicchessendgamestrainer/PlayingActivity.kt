package com.loloof64.android.basicchessendgamestrainer

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.Toast
import com.loloof64.android.basicchessendgamestrainer.graphic_board.PromotionPieceChooserDialogFragment
import kotlinx.android.synthetic.main.activity_playing.*

class PlayingActivity : AppCompatActivity(), PromotionPieceChooserDialogFragment.Companion.Listener {

    companion object {
        val currentPositionkey = "CurrentPosition"
        val playerHasWhiteKey = "PlayerHasWhite"
    }

    override fun reactToPromotionPieceSelection(piece: Int) {
        playingBoard.validatePromotionMove(piece)
        playingBoard.makeComputerPlay()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playing)

        /////////////////
        newGame()
        ////////////////////////
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putString(currentPositionkey, playingBoard.toFEN())
        outState?.putBoolean(playerHasWhiteKey, playingBoard.playerHasWhite())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        if (savedInstanceState != null) {
            newGame(savedInstanceState.getString(currentPositionkey),
                    savedInstanceState.getBoolean(playerHasWhiteKey))
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

    fun newGame(fen: String = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", playerHasWhite: Boolean? = null){
        playingBoard.new_game(fen, playerHasWhite)
        (applicationContext as MyApplication).uciNewGame(fen)
    }
}
