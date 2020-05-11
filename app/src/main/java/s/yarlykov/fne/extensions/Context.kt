package s.yarlykov.fne.extensions

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
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


fun Context.dimensionPix(dimenId : Int) : Int {
    return this.resources.getDimensionPixelOffset(dimenId)
}




