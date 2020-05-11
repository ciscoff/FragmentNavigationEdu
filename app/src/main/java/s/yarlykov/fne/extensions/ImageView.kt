package s.yarlykov.fne.extensions

import android.graphics.BitmapFactory
import android.widget.ImageView
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory

fun ImageView.setRoundedDrawable(drawableId: Int) {
    val resources = this.context.resources
    val srcBitmap = BitmapFactory.decodeResource(resources, drawableId)

    this.setImageDrawable(
        RoundedBitmapDrawableFactory.create(resources, srcBitmap).apply {
            cornerRadius = kotlin.math.max(srcBitmap.width, srcBitmap.height) / 2.0f
        }
    )
}