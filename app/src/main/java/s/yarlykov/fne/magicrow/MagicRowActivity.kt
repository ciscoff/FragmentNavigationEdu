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

        findViewById<TextView>(R.id.text_slider).apply {
            setOnTouchListener(OnSwipeTouchListener(this))
        }

    }
}
