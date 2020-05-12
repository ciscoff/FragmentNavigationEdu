package s.yarlykov.fne.ui.telegram.v05

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import s.yarlykov.fne.R
import s.yarlykov.fne.extensions.setRoundedDrawable

class TelegramActivityV5 : AppCompatActivity() {

    lateinit var roundedDrawable: RoundedBitmapDrawable
    lateinit var ivAvatar: ImageView
    lateinit var collapsingToolbar : CollapsingToolbarLayout
    lateinit var appBar : AppBarLayout

    var cornerRadiusMax = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_telegram_v05)

        findView()
    }

    private fun findView() {
        appBar = findViewById(R.id.appbar_layout)
        collapsingToolbar = findViewById(R.id.collapsing_toolbar)

        ivAvatar = findViewById(R.id.iv_rounded)
        ivAvatar.setRoundedDrawable(R.drawable.valery)


        roundedDrawable = ivAvatar.drawable as RoundedBitmapDrawable
        cornerRadiusMax = roundedDrawable.cornerRadius

        appBar.setExpanded(false)
    }

}