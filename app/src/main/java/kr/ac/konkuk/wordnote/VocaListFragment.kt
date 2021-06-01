package kr.ac.konkuk.wordnote

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView

class VocaListFragment : Fragment() {

    private var waitInit: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_voca_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        consumeInitiater()
    }

    fun setVocaList(vocaList: ArrayList<Voca>): VocaListFragment {
        waitInit = {
            val recyclerView = view!!.findViewById<RecyclerView>(R.id.recyclerView)
            val adapter = VocaRecylcerViewAdapter(vocaList)
            adapter.onItemSelected = {
                //update
                val intent =
                    Intent(activity, VocaUpdateActivity::class.java)
                intent.putExtra("voca", it)
                startActivity(intent)
            }

            recyclerView.adapter = adapter
            recyclerView.isNestedScrollingEnabled = false
            recyclerView.overScrollMode = ScrollView.OVER_SCROLL_NEVER

            val vocaCnt= view!!.findViewById<TextView>(R.id.vocaCnt)
            vocaCnt.text= "단어가 ${vocaList.size}개 있습니다"
        }

        consumeInitiater()
        return this
    }

    private fun consumeInitiater() {
        if (view != null && waitInit != null) {
            waitInit!!.run {
                this()
            }
            waitInit = null
        }
    }
}