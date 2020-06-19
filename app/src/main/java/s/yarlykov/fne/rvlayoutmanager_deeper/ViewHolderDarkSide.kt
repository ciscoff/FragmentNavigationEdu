package s.yarlykov.fne.rvlayoutmanager_deeper

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import s.yarlykov.fne.R

class ViewHolderDarkSide(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val tvChar = itemView.findViewById<TextView>(R.id.tv_dark)
    private val tvPosition = itemView.findViewById<TextView>(R.id.position_num)

    fun bind(content: String) {
        tvChar.text = content
        tvPosition.text = adapterPosition.toString()
    }
}