package s.yarlykov.fne.magicrow

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.animation.AnimationSet
import android.view.animation.DecelerateInterpolator
import s.yarlykov.fne.R

open class OnSwipeTouchListener04(private val view: View) :
    OnTouchListener {

    private val context = view.context

    private val animatorViewTranslateX =
        ObjectAnimator.ofFloat(view, "translationX", 0f).apply {
            interpolator = DecelerateInterpolator()
        }

    private val animatorViewTranslateY =
        ObjectAnimator.ofFloat(view, "translationY", 0f).apply {
            interpolator = DecelerateInterpolator()
        }

    private val animationSet = AnimatorSet().apply {
        playTogether(animatorViewTranslateX, animatorViewTranslateY)
        duration = context.resources.getInteger(R.integer.animation_duration).toLong()
    }

    private var dX = 0f
    private var dY = 0f

    override fun onTouch(view: View, event: MotionEvent): Boolean {

        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                dX = view.x - event.rawX
                dY = view.y - event.rawY
                true

            }
            MotionEvent.ACTION_UP -> {
                animationSet.start()
                true
            }
            MotionEvent.ACTION_MOVE -> {

                view.animate()
                    .x(event.rawX + dX)
                    .y(event.rawY + dY)
                    .setDuration(0)
                    .start()
                true
            }
            else -> {
                false
            }
        }
    }
}