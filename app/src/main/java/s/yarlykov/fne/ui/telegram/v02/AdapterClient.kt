package s.yarlykov.fne.ui.telegram.v02

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import s.yarlykov.fne.R
import s.yarlykov.fne.ui.telegram.data.ChatMessage
import s.yarlykov.fne.ui.telegram.data.MessageType
import s.yarlykov.fne.utils.logIt

class AdapterClient(private val model: List<String>) : RecyclerView.Adapter<AdapterClient.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val layoutId = R.layout.layout_item_card

        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                layoutId,
                parent,
                false
            )
        )
    }


    override fun getItemCount(): Int {
        return model.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(model[position])
    }

    inner class ViewHolder(private val listItem: View) : RecyclerView.ViewHolder(listItem) {

        private val tvMessage = listItem.findViewById<TextView>(R.id.tv_info)

        fun bind(message: String) {
            tvMessage.text = message
        }

    }
}