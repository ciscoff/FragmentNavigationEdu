package s.yarlykov.fne.ui.telegram.v07

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import s.yarlykov.fne.R
import kotlin.math.abs

class ActionBarLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val minHeight: Float
    private val maxHeight: Float

    init {
        maxHeight = context.resources.getDimension(R.dimen.avatar_max_height)
        minHeight = context.resources.getDimension(R.dimen.avatar_min_height)
    }

    private fun siblingScrollingUp(dy: Int) {
        var isOwnHeightChanged: Boolean

        layoutParams.apply {
            val h = measuredHeight - dy

            if (h <= minHeight) {
                isOwnHeightChanged = measuredHeight != minHeight.toInt()
                height = minHeight.toInt()
            } else {
                height = measuredHeight - dy
                isOwnHeightChanged = true
            }
        }

        if (isOwnHeightChanged) requestLayout()
    }

    private fun siblingScrollingDown(dy: Int) {

        var isOwnHeightChanged : Boolean

        layoutParams.apply {

            val h = measuredHeight + dy

            if (h >= maxHeight) {
                isOwnHeightChanged = measuredHeight != maxHeight.toInt()
                height = maxHeight.toInt()
            } else {
                height = measuredHeight + dy
                isOwnHeightChanged = true
            }
        }
        if (isOwnHeightChanged) requestLayout()
    }

    fun onOffsetChanged(offset: Int) {
        if (offset < 0) {
            siblingScrollingUp(abs(offset))
        } else if (offset > 0) {
            siblingScrollingDown(abs(offset))
        }
    }
}