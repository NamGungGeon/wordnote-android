package kr.ac.konkuk.wordnote

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextPaint
import androidx.appcompat.app.AppCompatActivity
import com.magicgoop.tagsphere.item.TextTagItem
import kr.ac.konkuk.wordnote.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        MyHistoryManager.useInstance(this) { manager -> }
        //init wordList
        VocaManager.useInstance(this) { manager ->

            //loaded!
            if (isDestroyed)
                return@useInstance

            runOnUiThread {
                if (manager.vocaList.isNotEmpty())
                    binding.apply {
                        val vocaList = ArrayList<Voca>(manager.vocaList)
                        vocaList.shuffle()
                        vocaList.subList(
                            0,
                            if (manager.vocaList.size > 20) 20 else manager.vocaList.size
                        ).map {
                            TextTagItem(text = it.word)
                        }.toList().let {
                            splashTagView.addTagList(it)
                        }
                        splashTagView.setTextPaint(
                            TextPaint().apply {
                                isAntiAlias = true
                                textSize = 30f
                                color = Color.WHITE
                            }
                        )

                        splashTagView.setRadius(3f)
                        splashTagView.requestLayout()
                        splashTagView.rotateOnTouch(false)
                        splashTagView.startAutoRotation(5f, 5f)
                    }
            }

            if (!isDestroyed)
                Thread {
                    Thread.sleep(2000)
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }.start()
        }
    }
}