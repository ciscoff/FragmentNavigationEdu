package s.yarlykov.fne.ui.telegram.v04

import android.os.Bundle
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import s.yarlykov.fne.R
import s.yarlykov.fne.extensions.dimensionPix
import s.yarlykov.fne.extensions.setRoundedDrawable
import kotlin.math.max
import kotlin.math.min

class TelegramActivityV4 : AppCompatActivity() {

    lateinit var seekBar: SeekBar
    lateinit var roundedDrawable: RoundedBitmapDrawable
    lateinit var sceneRoot: FrameLayout
    lateinit var ivAvatar: ImageView
    lateinit var ivRect: ImageView
    lateinit var tvProgress: TextView

    val interpolatorCorners = AccelerateInterpolator(3f)
    val interpolatorLayout = AccelerateInterpolator(1.2f)

    var cornerRadiusMax = 0f
    var step = 1f

    val seekMax = 100
    val seekProgress = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_telegram_v04)

        findView()
    }

    private fun findView() {

        sceneRoot = findViewById(R.id.scene_root)
        tvProgress = findViewById(R.id.tv_progress)
        ivRect = findViewById(R.id.iv_rect)

        seekBar = findViewById(R.id.seek_bar)
        seekBar.max = seekMax
        seekBar.progress = seekProgress
        seekBar.setOnSeekBarChangeListener(seekChangeListener)

        ivAvatar = findViewById(R.id.iv_rounded)
        ivAvatar.setRoundedDrawable(R.drawable.valery)

        roundedDrawable = ivAvatar.drawable as RoundedBitmapDrawable
        cornerRadiusMax = roundedDrawable.cornerRadius
        step = cornerRadiusMax / seekMax
    }

    private val seekChangeListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            updateAvatarCorderRadius(progress)
            updateAvatarMarginAndLayout(progress)
//            updateAvatarScale(progress)
//            updateRectImage(progress)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
        }
    }

    private fun updateAvatarCorderRadius(progress: Int) {

        val kC : Float = interpolatorCorners.getInterpolation(1f + progress.toFloat()/100f)
        roundedDrawable.cornerRadius = max(cornerRadiusMax - progress.toFloat() * step * kC, 0f)
    }

    /**
     * Картинка - квадрат (стороны равны), поэтому наращиваем размеры картинки (высота/ширина)
     * до наибольшего размера родителя (это его ширина).
     *
     * Алгоритм:
     * - уменьшаем marginStart, чтобы в итоге прижатся к левому краю родителя.
     * - увеличиваем width/height до размера parent.width. Размеры растут не от 0, а от начального
     * значения (initDiameter)
     *
     */
    private fun updateAvatarMarginAndLayout(progress: Int) {
        val parentWidth = sceneRoot.measuredWidth
        val initMargin = dimensionPix(R.dimen.margin_rounded)
        val initDiameter = dimensionPix(R.dimen.circle_diameter_v4)
        val widthDelta = parentWidth.toFloat() - initDiameter

//        val k: Float = getK(progress)

        val kL = interpolatorLayout.getInterpolation(1f + progress.toFloat()/100f)

        val avatarLayoutParams = ivAvatar.layoutParams
        val avatarMarginParams = ivAvatar.layoutParams as ViewGroup.MarginLayoutParams

        // Меняем marginStart
        val currentMargin = max(initMargin - ((initMargin.toFloat() * (progress.toFloat() / seekMax.toFloat())) * kL).toInt(), 0)
        avatarMarginParams.marginStart = currentMargin

        // Меняем размер ivAvatar
        avatarLayoutParams.width = min(initDiameter + ((widthDelta * (progress.toFloat() / seekMax.toFloat())) * kL).toInt(), parentWidth)
        avatarLayoutParams.height = avatarLayoutParams.width

        ivAvatar.layoutParams = avatarLayoutParams
        ivAvatar.requestLayout()
        ivAvatar.invalidate()
    }

    private fun updateAvatarMarginAndLayoutV1(progress: Int) {
        val parentWidth = sceneRoot.measuredWidth
        val initMargin = dimensionPix(R.dimen.margin_rounded)
        val initDiameter = dimensionPix(R.dimen.circle_diameter_v4)
        val widthDelta = parentWidth.toFloat() - initDiameter

        val avatarLayoutParams = ivAvatar.layoutParams
        val avatarMarginParams = ivAvatar.layoutParams as ViewGroup.MarginLayoutParams

        // Меняем marginStart
        val currentMargin =
            initMargin - (initMargin.toFloat() * (progress.toFloat() / seekMax.toFloat())).toInt()
        avatarMarginParams.marginStart = currentMargin

        // Меняем размер ivAvatar
        avatarLayoutParams.width =
            initDiameter + (widthDelta * (progress.toFloat() / seekMax.toFloat())).toInt()
        avatarLayoutParams.height = avatarLayoutParams.width

        ivAvatar.layoutParams = avatarLayoutParams
        ivAvatar.requestLayout()
        ivAvatar.invalidate()
    }


    /**
     * Это аналог интерполятора. Изменяет коэффициент
     */
    private fun getK(progress: Int): Float {
        val k: Float = when (progress) {
            in 20..60 -> {
                1.4f
            }
            in 60..80 -> {
                1.6f
            }
            in 80..100 -> {
                1.8f
            }
            else -> 1f
        }
        return k
    }


    private fun updateAvatarScale(progress: Int) {
        ivAvatar.scaleX = 1f + progress.toFloat() / seekMax.toFloat()
        ivAvatar.scaleY = 1f + progress.toFloat() / seekMax.toFloat()
    }

    /**
     * https://stackoverflow.com/questions/32835397/android-property-animation-how-to-increase-view-height
     */
    private fun updateRectImagev1(progress: Int) {
        val parentHeight = sceneRoot.measuredHeight
        val parentWidth = sceneRoot.measuredWidth

        val imageLayoutParams = ivRect.layoutParams
        val imageMarginParams = ivRect.layoutParams as ViewGroup.MarginLayoutParams

        // Меняем размеры
        imageLayoutParams.height =
            (parentHeight.toFloat() * (progress.toFloat() / seekMax.toFloat())).toInt()
        imageLayoutParams.width =
            (parentWidth.toFloat() * (progress.toFloat() / seekMax.toFloat())).toInt()

        // Меняем margin
        val initMargin = dimensionPix(R.dimen.margin_rounded)
        val currentMargin =
            initMargin - (initMargin.toFloat() * (progress.toFloat() / seekMax.toFloat())).toInt()
        imageMarginParams.topMargin = currentMargin
        imageMarginParams.marginStart = currentMargin

        val h = (parentHeight.toFloat() * (progress.toFloat() / seekMax.toFloat())).toInt()
        val w = (parentWidth.toFloat() * (progress.toFloat() / seekMax.toFloat())).toInt()

        ivRect.layoutParams = imageLayoutParams
        ivRect.requestLayout()
        ivRect.invalidate()

        val message = "progress=$progress, height=${h}, width=${w}"
        tvProgress.text = message

    }

    private fun updateRectImage(progress: Int) {
        val parentHeight = sceneRoot.measuredHeight
        val parentWidth = sceneRoot.measuredWidth

        val rectLayoutParams = ivRect.layoutParams
        val rectMarginParams = ivRect.layoutParams as ViewGroup.MarginLayoutParams

        // Меняем размеры
        rectLayoutParams.height =
            (parentHeight.toFloat() * (progress.toFloat() / seekMax.toFloat())).toInt()
        rectLayoutParams.width =
            (parentWidth.toFloat() * (progress.toFloat() / seekMax.toFloat())).toInt()

        // Меняем marginStart
        val initMargin = dimensionPix(R.dimen.margin_rounded)
        val currentMargin =
            initMargin - (initMargin.toFloat() * (progress.toFloat() / seekMax.toFloat())).toInt()
        rectMarginParams.marginStart = currentMargin

        val h = (parentHeight.toFloat() * (progress.toFloat() / seekMax.toFloat())).toInt()
        val w = (parentWidth.toFloat() * (progress.toFloat() / seekMax.toFloat())).toInt()

        ivRect.layoutParams = rectLayoutParams
        ivRect.requestLayout()
        ivRect.invalidate()

        val message = "progress=$progress, height=${h}, width=${w}"
        tvProgress.text = message

    }
}
