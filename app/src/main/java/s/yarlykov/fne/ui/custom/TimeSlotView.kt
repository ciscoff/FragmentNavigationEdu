package s.yarlykov.fne.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.View


class TimeSlotView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val specMode = MeasureSpec.getMode(heightMeasureSpec)
        val parentsHeight = MeasureSpec.getSize(heightMeasureSpec)
        val myHeight = parentsHeight / 6




        println("calculated height = $myHeight")


        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), myHeight)

//        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(myHeight, MeasureSpec.EXACTLY))


    }

}