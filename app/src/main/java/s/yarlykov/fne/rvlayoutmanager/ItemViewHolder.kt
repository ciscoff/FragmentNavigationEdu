package s.yarlykov.fne.rvlayoutmanager

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import s.yarlykov.fne.R

class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val ivPic = itemView.findViewById<ImageView>(R.id.iv_pic)
    private val tvText = itemView.findViewById<TextView>(R.id.tv_text)
    private val context = itemView.context

    fun bind(picId : Int) {
        ivPic.setImageResource(picId)
        tvText.text = context.getString(R.string.rv_item_text)
    }
}