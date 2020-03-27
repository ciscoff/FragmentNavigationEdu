package s.yarlykov.fne.moving

/**
 * https://developer.android.com/training/animation/layout
 * https://developer.android.com/reference/android/animation/LayoutTransition
 */
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import s.yarlykov.fne.R

class MovingActivity : AppCompatActivity() {

    private lateinit var movingCard: MaterialCardView
    private var shortAnimationDuration: Int = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_moving)

        val rv = findViewById<RecyclerView>(R.id.rv_text)
        movingCard = findViewById(R.id.card_moving)
        val topCard = findViewById<MaterialCardView>(R.id.card_top)



        topCard.setOnClickListener {

            if (movingCard.visibility == View.VISIBLE)
                movingCard.visibility = View.GONE
            else
                movingCard.visibility = View.VISIBLE
        }

        rv.apply {
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            adapter = TextAdapter(listOf("Anna", "Papa", "Mama", "Family"))
            layoutManager = LinearLayoutManager(this@MovingActivity, RecyclerView.VERTICAL, false)
        }

    }

    override fun onResume() {
        super.onResume()
//        crossfade()
    }

    private fun crossfade() {
        movingCard.apply {
            //            alpha = 0f
            visibility = View.VISIBLE

            pivotX = measuredWidth.toFloat()
            pivotY = 0f

            scaleX = 0f
            scaleY = 0f

            this.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(shortAnimationDuration.toLong())
                .setListener(null)

            animate()
                .alpha(1f)
                .setDuration(shortAnimationDuration.toLong())
                .setListener(null)
        }
    }

    private fun movingOn() {


    }
}
