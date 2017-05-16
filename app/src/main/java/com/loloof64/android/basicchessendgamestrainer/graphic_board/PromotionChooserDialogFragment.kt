package com.loloof64.android.basicchessendgamestrainer.graphic_board

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog.Builder
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import com.loloof64.android.basicchessendgamestrainer.R
import ictk.boardgame.chess.Bishop
import ictk.boardgame.chess.Knight
import ictk.boardgame.chess.Queen
import ictk.boardgame.chess.Rook
import java.lang.ref.WeakReference

class PromotionPieceChooserDialogFragment : DialogFragment() {

    companion object {
        val TitleKey = "title"
        val WhiteToPlayKey = "whiteToPlay"

        fun newInstance(title: String, whiteToPlay: Boolean) : PromotionPieceChooserDialogFragment {
            val dialog = PromotionPieceChooserDialogFragment()
            val args = Bundle()
            args.putString(TitleKey, title)
            args.putBoolean(WhiteToPlayKey, whiteToPlay)
            dialog.arguments = args
            return dialog
        }

        interface Listener {
            fun reactToPromotionPieceSelection(piece: Int) : Unit
        }
    }

    private var listener : Listener? = null

    private lateinit var promotionChooserQueenButton : ImageButton
    private lateinit var promotionChooserRookButton : ImageButton
    private lateinit var promotionChooserBishopButton : ImageButton
    private lateinit var promotionChooserKnightButton : ImageButton

    private lateinit var queenPromotionListener: PromotionButtonOnClickListener
    private lateinit var rookPromotionListener: PromotionButtonOnClickListener
    private lateinit var bishopPromotionListener: PromotionButtonOnClickListener
    private lateinit var knightPromotionListener: PromotionButtonOnClickListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val title = arguments.getString(PromotionPieceChooserDialogFragment.TitleKey)

        val whiteToPlay = arguments.getBoolean(WhiteToPlayKey)

        val nullParent: ViewGroup? = null
        val rootView = activity.layoutInflater.inflate(R.layout.promotion_chooser_dialog, nullParent)

        promotionChooserQueenButton = rootView.findViewById(R.id.promotion_chooser_queen_button) as ImageButton
        promotionChooserRookButton = rootView.findViewById(R.id.promotion_chooser_rook_button) as ImageButton
        promotionChooserBishopButton = rootView.findViewById(R.id.promotion_chooser_bishop_button) as ImageButton
        promotionChooserKnightButton = rootView.findViewById(R.id.promotion_chooser_knight_button) as ImageButton

        promotionChooserQueenButton.setImageResource(if (whiteToPlay) R.drawable.chess_ql else R.drawable.chess_qd)
        promotionChooserRookButton.setImageResource(if (whiteToPlay) R.drawable.chess_rl else R.drawable.chess_rd)
        promotionChooserBishopButton.setImageResource(if (whiteToPlay) R.drawable.chess_bl else R.drawable.chess_bd)
        promotionChooserKnightButton.setImageResource(if (whiteToPlay) R.drawable.chess_nl else R.drawable.chess_nd)

        promotionChooserQueenButton.setOnClickListener(queenPromotionListener)
        promotionChooserRookButton.setOnClickListener(rookPromotionListener)
        promotionChooserBishopButton.setOnClickListener(bishopPromotionListener)
        promotionChooserKnightButton.setOnClickListener(knightPromotionListener)

        val dialog = Builder(activity).setTitle(title).setView(rootView).create()
        queenPromotionListener.setDialog(dialog)
        rookPromotionListener.setDialog(dialog)
        bishopPromotionListener.setDialog(dialog)
        knightPromotionListener.setDialog(dialog)
        return dialog
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        when(context) {
            is Listener -> {
                listener = context
                queenPromotionListener = PromotionButtonOnClickListener(listener as Listener, Queen.INDEX.toInt())
                rookPromotionListener = PromotionButtonOnClickListener(listener as Listener, Rook.INDEX.toInt())
                bishopPromotionListener = PromotionButtonOnClickListener(listener as Listener, Bishop.INDEX.toInt())
                knightPromotionListener = PromotionButtonOnClickListener(listener as Listener, Knight.INDEX.toInt())
            }
            else -> throw IllegalArgumentException("Context must use PromotionPieceChooseDialogFragment.Listener trait !")
        }
    }

}

class PromotionButtonOnClickListener(listener: PromotionPieceChooserDialogFragment.Companion.Listener,
                                     val promotionPiece: Int) : View.OnClickListener {

    override fun onClick(relatedView: View?) {
        refListener.get()?.reactToPromotionPieceSelection(promotionPiece)
        refDialog.get()?.dismiss()
    }

    fun setDialog(dialog: Dialog){
        refDialog = WeakReference(dialog)
    }

    private val refListener = WeakReference(listener)
    private lateinit var refDialog: WeakReference<Dialog>
}

