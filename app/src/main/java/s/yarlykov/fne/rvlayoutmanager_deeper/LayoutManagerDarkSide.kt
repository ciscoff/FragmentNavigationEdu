package s.yarlykov.fne.rvlayoutmanager_deeper

import android.util.SparseArray
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.max
import kotlin.math.min

class LayoutManagerDarkSide : RecyclerView.LayoutManager() {

    private var decoratedChildWidth: Int = 0
    private var decoratedChildHeight: Int = 0

    // Количество видимых строк плюс 1
    private var visibleRowCount = 0

    // Adapter position для первого видимого элемента внутри RecyclerView
    private var firstVisiblePosition = 0

    override fun canScrollVertically(): Boolean = true

    /**
     * NOTE: Этот метод вызывается либо при первичной отрисовке данных из адаптера,
     * либо при их изменении одним из методов notify...().
     *
     * В любом случае в начале работы нужно сделать detach/scrap
     */
    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {

        /**
         * Текущее количество элементов в АДАПТЕРЕ (может быть пусто, если их удалили)
         *
         * Цепочка вызовов: сначала вызывается метод своего базового класса, который
         * далее обращается к адаптеру.
         * RecyclerView.LayoutManager.getItemCount()
         *  --> 'mRecyclerView.getAdapter().getItemCount()'
         *
         */
        if (itemCount == 0) {
            /**
             * Если адаптер вдруг остался с пустым контентом, то View с предыдущим
             * контентом все равно нужно detach'ить.
             */
            detachAndScrapAttachedViews(recycler)
            return
        }

        /**
         * См описание в исходнике
         */
//        state.itemCount

        /**
         * getChildCount() - Количество дочерних View, приаттаченных к RecyclerView БЕЗ учета
         * View, которые detached/scrapped.
         * Цепочка: Свой метод базового класса -> 'mChildHelper.getChildCount()'
         *
         * childCount == 0 при начальном Layout'е.
         *
         * В данном примере мы полагаем, что все элементы одинакового размера, поэтому
         * уместно при начальном layout'е взять одну View, измерить её и сохранить размеры
         * в глобальных переменных для последующего ипользования с другими View.
         * Итак, берем View, добавляем в ViewGroup, измеряем с учетом LayoutParams этой ViewGroup,
         * сохраняем данные измерений и детачим View в Scrap. Там она сохранится с учетом своей
         * позиции в адаптере.
         */
        if (childCount == 0) {
            val scrap = recycler.getViewForPosition(0)
            addView(scrap)
            measureChildWithMargins(scrap, 0, 0)


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

        // Рекомендуется всегда делать перерасчет количества строк.
        updateWindowSizing()

        fillRows(recycler)
    }

    private fun fillRows(
        recycler: RecyclerView.Recycler,
        direction: FillDirection = FillDirection.DIRECTION_NONE
    ) {
        val viewCache = SparseArray<View>(childCount)

        // Заполнить локальный кэш содержимым view port'а, то есть поместить все
        // видимые элементы (если они есть, то childCount > 0) в локальный кэш.
        for (i in 0 until childCount) {
            getChildAt(i)?.let { child ->
                val position = getPosition(child)
                viewCache.put(position, child)
            }
        }

        // Скрапим содержимое кэша
        for (i in 0 until viewCache.size()) {
            detachView(viewCache.valueAt(i))
        }

        when (direction) {
            FillDirection.DIRECTION_DOWN -> fillDown(recycler, viewCache)
            FillDirection.DIRECTION_UP -> fillUp(recycler, viewCache)
            FillDirection.DIRECTION_NONE -> {

                val leftOffset = 0
                var topOffset = 0

                // Теперь размещаем внутри view port количество элементов,
                // которое он может вместить, а именно visibleRowCount
                for (index in 0 until visibleRowCount) {
                    val nextPosition = childIndexToPosition(index)

                    var view = viewCache.get(nextPosition)

                    if (view == null) {
                        view = recycler.getViewForPosition(nextPosition)
                        addView(view)

                        measureChildWithMargins(view, 0, 0)
                        layoutDecorated(
                            view,
                            leftOffset,
                            topOffset,
                            leftOffset + decoratedChildWidth,
                            topOffset + decoratedChildHeight
                        )

                        topOffset += decoratedChildHeight
                    } else {
                        attachView(view)
                        viewCache.remove(nextPosition)
                    }
                }
            }
        }

        for (i in 0 until viewCache.size()) {
            recycler.recycleView(viewCache.valueAt(i))
        }
    }

    /**
     * Данный метод предназначен для отрисовки новых View, которые появляются в нижней части
     * списка при прокрутке контента вверх. Вызывается он только по этому случаю и в момент вызова
     * у RecyclerView все child в состоянии detach, то есть нельзя пользоваться getChildAt(i),
     * получим null. Всвязи с этим первый элемент берется из локального кэша.
     */
    private fun fillDown(recycler: RecyclerView.Recycler, viewCache: SparseArray<View>) {

        var position = viewCache.valueAt(0)
            ?.let { firstView -> getPosition(firstView) } ?: -1

        if (position == -1) return

        var fillDown = true
        var viewTop = 0

        while (fillDown && position < itemCount) {
            var child = viewCache.get(position)

            // Если есть элемент в кэше и его нижняя граница в области видимости
            if (child != null && getDecoratedBottom(child) > 0) {
                attachView(child)
                viewCache.remove(position)
            }
            // Если кэш закончился, то запрашиваем новый View
            else {
                child = recycler.getViewForPosition(position)
                addView(child)
                measureChildWithMargins(child, 0, 0)
                layoutDecorated(
                    child,
                    0,
                    viewTop,
                    decoratedChildWidth,
                    viewTop + decoratedChildHeight
                )
            }

            viewTop = getDecoratedBottom(child)
            fillDown = viewTop <= height

            position++
        }
    }

    private fun fillUp(recycler: RecyclerView.Recycler, viewCache: SparseArray<View>) {
    }

    override fun scrollVerticallyBy(
        dy: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State?
    ): Int {

        // 1. Нет Views в RecyclerView (return)
        if (childCount == 0) return 0

        // 2. Все элементы уместились в view port (return)
        val firstView: View? = getChildAt(0)
        val lastView: View? = getChildAt(childCount - 1)

        val viewSpan = getDecoratedBottom(lastView!!) - getDecoratedTop(firstView!!)
        if (viewSpan <= height) return 0

        // 3. Нужно вычислить delta и проскролить на её значение
        val delta = calculateVerticalScrollingDelta(dy, firstView, lastView)
        offsetChildrenVertical(-delta)

        // Добавить новые элементы, если требуется
        when {
            // Находим самый нижний элемент и от него добавляем вниз
            (dy > 0) -> {
                if (getDecoratedBottom(lastView) < height) {
                    fillRows(recycler, FillDirection.DIRECTION_DOWN)
                }

            }
            // Находим самый верхний элемент и от него добавляем вверх
            (dy < 0) -> {

                if (getDecoratedTop(firstView) > 0) {

                }
            }
        }

        return delta
    }

    /**
     * Вычислить реальное значение для dY
     */
    private fun calculateVerticalScrollingDelta(dy: Int, firstView: View, lastView: View): Int {
        val bottomOffset: Int
        val topOffset: Int

        val topBoundReached = getPosition(firstView) == 0
        val bottomBoundReached = getPosition(lastView) == (itemCount - 1)

        return when {

            /**
             * Палец и контент идут вверх
             *
             * В это место попадаем, если нижняя граница последнего элемента НИЖЕ границы
             * view port, а значит результат getDecoratedBottom(bottomView) - verticalSpace
             * имеет положительное значение.
             */
            dy > 0 -> {
                if (bottomBoundReached) {
                    bottomOffset = getDecoratedBottom(lastView) - verticalSpace
                    min(bottomOffset, dy)  // Минимальное из двух ПОЛОЖИТЕЛЬНЫХ чисел
                } else {
                    dy
                }
            }

            /**
             * Палец и контент идут вниз
             */
            dy < 0 -> {
                if (topBoundReached) {
                    topOffset = getDecoratedTop(firstView)
                    max(dy, topOffset) // Максимальное из двух ОТРИЦАТЕЛЬНЫХ чисел
                } else {
                    -dy
                }
            }
            else -> {
                0
            }
        }
    }


    /**
     * Маппинг между индексом внутри RecyclerView и adapter position
     */
    private fun childIndexToPosition(childIndex: Int): Int {
        return firstVisiblePosition + childIndex
    }

    private val firstVisibleRow: Int
        get() = firstVisiblePosition

    private val lastVisibleRow: Int
        get() = firstVisibleRow + visibleRowCount

    private val totalRowCount: Int
        get() = itemCount

    /**
     * "Полезное" вертикальное пространство для размещения дочерних Views.
     * То есть высота RecyclerView минус вертикальные padding'и.
     */
    private val verticalSpace: Int
        //        get() = height - paddingTop - paddingBottom
        get() = height

    /**
     * Расчитать количество видимых строк плюс 1, но не более количества элементов в адаптере.
     */
    private fun updateWindowSizing() {
        visibleRowCount = (verticalSpace / decoratedChildHeight) + 1

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