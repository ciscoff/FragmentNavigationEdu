package s.yarlykov.fne.magicrow

import android.animation.ObjectAnimator
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.animation.DecelerateInterpolator
import s.yarlykov.fne.R
import s.yarlykov.fne.utils.logIt
import kotlin.math.abs

open class OnSwipeTouchListener05(private val view: View) :
    OnTouchListener {

    private val context = view.context


    private fun animator(shift: Float) =
        ObjectAnimator.ofFloat(view, "translationX", shift).apply {
            interpolator = DecelerateInterpolator()
            duration = context.resources.getInteger(R.integer.animation_duration).toLong()
        }

    private var dX = 0f

    override fun onTouch(view: View, event: MotionEvent): Boolean {

        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                dX = view.x - event.rawX
                true
            }
            MotionEvent.ACTION_UP -> {
                System.out.println("view.x=${view.x}, view.width=${view.width}")

                if(view.x < 0 && abs(view.x) > view.width/2) {
                    animator(-(view.width).toFloat()).start()
                } else if (view.x > 0 && view.x > view.width/2) {
                    animator((view.width).toFloat()).start()
                } else {
                    animator(0f).start()
                }

                // Альтернативный вариант анимации вместо ObjectAnimator
//                view.animate()
//                    .x(0f)
//                    .setDuration(context.resources.getInteger(R.integer.animation_duration).toLong())
//                    .start()
                true
            }
            MotionEvent.ACTION_MOVE -> {

                view.animate()
                    .x(event.rawX + dX)
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