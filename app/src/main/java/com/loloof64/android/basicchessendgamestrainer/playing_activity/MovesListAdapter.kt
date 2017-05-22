package com.loloof64.android.basicchessendgamestrainer.playing_activity

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.loloof64.android.basicchessendgamestrainer.R

class MovesListAdapter : RecyclerView.Adapter<MovesListAdapter.Companion.ViewHolder>() {
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
        holder?.textView?.text = inputsList[position]
    }

    override fun getItemCount(): Int {
        return inputsList.size
    }

    fun addText(txt: String){
        inputsList.add(txt)
        update()
    }

    fun clear() {
        inputsList.clear()
        update()
    }

    var items: Array<String>
        get() = inputsList.toTypedArray()
        set(value){
            inputsList = value.toMutableList()
            update()
        }

    private fun update(){
        notifyDataSetChanged()
    }

    private var inputsList = mutableListOf<String>()
}