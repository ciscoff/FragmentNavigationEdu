package s.yarlykov.fne.ui.telegram

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import s.yarlykov.fne.R

import kotlinx.android.synthetic.main.activity_chat.*
import s.yarlykov.fne.ui.telegram.data.ClientMessage
import s.yarlykov.fne.ui.telegram.data.DoerMessage

class ChatActivity : AppCompatActivity() {

    private val model = listOf(
        ClientMessage("Добрый день !", "24.04, 18:37"),
        DoerMessage("Здравствуйте !", "24.04, 18:40")
    )

    companion object {
        const val KEY_CHAT_ID = "KEY_CHAT_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_chat)

        val toolbar: Toolbar = findViewById<View>(R.id.toolbar_shared) as Toolbar
        setSupportActionBar(toolbar)

        // Скрываем Status Bar (полноэкранный режим)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        supportActionBar?.title = "Чат с клиентом"
        toolbar.setNavigationIcon(R.drawable.ic_apps)

        getExtras()


        initRecyclerView()
    }

    // Получить chatId
    private fun getExtras(): Int {
        return intent.getIntExtra(KEY_CHAT_ID, 0)
    }

    private fun initRecyclerView() {
        val view = findViewById<RecyclerView>(R.id.chat_recyclerview)
        val chatAdapter = AdapterClientChat(model.reversed())

        view.apply {
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(view.context, RecyclerView.VERTICAL, true)
        }
    }
}
