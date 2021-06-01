package kr.ac.konkuk.wordnote

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kr.ac.konkuk.wordnote.databinding.ActivityFlickerBinding
import java.util.*

class FlickerActivity : AppCompatActivity() {
    lateinit var binding: ActivityFlickerBinding

    private var firstFlicker = true
    private val vocaList = Stack<Voca>()
    lateinit var currentVoca: Voca
    private var lastClicked: Long = 0
    private var originWordListSize: Int = 0
    private lateinit var tts: TextToSpeech
    private var ttsReady: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlickerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title= "단어 외우기"

        initTTS()
        VocaManager.useInstance(this) { manager ->
            vocaList.addAll(manager.vocaList)
            originWordListSize = vocaList.size
            vocaList.shuffle()

            runOnUiThread {
                nextWord()
                binding.root.setOnClickListener {
                    //next
                    if (System.currentTimeMillis() - lastClicked < 1000) {
                        lastClicked = 0
                        nextWord()
                    } else {
                        lastClicked = System.currentTimeMillis()
                    }
                }
            }
        }
    }

    private fun initTTS() {
        binding.ttsStartBtn.visibility = View.GONE
        tts = TextToSpeech(this) {
            ttsReady = true
            tts.language = Locale.US

            binding.ttsStartBtn.visibility = View.VISIBLE
        }
    }

    private fun nextWord() {
        if (vocaList.isEmpty()) {
            //end
            Toast.makeText(this, "${originWordListSize}개의 단어를 학습을 완료했습니다!", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        if (firstFlicker) {
            firstFlicker = false
            Toast.makeText(applicationContext, "두번 터치하거나 다음 버튼을 누르면 다음 단어로 이동합니다", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "다음 단어를 표시합니다", Toast.LENGTH_SHORT).show()
        }

        supportActionBar?.title= "단어 외우기 (${originWordListSize - vocaList.size} / ${originWordListSize})"
        currentVoca = vocaList.pop()
        binding.apply {
            word.text = currentVoca.word
            meaning.text = currentVoca.meaning
            binding.nextVocaBtn.setOnClickListener {
                nextWord()
            }

            ttsStartBtn.setOnClickListener {
                if (ttsReady)
                    tts.speak(currentVoca.word, TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }
    }
}