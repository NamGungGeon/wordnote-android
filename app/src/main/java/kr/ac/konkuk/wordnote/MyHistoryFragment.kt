package kr.ac.konkuk.wordnote

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class MyHistoryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_history, container, false)
    }

    fun setHistory(historyList: ArrayList<MyHistory>) {
        val recyclerView = view!! as RecyclerView
        recyclerView.adapter = MyHistoryRecyclerViewAdapter(historyList)
    }
}