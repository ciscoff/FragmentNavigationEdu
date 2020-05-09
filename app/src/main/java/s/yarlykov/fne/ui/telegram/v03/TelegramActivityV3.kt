package s.yarlykov.fne.ui.telegram.v02

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentManager
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

class TelegramActivityV3 : AppCompatActivity() {

    lateinit var fragmentManager: FragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_telegram)

        fragmentManager = supportFragmentManager

        loadChatFragment()
    }


    private fun loadChatFragment() {

        val fragmentChat = FragmentChat.newInstance()

        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragmentChat)
        transaction.commit()
    }

}
