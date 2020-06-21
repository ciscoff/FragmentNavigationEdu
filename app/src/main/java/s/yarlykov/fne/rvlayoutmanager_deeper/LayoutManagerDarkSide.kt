package s.yarlykov.fne.rvlayoutmanager_deeper


import androidx.recyclerview.widget.RecyclerView

class LayoutManagerDarkSide : RecyclerView.LayoutManager() {

    private var childDecoratedWidth: Int = 0
    private var childDecoratedHeight: Int = 0


    /**
     *
     */
    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State?) {

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
            childDecoratedWidth = getDecoratedMeasuredWidth(scrap)
            childDecoratedHeight = getDecoratedMeasuredHeight(scrap)


        }

    }


    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.WRAP_CONTENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )
    }
}