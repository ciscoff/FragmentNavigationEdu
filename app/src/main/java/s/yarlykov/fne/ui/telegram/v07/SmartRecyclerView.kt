package s.yarlykov.fne.ui.telegram.v07

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import s.yarlykov.fne.R
import s.yarlykov.fne.utils.logIt
import kotlin.math.max
import kotlin.math.min

class SmartRecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    private var lastRawY = 0f

    private var minY = 0
    private var maxY = 0

    private val maxOffset: Int
        get() = maxY - minY

    init {
        val tv = TypedValue()

        minY = if (context.theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
        } else {
            context.resources.getDimension(R.dimen.action_bar_min_height).toInt()
        }
        maxY = context.resources.getDimension(R.dimen.action_bar_max_height).toInt()
    }

    private var topOverScroll = false
    private var bottomOverScroll = false

    fun onTopOverScroll() {
        topOverScroll = true
    }

    fun onNoOverScroll() {
        topOverScroll = false
        bottomOverScroll = false
    }

    val isScrollAllowed: Boolean
        get() = !isPullingUp && isPullingDown

    private fun onTouchBegin(view: View, event: MotionEvent): Boolean {
        val rect = Rect()
        view.getGlobalVisibleRect(rect)

        return if (rect.contains(event.rawX.toInt(), event.rawY.toInt())) {
            lastRawY = event.rawY
            topOverScroll = false
            isPullingDown = false
            isPullingUp = false
            true
        } else {
            false
        }
    }

    var isPullingDown = false
    var isPullingUp = false

    var offsetListener : ((Int) -> Unit)? = null

    fun setOnOffsetListener(op: (Int) -> Unit) {
        offsetListener = op
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        val layoutManager = layoutManager as CustomLayoutManager

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                onTouchBegin(this, event)
            }
            MotionEvent.ACTION_MOVE -> {
                // Полная дистанция от места тача до текущего положения пальца
                val rawOffset = event.rawY - lastRawY
                lastRawY = event.rawY

                // Палец тянет вниз
                if (rawOffset > 0) {

                    if (isPullingDown) {
                        // TODO Moving
                        offsetListener?.invoke(rawOffset.toInt())
//                        if (translationY < maxOffset) {
//                            translationY = min(translationY + rawOffset, maxOffset.toFloat())
//                            offsetListener?.invoke(translationY.toInt() + minY)
//                        }

                    } else if (layoutManager.firstVisiblePosition == 0 && layoutManager.firstVisibleTop == 0) {
                        isPullingDown = true
                        isPullingUp = false
                        offsetListener?.invoke(rawOffset.toInt())
                        // TODO Moving
//                        if (translationY < maxOffset) {
//                            translationY = min(translationY + rawOffset, maxOffset.toFloat())
//                            offsetListener?.invoke(translationY.toInt())
//                        }

                    }


//                    if (layoutManager.firstVisiblePosition == 0 && layoutManager.firstVisibleTop == 0/*topOverScroll*/) {
//                        logIt("MotionEvent.ACTION_MOVE, topOverScroll=$topOverScroll", TAG_DEBUG)
//                        if (translationY < maxOffset) {
//                            translationY = min(translationY + rawOffset, maxOffset.toFloat())
//                        }
//                    }
                } else if (rawOffset < 0) {

                    if (isPullingUp) {
//                        offsetListener?.invoke(rawOffset.toInt())
//                        translationY = max(translationY + rawOffset, 0f)
//                        offsetListener?.invoke(translationY.toInt())
                    } else if (layoutManager.firstVisiblePosition == 0 && layoutManager.firstVisibleTop == 0) {
                        isPullingUp = true
                        isPullingDown = false
//                        offsetListener?.invoke(rawOffset.toInt())
//                        translationY = max(translationY + rawOffset, 0f)
//                        offsetListener?.invoke(translationY.toInt())
                    }

//                    if (layoutManager.firstVisiblePosition == 0) {
//                        if (translationY > 0) {
//                            translationY = max(translationY + rawOffset, 0f)
//                        }
//                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                isPullingDown = false
                topOverScroll = false
                bottomOverScroll = false
                isPullingUp = false
            }
        }

        return super.onTouchEvent(event)
    }
}