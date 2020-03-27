package s.yarlykov.fne.moving

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import s.yarlykov.fne.R

class TextAdapter(private val model : List<String>) : RecyclerView.Adapter<TextAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_list_item,
                parent,
                false
            ))

    }

    override fun getItemCount(): Int {
        return model.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(model[position])
    }

    inner class ViewHolder(private val listItem : View) : RecyclerView.ViewHolder(listItem) {

        private val tvText : TextView = listItem.findViewById(R.id.tv_text)

        fun bind(text : String) {
            tvText.text = text
        }

    }
}