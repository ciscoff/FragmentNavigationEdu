package s.yarlykov.fne.extensions

import android.content.Context
import android.content.res.TypedArray
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.util.TypedValue
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory

/**
 * Функция загружает из ресурсов Drawable и превращает его Circle-картинку
 */
fun Context.roundedDrawable(drawableId: Int): Drawable {

    val resources = this.resources

    val srcBitmap = BitmapFactory.decodeResource(resources, drawableId)

    return RoundedBitmapDrawableFactory.create(resources, srcBitmap).apply {
        cornerRadius = kotlin.math.max(srcBitmap.width, srcBitmap.height) / 2.0f
    }
}

fun Context.dimensionPix(dimenId: Int): Int {
    return this.resources.getDimensionPixelOffset(dimenId)
}

// ??? Не проверял
inline fun <reified T : Any?> Context.themeAttributeValue(
    attributeId: Int,
    capture: TypedArray.() -> T
): T {
    val typedArray = theme.obtainStyledAttributes(intArrayOf(attributeId))

    return with(typedArray) {

        val result = capture()
        recycle()
        result
    }
}


/**
 * android.R.attr.actionBarSize
 */
fun Context.resolveAttribute(attributeId: Int): Int? {
    val typedValue = TypedValue()
    return if (theme.resolveAttribute(attributeId, typedValue, true)) {
        TypedValue.complexToDimensionPixelSize(typedValue.data, resources.displayMetrics)
    } else {
        null
    }
}




