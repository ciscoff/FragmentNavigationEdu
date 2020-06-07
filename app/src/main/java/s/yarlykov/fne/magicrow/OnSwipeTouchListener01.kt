package s.yarlykov.fne.magicrow

import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.animation.AnimationUtils
import s.yarlykov.fne.R
import kotlin.math.abs

open class OnSwipeTouchListener01(private val view: View) : OnTouchListener {

    private val context = view.context

    private val toLeft = AnimationUtils.loadAnimation(context, R.anim.magicrow_to_left)
    private val toRight = AnimationUtils.loadAnimation(context, R.anim.magicrow_to_right)
    private val toCenterFromLeft =
        AnimationUtils.loadAnimation(context, R.anim.magicrow_left_to_center)
    private val toCenterFromRight =
        AnimationUtils.loadAnimation(context, R.anim.magicrow_right_to_center)

    private val gestureDetector: GestureDetector

    private var inLeftPosition = false
    private var inRightPosition = false
    private var inCenterPosition = true

    fun onSwipeLeft() {
        if(inCenterPosition) {
            view.startAnimation(toLeft)
            inLeftPosition = true
            inRightPosition = false
            inCenterPosition = false
        }
        else if (inRightPosition) {
            view.startAnimation(toCenterFromRight)
            inCenterPosition = true
            inLeftPosition = false
            inRightPosition = false
        }
    }

    fun onSwipeRight() {
        if(inCenterPosition) {
            view.startAnimation(toRight)
            inRightPosition = true
            inLeftPosition = false
            inCenterPosition = false
        }
        else if (inLeftPosition) {
            view.startAnimation(toCenterFromLeft)
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
            if (abs(distanceX) > abs(distanceY) && abs(
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