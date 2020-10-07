package s.yarlykov.fne.ui.telegram.v07

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import s.yarlykov.fne.utils.logIt
import kotlin.math.abs

class CustomLayoutManager(context: Context, private val appBar: ActionBarLayout) :
    LinearLayoutManager(context) {

    lateinit var recyclerView: SmartRecyclerView

    override fun onAttachedToWindow(view: RecyclerView) {
        super.onAttachedToWindow(view)
        recyclerView = view as SmartRecyclerView
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        super.onLayoutChildren(recycler, state)
        logIt("onLayoutChildren", TAG_DEBUG)
    }


    val firstVisiblePosition: Int
        get() {
            return getChildAt(0)?.let { child ->
                getPosition(child)
            } ?: NOT_FIRST_ON_TOP
        }

    val firstVisibleTop: Int
        get() {
            return getChildAt(0)?.let { child ->
                getDecoratedTop(child)
            } ?: Int.MIN_VALUE
        }

    override fun scrollVerticallyBy(
        dy: Int,
        recycler: RecyclerView.Recycler?,
        state: RecyclerView.State?
    ): Int {
        val scrollRange = super.scrollVerticallyBy(dy, recycler, state)
        val overScroll = dy - scrollRange

//        if(abs(overScroll) in 1..8) return 0

        if (overScroll < 0) {
            // TODO TOP_OVER_SCROLL
            logIt("TOP_OVER_SCROLL", TAG_DEBUG)
//            recyclerView.onTopOverScroll()
        } else if (overScroll > 0) {
            // TODO BOTTOM_OVER_SCROLL
            logIt("BOTTOM_OVER_SCROLL", TAG_DEBUG)
//            recyclerView.bottomOverScroll = true
        } else {
//            logIt("NO_OVER_SCROLL", TAG_DEBUG)
//            recyclerView.onNoOverScroll()
        }

        return if(recyclerView.isScrollAllowed) scrollRange else 0
    }

//    override fun scrollVerticallyBy(
//        dy: Int,
//        recycler: RecyclerView.Recycler?,
//        state: RecyclerView.State?
//    ): Int {
//        val scrollRange = super.scrollVerticallyBy(dy, recycler, state)
//        val overScroll = dy - scrollRange
//
//        var result = scrollRange
//
//        val y = when {
//            // TODO TOP_OVER_SCROLL
//            (overScroll < 0) -> {
//                logIt("TOP_OVER_SCROLL=$overScroll, dy=$dy", TAG_DEBUG)
//                appBar.siblingScrollingDown(abs(dy))
//                0
//            }
//            // TODO BOTTOM_OVER_SCROLL
//            (overScroll > 0) -> {
//                logIt("BOTTOM_OVER_SCROLL=$overScroll, dy=$dy", TAG_DEBUG)
//                scrollRange
//            }
//            else -> {
//                // SCROLL UP
//                if (dy > 0) {
//
//                    logIt("SCROLL_UP=$dy", TAG_DEBUG)
//                    if (appBar.siblingScrollingUp(abs(dy))) {
//                        0
//                    } else {
//                        scrollRange
//                    }
//                }
//                // SCROLL DOWN
//                else {
//                    logIt("SCROLL_DOWN=$dy", TAG_DEBUG)
//                    scrollRange
//                }
//            }
//        }
//        return y
//    }

}