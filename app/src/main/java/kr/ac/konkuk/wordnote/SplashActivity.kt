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

        //init wordList
        VocaManager.getInstance(this, object : VocaManager.Callback {
            override fun onFinishIO(vocaManager: VocaManager) {
                //loaded!
                if (isDestroyed)
                    return

                runOnUiThread {
                    binding.apply {
                        vocaManager.vocaList.subList(0, 20).map {
                            TextTagItem(text = it.word)
                        }.toList().let {
                            splashTagView.addTagList(it)
                        }
                        splashTagView.setTextPaint(
                            TextPaint().apply {
                                isAntiAlias = true
                                textSize = 30f
                                color = Color.DKGRAY
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
        })
    }
}