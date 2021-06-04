package kr.ac.konkuk.wordnote

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment

class InstantVocaFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_instant_voca, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {
        if (view == null)
            return
        val view = view!!
        val checkBtn = view.findViewById<ImageButton>(R.id.checkBtn)
        val fragment: VocaFragment =
            childFragmentManager.findFragmentById(R.id.voca_fragment) as VocaFragment
        VocaManager.useInstance(context!!) { manager ->
            val vocaList =
                manager.vocaList.sortedWith(compareBy({ it.checkCnt }, { it.getHitRate() }))

            if (vocaList.isNotEmpty()) {
                val selectedVoca = vocaList.shuffled()[0]
                fragment.setVoca(selectedVoca)
                checkBtn.visibility = View.VISIBLE
                checkBtn.setOnClickListener {
                    val foundVoca = manager.vocaList.find { voca ->
                        voca == selectedVoca
                    }
                    if (foundVoca != null) {
                        foundVoca.checkCnt++

                        MyHistoryManager.useInstance(context!!) { manager ->
                            manager.historyList.add(
                                0,
                                MyHistory(
                                    MyHistory.NAME_INSTANCE_VOCA,
                                    "${foundVoca.word}/${foundVoca.meaning}"
                                )
                            )
                        }
                    }
                    manager.saveWordList()
                    Toast.makeText(context!!, "잘했어요!", Toast.LENGTH_SHORT).show()

                    init()
                }
            } else {
                fragment.view?.visibility = View.GONE
                checkBtn.visibility = View.GONE
            }
        }
    }
}