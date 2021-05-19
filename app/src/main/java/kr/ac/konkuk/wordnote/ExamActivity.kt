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
    private val wordList = Stack<Word>()
    lateinit var currentWord: Word
    private val fragment = ExamAnswerFragment()
    private lateinit var wordManager: WordManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityExamBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        supportFragmentManager.beginTransaction().replace(binding.examAnswerFragment.id, fragment)
            .runOnCommit {
                WordManager.getInstance(this, object : WordManager.Callback {
                    override fun onFinishIO(wordManager: WordManager) {
                        runOnUiThread {
                            this@ExamActivity.wordManager = wordManager
                            originWordListSize = wordManager.wordList.size
                            wordList.addAll(wordManager.wordList)
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

        currentWord = wordList.pop()
        binding.word.text = currentWord.word
        binding.nextBtn.visibility = View.GONE
        binding.status.text = "${originWordListSize - wordList.size} / ${originWordListSize}"
        binding.hitrate.text = "정답률: ${(currentWord.getHitRate() * 100).toInt()}%"

        val meaningList = wordManager.getMeaningWithoutDuplicated(currentWord.meaning)

        val currentMeaningList = ArrayList<String>()
        currentMeaningList.add(currentWord.meaning)
        currentMeaningList.addAll(meaningList.subList(0, 3))
        currentMeaningList.shuffle()

        fragment.init(currentMeaningList, currentWord)
        fragment.onAnswerSelected = object : ExamAnswerFragment.OnAnswerSelected {
            override fun onSelected(result: Boolean, selected: String, answer: Word) {
                answer.reflectExam(result)
                binding.hitrate.text = "정답률: ${(currentWord.getHitRate() * 100).toInt()}%"
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