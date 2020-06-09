package s.yarlykov.fne.magicrow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.TextView
import s.yarlykov.fne.R

class MagicRowActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_magic_row)

        findViewById<TextView>(R.id.text_slider_1).apply {
            setOnTouchListener(OnSwipeTouchListener01(this))
        }

        findViewById<TextView>(R.id.text_slider_2).apply {
            setOnTouchListener(OnSwipeTouchListener02(this))
        }

        findViewById<TextView>(R.id.text_slider_3).apply {
            setOnTouchListener(OnSwipeTouchListener03(this))
        }

        findViewById<TextView>(R.id.text_slider_4).apply {
            setOnTouchListener(OnSwipeTouchListener04(this))
        }

        findViewById<TextView>(R.id.text_slider_5).apply {
            setOnTouchListener(OnSwipeTouchListener05(this))
        }
    }
}
