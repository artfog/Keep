package com.example.keep
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.net.toUri
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

        if (item.text_title == "")  holder.itemView.textTitle.visibility = View.GONE
        else holder.itemView.textTitle.text = item.text_title

        if (item.text_long == "")  holder.itemView.textMore.visibility = View.GONE
        else holder.itemView.textMore.text = item.text_long

        if (item.text_title == "" && item.text_long == "") {
            holder.itemView.imageShow.scaleType = ImageView.ScaleType.FIT_XY
            holder.itemView.imageShow.setImageURI(item.text_image.toUri())
        }
        else holder.itemView.imageShow.visibility = View.GONE

        if (item.text_bg_color != "") {
            var colorBG = item.text_bg_color
            colorBG = when (colorBG) {
                "Red" -> "#FCA8A2"
                "Green" -> "#A7F282"
                "Blue" -> "#82D2F2"
                "Pink" -> "#F2ACF3"
                else -> "#F2F6BC"
            }
            holder.itemView.cardView.setCardBackgroundColor(Color.parseColor(colorBG))
        }

        holder.itemView.setOnClickListener {
            listener.itemClicked(items[position])
        }

    }
}

