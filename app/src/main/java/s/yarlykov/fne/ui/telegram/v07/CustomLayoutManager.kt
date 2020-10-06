package s.yarlykov.fne.ui.telegram.v07

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import s.yarlykov.fne.utils.logIt
import kotlin.math.abs

class CustomLayoutManager(context: Context, private val appBar: ActionBarLayout) :
    LinearLayoutManager(context) {

    override fun scrollVerticallyBy(
        dy: Int,
        recycler: RecyclerView.Recycler?,
        state: RecyclerView.State?
    ): Int {
        val scrollRange = super.scrollVerticallyBy(dy, recycler, state)
        val overScroll = dy - scrollRange

        logIt("overScroll=$overScroll, dy=$dy")

        var result = scrollRange

        val y =  when {
            // TODO TOP_OVER_SCROLL
            (overScroll < 0) -> {
                appBar.siblingScrollingDown(abs(dy))
                scrollRange
            }
            // TODO BOTTOM_OVER_SCROLL
            (overScroll > 0) -> {
                scrollRange
            }
            else -> {
                // SCROLL UP
                if (dy > 0) {
                    if (appBar.siblingScrollingUp(abs(dy))) {
                        0
                    } else {
                        scrollRange
                    }
                }
                // SCROLL DOWN
                else {
                    scrollRange
                }
            }
        }

        logIt("y=$y", "TETE")
        return y
//
//
//
//        if (overScroll < 0) {
//
//            return if (appBar.siblingScrollingDown(abs(overScroll))) {
//                0
//            } else {
//                scrollRange
//            }
//        }
//
//
//
//        if (overScroll > 0) {
//            // TODO BOTTOM_OVER_SCROLL
//            if (appBar.siblingScrollingUp(abs(overScroll))) {
//                result = 0
//            }
//
//        }
//
//        return result
    }
}