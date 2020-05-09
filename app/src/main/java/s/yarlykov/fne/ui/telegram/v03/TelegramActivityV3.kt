package s.yarlykov.fne.ui.telegram.v02

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
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
import kotlin.math.max

class TelegramActivityV3 : AppCompatActivity() {

    lateinit var fragmentManager: FragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_telegram)

        fragmentManager = supportFragmentManager

        loadChatFragment()

        val image = findViewById<ImageView>(R.id.iv_rounded)

        val srcBitmap = BitmapFactory.decodeResource(resources, R.drawable.valery)

        val roundedBitmap =  RoundedBitmapDrawableFactory.create(resources, srcBitmap).apply {
            cornerRadius = max(srcBitmap.width, srcBitmap.height) / 2.0f
        }

        image.setImageDrawable(roundedBitmap)
    }


    private fun loadChatFragment() {

        val fragmentChat = FragmentChat.newInstance()

        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragmentChat)
        transaction.commit()
    }



}
