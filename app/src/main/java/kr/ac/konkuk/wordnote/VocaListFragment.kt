package kr.ac.konkuk.wordnote

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.collections.ArrayList

class VocaListFragment : Fragment() {

    private var initiaters: Queue<(() -> Unit)> = LinkedList()
    var vocaList: ArrayList<Voca> = ArrayList()

    private lateinit var recyclerView: RecyclerView
    private lateinit var vocaSearchInput: EditText
    private lateinit var vocaCntView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_voca_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        vocaSearchInput = view.findViewById(R.id.voca_search_input)
        vocaCntView = view.findViewById(R.id.vocaCnt)

        vocaSearchInput.addTextChangedListener { text ->
            val keyword = text.toString()
            Log.i("keyword", keyword)
            val adapter = (recyclerView.adapter as VocaRecylcerViewAdapter)

            if (keyword == "") {
                adapter.values = vocaList
                vocaCntView.text = "단어가 ${adapter.values.size}개 있습니다"
            } else {
                adapter.values = vocaList.filter {
                    it.word.contains(keyword) || it.meaning.contains(keyword)
                }
                vocaCntView.text = "검색 결과가 ${adapter.values.size}개 있습니다"
            }
            adapter.notifyDataSetChanged()

        }

        consumeInitiater()
    }

    fun setVocaList(vocaList: ArrayList<Voca>): VocaListFragment {
        this.vocaList = vocaList
        initiaters.offer {
            val adapter = VocaRecylcerViewAdapter(vocaList)
            adapter.onItemSelected = { voca ->
                val intent = Intent(context!!, VocaUpdateActivity::class.java)
                intent.putExtra("voca", voca)
                startActivity(intent)
            }
            adapter.onItemLongSelected = { voca ->
                requestRemove(voca)
            }
            recyclerView.adapter = adapter
            recyclerView.isNestedScrollingEnabled = false
            recyclerView.overScrollMode = ScrollView.OVER_SCROLL_NEVER

            vocaCntView.text = "단어가 ${vocaList.size}개 있습니다"
        }

        consumeInitiater()
        return this
    }

    private fun requestRemove(voca: Voca) {
        AlertDialog.Builder(context!!).setTitle("단어 삭제")
            .setMessage("${voca.word}/${voca.meaning}\n\n단어를 삭제하면 해당 단어가 포함된 단어장에서도 모두 삭제됩니다. 계속하시겠습니까?\n")
            .setPositiveButton("삭제") { dialog, i ->
                VocaManager.useInstance(context!!) { manager ->
                    val iterator = manager.vocaList.iterator()
                    while (iterator.hasNext()) {
                        if (iterator.next() == voca) {
                            iterator.remove()
                            break
                        }
                    }
                    manager.saveWordList()

                    Toast.makeText(
                        context!!,
                        "${voca.word}가 삭제되었습니다",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                dialog.dismiss()

                if (activity != null && activity is VocaBookActivity) {
                    (activity as VocaBookActivity).requireRefresh()
                }
            }
            .setNegativeButton("닫기") { dialog, i ->
                dialog.dismiss()
            }.create().show()
    }

    fun startListeningSelectedVoca(onUpdate: ((ArrayList<Voca>) -> Unit)) {
        initiaters.offer {
            (recyclerView.adapter as VocaRecylcerViewAdapter).apply {
                onItemSelected = null
                setSelectMode(onUpdate)
            }
        }
        consumeInitiater()
    }

    private fun consumeInitiater() {
        while (initiaters.isNotEmpty()) {
            if (view != null) {
                initiaters.poll()?.run {
                    this()
                }
            } else {
                break
            }
        }
    }
}