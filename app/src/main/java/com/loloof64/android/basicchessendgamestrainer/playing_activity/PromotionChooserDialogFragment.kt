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

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.appcompat.app.AlertDialog.Builder
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import com.loloof64.android.basicchessendgamestrainer.R
import com.loloof64.android.basicchessendgamestrainer.utils.PromotionPieceType
import java.lang.ref.WeakReference

class PromotionPieceChooserDialogFragment : DialogFragment() {

    companion object {
        const val TitleKey = "title"
        const val WhiteToPlayKey = "whiteToPlay"

        fun newInstance(title: String, whiteToPlay: Boolean) : PromotionPieceChooserDialogFragment {
            val dialog = PromotionPieceChooserDialogFragment()
            val args = Bundle()
            args.putString(TitleKey, title)
            args.putBoolean(WhiteToPlayKey, whiteToPlay)
            dialog.arguments = args
            return dialog
        }

        interface Listener {
            fun reactToPromotionPieceSelection(piece: PromotionPieceType)
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
        val title = arguments!!.getString(PromotionPieceChooserDialogFragment.TitleKey)

        val whiteToPlay = arguments!!.getBoolean(WhiteToPlayKey)

        val nullParent: ViewGroup? = null
        val rootView = activity!!.layoutInflater.inflate(R.layout.promotion_chooser_dialog, nullParent)

        promotionChooserQueenButton = rootView.findViewById(R.id.promotion_chooser_queen_button)
        promotionChooserRookButton = rootView.findViewById(R.id.promotion_chooser_rook_button)
        promotionChooserBishopButton = rootView.findViewById(R.id.promotion_chooser_bishop_button)
        promotionChooserKnightButton = rootView.findViewById(R.id.promotion_chooser_knight_button)

        promotionChooserQueenButton.setImageResource(if (whiteToPlay) R.drawable.chess_ql else R.drawable.chess_qd)
        promotionChooserRookButton.setImageResource(if (whiteToPlay) R.drawable.chess_rl else R.drawable.chess_rd)
        promotionChooserBishopButton.setImageResource(if (whiteToPlay) R.drawable.chess_bl else R.drawable.chess_bd)
        promotionChooserKnightButton.setImageResource(if (whiteToPlay) R.drawable.chess_nl else R.drawable.chess_nd)

        queenPromotionListener = PromotionButtonOnClickListener(listener as Listener,
                PromotionPieceType.Queen)
        rookPromotionListener = PromotionButtonOnClickListener(listener as Listener,
                PromotionPieceType.Rook)
        bishopPromotionListener = PromotionButtonOnClickListener(listener as Listener,
                PromotionPieceType.Bishop)
        knightPromotionListener = PromotionButtonOnClickListener(listener as Listener,
                PromotionPieceType.Knight)

        promotionChooserQueenButton.setOnClickListener(queenPromotionListener)
        promotionChooserRookButton.setOnClickListener(rookPromotionListener)
        promotionChooserBishopButton.setOnClickListener(bishopPromotionListener)
        promotionChooserKnightButton.setOnClickListener(knightPromotionListener)

        val dialog = Builder(activity as FragmentActivity).setTitle(title).setView(rootView).create()
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
            }
            else -> throw IllegalArgumentException("Context must use PromotionPieceChooseDialogFragment.Listener trait !")
        }
    }

}

class PromotionButtonOnClickListener(listener: PromotionPieceChooserDialogFragment.Companion.Listener,
                                     private val promotionPiece: PromotionPieceType) : View.OnClickListener {

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

