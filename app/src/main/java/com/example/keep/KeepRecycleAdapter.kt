package com.example.keep
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
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
        holder.itemView.setOnClickListener {
            listener.itemClicked(items[position])
        }

    }
}

