package s.yarlykov.fne.rvlayoutmanager

import android.graphics.Rect
import android.util.SparseArray
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import s.yarlykov.fne.utils.logIt
import kotlin.math.max
import kotlin.math.min


const val VIEW_HEIGHT_PERCENT = 0.75f
const val SCALE_THRESHOLD_PERCENT = 0.66f

private val viewCache = SparseArray<View>()

class CustomLayoutManager : RecyclerView.LayoutManager() {

    /**
     * Генерит LayoutParams для элементов списка
     * Жестко устанавливаем для элементов списка размеры на весь экран (на весь RecyclerView)
     */
    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.MATCH_PARENT,
            RecyclerView.LayoutParams.MATCH_PARENT
        )
    }

    /**
     * Хотим скролиться по вертикали
     */
    override fun canScrollVertically(): Boolean {
        return true
    }

    override fun scrollVerticallyBy(
        dy: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State?
    ): Int {
        val delta = scrollVerticallyInternal(dy)
        offsetChildrenVertical(-delta)
        fill(recycler)
        return delta
    }

    /**
     * dy - distance to scroll in pixels.
     * dy > 0, если палец идет вверх
     * dy < 0, если палец идет вниз
     *
     * С одной стороны странно, что у dy такие знаки, я то сначала думал, что должно быть как раз наоборот,
     * но видимо смысл dy в том, чтобы представлять знаковое смещение, которое нужно ПРИБАВИТЬ к текущей Y
     * координате элемента списка, чтобы получить его новые коррдинаты. То есть dy - это дельта, которая
     * всегда прибавляется и получаем новое положение по Y.
     *
     * Не совсем так. См ниже.
     *
     * https://androidx.tech/artifacts/recyclerview/recyclerview/1.1.0-beta03-source/androidx/recyclerview/widget/LinearLayoutManager.java.html
     * NOTE: В оригинальном коде в методе scrollBy знак аргумента dy (delta) используется для того, чтобы
     * определить направление скрола. Потом от него берется Math.abs(delta)
     *
     * И ещё один момент. Значение dy затем передается в метод offsetChildrenVertical, но уже
     * с противоположным знаком. И этот знак уже совпадает с направлением оси Y и направением скрола.
     * Например, изначально dy = -10. Это значит, что палец идет вниз и передвигать вьюхт тоже нужно вниз.
     * Но движение вниз имеет положительной офсет, поэтому знак инвертируется и dy попадает в
     * offsetChildrenVertical с правильным знаком.
     *
     * Итак, этот метод ничего не скролит, а просто вычисляет на сколько же на самом деле нужно проскролить.
     */

    private fun scrollVerticallyInternal(dy: Int): Int {
        // Количество приаттаченных элементов к RecyclerView (Наверное количество видимых элементов)
        val childCount = childCount
        // Количество элементов в адаптере
        val itemCount = itemCount

        if (childCount == 0) {
            return 0
        }

        val topView = getChildAt(0)
        val bottomView = getChildAt(childCount - 1)

        // Случай, когда все вьюшки поместились на экране
        val viewSpan = bottomView?.let { bv ->
            topView?.let { tv ->
                getDecoratedBottom(bv) - getDecoratedTop(tv)
            }
        } ?: 0

        if (viewSpan <= height) {
            return 0
        }

        var delta = 0

        // Если контент уезжает вниз, то есть прокручиваем к верхней части контента
        if (dy < 0) {

//            logIt("content is moving down, dy=$dy")

            val firstVisibleView = getChildAt(0)!!
            val firstVisibleViewAdapterPos = getPosition(firstVisibleView)

            delta =
                    // если верхняя вюшка не самая первая в адаптере
                if (firstVisibleViewAdapterPos > 0) {
                    dy
                }
                // если верхняя вьюшка самая первая в адаптере и выше вьюшек больше быть не может
                else {
                    val viewTop = getDecoratedTop(firstVisibleView)
                    logIt("content is moving down, viewTop=$viewTop, dy=$dy")

                    max(viewTop, dy)
                }

        }
        // если контент уезжает вверх
        else if (dy > 0) {

//            logIt("content is moving up, dy=$dy")

            val lastView = getChildAt(childCount - 1)!!
            val lastViewAdapterPos = getPosition(lastView)

            delta =
                    // если нижняя вюшка не самая последняя в адаптере
                if (lastViewAdapterPos < itemCount - 1) {
                    dy
                }
                // если нижняя вьюшка самая последняя в адаптере и ниже вьюшек больше быть не может
                else {
                    val viewBottom = getDecoratedBottom(lastView)
                    val parentBottom = height
                    min(viewBottom - parentBottom, dy)
                }
        }
        return delta
    }

    /**
     * Метод, который располагает элементы внутри RecyclerView
     */
    fun onLayoutChildrenOld(recycler: RecyclerView.Recycler, state: RecyclerView.State?) {
        val view = recycler.getViewForPosition(0)

        val viewHeight: Int = (height * VIEW_HEIGHT_PERCENT).toInt()

        /**
         * Далее три основных шага по размещению дочерней view в ViewGroup:
         * 1. Добавить в контейнер
         * 2. Измерить (measure)
         * 3. Разместить (layout)
         *
         * Вообще measure должна отдать нам размер самой view, размер того, что мы видим
         * на экране. measure не содержит маргинов этого view и прочих insets.
         */
        addView(view)

        val widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(viewHeight, View.MeasureSpec.EXACTLY)
        measureChildWithDecorationsAndMargin(view, widthSpec, heightSpec)
        layoutDecorated(view, 0, 0, width, viewHeight)
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State?) {
        detachAndScrapAttachedViews(recycler)
        fill(recycler)
    }

    private fun fill(recycler: RecyclerView.Recycler) {


        val anchorView = getAnchorView()
        viewCache.clear()

        //Помещаем вьюшки в кэш и...
        run {
            var i = 0
            val cnt = childCount
            while (i < cnt) {

                getChildAt(i)?.let {view ->
                    val pos = getPosition(view)
                    viewCache.put(pos, view)
                }

                i++
            }
        }

        //... и удалям из лэйаута
        for (i in 0 until viewCache.size()) {
            detachView(viewCache.valueAt(i))
        }

        fillUp(anchorView, recycler)
        fillDown(anchorView, recycler)

        //отправляем в корзину всё, что не потребовалось в этом цикле лэйаута
        //эти вьюшки или ушли за экран или не понадобились, потому что соответствующие элементы
        //удалились из адаптера
        for (i in 0 until viewCache.size()) {
            recycler.recycleView(viewCache.valueAt(i))
        }

        updateViewScale()
    }

    private fun fillUp(anchorView: View?, recycler: RecyclerView.Recycler) {
        var anchorPos = 0
        var anchorTop = 0
        if (anchorView != null) {
            anchorPos = getPosition(anchorView)
            anchorTop = getDecoratedTop(anchorView)
        }

        var fillUp = true
        var pos = anchorPos - 1
        var viewBottom =
            anchorTop //нижняя граница следующей вьюшки будет начитаться от верхней границы предыдущей
        val viewHeight = (height * VIEW_HEIGHT_PERCENT).toInt()
        val widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(viewHeight, View.MeasureSpec.EXACTLY)
        while (fillUp && pos >= 0) {
            var view: View? = viewCache.get(pos) //проверяем кэш
            if (view == null) {
                //если вьюшки нет в кэше - просим у recycler новую, измеряем и лэйаутим её
                view = recycler.getViewForPosition(pos)
                addView(view, 0)
                measureChildWithDecorationsAndMargin(view, widthSpec, heightSpec)
                val decoratedMeasuredWidth = getDecoratedMeasuredWidth(view)
                layoutDecorated(
                    view,
                    0,
                    viewBottom - viewHeight,
                    decoratedMeasuredWidth,
                    viewBottom
                )
            } else {
                //если вьюшка есть в кэше - просто аттачим её обратно
                //нет необходимости проводить measure/layout цикл.
                attachView(view)
                viewCache.remove(pos)
            }
            viewBottom = getDecoratedTop(view)
            fillUp = viewBottom > 0
            pos--
        }
    }

    /**
     * Заполнить экран (RecyclerView) элементами, взятыми у Recycler
     */
    private fun fillDown(anchorView: View?, recycler: RecyclerView.Recycler) {
        var anchorPos = 0
        var anchorTop = 0
        if (anchorView != null) {
            anchorPos = getPosition(anchorView)
            anchorTop = getDecoratedTop(anchorView)
        }

        var pos = anchorPos
        var fillDown = true
        val height = height
        var viewTop = anchorTop
        val itemCount = itemCount
        val viewHeight = (getHeight() * VIEW_HEIGHT_PERCENT).toInt()
        val widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(viewHeight, View.MeasureSpec.EXACTLY)

        while (fillDown && pos < itemCount) {
            var view: View? = viewCache.get(pos)
            if (view == null) {
                view = recycler.getViewForPosition(pos)
                addView(view)
                measureChildWithDecorationsAndMargin(view, widthSpec, heightSpec)
                val decoratedMeasuredWidth = getDecoratedMeasuredWidth(view)
                layoutDecorated(view, 0, viewTop, decoratedMeasuredWidth, viewTop + viewHeight)
            } else {
                attachView(view)
                viewCache.remove(pos)
            }
            viewTop = getDecoratedBottom(view)
            fillDown = viewTop <= height
            pos++
        }
    }

    /**
     * Получить размер view с учетом всех insets, а именно отступов, которые насчитал декоратор,
     * а также маргинов нашей view
     */
    private fun measureChildWithDecorationsAndMargin(child: View, widthSpec: Int, heightSpec: Int) {

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

    // метод вернет вьюшку с максимальной видимой площадью
    private fun getAnchorView(): View? {
        val childCount = childCount
        val viewsOnScreen = mutableMapOf<Int, View>()
        val mainRect = Rect(0, 0, width, height)
        for (i in 0 until childCount) {

            getChildAt(i)?.let { view ->
                val top = getDecoratedTop(view)
                val bottom = getDecoratedBottom(view)
                val left = getDecoratedLeft(view)
                val right = getDecoratedRight(view)
                val viewRect = Rect(left, top, right, bottom)
                val intersect = viewRect.intersect(mainRect)
                if (intersect) {
                    val square = viewRect.width() * viewRect.height()
                    viewsOnScreen[square] = view
                }
            }
        }
        if (viewsOnScreen.isEmpty()) {
            return null
        }
        var maxSquare: Int? = null
        for (square in viewsOnScreen.keys) {
            maxSquare = if (maxSquare == null) square else max(maxSquare, square)
        }
        return viewsOnScreen.get(maxSquare)
    }

    private fun updateViewScale() {
        val childCount = childCount
        val height = height
        val thresholdPx =
            (height * SCALE_THRESHOLD_PERCENT).toInt() // SCALE_THRESHOLD_PERCENT = 0.66f or 2/3
        for (i in 0 until childCount) {
            var scale = 1f

            getChildAt(i)?.let {view ->
                val viewTop = getDecoratedTop(view)
                if (viewTop >= thresholdPx) {
                    val delta = viewTop - thresholdPx
                    scale = (height - delta) / height.toFloat()
                    scale = Math.max(scale, 0f)
                }
                view.pivotX = (view.height / 2).toFloat()
                view.pivotY = (view.height / -2).toFloat()
                view.scaleX = scale
                view.scaleY = scale
            }
        }
    }
}