package kr.ac.konkuk.wordnote.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.ac.konkuk.wordnote.R
import kr.ac.konkuk.wordnote.bean.Voca

class ExamAnswerFragment : Fragment() {

    private var submitted:Boolean= false

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
                submitted= false

                isNestedScrollingEnabled = false
                overScrollMode = ScrollView.OVER_SCROLL_NEVER
                layoutManager = LinearLayoutManager(context)
                adapter = ExamAnswerRecyclerViewAdapter(answers)
                (adapter as ExamAnswerRecyclerViewAdapter).apply {
                    onItemSelected = object : ExamAnswerRecyclerViewAdapter.OnItemSelected {
                        override fun onSelected(item: String) {
                            //right?
                            if(!submitted){
                                onAnswerSelected?.onSelected(item == answer.meaning, item, answer)
                                highlight(answer.meaning)

                                submitted= true
                            }
                        }
                    }
                }
            }
        }
    }
}
