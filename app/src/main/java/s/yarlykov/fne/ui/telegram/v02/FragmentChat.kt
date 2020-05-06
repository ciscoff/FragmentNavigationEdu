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
import androidx.transition.ChangeBounds
import androidx.transition.Fade
import androidx.transition.TransitionInflater
import androidx.transition.TransitionSet
import com.google.android.material.appbar.CollapsingToolbarLayout
import s.yarlykov.fne.R
import s.yarlykov.fne.ui.telegram.data.ClientMessage
import s.yarlykov.fne.ui.telegram.data.DoerMessage

const val FADE_DURATION = 300L
const val MOVE_DURATION = 500L

class FragmentChat : Fragment() {

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

    companion object {
        fun newInstance(): FragmentChat = FragmentChat()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_chat, container, false)

        val toolbar: Toolbar = root.findViewById<View>(R.id.toolbar_telegram) as Toolbar
        toolbar.title = getString(R.string.title_chat)

        val collapsingToolbar = root.findViewById<CollapsingToolbarLayout>(R.id.collapsing_toolbar)
//        collapsingToolbar.title = getString(R.string.title_chat)

        toolbar.setOnClickListener {
            performTransition(root)
        }

        /**
         *  У фрагментов нет возможности иметь ActionBar. Это свойство активити.
         */
        with(activity as AppCompatActivity) {
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()

        initRecyclerView(view)
    }

    private fun initRecyclerView(root: View) {
        val view = root.findViewById<RecyclerView>(R.id.rv_chat)
        val chatAdapter =
            AdapterChat(model.reversed())

        view.apply {
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(view.context, RecyclerView.VERTICAL, true)
        }
    }

    private fun performTransition(root: View) {

        val fragmentManager = activity?.supportFragmentManager

        val nextFragment = FragmentClient.newInstance()
        val currentFragment = this

        val exitFade = Fade().also {
            it.setDuration(FADE_DURATION)
        }

        val enterFade = Fade().also {
            it.setDuration(FADE_DURATION)
        }

        currentFragment.setExitTransition(exitFade)
        nextFragment.setSharedElementEnterTransition(ChangeBounds())
        nextFragment.setEnterTransition(enterFade)

        val logoImage = root.findViewById<ImageView>(R.id.backdrop)

        val transaction = fragmentManager
            ?.beginTransaction()
            ?.addSharedElement(logoImage, logoImage.transitionName)
            ?.replace(R.id.fragment_container, nextFragment)
//            ?.setReorderingAllowed(true)
            ?.addToBackStack(currentFragment::class.java.simpleName)
            ?.commit()
    }
}