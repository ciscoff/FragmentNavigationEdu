package s.yarlykov.fne.ui.telegram.v03

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView

class BottomCenterImageView : AppCompatImageView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        scaleType = ScaleType.MATRIX
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        recomputeImageMatrix()
    }

    private fun recomputeImageMatrix() {
        val drawable = drawable ?: return

        val viewWidth = width - paddingLeft - paddingRight
        val viewHeight = height - paddingTop - paddingBottom

        val drawableWidth = drawable.intrinsicWidth
        val drawableHeight = drawable.intrinsicHeight

        imageMatrix = imageMatrix.apply {
            setTranslate(
                Math.round((viewWidth - drawableWidth) * 0.5f).toFloat(),
                Math.round((viewHeight - drawableHeight).toFloat()).toFloat()
            )
        }
    }
}