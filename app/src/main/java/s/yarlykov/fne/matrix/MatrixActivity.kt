package s.yarlykov.fne.matrix

import android.graphics.Matrix
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import kotlinx.android.synthetic.main.activity_matrix.*
import s.yarlykov.fne.R

var oneOne = 2.0f;      var oneTwo = 0.0f;      var oneThree = 0.0f
var twoOne = 0.0f;      var twoTwo = 2.0f;      var twoThree = 0.0f
var threeOne = 0.0f;    var threeTwo = 0.0f;    var threeThree = 1.0f


private val matrixScale = Matrix().apply {
    setValues(
        floatArrayOf(
            2.0f, 0.0f, 0.0f,
            0.0f, 2.0f, 0.0f,
            0.0f, 0.0f, 1.0f
        )
    )
}

private val matrixTranslate = Matrix().apply {
    setValues(
        floatArrayOf(
            1.0f, 0.0f, 0.0f,
            0.0f, 1.0f, -100.0f,
            0.0f, 0.0f, 1.0f
        )
    )
}

// Угол 45 градусов. Тангенс = 1.0
private val matrixSkewX = Matrix().apply {
    setValues(
        floatArrayOf(
            1.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 1.0f
        )
    )
}

// Угол 17 градусов. Тангенс = 0.3
private val matrixSkewY = Matrix().apply {
    setValues(
        floatArrayOf(
            1.0f, 0.0f, 0.0f,
            0.3f, 1.0f, 0.0f,
            0.0f, 0.0f, 1.0f
        )
    )
}

class MatrixActivity : AppCompatActivity() {

    private val matrix = Matrix().apply {
        setValues(
            floatArrayOf(
                oneOne, oneTwo, oneThree,
                twoOne, twoTwo, twoThree,
                threeOne, threeTwo, threeThree
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matrix)

        ivCloud.imageMatrix = matrixSkewY

        ivWeather.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            oups()
        }

       //ivWeather.imageMatrix = matrixTranslate
    }

    private fun oups() {
        val drawable = ivWeather.drawable ?: return

        val viewWidth: Float = getImageViewWidth(ivWeather).toFloat()
        val viewHeight: Float = getImageViewHeight(ivWeather).toFloat()
        val drawableWidth = drawable.intrinsicWidth
        val drawableHeight = drawable.intrinsicHeight

        val widthScale = viewWidth / drawableWidth
        val heightScale = viewHeight / drawableHeight
        val scale = widthScale.coerceAtLeast(heightScale)

        val baseMatrix = Matrix().apply {
            reset()
        }
        baseMatrix.postScale(scale, scale)
        ivWeather.imageMatrix = baseMatrix
    }

    private fun getImageViewWidth(imageView: ImageView): Int {
        return imageView.width - imageView.paddingLeft - imageView.paddingRight
    }

    private fun getImageViewHeight(imageView: ImageView): Int {
        return imageView.height - imageView.paddingTop - imageView.paddingBottom
    }
}