package kr.ac.konkuk.wordnote

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kr.ac.konkuk.wordnote.databinding.ActivityExamListenRequireMeaningBinding
import java.util.*
import kotlin.collections.ArrayList

class ExamListenRequireMeaningActivity : AppCompatActivity() {
    lateinit var binding: ActivityExamListenRequireMeaningBinding
    var originWordListSize: Int = 0
    private val wordList = Stack<Voca>()
    lateinit var currentVoca: Voca
    private val fragment = ExamAnswerFragment()

    private lateinit var tts: TextToSpeech
    private var ttsReady: Boolean = false

    private var rightCnt = 0
    private var wrongCnt = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityExamListenRequireMeaningBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initTTS()
        supportFragmentManager.beginTransaction().replace(binding.examAnswerFragment.id, fragment)
            .runOnCommit {
                VocaManager.useInstance(this) { manager ->
                    runOnUiThread {
                        originWordListSize = manager.vocaList.size
                        wordList.addAll(manager.vocaList)
                        wordList.sortWith { o1, o2 -> if (o1.getHitRate() * 100 < o2.getHitRate() * 100) 1 else -1 }

                        nextWord(manager)

                        binding.nextBtn.setOnClickListener {
                            nextWord(manager)
                        }
                    }
                }
            }.commit()
    }


    private fun initTTS() {
        binding.ttsStartBtn.visibility = View.GONE
        tts = TextToSpeech(this) {
            ttsReady = true
            tts.language = Locale.US

            binding.ttsStartBtn.visibility = View.VISIBLE
        }
    }

    private fun nextWord(manager: VocaManager) {
        if (wordList.isEmpty()) {

            val msg =
                "${rightCnt + wrongCnt}개 중 ${rightCnt}개를 맞췄습니다\n정답률: ${
                    String.format(
                        ".2f",
                        rightCnt / (wrongCnt + rightCnt) * 100
                    )
                }"

            MyHistoryManager.useInstance(this) { manager ->
                manager.historyList.add(
                    0,
                    MyHistory(
                        MyHistory.NAME_EXAM_LISTENING_REQUIRE_MEANING,
                        msg
                    )
                )
            }
            AlertDialog.Builder(this).setTitle("시험 종료")
                .setMessage(msg)
                .setPositiveButton("닫기") { dialog, i ->
                    dialog.dismiss()
                    finish()
                }.setCancelable(false).create().show()

            return
        }

        currentVoca = wordList.pop()


        binding.ttsStartBtn.setOnClickListener {
            if (ttsReady)
                tts.speak(currentVoca.word, TextToSpeech.QUEUE_FLUSH, null, null)
        }
        binding.nextBtn.visibility = View.GONE
        supportActionBar?.title =
            "단어 듣기평가 (${originWordListSize - wordList.size + 1} / ${originWordListSize})"
        binding.hitrate.text = "내 정답률: ${(currentVoca.getHitRate() * 100).toInt()}%"

        val meaningList = manager.getMeaningWithoutDuplicated(currentVoca.meaning)

        val currentMeaningList = ArrayList<String>()
        currentMeaningList.add(currentVoca.meaning)
        currentMeaningList.addAll(meaningList.subList(0, 3))
        currentMeaningList.shuffle()

        fragment.init(currentMeaningList, currentVoca)
        fragment.onAnswerSelected = object : ExamAnswerFragment.OnAnswerSelected {
            override fun onSelected(result: Boolean, selected: String, answer: Voca) {
                answer.reflectExam(result)
                binding.hitrate.text = "정답률: ${(currentVoca.getHitRate() * 100).toInt()}%"
                if (result) {
                    Toast.makeText(applicationContext, "정답입니다", Toast.LENGTH_SHORT).show()
                    rightCnt++
                } else {
                    Toast.makeText(applicationContext, "오답입니다", Toast.LENGTH_SHORT).show()
                    wrongCnt++
                }
                binding.nextBtn.visibility = View.VISIBLE
            }
        }
    }

}