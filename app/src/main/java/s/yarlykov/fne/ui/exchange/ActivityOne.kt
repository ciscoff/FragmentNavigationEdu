package s.yarlykov.fne.ui.exchange

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import s.yarlykov.fne.R
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_one.*


class PojoList {
    var list : List<Pojo>? = null

    override fun toString(): String {
        return list!!.map {
            it.toString()
        }.joinToString ("\n")

    }
}

class ActivityOne : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_one)

        btn_action.setOnClickListener {
            sendPojo()
        }
    }

    private fun sendPojo() {

        val pojoList = PojoList()

        pojoList.list = listOf(
            Pojo().also {
                it.name = "alice"
                it.age = 15
                it.kids = listOf("cat", "dog")
                it.kidsAge = listOf(5, 4)
            },
            Pojo().also {
                it.name = "bob"
                it.age = 20
                it.kids = listOf("molly", "dolly")
                it.kidsAge = listOf(10, 12)
            }
        )

        val gson = GsonBuilder().create()

        val intent = Intent(this, ActivityTwo::class.java).also {
            it.putExtra(Pojo::class.java.simpleName, gson.toJson(pojoList))
        }

        startActivity(intent)
    }
}
