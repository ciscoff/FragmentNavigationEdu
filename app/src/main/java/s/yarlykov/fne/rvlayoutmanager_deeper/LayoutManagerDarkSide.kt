package s.yarlykov.fne.rvlayoutmanager_deeper


import android.content.Context
import android.util.SparseArray
import android.view.View
import androidx.collection.SparseArrayCompat
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.min

class LayoutManagerDarkSide : RecyclerView.LayoutManager() {

    private var decoratedChildWidth: Int = 0
    private var decoratedChildHeight: Int = 0

    // Количество видимых строк плюс 1
    private var visibleRowCount = 0

    // Adapter position для первого видимого элемента внутри RecyclerView
    private var firstVisiblePosition = 0

    /**
     *
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
         * getChildCount() - Количество дочерних View приаттаченных к RecyclerView БЕЗ учета
         * Viewб которые detached and/or scrapped. 'mChildHelper.getChildCount()'
         *
         * childCount == 0 при начальном Layout'е. Если Layout начальный, то мы должны взять одну
         * View, измерить её и сохранить размеры в глобальных переменных для последующего
         * ипользования с другими View. В шашем случае все View одинакового размера.
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

    private fun fillRows(recycler: RecyclerView.Recycler) {
        val viewCache = SparseArray<View>(childCount)

        var leftOffset = 0
        var topOffset = 0

        // Заполнить локальный кэш
        if (childCount != 0) {

            // Все видимое в кэш
            for (i in 0 until childCount) {
                val position = positionOfIndex(i)
                viewCache.put(position, getChildAt(i))
            }

            // Скрапим содержимое кэша
            for (i in 0 until viewCache.size()) {
                detachView(viewCache.valueAt(i))
            }
        }

        for (i in 0 until getVisibleChildCount()) {
            val nextPosition = positionOfIndex(i)

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

        for (i in 0 until viewCache.size()) {
            recycler.recycleView(viewCache.valueAt(i))
        }
    }

    private fun getVisibleChildCount(): Int = visibleRowCount

    /**
     * Маппинг между индексом внутри RecyclerView и adapter position
     */
    private fun positionOfIndex(childIndex: Int): Int {
        return firstVisiblePosition + childIndex
    }

    /**
     * Расчитать количество видимых строк плюс 1, но не более количества элементов в адаптере.
     */
    private fun updateWindowSizing() {
        visibleRowCount = (getVerticalSpace() / decoratedChildHeight) + 1

        if (getVerticalSpace() % decoratedChildHeight > 0) {
            visibleRowCount++
        }

        visibleRowCount = min(getTotalRowCount(), visibleRowCount)
    }

    private fun getTotalRowCount(): Int {
        return itemCount
    }

    private fun getHorizontalSpace(): Int {
        return width - paddingLeft - paddingRight
    }

    private fun getVerticalSpace(): Int {
        return height - paddingTop - paddingBottom
    }

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.WRAP_CONTENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )
    }
}