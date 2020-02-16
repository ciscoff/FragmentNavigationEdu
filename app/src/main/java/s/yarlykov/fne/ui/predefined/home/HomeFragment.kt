package s.yarlykov.fne.ui.predefined.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import s.yarlykov.fne.R

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var navController: NavController
    private lateinit var buttons : Map<Int, Button>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_home, container, false)

        val textView: TextView = root.findViewById(R.id.text_home)
        homeViewModel.text.observe(this, Observer {
            textView.text = it
        })

        buttons = root.run {
            mapOf(
                R.id.nav_location to findViewById<Button>(R.id.nav_location) as Button,
                R.id.nav_date to findViewById<Button>(R.id.nav_date) as Button,
                R.id.nav_price to findViewById<Button>(R.id.nav_price) as Button
            )
        }

        /**
         * https://developer.android.com/reference/androidx/navigation/fragment/NavHostFragment
         */
        val navHostFragment = childFragmentManager.findFragmentById(R.id.nav_ticket_controller)
        navController = Navigation.findNavController(navHostFragment?.view!!)

        initNavigationControls(navController)

        return root
    }

    private fun initNavigationControls(navController: NavController) {

        /**
         * Кликнутая кнопка становится disabled
         */
        fun disableClicked(id: Int) {
            buttons.entries.forEach {
                it.value.isEnabled = id != it.key
                it.value.setBackgroundResource(
                    if(it.value.isEnabled)
                        R.drawable.background_button_inactive
                    else
                        R.drawable.background_button_active
                )
            }
        }

        /**
         * При клике на кнопку переключаем фрагмент и дизейблим кнопку
         */
        fun setButtonClickListener(buttonId : Int) {
            with(buttonId){
                buttons[this]?.setOnClickListener {
                    navController.navigate(this)
                    disableClicked(this)
                }
            }
        }

        buttons.keys.forEach { buttonId ->
            setButtonClickListener(buttonId)
        }
    }
}