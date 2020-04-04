package s.yarlykov.fne.rvlayoutmanager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import s.yarlykov.fne.R

class RvActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rv)

        val pics = mutableListOf<Int>()

        with(resources.obtainTypedArray(R.array.month_pics)) {
            (0 until length()).forEach { i ->
                pics.add(getResourceId(i, R.drawable.bkg_06_jun))
            }
            recycle()
        }

        initRecycleView(pics)

    }

    private fun initRecycleView(data: List<Int>) {

        val recycleView = findViewById<RecyclerView>(R.id.cool_list)

        recycleView.apply {
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()

            // Создать адаптер и подписать презентер на клики по элементам списка
            adapter = ItemsAdapter(data)

            layoutManager = CustomLayoutManager()
        }
    }



}
