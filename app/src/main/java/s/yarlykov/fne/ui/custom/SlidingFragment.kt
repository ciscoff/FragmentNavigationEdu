package s.yarlykov.fne.ui.custom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import s.yarlykov.fne.R


class SlidingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  {

        val root = inflater.inflate(R.layout.fragment_sliding, container, false)

        root
            .findViewById<TextView>(R.id.tv_content)
            .setBackgroundColor(this.hashCode())
        return root
    }
}