package s.yarlykov.fne.rvlayoutmanager_deeper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import s.yarlykov.fne.R

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
            layoutManager = CustomLinearLayoutManager(context).apply {
                setOrientation(resources.configuration.orientation)
            }
        }
    }

    private fun findView() {
        rv = findViewById(R.id.rv_dark_side)
    }

    private fun warmUp() {
        for (ch in 'A'..'S') {
            alphaBeta.add(ch.toString())
        }
    }
}