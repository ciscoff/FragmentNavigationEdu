package s.yarlykov.fne.ui.telegram.v07

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import s.yarlykov.fne.R

class ActionBarLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    val minHeight: Float
    val maxHeight: Float

    init {
        maxHeight = context.resources.getDimension(R.dimen.avatar_max_height)
        minHeight = context.resources.getDimension(R.dimen.avatar_min_height)
    }

    fun siblingScrollingUp(dy: Int): Boolean {

        var isMoving : Boolean

        layoutParams.apply {
            val h = measuredHeight - dy

            if (h <= minHeight) {
                isMoving = height != minHeight.toInt()
                height = minHeight.toInt()
            } else {
                height = measuredHeight - dy
                isMoving = true
            }
        }

        if (isMoving) requestLayout()
        return isMoving
    }

    fun siblingScrollingDown(dy: Int): Boolean {

        var isMoving = false

        layoutParams.apply {
            val h = measuredHeight + dy

            if (h >= maxHeight) {
                isMoving = height != maxHeight.toInt()
                height = maxHeight.toInt()
            } else {
                height = measuredHeight + dy
                isMoving = true
            }
        }
        if (isMoving) requestLayout()
        return isMoving
    }

}