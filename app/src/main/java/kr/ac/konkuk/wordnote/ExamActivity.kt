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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityExamBinding.inflate(layoutInflater)
        setContentView(binding.root)

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


    private fun nextWord(manager: VocaManager) {
        if (wordList.isEmpty()) {
            Toast.makeText(this, "더 이상 단어가 없습니다", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        currentVoca = wordList.pop()
        binding.word.text = currentVoca.word
        binding.nextBtn.visibility = View.GONE
        supportActionBar?.title= "단어시험 (${originWordListSize - wordList.size} / ${originWordListSize})"
        binding.hitrate.text = "정답률: ${(currentVoca.getHitRate() * 100).toInt()}%"

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
                } else {
                    Toast.makeText(applicationContext, "오답입니다", Toast.LENGTH_SHORT).show()
                }
                binding.nextBtn.visibility = View.VISIBLE
            }
        }
    }

}