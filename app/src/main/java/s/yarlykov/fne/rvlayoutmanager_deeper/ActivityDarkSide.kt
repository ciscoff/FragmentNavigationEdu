package s.yarlykov.fne.rvlayoutmanager_deeper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import s.yarlykov.fne.R
import s.yarlykov.fne.rvlayoutmanager_deeper.imported.FixedGridLayoutManager

class ActivityDarkSide : AppCompatActivity() {

    private val alphaBeta = ArrayList<String>()

    private lateinit var rv : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dark_side)

        findView()
        warmUp()

        val adapterDarkSide = AdapterDarkSide(alphaBeta)

        rv.apply {
            adapter = adapterDarkSide
//            layoutManager = CustomLinearLayoutManager(context).apply {
//                setOrientation(resources.configuration.orientation)
//            }

            //FixedGridLayoutManager
//            layoutManager = FixedGridLayoutManager().apply {
//                setTotalColumnCount(6)
//            }
            layoutManager = CustomGridLayoutManager(context, 6)
        }
    }

    private fun findView() {
        rv = findViewById(R.id.rv_dark_side)
    }

    private fun warmUp() {
        for (ch in 'a'..'z') {
            alphaBeta.add(ch.toString())
        }
//        for (ch in 'A'..'Z') {
//            alphaBeta.add(ch.toString())
//        }
    }
}