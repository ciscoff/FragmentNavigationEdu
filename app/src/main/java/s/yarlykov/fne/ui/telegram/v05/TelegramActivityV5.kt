package s.yarlykov.fne.ui.telegram.v05

import android.os.Bundle
import android.view.animation.AccelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import s.yarlykov.fne.R
import s.yarlykov.fne.extensions.setRoundedDrawable
import kotlin.math.max

class TelegramActivityV5 : AppCompatActivity() {

    lateinit var collapsingToolbar: CollapsingToolbarLayout
    lateinit var roundedDrawable: RoundedBitmapDrawable
    lateinit var appBar: AppBarLayout
    lateinit var ivAvatar: ImageView
    lateinit var tvLog: TextView

    var cornerRadiusMax = 0f
    var step = 1f

    val interpolatorCorners = AccelerateInterpolator(3f)
    val interpolatorLayout = AccelerateInterpolator(1.2f)

    private var scrollMax = 1
        set(value) {
            step = cornerRadiusMax / value
            field = value
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_telegram_v05)

        findView()
    }

    override fun onResume() {
        super.onResume()
        scrollMax = max(scrollMax, appBar.getTotalScrollRange())
    }

    private fun findView() {
        tvLog = findViewById(R.id.tv_logging)
        appBar = findViewById(R.id.appbar_layout)
        collapsingToolbar = findViewById(R.id.collapsing_toolbar)

        appBar.addOnOffsetChangedListener(listener)

        ivAvatar = findViewById(R.id.iv_rounded)
        ivAvatar.setRoundedDrawable(R.drawable.valery)

        roundedDrawable = ivAvatar.drawable as RoundedBitmapDrawable
        cornerRadiusMax = roundedDrawable.cornerRadius

        appBar.setExpanded(false)
    }

    private fun updateAvatarCorderRadius(scrollOffset: Int) {

        val progress = scrollMax + scrollOffset

        // Коффициент интерполятора, на который нужно корректировать progress.
        val kC: Float =
            interpolatorCorners.getInterpolation(1f + progress / scrollMax)
        roundedDrawable.cornerRadius = max(cornerRadiusMax - progress * step * kC, 0f)
    }

    private val listener = object : AppBarLayout.OnOffsetChangedListener {
        override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {

            // Он же устанавливает значение для step
            scrollMax = max(scrollMax, appBar.totalScrollRange)

            updateAvatarCorderRadius(verticalOffset)

            if (::tvLog.isInitialized) {
                val t = "offset= $verticalOffset, scroll range = ${scrollMax}"
                tvLog.text = t
            }
        }
    }
}