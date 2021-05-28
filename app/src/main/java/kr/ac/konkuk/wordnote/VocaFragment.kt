package kr.ac.konkuk.wordnote

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class VocaFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_voca, container, false)
    }

    fun setVoca(voca: Voca) {
        if (view == null)
            return

        val view = view!!
        val wordView = view.findViewById<TextView>(R.id.word)
        val meanView = view.findViewById<TextView>(R.id.meaning)

        wordView.text = voca.word
        meanView.text = voca.meaning
    }
}