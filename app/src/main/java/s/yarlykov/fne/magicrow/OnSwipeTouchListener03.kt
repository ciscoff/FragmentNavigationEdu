package s.yarlykov.fne.magicrow

import android.animation.Animator
import android.animation.ObjectAnimator
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.animation.DecelerateInterpolator
import s.yarlykov.fne.R
import s.yarlykov.fne.extensions.dimensionPix
import s.yarlykov.fne.utils.logIt

open class OnSwipeTouchListener03(private val view: View) :
    OnTouchListener {

    private val context = view.context

    private val xShiftF = context.dimensionPix(R.dimen.shift_distance).toFloat()

    private val animationDuration =
        context.resources.getInteger(R.integer.animation_duration).toLong()

    private val animatorViewTranslateLeft =
        ObjectAnimator.ofFloat(view, "translationX", -xShiftF).apply {
            interpolator = DecelerateInterpolator()
            duration = animationDuration
            repeatCount = 0
            addListener(LocalAnimationListener())
        }
    private val animatorViewTranslateRight =
        ObjectAnimator.ofFloat(view, "translationX", xShiftF).apply {
            interpolator = DecelerateInterpolator()
            duration = animationDuration
            repeatCount = 0
            addListener(LocalAnimationListener())
        }
    private val animatorViewTranslateCenter =
        ObjectAnimator.ofFloat(view, "translationX", 0f).apply {
            interpolator = DecelerateInterpolator()
            duration = animationDuration
            repeatCount = 0
            addListener(LocalAnimationListener())
        }


    private val gestureDetector: GestureDetector

    private var inLeftPosition = false
    private var inRightPosition = false
    private var inCenterPosition = true
    private var isAnimating = false

    fun onSwipeLeft() {
        if (inCenterPosition) {
            animatorViewTranslateLeft.start()
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
            animatorViewTranslateRight.start()
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

        return if (gestureDetector.onTouchEvent(event)) {
            true
        } else {
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    isAnimating = false
                    true
                }
                else -> {
                    false
                }
            }
        }
    }

    /**
     * https://stackoverflow.com/questions/9398057/android-move-a-view-on-touch-move-action-move
     */
    private inner class GestureListener : SimpleOnGestureListener() {

        override fun onDown(e: MotionEvent): Boolean {
            view.parent.requestDisallowInterceptTouchEvent(true)
            return true
        }

        override fun onScroll(
            e1: MotionEvent,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            val distanceX = e2.x - e1.x

            // Двинули палец влево
            if (distanceX < 0 && !isAnimating) {
                onSwipeLeft()
            }
            // Двинули палец влево
            else if (distanceX > 0 && !isAnimating) {
                onSwipeRight()
            }

            return true
        }
    }

    private inner class LocalAnimationListener : Animator.AnimatorListener {
        override fun onAnimationRepeat(animation: Animator?) {
        }

        override fun onAnimationEnd(animation: Animator?) {
//            isAnimating = false
        }

        override fun onAnimationCancel(animation: Animator?) {
        }

        override fun onAnimationStart(animation: Animator?) {
            isAnimating = true
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