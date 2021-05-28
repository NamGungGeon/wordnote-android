package kr.ac.konkuk.wordnote

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast

class TodayVocaFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_today_voca, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                    }
                    manager.saveWordList()
                    Toast.makeText(context!!, "잘했어요!", Toast.LENGTH_SHORT).show()
                    view.visibility = View.GONE
                }
            } else {
                fragment.view?.visibility = View.GONE
                checkBtn.visibility = View.GONE
            }

        }

    }
}