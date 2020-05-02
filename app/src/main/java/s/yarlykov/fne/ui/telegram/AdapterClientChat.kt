package s.yarlykov.fne.ui.telegram

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

class AdapterClientChat(private val model: List<ChatMessage>) : RecyclerView.Adapter<AdapterClientChat.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val layoutId = when (viewType) {
            MessageType.Client.ordinal -> {
                R.layout.layout_item_chat_client
            }
            else -> {
                R.layout.layout_item_chat_doer
            }
        }

        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                layoutId,
                parent,
                false
            )
        )
    }

    override fun getItemViewType(position: Int): Int {
        logIt("Chat: getItemViewType = ${model[position].type}")
        return model[position].type.ordinal
    }

    override fun getItemCount(): Int {
        logIt("Chat: getItemCount = ${model.size}")
        return model.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(model[position])
    }

    inner class ViewHolder(private val listItem: View) : RecyclerView.ViewHolder(listItem) {

        private val tvMessage = listItem.findViewById<TextView>(R.id.tv_message)
        private val tvTime = listItem.findViewById<TextView>(R.id.tv_time)

        private val ivLogo = listItem.findViewById<ImageView>(R.id.iv_logo)
        private val ivStatus = listItem.findViewById<ImageView>(R.id.iv_status)
        private val ivThumbnail = listItem.findViewById<ImageView>(R.id.iv_thumbnail)

        fun bind(message: ChatMessage) {

            when (message.type) {
                MessageType.Client -> {
                    tvMessage.text = message.message
                    tvTime.text = message.date
                }
                MessageType.Doer -> {
                    tvMessage.text = message.message
                    tvTime.text = message.date
                }
                else -> {
                }
            }
        }

    }
}