package s.yarlykov.fne.ui.telegram.v07

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_telegram_v07.*
import kotlinx.android.synthetic.main.activity_telegram_v07.listView
import kotlinx.android.synthetic.main.layout_test_coordinator.*
import s.yarlykov.fne.R

class TelegramActivityV07 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_telegram_v07)
//        setContentView(R.layout.layout_test_coordinator)
//        collapsingToolbar.title = "HELLO WORLD"
        initList()

        val y = com.google.android.material.appbar.AppBarLayout.ScrollingViewBehavior()
    }

    private fun initList() {

        listView.apply {
            layoutManager = CustomLayoutManager(this@TelegramActivityV07, actionBarLayout)
            setHasFixedSize(true)
            itemAnimator = null
            adapter = AdapterLinear()
//            addOnScrollListener(scrollListener)
        }

        (listView as SmartRecyclerView).setOnOffsetListener(::temt)
    }

    fun temt(offset : Int) {
        actionBarLayout.onOffsetChanged(offset)
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