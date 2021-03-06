package kr.ac.konkuk.wordnote.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kr.ac.konkuk.wordnote.bean.MyHistory
import kr.ac.konkuk.wordnote.manager.MyHistoryManager
import kr.ac.konkuk.wordnote.bean.Voca
import kr.ac.konkuk.wordnote.manager.VocaManager
import kr.ac.konkuk.wordnote.databinding.ActivityExamBinding
import java.util.*
import kotlin.collections.ArrayList

class ExamActivity : AppCompatActivity() {
    companion object {
        val KEY_NAME_BOOK_NAME = "BOOK_NAME"
        val KEY_NAME_VOCA_CNT = "VOCA_CNT"
    }

    lateinit var binding: ActivityExamBinding
    var originWordListSize: Int = 0
    private val vocaList = Stack<Voca>()
    lateinit var currentVoca: Voca
    private val fragment = ExamAnswerFragment()

    private var rightCnt = 0
    private var wrongCnt = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityExamBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val bookName = intent.getStringExtra(FlickerActivity.KEY_NAME_BOOK_NAME)
        val vocaCnt = intent.getIntExtra(FlickerActivity.KEY_NAME_VOCA_CNT, 5)
        if (bookName == null) {
            Toast.makeText(this, "단어장 이름이 없습니다", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        supportFragmentManager.beginTransaction().replace(binding.examAnswerFragment.id, fragment)
            .runOnCommit {
                VocaManager.useInstance(this) { manager ->
                    runOnUiThread {
                        if (manager.getVocaList(bookName).size < vocaCnt) {
                            Toast.makeText(
                                this@ExamActivity,
                                "단어장의 단어 수가 선택한 단어 갯수 ${vocaCnt}개 보다 작습니다",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                            return@runOnUiThread
                        }
                        val vocaList = ArrayList(manager.vocaList)
                        vocaList.shuffle()
                        this.vocaList.addAll(vocaList.subList(0, vocaCnt))
                        this.vocaList.sortWith { o1, o2 -> if (o1.getHitRate() < o2.getHitRate()) 1 else -1 }

                        originWordListSize = this.vocaList.size

                        nextWord(manager)

                        binding.nextBtn.setOnClickListener {
                            nextWord(manager)
                        }
                    }
                }
            }.commit()
    }


    private fun nextWord(manager: VocaManager) {
        if (vocaList.isEmpty()) {
            val msg =
                "${rightCnt + wrongCnt}개 중 ${rightCnt}개를 맞췄습니다\n정답률: ${
                    String.format(
                        "%.2f",
                        (rightCnt.toFloat() / (wrongCnt + rightCnt).toFloat() * 100)
                    )
                }%"

            MyHistoryManager.useInstance(this) { manager ->
                manager.historyList.add(
                    0,
                    MyHistory(
                        MyHistory.NAME_EXAM_VOCA,
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

        currentVoca = vocaList.pop()
        binding.word.text = currentVoca.word
        binding.nextBtn.visibility = View.GONE
        supportActionBar?.title =
            "단어시험 (${originWordListSize - vocaList.size} / ${originWordListSize})"
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
                binding.hitrate.text = "내 정답률: ${(currentVoca.getHitRate() * 100).toInt()}%"
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