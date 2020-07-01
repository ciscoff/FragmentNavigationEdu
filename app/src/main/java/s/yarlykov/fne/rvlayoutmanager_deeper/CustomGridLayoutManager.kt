package s.yarlykov.fne.rvlayoutmanager_deeper

import android.content.Context
import android.graphics.Rect
import android.util.SparseArray
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.min

const val maxColumns = 5

class CustomGridLayoutManager(private val context: Context, private val columns: Int) :
    RecyclerView.LayoutManager() {

    init {
        if(columns <= 0) throw IllegalArgumentException("Argument 'columns' cannot be less than or equal to 0")
    }

    private var decoratedChildWidth: Int = 0
    private var decoratedChildHeight: Int = 0
    private var visibleRowCount: Int = 0

    // Adapter position для первого видимого элемента внутри RecyclerView
    private var firstVisiblePosition = 0


    private val verticalSpace: Int
        get() = height

    private val horizontalSpace: Int
        get() = width

    private val totalRowCount: Int
        get() {
            var tmp = itemCount / columns
            if (itemCount % columns != 0) tmp++
            return tmp
        }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State?) {
        /**
         * Адаптер пустой. Это может быть начальное состояние или удаление элементов
         * из adapter data set.
         */
        if (itemCount == 0) {
            detachAndScrapAttachedViews(recycler)
            return
        }

        if (childCount == 0) {
            val scrap = recycler.getViewForPosition(0)
            addView(scrap)

            val (viewWidth, viewHeight) = itemDimensions()

            val widthSpec = View.MeasureSpec.makeMeasureSpec(viewWidth, View.MeasureSpec.EXACTLY)
            val heightSpec =
                View.MeasureSpec.makeMeasureSpec(viewHeight, View.MeasureSpec.EXACTLY)

            measureChildWithDecorationsAndMargins(scrap, widthSpec, heightSpec)

            /**
             * Все элементы будут одинаковой высоты и ширины
             */
            decoratedChildWidth = getDecoratedMeasuredWidth(scrap)
            decoratedChildHeight = getDecoratedMeasuredHeight(scrap)

            /**
             * Данные сохранены, можно детачить в скрап
             */
            detachAndScrapView(scrap, recycler)
        }

        updateWindowSizing()

        fillGrid(recycler)
    }

    private fun fillGrid(recycler: RecyclerView.Recycler) {

        var leftOffset = 0
        var topOffset = 0
        var index = 0

        firstVisiblePosition = 0

        while(index < itemCount && topOffset <= height) {
            val nextPosition = childIndexToPosition(index++)

            val view = recycler.getViewForPosition(nextPosition)
            addView(view)
            makeChildMeasure(view)
            layoutDecorated(
                view,
                leftOffset,
                topOffset,
                leftOffset + decoratedChildWidth,
                topOffset + decoratedChildHeight
            )

            leftOffset += decoratedChildWidth

            if(leftOffset >= width) {
                leftOffset = 0
                topOffset += decoratedChildHeight
            }
        }
    }

    /**
     * На входе - total view size, который состоит из размера самой view, плюс её маргины,
     * плюс инсеты декоратора.
     */
    private fun measureChildWithDecorationsAndMargins(
        child: View,
        widthSpec: Int,
        heightSpec: Int
    ) {

        val decorRect = Rect()

        // У декоратора запрашиваем инсеты для view и получаем их в Rect
        calculateItemDecorationsForChild(child, decorRect)

        val lp = child.layoutParams as RecyclerView.LayoutParams

        val widthSpecUpdated = updateSpecWithExtra(
            widthSpec,
            lp.leftMargin + decorRect.left,
            lp.rightMargin + decorRect.right
        )

        val heightSpecUpdated = updateSpecWithExtra(
            heightSpec,
            lp.topMargin + decorRect.top,
            lp.bottomMargin + decorRect.bottom
        )

        child.measure(widthSpecUpdated, heightSpecUpdated)
    }

    /**
     * Корректируем отдельную размерность (ширина/высота) view с учетом
     * имеющихся insets.
     */
    private fun updateSpecWithExtra(spec: Int, startInset: Int, endInset: Int): Int {
        if (startInset == 0 && endInset == 0) {
            return spec
        }

        val mode = View.MeasureSpec.getMode(spec)

        if (mode == View.MeasureSpec.AT_MOST || mode == View.MeasureSpec.EXACTLY) {
            return View.MeasureSpec.makeMeasureSpec(
                View.MeasureSpec.getSize(spec) - startInset - endInset, mode
            )
        }
        return spec
    }

    private fun makeChildMeasure(view: View) {
        val widthSpec =
            View.MeasureSpec.makeMeasureSpec(decoratedChildWidth, View.MeasureSpec.EXACTLY)
        val heightSpec =
            View.MeasureSpec.makeMeasureSpec(decoratedChildHeight, View.MeasureSpec.EXACTLY)

        measureChildWithDecorationsAndMargins(view, widthSpec, heightSpec)
    }

    /**
     * Размер одного элемента. Они будут квадратные. Размер стороны - ширина столбца
     * в текущей ориентации экрана.
     */
    private fun itemDimensions(): Pair<Int, Int> {
        val divider = if (columns > maxColumns) maxColumns else columns
        val dimension = width / divider
        return dimension to dimension
    }

    /**
     * Маппинг между индексом внутри RecyclerView и adapter position
     */
    private fun childIndexToPosition(childIndex: Int): Int {
        return firstVisiblePosition + childIndex
    }

    private fun updateWindowSizing() {

        visibleRowCount = verticalSpace / decoratedChildHeight

        if (verticalSpace % decoratedChildHeight != 0) {
            visibleRowCount++
        }
        visibleRowCount = min(totalRowCount, visibleRowCount)
    }

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.WRAP_CONTENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )
    }
}