package s.yarlykov.fne.ui.telegram.v01

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import s.yarlykov.fne.R
import s.yarlykov.fne.ui.telegram.data.ClientMessage
import s.yarlykov.fne.ui.telegram.data.DoerMessage

class ChatActivity : AppCompatActivity() {

    private val model = listOf(
        ClientMessage("Добрый день !", "24.04, 18:17"),
        DoerMessage("Здравствуйте !", "24.04, 18:18"),
        ClientMessage("Добрый день !", "24.04, 18:19"),
        DoerMessage("Здравствуйте !", "24.04, 18:20"),
        ClientMessage("Добрый день !", "24.04, 18:21"),
        DoerMessage("Здравствуйте !", "24.04, 18:22"),
        ClientMessage("Добрый день !", "24.04, 18:23"),
        DoerMessage("Здравствуйте !", "24.04, 18:25"),
        ClientMessage("Добрый день !", "24.04, 18:27"),
        DoerMessage("Здравствуйте !", "24.04, 18:30"),
        ClientMessage("Добрый день !", "24.04, 18:32"),
        DoerMessage("Здравствуйте !", "24.04, 18:33"),
        ClientMessage("Добрый день !", "24.04, 18:34"),
        DoerMessage("Здравствуйте !", "24.04, 18:35"),
        ClientMessage("Добрый день !", "24.04, 18:37"),
        DoerMessage("Здравствуйте !", "24.04, 18:40")
    )

    private var isExpanded = true

    companion object {
        const val KEY_CHAT_ID = "KEY_CHAT_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_chat)

        val appBarLayout = findViewById<AppBarLayout>(R.id.chat_appbar)

        val toolbar: Toolbar = findViewById<View>(R.id.toolbar_shared) as Toolbar
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val collapsingToolbar = findViewById<CollapsingToolbarLayout>(R.id.collapsing_toolbar)
        collapsingToolbar.title = getString(R.string.action_settings)


        toolbar.setOnClickListener {
            isExpanded = !isExpanded
            appBarLayout.setExpanded(isExpanded, true)
        }

        initRecyclerView()

        loadBackdrop()

        // Скрываем Status Bar (полноэкранный режим)
//        window.setFlags(
//            WindowManager.LayoutParams.FLAG_FULLSCREEN,
//            WindowManager.LayoutParams.FLAG_FULLSCREEN)

//        supportActionBar?.title = "Чат с клиентом"
//        toolbar.setNavigationIcon(R.drawable.ic_apps)



        getExtras()


    }

    private fun loadBackdrop() {
        val imageView = findViewById<ImageView>(R.id.backdrop)
        Glide.with(this).load(R.drawable.bkg_01_jan)
            .apply(RequestOptions.centerCropTransform()).into(imageView)
    }

    // Получить chatId
    private fun getExtras(): Int {
        return intent.getIntExtra(KEY_CHAT_ID, 0)
    }

    private fun initRecyclerView() {
        val view = findViewById<RecyclerView>(R.id.chat_recyclerview)
        val chatAdapter =
            AdapterClientChat(model.reversed())

        view.apply {
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(view.context, RecyclerView.VERTICAL, true)
        }
    }
}
