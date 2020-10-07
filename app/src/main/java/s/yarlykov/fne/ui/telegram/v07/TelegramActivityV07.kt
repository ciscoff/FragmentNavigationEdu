package s.yarlykov.fne.ui.telegram.v07

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_telegram_v07.*
import s.yarlykov.fne.R

class TelegramActivityV07 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_telegram_v07)
        initList()
    }

    private fun initList() {

        listView.apply {
            layoutManager = CustomLayoutManager(this@TelegramActivityV07, actionBarLayout)
            setHasFixedSize(true)
            itemAnimator = null
            adapter = AdapterLinear()
        }

        (listView as SmartRecyclerView).setOnOffsetListener(::dataExchanger)
    }

    /**
     * Рестранслятор сообщения между дочерними View. Надо бы заменить на что-то более путевое.
     */
    private fun dataExchanger(offset: Int) {
        actionBarLayout.onOffsetChanged(offset)
    }
}