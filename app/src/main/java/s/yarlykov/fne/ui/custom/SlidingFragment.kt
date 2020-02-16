package s.yarlykov.fne.ui.custom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import s.yarlykov.fne.R


class SlidingFragment : Fragment() {

    companion object {
        fun create(num: Int): SlidingFragment {
            return SlidingFragment().also {f ->
                f.arguments = Bundle().also { it.putInt("num", num) }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val num = arguments?.getInt("num", 1)

        val root = inflater.inflate(R.layout.fragment_sliding, container, false)

        root
            .findViewById<TextView>(R.id.tv_content)
            .setBackgroundColor(this.hashCode())



        when(num) {
            0 -> {
                root.findViewById<Button>(R.id.btn_1).visibility = View.VISIBLE
            }
            1 -> {
                root.findViewById<Button>(R.id.btn_1).visibility = View.VISIBLE
                root.findViewById<Button>(R.id.btn_2).visibility = View.VISIBLE
            }
            2 -> {
                root.findViewById<Button>(R.id.btn_1).visibility = View.VISIBLE
                root.findViewById<Button>(R.id.btn_2).visibility = View.VISIBLE
                root.findViewById<Button>(R.id.btn_3).visibility = View.VISIBLE
            }
        }

        return root
    }
}