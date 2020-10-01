package s.yarlykov.fne.extensions

import android.graphics.BitmapFactory
import android.widget.ImageView
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import kotlin.math.max

fun ImageView.setRoundedDrawable(drawableId: Int) {
    val resources = this.context.resources
    val srcBitmap = BitmapFactory.decodeResource(resources, drawableId)

    this.setImageDrawable(
        RoundedBitmapDrawableFactory.create(resources, srcBitmap).apply {
            cornerRadius = max(srcBitmap.width, srcBitmap.height) / 2.0f
        }
    )
}