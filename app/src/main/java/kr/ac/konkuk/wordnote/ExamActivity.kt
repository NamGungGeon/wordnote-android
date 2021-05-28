package kr.ac.konkuk.wordnote

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kr.ac.konkuk.wordnote.databinding.ActivityExamBinding
import java.util.*
import kotlin.collections.ArrayList

class ExamActivity : AppCompatActivity() {
    lateinit var binding: ActivityExamBinding
    var originWordListSize: Int = 0
    private val wordList = Stack<Voca>()
    lateinit var currentVoca: Voca
    private val fragment = ExamAnswerFragment()
    private lateinit var vocaManager: VocaManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityExamBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        supportFragmentManager.beginTransaction().replace(binding.examAnswerFragment.id, fragment)
            .runOnCommit {
                VocaManager.getInstance(this, object : VocaManager.Callback {
                    override fun onFinishIO(vocaManager: VocaManager) {
                        runOnUiThread {
                            this@ExamActivity.vocaManager = vocaManager
                            originWordListSize = vocaManager.vocaList.size
                            wordList.addAll(vocaManager.vocaList)
                            wordList.sortWith { o1, o2 -> if (o1.getHitRate() * 100 < o2.getHitRate() * 100) 1 else -1 }

                            nextWord()

                            binding.nextBtn.setOnClickListener {
                                nextWord()
                            }
                        }
                    }
                })
            }.commit()

    }

    private fun nextWord() {
        if (wordList.isEmpty()) {
            Toast.makeText(this, "더 이상 단어가 없습니다", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        currentVoca = wordList.pop()
        binding.word.text = currentVoca.word
        binding.nextBtn.visibility = View.GONE
        binding.status.text = "${originWordListSize - wordList.size} / ${originWordListSize}"
        binding.hitrate.text = "정답률: ${(currentVoca.getHitRate() * 100).toInt()}%"

        val meaningList = vocaManager.getMeaningWithoutDuplicated(currentVoca.meaning)

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
                } else {
                    Toast.makeText(applicationContext, "오답입니다", Toast.LENGTH_SHORT).show()
                }
                binding.nextBtn.visibility = View.VISIBLE
            }
        }
    }

}