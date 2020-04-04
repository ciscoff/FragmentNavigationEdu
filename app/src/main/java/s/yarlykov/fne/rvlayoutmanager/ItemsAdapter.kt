package s.yarlykov.fne.rvlayoutmanager

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import s.yarlykov.fne.R

class ItemsAdapter(private val model: List<Int>) :
    RecyclerView.Adapter<ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_list_item_for_cool_rv,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = model.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(model[position])
    }
}