package kr.ac.konkuk.wordnote

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kr.ac.konkuk.wordnote.databinding.ActivityFlickerBinding
import java.util.*
import kotlin.collections.ArrayList

class FlickerActivity : AppCompatActivity() {
    companion object {
        val KEY_NAME_BOOK_NAME = "BOOK_NAME"
        val KEY_NAME_VOCA_CNT = "VOCA_CNT"
    }

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

        supportActionBar?.title = "단어 외우기"

        val bookName = intent.getStringExtra(KEY_NAME_BOOK_NAME)
        val vocaCnt = intent.getIntExtra(KEY_NAME_VOCA_CNT, 5)

        if (bookName == null) {
            Toast.makeText(this, "단어장 이름이 없습니다", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        initTTS()
        VocaManager.useInstance(this) { manager ->
            runOnUiThread {
                if (manager.getVocaList(bookName).size < vocaCnt) {
                    Toast.makeText(
                        this@FlickerActivity,
                        "단어장의 단어 수가 선택한 단어 갯수 ${vocaCnt}개 보다 작습니다",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                    return@runOnUiThread
                }
                val vocaList = ArrayList(manager.vocaList)
                vocaList.shuffle()
                this.vocaList.addAll(vocaList.subList(0, vocaCnt))

                originWordListSize = this.vocaList.size

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
            val msg =
                "${originWordListSize}개에 대한 학습을 완료했습니다"

            MyHistoryManager.useInstance(this) { manager ->
                manager.historyList.add(
                    0,
                    MyHistory(
                        MyHistory.NAME_FLIKER_VOCA,
                        msg
                    )
                )
            }
            AlertDialog.Builder(this).setTitle("끝났습니다!")
                .setMessage(msg)
                .setPositiveButton("닫기") { dialog, i ->
                    dialog.dismiss()
                    finish()
                }.setCancelable(false).create().show()
            return
        }

        if (firstFlicker) {
            firstFlicker = false
            Toast.makeText(
                applicationContext,
                "두번 터치하거나 다음 버튼을 누르면 다음 단어로 이동합니다",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(this, "다음 단어를 표시합니다", Toast.LENGTH_SHORT).show()
        }

        supportActionBar?.title =
            "단어 외우기 (${originWordListSize - vocaList.size} / ${originWordListSize})"
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