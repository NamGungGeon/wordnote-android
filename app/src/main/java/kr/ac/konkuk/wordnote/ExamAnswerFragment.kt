package kr.ac.konkuk.wordnote

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ExamAnswerFragment : Fragment() {

    interface OnAnswerSelected {
        fun onSelected(result: Boolean, selected: String, answer: Voca)
    }

    var onAnswerSelected: OnAnswerSelected? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_answer_list, container, false)
    }

    fun init(answers: List<String>, answer: Voca) {
        if (view is RecyclerView) {
            (view as RecyclerView).apply {
                view
                isNestedScrollingEnabled = false
                overScrollMode = ScrollView.OVER_SCROLL_NEVER
                layoutManager = LinearLayoutManager(context)
                adapter = MyExamAnswerRecyclerViewAdapter(answers)
                (adapter as MyExamAnswerRecyclerViewAdapter).apply {
                    onItemSelected = object : MyExamAnswerRecyclerViewAdapter.OnItemSelected {
                        override fun onSelected(item: String) {
                            //right?
                            onAnswerSelected?.onSelected(item == answer.meaning, item, answer)
                            highlight(answer.meaning)
                        }
                    }
                }
            }
        }
    }
}
