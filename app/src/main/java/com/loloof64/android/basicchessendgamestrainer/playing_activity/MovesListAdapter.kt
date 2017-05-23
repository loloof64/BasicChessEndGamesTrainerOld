package com.loloof64.android.basicchessendgamestrainer.playing_activity

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.loloof64.android.basicchessendgamestrainer.R
import java.lang.ref.WeakReference

data class RowInput(val san:String, val relatedFen: String, val moveToHighlight: MoveToHighlight)
data class MoveToHighlight(val startFile: Int, val startRank : Int,
                           val endFile: Int, val endRank : Int)

abstract class ItemClickListener {
    abstract fun onClick(weakRefContext: WeakReference<Context>,
                         positionFen: String, moveToHighlight: MoveToHighlight):Unit
}

class MovesListAdapter(val weakRefContext: WeakReference<Context>, val itemClickListener: ItemClickListener) : RecyclerView.Adapter<MovesListAdapter.Companion.ViewHolder>() {
    companion object {
        class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val layout = LayoutInflater.from(parent?.context).inflate(
                R.layout.playing_activity_moves_list_single_item, parent, false) as LinearLayout
        val txtView = layout.findViewById(R.id.moves_list_view_item) as TextView

        layout.removeView(txtView)
        return ViewHolder(txtView)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.textView?.text = inputsList[position].san
        holder?.textView?.setBackgroundColor(
                if (position == _selectedNavigationItem && _switchingPositionFeatureActive) Color.parseColor("#FFDD26")
                else Color.parseColor("#CDCDCD")
        )
        holder?.textView?.setOnClickListener {
            _selectedNavigationItem = position
            updateHostView()
            update()
        }
    }

    override fun getItemCount(): Int {
        return inputsList.size
    }

    fun addPosition(san: String, fen: String, moveToHighlight: MoveToHighlight){
        inputsList.add(RowInput(san, fen, moveToHighlight))
        update()
    }

    fun clear() {
        inputsList.clear()
        update()
    }

    fun goBackInHistory(){
        if (_switchingPositionFeatureActive){
            if (_selectedNavigationItem > 1) {
                _selectedNavigationItem -= 1
                if (_selectedNavigationItem % 3 == 0){ // pointing a move number
                    if (_selectedNavigationItem > 1) _selectedNavigationItem -= 1 // going further back
                    else _selectedNavigationItem += 1 //cancelling
                }
                if (items[_selectedNavigationItem].san == ".."){ // pointing to a 'non-move'
                    _selectedNavigationItem += 1 // cancelling one step
                }
                updateHostView()
                update()
            }
        }
    }

    fun goForwardInHistory(){
        if (_switchingPositionFeatureActive){
            if (_selectedNavigationItem < (inputsList.size - 1)) {
                _selectedNavigationItem += 1
                if (_selectedNavigationItem % 3 == 0){ // pointing a move number
                    if (_selectedNavigationItem < (inputsList.size - 1)) _selectedNavigationItem += 1 // going further
                    else _selectedNavigationItem -= 1 // cancelling
                }
                updateHostView()
                update()
            }
        }
    }

    var switchingPosition: Boolean
        get() = _switchingPositionFeatureActive
        set(value) {
            if (inputsList.size > 0) {
                _switchingPositionFeatureActive = value
                _selectedNavigationItem = inputsList.size - 1
                updateHostView()
                update()
            }
        }

    var items: Array<RowInput>
        get() = inputsList.toTypedArray()
        set(value){
            inputsList = value.toMutableList()
            update()
        }

    var selectedNavigationItem: Int
        get() = _selectedNavigationItem
        set(value) {
            if (_switchingPositionFeatureActive){
                _selectedNavigationItem = value
                updateHostView()
                update()
            }
        }

    private fun updateHostView(){ // switch the current position in host view (Playing activity)
        val relatedFen = inputsList[_selectedNavigationItem].relatedFen
        val moveToHighlight = inputsList[_selectedNavigationItem].moveToHighlight
        if (relatedFen.isNotEmpty() && _switchingPositionFeatureActive) {
            itemClickListener.onClick(weakRefContext, relatedFen, moveToHighlight)
        }
    }

    private fun update(){ // switch the position highlighter in host view (Playing activity)
        notifyDataSetChanged()
    }

    private var inputsList = mutableListOf<RowInput>()
    private var _switchingPositionFeatureActive = false
    private var _selectedNavigationItem = -1
}