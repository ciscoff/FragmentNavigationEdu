package s.yarlykov.fne.ui.predefined.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import s.yarlykov.fne.R
import s.yarlykov.fne.ui.custom.SlidingFragment

private const val NUM_PAGES = 3

class GalleryFragment : Fragment() {

    private lateinit var viewPager: ViewPager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_gallery, container, false)

        root.findViewById<ViewPager>(R.id.view_pager)?.also { pager ->
            viewPager = pager
            viewPager.adapter = ScreenSlidePagerAdapter(childFragmentManager)
        }

        return root
    }

    private inner class ScreenSlidePagerAdapter(fm: FragmentManager) :
        FragmentStatePagerAdapter(fm) {
        override fun getCount(): Int = NUM_PAGES

        override fun getItem(position: Int): Fragment = SlidingFragment.create(position)
    }
}