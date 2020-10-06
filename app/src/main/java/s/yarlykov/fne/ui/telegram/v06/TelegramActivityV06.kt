package s.yarlykov.fne.ui.telegram.v06

import android.graphics.Matrix
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_telegram_v06.*
import s.yarlykov.fne.R
import s.yarlykov.fne.utils.logIt

class TelegramActivityV06 : AppCompatActivity(), SeekBar.OnSeekBarChangeListener {

    private val seekMax = 100f

    private var heightMax = 0f
    private var heightMin = 0f
    private var heightStep = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_telegram_v06)

        heightMax = resources.getDimension(R.dimen.avatar_max_height)
        heightMin = resources.getDimension(R.dimen.avatar_min_height)
        heightStep = (heightMax - heightMin) / seekMax

        seekBar.apply {
            max = seekMax.toInt()
            progress = seekMax.toInt()
            setOnSeekBarChangeListener(this@TelegramActivityV06)
        }

        ivValery.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            updateImageViewTransform(seekBar.progress)
        }
    }

    private fun updateImageViewTransform(progress: Int) {
        val drawable = ivValery.drawable ?: return

        val viewWidth: Float = getImageViewWidth(ivValery).toFloat()
        val viewHeight: Float = getImageViewHeight(ivValery).toFloat()

        val drawableWidth = drawable.intrinsicWidth
        val drawableHeight = drawable.intrinsicHeight

        val widthScale = viewWidth / drawableWidth
        val heightScale = viewHeight / drawableHeight
        val scale = widthScale.coerceAtLeast(heightScale)

        val k = 0.1f * (seekMax - progress)/seekMax

        val baseMatrix = Matrix().apply {
            reset()
            postTranslate(0f, (viewHeight - drawableHeight) * (0.1f + k))
        }

        baseMatrix.postScale(scale, scale)
        ivValery.imageMatrix = baseMatrix
    }

    private fun updateImageViewHeight(progress: Int) {
        val avatarLayoutParams = ivValery.layoutParams
        avatarLayoutParams.height = (heightMin + progress * heightStep).toInt()
        ivValery.layoutParams = avatarLayoutParams
    }

    private fun getImageViewWidth(imageView: ImageView): Int {
        return imageView.width - imageView.paddingLeft - imageView.paddingRight
    }

    private fun getImageViewHeight(imageView: ImageView): Int {
        return imageView.height - imageView.paddingTop - imageView.paddingBottom
    }

    /**
     * После того как ползунок сдвинулся вызываем updateImageViewHeight, которая
     * меняет высоту ImageView. Изменения выполняются через layoutParams, что в свою очередь
     * приводит к вызову ivValery.addOnLayoutChangeListener(). И именно оттуда вызываем
     * функцию, которая делает трасформацию картинки. То есть трансформация выполняется
     * после layout.
     */
    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        updateImageViewHeight(progress)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
    }
}