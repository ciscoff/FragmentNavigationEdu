package s.yarlykov.fne.magicrow

import android.animation.ObjectAnimator
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.animation.DecelerateInterpolator
import s.yarlykov.fne.R
import s.yarlykov.fne.extensions.dimensionPix
import kotlin.math.abs

open class OnSwipeTouchListener02(private val view: View) : OnTouchListener {

    private val context = view.context

    private val xShiftF = context.dimensionPix(R.dimen.shift_distance).toFloat()
    private val animationDuration =
        context.resources.getInteger(R.integer.animation_duration).toLong()


    private val animatorTranslateLeft =
        ObjectAnimator.ofFloat(view, "translationX", -xShiftF).apply {
            interpolator = DecelerateInterpolator()
            duration = animationDuration
        }
    private val animatorTranslateRight =
        ObjectAnimator.ofFloat(view, "translationX", xShiftF).apply {
            interpolator = DecelerateInterpolator()
            duration = animationDuration
        }
    private val animatorViewTranslateCenter =
        ObjectAnimator.ofFloat(view, "translationX", 0.1f).apply {
            interpolator = DecelerateInterpolator()
            duration = animationDuration
        }

    private val gestureDetector: GestureDetector


    private var inLeftPosition = false
    private var inRightPosition = false
    private var inCenterPosition = true

    fun onSwipeLeft() {
        if (inCenterPosition) {
            animatorTranslateLeft.start()
            inLeftPosition = true
            inRightPosition = false
            inCenterPosition = false
        } else if (inRightPosition) {
            animatorViewTranslateCenter.start()
            inCenterPosition = true
            inLeftPosition = false
            inRightPosition = false
        }
    }

    fun onSwipeRight() {
        if (inCenterPosition) {
            animatorTranslateRight.start()
            inRightPosition = true
            inLeftPosition = false
            inCenterPosition = false
        } else if (inLeftPosition) {
            animatorViewTranslateCenter.start()
            inCenterPosition = true
            inLeftPosition = false
            inRightPosition = false
        }
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    /**
     * https://stackoverflow.com/questions/9398057/android-move-a-view-on-touch-move-action-move
     */
    private inner class GestureListener : SimpleOnGestureListener() {

        override fun onDown(e: MotionEvent): Boolean {
            view.parent.requestDisallowInterceptTouchEvent(true)
            return true
        }

        override fun onFling(
            e1: MotionEvent,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            val distanceX = e2.x - e1.x
            val distanceY = e2.y - e1.y
            if (abs(distanceX) > Math.abs(distanceY) && abs(
                    distanceX
                ) > Companion.SWIPE_DISTANCE_THRESHOLD && abs(
                    velocityX
                ) > Companion.SWIPE_VELOCITY_THRESHOLD
            ) {
                if (distanceX > 0) onSwipeRight() else onSwipeLeft()
                return true
            }
            return false
        }

    }

    companion object {
        private const val SWIPE_DISTANCE_THRESHOLD = 20
        private const val SWIPE_VELOCITY_THRESHOLD = 20
    }

    init {
        gestureDetector = GestureDetector(context, GestureListener())
    }
}