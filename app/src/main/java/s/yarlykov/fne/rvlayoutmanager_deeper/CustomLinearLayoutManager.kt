package s.yarlykov.fne.rvlayoutmanager_deeper

import android.content.Context
import android.content.res.Configuration
import android.graphics.Rect
import android.util.SparseArray
import android.view.View
import androidx.core.util.isEmpty
import androidx.recyclerview.widget.RecyclerView
import s.yarlykov.fne.extensions.resolveAttribute
import s.yarlykov.fne.utils.logIt
import kotlin.math.max
import kotlin.math.min

class CustomLinearLayoutManager(private val context: Context) : RecyclerView.LayoutManager() {

    private var decoratedChildWidth: Int = 0
    private var decoratedChildHeight: Int = 0

    // Количество видимых строк плюс 1
    private var visibleRowCount = 0

    // Количество видимых столбцов плюс 1
    private var visibleColCount = 0

    // Adapter position для первого видимого элемента внутри RecyclerView
    private var firstVisiblePosition = 0

    private var orientation = Configuration.ORIENTATION_PORTRAIT

    override fun canScrollVertically(): Boolean =
        orientation == Configuration.ORIENTATION_PORTRAIT

    override fun canScrollHorizontally(): Boolean =
        orientation == Configuration.ORIENTATION_LANDSCAPE

    fun setOrientation(_orientation: Int) {
        logIt("setOrientation orientation=${_orientation}")
        orientation = _orientation
    }

    /**
     * NOTE: Этот метод вызывается либо при первичной отрисовке данных из адаптера,
     * либо при их изменении одним из методов notify...().
     *
     * В любом случае в начале работы нужно сделать detach/scrap
     */
    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        val dbgPrefix = "${object {}.javaClass.enclosingMethod?.name}"

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
         * getChildCount() - Количество дочерних View, attached к RecyclerView.
         * Те что detached/scrapped в этот счетчик не попадают. Поэтому если
         * сделать для всех detach/scrap, а потом пробовать что-то вроде
         * getChildCount(0), то получим null.
         *
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

            val (viewWidth, viewHeight) =
                if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    width to context.resolveAttribute(android.R.attr.actionBarSize)
                } else {
                    context.resolveAttribute(android.R.attr.actionBarSize) to height
                }

            val widthSpec = View.MeasureSpec.makeMeasureSpec(viewWidth!!, View.MeasureSpec.EXACTLY)
            val heightSpec =
                View.MeasureSpec.makeMeasureSpec(viewHeight!!, View.MeasureSpec.EXACTLY)

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

        // Рекомендуется всегда делать перерасчет количества строк, которые вмещает view port.
        updateWindowSizing()

        fillRows(recycler)
    }

    /**
     * На входе - total view size, который состоит из размера самой view, плюс её маргины,
     * плюс инсеты декоратора.
     * а также маргинов нашей view
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

    /**
     * Заполнить строками view port
     */
    private fun fillRows(
        recycler: RecyclerView.Recycler,
        direction: FillDirection = FillDirection.DIRECTION_NONE
    ) {
        val dbgPrefix = "${object {}.javaClass.enclosingMethod?.name}"

        val viewCache = SparseArray<View>(childCount)

        // Заполнить локальный кэш содержимым view port'а, то есть поместить все
        // видимые элементы (если они есть, то childCount > 0) в локальный кэш.
        for (i in 0 until childCount) {
            getChildAt(i)?.let { child ->
                val position = getPosition(child)
                child.tag = position
                viewCache.put(position, child)
            }
        }

        // Скрапим содержимое кэша
        for (i in 0 until viewCache.size()) {
            detachView(viewCache.valueAt(i))
        }

        val anchorView = searchAnchorView(viewCache, direction)

        anchorView?.let {
            logIt("$dbgPrefix anchorView position = ${getPosition(it)}")
        } ?: run {logIt("$dbgPrefix anchorView position = null")}

        when (direction) {
            // Снизу образовалось свободное пространство
            FillDirection.DIRECTION_DOWN -> fillDown(anchorView, recycler, viewCache)
            // Сверху образовалось свободное пространство
            FillDirection.DIRECTION_UP -> fillUp(anchorView, recycler, viewCache)
            // Справа образовалось свободное пространство
            FillDirection.DIRECTION_RIGHT -> fillRight(anchorView, recycler, viewCache)
            // Слева образовалось свободное пространство
            FillDirection.DIRECTION_LEFT -> fillLeft(anchorView, recycler, viewCache)
            // Начальная отрисовка или при скроле все вью остались прежними.
            FillDirection.DIRECTION_NONE -> {

                if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    fillVertical(recycler, viewCache)
                } else {
                    fillHorizontal(recycler, viewCache)
                }
            }
        }

        // Отресайклить неиспользуемые View
        for (i in 0 until viewCache.size()) {
            recycler.recycleView(viewCache.valueAt(i))
        }

        viewCache.clear()
    }

    private fun fillVertical(recycler: RecyclerView.Recycler, viewCache: SparseArray<View>) {
        val leftOffset = 0
        var topOffset = 0

        firstVisiblePosition = if (viewCache.isEmpty()) 0 else viewCache.keyAt(0)

        // Теперь размещаем внутри view port количество элементов,
        // которое он может вместить, а именно visibleRowCount
        for (index in 0 until visibleRowCount) {
            val nextPosition = childIndexToPosition(index)

            var view = viewCache.get(nextPosition)

            if (view == null) {
                view = recycler.getViewForPosition(nextPosition)
                addView(view)

                makeChildMeasure(view)

//                measureChildWithMargins(view, 0, 0)
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

    private fun fillHorizontal(recycler: RecyclerView.Recycler, viewCache: SparseArray<View>) {
        var leftOffset = 0
        val topOffset = 0

        firstVisiblePosition = if (viewCache.isEmpty()) 0 else viewCache.keyAt(0)

        // Теперь размещаем внутри view port количество элементов,
        // которое он может вместить, а именно visibleColCount
        for (index in 0 until visibleColCount) {
            val nextPosition = childIndexToPosition(index)

            var view = viewCache.get(nextPosition)

            if (view == null) {
                view = recycler.getViewForPosition(nextPosition)
                addView(view)

                makeChildMeasure(view)

//                measureChildWithMargins(view, 0, 0)
                layoutDecorated(
                    view,
                    leftOffset,
                    topOffset,
                    leftOffset + decoratedChildWidth,
                    topOffset + decoratedChildHeight
                )
                leftOffset += decoratedChildWidth
            } else {
                attachView(view)
                viewCache.remove(nextPosition)
            }
        }
    }

    private fun makeChildMeasure(view: View) {
        val widthSpec =
            View.MeasureSpec.makeMeasureSpec(decoratedChildWidth, View.MeasureSpec.EXACTLY)
        val heightSpec =
            View.MeasureSpec.makeMeasureSpec(decoratedChildHeight, View.MeasureSpec.EXACTLY)

        measureChildWithDecorationsAndMargins(view, widthSpec, heightSpec)
    }

    /**
     * Данный метод предназначен для отрисовки новых View, которые появляются в нижней части
     * списка при прокрутке контента вверх. Вызывается он только по этому случаю и в момент вызова
     * у RecyclerView все child в состоянии detach, то есть нельзя пользоваться getChildAt(i),
     * получим null. В связи с этим первый элемент берется из локального кэша.
     *
     * Направление заполнения: сверху вниз
     */
    private fun fillDown(
        anchorView: View?,
        recycler: RecyclerView.Recycler,
        viewCache: SparseArray<View>
    ) {
        val dbgPrefix = "${object {}.javaClass.enclosingMethod?.name}"

        var position = anchorView
            ?.let { getPosition(it) } ?: run { viewCache.keyAt(0) }

        logIt("$dbgPrefix anchorView position = $position")

        var fillDown = true
        var viewTop = 0

        while (fillDown && position < itemCount) {
            var child = viewCache.get(position)

            // В кэше есть элемент
            if (child != null) {
                // И его нижняя граница в области видимости
                if (getDecoratedBottom(child) > 0) {
                    attachView(child)
                    viewCache.remove(position)
                }
            }
            /**
             * Если кэш закончился, то запрашиваем новый View и добавляем его.
             * Важный момент ! Добавляемый View будет последним в иерархии дочерних
             * View и будет иметь последний индекс ! Порядок индексов необходимо
             * контролировать. Если такой View добавить самым первым, то он хоть
             * и будет в layout'е на своем месте - внизу, но на getChildAt(0) тоже
             * будет возвращаться именно он, а это неверно !!!
             */
            else {
                child = recycler.getViewForPosition(position)
                addView(child)
                makeChildMeasure(child)

//                measureChildWithMargins(child, 0, 0)
                layoutDecorated(
                    child,
                    0,
                    viewTop,
                    decoratedChildWidth,
                    viewTop + decoratedChildHeight
                )
            }

            viewTop = getDecoratedBottom(child)
            fillDown = viewTop <= verticalSpace
            position++
        }
    }

    /**
     * Заполнить верхнюю часть RecyclerView при прокрутке контента вниз.
     *
     * Заполняем снизу вверхиз кэша, а потом добавляем на самый верх новый
     * элемент. И добавляем его с явным указанием индекса 0 в иерархии View.
     */
    private fun fillUp(
        anchorView: View?,
        recycler: RecyclerView.Recycler,
        viewCache: SparseArray<View>
    ) {
        val dbgPrefix = "${object {}.javaClass.enclosingMethod?.name}"

        var position = anchorView
            ?.let { getPosition(it) } ?: run { viewCache.keyAt(viewCache.size() - 1) }

        var fillUp = true
        var viewBottom = 0

        while (fillUp && position >= 0) {
            var child = viewCache.get(position)

            // В кэше есть элемент и его верхняя граница в области видимости
            if (child != null && getDecoratedTop(child) < verticalSpace) {
                attachView(child, 0)
                viewCache.remove(position)
            }
            // Если кэш закончился, то запрашиваем новый View
            else {
                child = recycler.getViewForPosition(position)
                addView(child, 0)
                makeChildMeasure(child)

//                measureChildWithMargins(child, 0, 0)
                layoutDecorated(
                    child,
                    0,
                    viewBottom - decoratedChildHeight,
                    decoratedChildWidth,
                    viewBottom
                )
            }

            viewBottom = getDecoratedTop(child)
            fillUp = viewBottom >= 0
            position--
        }
    }

    /**
     * Контент уходит влево, заполняем слева направо.
     */
    private fun fillRight(
        anchorView: View?,
        recycler: RecyclerView.Recycler,
        viewCache: SparseArray<View>
    ) {
        val dbgPrefix = "${object {}.javaClass.enclosingMethod?.name}"

        var position = anchorView
            ?.let { getPosition(it) } ?: run { viewCache.keyAt(0) }

        logIt("$dbgPrefix anchorView position = $position")

        var fillRight = true
        var viewLeft = 0

        while (fillRight && position < itemCount) {
            var child = viewCache.get(position)

            // В кэше есть элемент
            if (child != null) {
                // И его правая граница в области видимости
                if (getDecoratedRight(child) > 0) {
                    attachView(child)
                    viewCache.remove(position)
                }
            } else {
                child = recycler.getViewForPosition(position)
                addView(child)  // Добавляем в "хвост"
                makeChildMeasure(child)

                layoutDecorated(
                    child,
                    viewLeft,
                    0,
                    viewLeft + decoratedChildWidth,
                    decoratedChildHeight
                )
            }

            viewLeft = getDecoratedRight(child)
            fillRight = viewLeft <= horizontalSpace
            position++
        }
    }

    /**
     * Контент уходит вправо, заполняем справо налево.
     */
    private fun fillLeft(
        anchorView: View?,
        recycler: RecyclerView.Recycler,
        viewCache: SparseArray<View>
    ) {
        val dbgPrefix = "${object {}.javaClass.enclosingMethod?.name}"

        var position = anchorView
            ?.let { getPosition(it) } ?: run { viewCache.keyAt(viewCache.size() - 1) }

        var fillLeft = true
        var viewRight = 0

        while (fillLeft && position >= 0) {
            var child = viewCache.get(position)

            // В кэше есть элемент и его левая граница в области видимости
            if (child != null && getDecoratedLeft(child) < horizontalSpace) {
                attachView(child, 0)
                viewCache.remove(position)
            }
            // Если кэш закончился, то запрашиваем новый View
            else {
                child = recycler.getViewForPosition(position)
                addView(child, 0)
                makeChildMeasure(child)

                layoutDecorated(
                    child,
                    viewRight - decoratedChildWidth,
                    0,
                    viewRight,
                    decoratedChildHeight
                )
            }

            viewRight = getDecoratedLeft(child)
            fillLeft = viewRight >= 0
            position--
        }
    }

    /**
     * Не путай направления: Направление движения пальца ПРОТИВОПОЛОЖНО направлению
     * заполнения view port'а элементами. Например, тащим палец влево, освобождаются
     * места справа и заполняем элементами слева направо.
     */
    override fun scrollHorizontallyBy(
        dx: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State?
    ): Int {
        val dbgPrefix = "${object {}.javaClass.enclosingMethod?.name}"

        // 1. Вычислить delta
        val delta = calculateHorizontalScrollOffset(dx)

        // 2. Проскролить на значение delta
        offsetChildrenHorizontal(-delta)

        // 3. ПОСЛЕ того как всех сдвинули добавляем новые элементы там где это требуется.
        val leftView = getChildAt(0)!!
        val rightView = getChildAt(childCount - 1)!!
        when {
            // Палец влево.
            (dx > 0) -> {
                if (getDecoratedRight(rightView) < horizontalSpace) {
                    fillRows(recycler, FillDirection.DIRECTION_RIGHT)
                }
            }
            // Палец вправо.
            (dx < 0) -> {
                if (getDecoratedLeft(leftView) > 0) {
                    fillRows(recycler, FillDirection.DIRECTION_LEFT)
                }
            }
            else -> fillRows(recycler)
        }

        return delta
    }


    override fun scrollVerticallyBy(
        dy: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State?
    ): Int {
        val dbgPrefix = "${object {}.javaClass.enclosingMethod?.name}"

        // 1. Вычислить delta
        val delta = calculateVerticalScrollOffset(dy)

        // 2. Проскролить на значение delta
        offsetChildrenVertical(-delta)

        // 3. ПОСЛЕ того как всех сдвинули добавляем новые элементы там где это требуется.
        val topView = getChildAt(0)!!
        val bottomView = getChildAt(childCount - 1)!!
        when {
            // Палец вверх.
            (dy > 0) -> {
                if (getDecoratedBottom(bottomView) < verticalSpace) {
                    fillRows(recycler, FillDirection.DIRECTION_DOWN)
                }
            }
            // Палец вниз.
            (dy < 0) -> {
                if (getDecoratedTop(topView) > 0) {
                    fillRows(recycler, FillDirection.DIRECTION_UP)
                }
            }
            else -> fillRows(recycler)
        }

        return delta
    }

    //         Рис. searchAnchorView()
    //
    //         DIRECTION_DOWN
    //
    //        Anchor position 3
    //         |-------------|               DIRECTION_UP
    //         | 2           |
    //         |-------------|            Anchor position 8
    //      |==|=3===========|==|       |===================|
    //      |  |-------------|  |       |  |-------------|  |
    //      |  | 4           |  |       |  | 3           |  |
    //      |  |-------------|  |       |  |-------------|  |
    //      |  | 5           |  |       |  | 4           |  |
    //      |  |-------------|  |       |  |-------------|  |
    //      |  | 6           |  |       |  | 5           |  |
    //      |  |-------------|  |       |  |-------------|  |
    //      |  | 7           |  |       |  | 6           |  |
    //      |  |-------------|  |       |  |-------------|  |
    //      |  | 8           |  |       |  | 7           |  |
    //      |  |-------------|  |       |  |-------------|  |
    //      |===================|       |==|=8===========|==|
    //                                     |-------------|
    //                                     | 9           |
    //                                     |-------------|
    /**
     * Поиск якорной View.
     * Если в результате прокрутки освободилось место снизу, то заполняем view port
     * вьюхами сверху вниз (DIRECTION_DOWN). Якорная вью в этом случае - первая видимая сверху.
     * На рисунке слева она вторая сверху (позиция в адаптере 3)
     *
     * Если в результате прокрутки освободилось место сверху, то заполняем view port
     * вьюхами снизу вверх (DIRECTION_UP). Якорная вью в этом случае - первая видимая снизу.
     * На рисунке справа она вторая снизу (позиция в адаптере 8)
     *
     * Если в результате прокрутки освободилось место справа, то заполняем view port
     * вьюхами слева направо (DIRECTION_RIGHT). Якорная вью - первая видимая слева.
     *
     * Если в результате прокрутки освободилось место слева, то заполняем view port
     * вьюхами справа налево  (DIRECTION_LEFT). Якорная вью - первая видимая справа.
     */
    private fun searchAnchorView(viewCache: SparseArray<View>, direction: FillDirection): View? {
        var view: View? = null
        var search = true
        var index: Int

        when (direction) {
            FillDirection.DIRECTION_DOWN -> {
                index = 0
                while (search && index < (viewCache.size() - 1)) {
                    viewCache.valueAt(index)?.let { child ->
                        if (getDecoratedBottom(child) >= 0) {
                            view = child
                            search = false
                            return@let
                        } else {
                            index++
                        }
                    }
                }
            }
            FillDirection.DIRECTION_UP -> {
                index = viewCache.size() - 1
                while (search && index >= 0) {
                    viewCache.valueAt(index)?.let { child ->
                        if (getDecoratedTop(child) <= verticalSpace) {
                            view = child
                            search = false
                            return@let
                        } else {
                            index--
                        }
                    }
                }
            }
            FillDirection.DIRECTION_RIGHT -> {
                index = 0
                while (search && index < (viewCache.size() - 1)) {
                    viewCache.valueAt(index)?.let { child ->
                        if (getDecoratedRight(child) >= 0) {
                            view = child
                            search = false
                            return@let
                        } else {
                            index++
                        }
                    }
                }
            }
            FillDirection.DIRECTION_LEFT -> {
                index = viewCache.size() - 1
                while (search && index >= 0) {
                    viewCache.valueAt(index)?.let { child ->
                        if (getDecoratedLeft(child) <= horizontalSpace) {
                            view = child
                            search = false
                            return@let
                        } else {
                            index--
                        }
                    }
                }
            }
            FillDirection.DIRECTION_NONE -> {
//                view = viewCache.valueAt(0)
            }
        }

        return view
    }


    /**
     * Вычислить реальное значение для dY
     *
     * NOTE: Размеры getDecorated... определяют максимальную поверхность,
     * которую занимает элемент списка. Туда входят и его маргины и его декораторы, то есть
     * с учетом decorated можно сказать, что элементы списка плотно лежат друг на друге
     * без каких-либо промежутков: каждый последующий элемент начинается сразу после окончания
     * предыдущего. Не может быть так, что один вышел за пределы viewport'а, соседний остался
     * внутри и между ними какое-то пространство никем не занятое и по которому
     * проходит border viewport'а. Все элементы смежные. Первый закончился, со следующего
     * пикселя начинается второй и т.д.
     */
    private fun calculateVerticalScrollOffset(dy: Int): Int {
        val dbgPrefix = "${object {}.javaClass.enclosingMethod?.name}"

        // 1. Нет Views в RecyclerView (return)
        if (childCount == 0) return 0

        // 2. Все элементы уместились в view port (return 0)
        val topView = getChildAt(0)!!
        val bottomView = getChildAt(childCount - 1)!!

        val viewSpan = getDecoratedBottom(bottomView) - getDecoratedTop(topView)
        if (viewSpan < verticalSpace) return 0

        val bottomOffset: Int
        val topOffset: Int

        val topBoundReached = getPosition(topView) == 0
        val bottomBoundReached = getPosition(bottomView) == (itemCount - 1)

        return when {

            /**
             * Палец и контент идут вверх
             *
             * В это место попадаем, если нижняя граница последнего элемента НИЖЕ границы
             * view port, а значит результат getDecoratedBottom(bottomView) - verticalSpace
             * имеет положительное значение.
             */
            dy > 0 -> {
                bottomOffset = getDecoratedBottom(bottomView) - verticalSpace

                if (bottomBoundReached) {
                    logIt("$dbgPrefix bottomBoundReached")
                    min(bottomOffset, dy)  // Минимальное из двух ПОЛОЖИТЕЛЬНЫХ чисел
                } else {
                    val position = getPosition(bottomView)

                    // Расчитываем случай когда dy больше чем высота оставшихся элементов в адаптере
                    min(dy, (itemCount - position - 1) * decoratedChildHeight + bottomOffset)
                }
            }

            /**
             * Палец и контент идут вниз
             */
            dy < 0 -> {
                topOffset = getDecoratedTop(topView)

                if (topBoundReached) {
                    logIt("$dbgPrefix topBoundReached")
                    max(dy, topOffset) // Максимальное из двух ОТРИЦАТЕЛЬНЫХ чисел
                } else {
                    val position = getPosition(topView)
                    // Расчитываем случай когда dy больше чем высота оставшихся элементов в адаптере
                    max(dy, -position * decoratedChildHeight + topOffset)
                }
            }
            else -> {
                0
            }
        }
    }

    /**
     * Допилить. На осилил )
     */
    private fun calculateHorizontalScrollOffset(dx: Int): Int {
        val dbgPrefix = "${object {}.javaClass.enclosingMethod?.name}"

        // 1. Нет Views в RecyclerView (return)
        if (childCount == 0) return 0

        // 2. Все элементы уместились в view port (return 0)
        val leftView = getChildAt(0)!!
        val rightView = getChildAt(childCount - 1)!!

        val viewSpan = getDecoratedRight(rightView) - getDecoratedLeft(leftView)
        if (viewSpan < horizontalSpace) return 0

        val rightOffset: Int
        val leftOffset: Int

        val leftBoundReached = getPosition(leftView) == 0
        val rightBoundReached = getPosition(rightView) == (itemCount - 1)

        return when {

            /**
             * Палец и контент идут влево
             *
             * В это место попадаем, если правая граница последнего элемента правее границы
             * view port, а значит результат getDecoratedRight(bottomView) - horizontalSpace
             * имеет положительное значение.
             */
            dx > 0 -> {
                rightOffset = getDecoratedRight(rightView) - horizontalSpace

                if (rightBoundReached) {
                    logIt("$dbgPrefix rightBoundReached")
                    min(rightOffset, dx)  // Минимальное из двух ПОЛОЖИТЕЛЬНЫХ чисел
                } else {
                    val position = getPosition(rightView)

                    // Расчитываем случай когда dy больше чем высота оставшихся элементов в адаптере
                    min(dx, (itemCount - position - 1) * decoratedChildWidth + rightOffset)
                }
            }

            /**
             * Палец и контент идут вниз
             */
            dx < 0 -> {
                leftOffset = getDecoratedLeft(leftView)

                if (leftBoundReached) {
                    logIt("$dbgPrefix leftBoundReached")
                    max(dx, leftOffset) // Максимальное из двух ОТРИЦАТЕЛЬНЫХ чисел
                } else {
                    val position = getPosition(leftView)
                    // Расчитываем случай когда dy больше чем высота оставшихся элементов в адаптере
                    max(dx, -position * decoratedChildWidth + leftOffset)
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

    private val totalColCount: Int
        get() = itemCount

    /**
     * "Полезное" вертикальное пространство для размещения дочерних Views.
     * То есть высота RecyclerView минус вертикальные padding'и.
     */
    private val verticalSpace: Int
        get() = height

    private val horizontalSpace: Int
        get() = width

    /**
     * Расчитать количество видимых строк плюс 1, но не более количества элементов в адаптере.
     */
    private fun updateWindowSizing() {

        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            visibleRowCount = verticalSpace / decoratedChildHeight
            visibleColCount = 1

            if (verticalSpace % decoratedChildHeight != 0) {
                visibleRowCount++
            }
            visibleRowCount = min(totalRowCount, visibleRowCount)

        } else {
            visibleRowCount = 1
            visibleColCount = horizontalSpace / decoratedChildWidth

            if (horizontalSpace % decoratedChildWidth != 0) {
                visibleColCount++
            }
            visibleColCount = min(totalColCount, visibleColCount)
        }
    }

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {

//        val dimension : Int = context.resolveAttribute(android.R.attr.actionBarSize) ?: RecyclerView.LayoutParams.WRAP_CONTENT
//
//        return if(orientation == Configuration.ORIENTATION_PORTRAIT) {
//            RecyclerView.LayoutParams(
//                RecyclerView.LayoutParams.MATCH_PARENT,
//                dimension)
//        } else {
//            RecyclerView.LayoutParams(
//                dimension,
//                RecyclerView.LayoutParams.MATCH_PARENT)
//        }

        return RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.WRAP_CONTENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )
    }
}