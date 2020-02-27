package s.yarlykov.fne.ui.exchange

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_two.*
import s.yarlykov.fne.R

class ActivityTwo : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_two)
        extractData()
    }

    fun extractData() {
        val gson = GsonBuilder().create()

        val pojoList = gson.fromJson<PojoList>(
            intent.getStringExtra(Pojo::class.java.simpleName),
            PojoList::class.java)

        System.out.println(pojoList)

        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        pojoList.list!!.forEach {
            val llView = inflater.inflate(R.layout.layout_row, null)
            llView.id = it.hashCode()

            val tv = llView.findViewById<TextView>(R.id.tv_content)
            tv.text = it.name
            ll_content.addView(llView)
        }
    }
}
