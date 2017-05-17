package com.loloof64.android.basicchessendgamestrainer

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.loloof64.android.basicchessendgamestrainer.graphic_board.PromotionPieceChooserDialogFragment
import kotlinx.android.synthetic.main.activity_playing.*

class PlayingActivity : AppCompatActivity(), PromotionPieceChooserDialogFragment.Companion.Listener {

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

    fun newGame(fen: String = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"){
        playingBoard.new_game(fen)
        (applicationContext as MyApplication).uciNewGame(fen)
    }
}
