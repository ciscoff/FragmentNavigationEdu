package s.yarlykov.fne.magicrow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.TextView
import s.yarlykov.fne.R

class MagicRowActivity : AppCompatActivity() {

    private var inLeftPosition = false
    private var inRightPosition = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_magic_row)
        
        val tv = findViewById<TextView>(R.id.text_slider)

        val toLeft = AnimationUtils.loadAnimation(this, R.anim.magicrow_to_left)
        val toRight = AnimationUtils.loadAnimation(this, R.anim.magicrow_to_right)
        val toCenterFromLeft = AnimationUtils.loadAnimation(this, R.anim.magicrow_left_to_center)
        val toCenterFromRight = AnimationUtils.loadAnimation(this, R.anim.magicrow_right_to_center)

        
        tv.setOnTouchListener(object : OnSwipeTouchListener(this) {

            override fun onSwipeLeft() {
                if(inRightPosition) {
//                    tv.translationX = 0f
                    tv.startAnimation(toCenterFromRight)
                    inRightPosition = false
                } else {
//                    tv.translationX = -100f
                    tv.startAnimation(toLeft)
                    inLeftPosition = true
                }
            }

            override fun onSwipeRight() {
                if(inLeftPosition) {
//                    tv.translationX = 0f
                    tv.startAnimation(toCenterFromLeft)
                    inLeftPosition = false
                } else {
//                    tv.translationX = 100f
                    tv.startAnimation(toRight)
                    inRightPosition = true
                }
            }
        })
        
    }
}
