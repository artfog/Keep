package com.example.keep
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_add_text.*
import kotlinx.android.synthetic.main.item_text.view.*



class KeepRecyclerAdapter(
    private val listener: ClickListener,
    private val items: MutableList<TextItems>
) :
    RecyclerView.Adapter<KeepRecyclerAdapter.KeepViewHolder>() {

    class KeepViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KeepViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_text, parent, false)
        return KeepViewHolder(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: KeepViewHolder, position: Int) {
        val item = items[position]
        holder.itemView.textTitle.text = item.text_title
        holder.itemView.textMore.text = item.text_long
        var colorBG = item.text_bg_color
        if (colorBG == "Red") colorBG = "#F09393"
        else if(colorBG == "Green") colorBG = "#A7F282"
        else if(colorBG == "Blue") colorBG = "#82D2F2"
        else if(colorBG == "Pink") colorBG = "#F2ACF3"
        else colorBG = "#F2F6BC"
        if (item.text_bg_color != "") holder.itemView.setBackgroundColor(Color.parseColor(colorBG))
        holder.itemView.setOnClickListener {
            listener.itemClicked(items[position])
        }

    }
}

