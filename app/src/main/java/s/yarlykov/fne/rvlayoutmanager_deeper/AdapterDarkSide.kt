package s.yarlykov.fne.rvlayoutmanager_deeper

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import s.yarlykov.fne.R

class AdapterDarkSide (private val model : ArrayList<String>) : RecyclerView.Adapter<ViewHolderDarkSide>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderDarkSide {
        return ViewHolderDarkSide(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_item_list_view_dark_side,
                parent,
                false
            ))
    }

    override fun getItemCount(): Int  = model.size

    override fun onBindViewHolder(holder: ViewHolderDarkSide, position: Int) {
        holder.bind(model[position])
    }
}