package s.yarlykov.fne.ui.telegram.v02

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import s.yarlykov.fne.R

class FragmentClient : Fragment() {
    private val model = listOf(
       "Уведомления",
        "Телефон",
        "email",
        "telegram",
        "skype",
        "Social",
        "Facebook"
    )

    companion object {
        fun newInstance(): FragmentClient = FragmentClient()
    }

    private var isExpanded = true


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_client, container, false)

        val appBarLayout = root.findViewById<AppBarLayout>(R.id.telegram_appbar)

        val toolbar: Toolbar = root.findViewById<View>(R.id.toolbar_telegram) as Toolbar

        val collapsingToolbar = root.findViewById<CollapsingToolbarLayout>(R.id.collapsing_toolbar)
        collapsingToolbar.title = getString(R.string.title_chat)

        /**
         *  У фрагментов нет возможности иметь ActionBar. Это свойство активити.
         */
        with(activity as AppCompatActivity) {
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        toolbar.setOnClickListener {
            isExpanded = !isExpanded
            appBarLayout.setExpanded(isExpanded, true)
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postponeEnterTransition()
        initRecyclerView(view)
        startPostponedEnterTransition()
//        loadBackdrop(view)

    }

    private fun initRecyclerView(root: View) {
        val view = root.findViewById<RecyclerView>(R.id.rv_client)
        val chatAdapter =
            AdapterClient(model.reversed())

        view.apply {
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(view.context, RecyclerView.VERTICAL, true)
        }
    }

    private fun loadBackdrop(view: View) {
        val imageView = view.findViewById<ImageView>(R.id.backdrop)
        Glide.with(this).load(R.drawable.bkg_01_jan)
            .apply(RequestOptions.centerCropTransform()).into(imageView)
    }
}