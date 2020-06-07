package s.yarlykov.fne.magicrow

import android.animation.ObjectAnimator
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.widget.Scroller
import s.yarlykov.fne.R
import kotlin.math.abs

open class OnSwipeTouchListener(private val view: View, isViewScrolling : Boolean = true) : OnTouchListener {

    private val context = view.context

    private val toLeft = AnimationUtils.loadAnimation(context, R.anim.magicrow_to_left)
    private val toRight = AnimationUtils.loadAnimation(context, R.anim.magicrow_to_right)
    private val toCenterFromLeft =
        AnimationUtils.loadAnimation(context, R.anim.magicrow_left_to_center)
    private val toCenterFromRight =
        AnimationUtils.loadAnimation(context, R.anim.magicrow_right_to_center)

    private var xShiftF = 200f
    private val xShift = 200

    private val animatorScrollViewContent = ObjectAnimator.ofInt(view, "scrollX", xShift).apply {
        duration = 200
    }
    private val animatorViewTranslateLeft = ObjectAnimator.ofFloat(view, "translationX", -xShiftF).apply {
        interpolator = DecelerateInterpolator()
        duration = 200
    }
    private val animatorViewTranslateRight = ObjectAnimator.ofFloat(view, "translationX", xShiftF).apply {
        interpolator = DecelerateInterpolator()
        duration = 200
    }
    private val animatorViewTranslateCenter = ObjectAnimator.ofFloat(view, "translationX", 0.1f).apply {
        interpolator = DecelerateInterpolator()
        duration = 200
    }


    private val gestureDetector: GestureDetector
    private val scroller = Scroller(context, null, true).apply {
        finalX = 200
    }

    private var inLeftPosition = false
    private var inRightPosition = false
    private var inCenterPosition = true

    fun onSwipeLeft() {
        if(inCenterPosition) {
//            view.startAnimation(toLeft)
            animatorViewTranslateLeft.start()
            inLeftPosition = true
            inRightPosition = false
            inCenterPosition = false
        }
        else if (inRightPosition) {
//            view.startAnimation(toCenterFromRight)
            animatorViewTranslateCenter.start()
            inCenterPosition = true
            inLeftPosition = false
            inRightPosition = false
        }
    }

    fun onSwipeRight() {
        if(inCenterPosition) {
//            view.startAnimation(toRight)
            animatorViewTranslateRight.start()
            inRightPosition = true
            inLeftPosition = false
            inCenterPosition = false
        }
        else if (inLeftPosition) {
//            view.startAnimation(toCenterFromLeft)
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


        var dX = 0f

        override fun onDown(e: MotionEvent): Boolean {
            view.parent.requestDisallowInterceptTouchEvent(true)
            return true
        }



//        override fun onFling(
//            e1: MotionEvent,
//            e2: MotionEvent,
//            velocityX: Float,
//            velocityY: Float
//        ): Boolean {
//            val distanceX = e2.x - e1.x
//            val distanceY = e2.y - e1.y
//            if (abs(distanceX) > Math.abs(distanceY) && abs(
//                    distanceX
//                ) > Companion.SWIPE_DISTANCE_THRESHOLD && abs(
//                    velocityX
//                ) > Companion.SWIPE_VELOCITY_THRESHOLD
//            ) {
//                if (distanceX > 0) onSwipeRight() else onSwipeLeft()
//                return true
//            }
//            return false
//        }

        override fun onScroll(
            e1: MotionEvent,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            val distanceX = e2.x - 0
//            val distanceY = e2.y - e1.y
//
//            if(scroller.computeScrollOffset()) {
//                scroller.forceFinished(true)
//            }
//
//            scroller.startScroll(scroller.currX, scroller.currY, 200, scroller.currY)

            view.animate().x()
//            view.postInvalidate()

            return true
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