package s.yarlykov.fne.ui.telegram.v07

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_telegram_v07.*
import s.yarlykov.fne.R
import kotlin.math.abs

class TelegramActivityV07 : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_telegram_v07)
        initList()
    }

    private fun initList() {

        listView.apply {
            layoutManager = CustomLayoutManager(this@TelegramActivityV07, actionBarLayout)
            setHasFixedSize(true)
            itemAnimator = null
            adapter = AdapterLinear()
            addOnScrollListener(scrollListener)
        }
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
//            if (dy > 0) {
//                actionBarLayout.siblingScrollingUp(abs(dy))
//            } else {
//                actionBarLayout.siblingScrollingDown(abs(dy))
//            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
        }
    }
}