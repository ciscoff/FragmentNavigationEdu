package s.yarlykov.fne.ui.telegram.v03

/**
 * Matrix crop
 * https://stackoverflow.com/questions/29783358/how-set-imageview-scaletype-to-topcrop
 */

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Matrix
import android.graphics.RectF
import android.os.Bundle
import android.transition.Scene
import android.transition.Transition
import android.transition.TransitionInflater
import android.transition.TransitionManager
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.addListener
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.fragment.app.FragmentManager
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import s.yarlykov.fne.R
import s.yarlykov.fne.extensions.setRoundedDrawable
import s.yarlykov.fne.ui.telegram.v02.FragmentChat

class TelegramActivityV3 : AppCompatActivity() {

    private lateinit var fragmentManager: FragmentManager
    private lateinit var transition: Transition

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_telegram)

        fragmentManager = supportFragmentManager

        loadChatFragment()

        val ivAvatar = findViewById<ImageView>(R.id.iv_rounded)
        ivAvatar.setRoundedDrawable(R.drawable.valery)

        val rd : RoundedBitmapDrawable = ivAvatar.drawable as RoundedBitmapDrawable
//        ObjectAnimator.ofFloat(rd, "cornerRadius", 0f).apply {
//            duration = 2000
//            start()
//        }

        val animSet = AnimatorSet()
        val animRadius = ObjectAnimator.ofFloat(rd, "cornerRadius", 0f)
        val animScaleX = ObjectAnimator.ofFloat(ivAvatar, "scaleX", 1f, 2f)
        val animScaleY = ObjectAnimator.ofFloat(ivAvatar, "scaleY", 1f, 2f)
        val animX = ObjectAnimator.ofInt(ivAvatar, "width", 1000)
//        val animY = ObjectAnimator.ofFloat(ivAvatar, "height", 250f)

        animSet.playTogether(animRadius, animScaleX, animScaleY)
        animSet.duration = 2000
//        animSet.start()

        increaseViewSize(ivAvatar, 500)


//        runOldAnimation()
    }

    private fun loadChatFragment() {
        val fragmentChat = FragmentChat.newInstance()
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragmentChat)
        transaction.commit()
    }


    private fun runOldAnimation() {


//        val fadeTransition: Transition = Fade()
//        val changeBounds = ChangeBounds()

        val sceneA = findViewById<FrameLayout>(R.id.scene_a)
//        val sceneB = findViewById<FrameLayout>(R.id.scene_b)

        val sceneRoot = findViewById<FrameLayout>(R.id.scene_root)
//        val endingScene = Scene.getSceneForLayout(sceneRoot, R.layout.scene_image_scale_b, this)

        val sceneB = layoutInflater.inflate(R.layout.scene_image_scale_b, null)
//        val endingScene = Scene(sceneRoot, sceneB)

        transition = TransitionInflater.from(this)
            .inflateTransition(R.transition.v3_transition)

        sceneRoot.setOnClickListener {
            //            TransitionManager.go(endingScene, changeBounds)

//            TransitionManager.beginDelayedTransition(sceneRoot, changeBounds)

            val ivMatrix = sceneB.findViewById<ImageView>(R.id.iv_matrix)
            ivMatrix.scaleType = ImageView.ScaleType.MATRIX

            Picasso
                .get()
                .load(R.drawable.valery)
                .into(ivMatrix, object : Callback.EmptyCallback() {

                    override fun onSuccess() {

                        ivMatrix.drawable?.let { drawable ->
                            val drawableRect = RectF(
                                0f,
                                0f,
                                drawable.intrinsicWidth.toFloat(),
                                drawable.intrinsicHeight.toFloat()
                            )
                            val viewRect = RectF(
                                0f,
                                0f,
                                ivMatrix.width.toFloat(),
                                ivMatrix.height.toFloat()
                            )

                            val scale: Float =
                                if (drawableRect.width() * viewRect.height() > drawableRect.height() * viewRect.width()) {
                                    viewRect.height() / drawableRect.height()
                                } else {
                                    viewRect.width() / drawableRect.width()
                                }

                            val matrix = Matrix().apply {
                                setScale(scale, scale)
//                                setRectToRect(drawableRect, viewRect, Matrix.ScaleToFit.CENTER)
                            }

                            ivMatrix.setImageMatrix(matrix)
                        }
                    }
                })

//            sceneA.visibility = View.INVISIBLE
//            sceneB.visibility = View.VISIBLE
            val endingScene = Scene(sceneRoot, sceneB)
            TransitionManager.go(endingScene, transition)
        }

    }

    private fun increaseViewSize(view: View, increaseValue: Int) {
        val valueAnimator =
            ValueAnimator.ofInt(view.measuredHeight, view.measuredHeight + increaseValue)
        valueAnimator.duration = 1500L
        valueAnimator.addUpdateListener {
            val animatedValue = valueAnimator.animatedValue as Int
            val layoutParams = view.layoutParams
            layoutParams.height = animatedValue
            view.layoutParams = layoutParams
        }
        valueAnimator.start()
    }


}
