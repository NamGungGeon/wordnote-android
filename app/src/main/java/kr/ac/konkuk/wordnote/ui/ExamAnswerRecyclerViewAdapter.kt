package kr.ac.konkuk.wordnote.ui

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kr.ac.konkuk.wordnote.R

class ExamAnswerRecyclerViewAdapter(
    private val values: List<String>
) : RecyclerView.Adapter<ExamAnswerRecyclerViewAdapter.ViewHolder>() {

    interface OnItemSelected {
        fun onSelected(item: String)
    }

    var onItemSelected: OnItemSelected? = null
    var selected: String? = null
    var answer: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_answer, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.textView.apply {
            text = item
            if (item == selected || item == answer) {
                setTypeface(null, Typeface.BOLD)
                if (item == selected) {
                    setTextColor(resources.getColor(R.color.wrong, null))
                }
                if (item == answer) {
                    setTextColor(resources.getColor(R.color.right, null))
                }
                holder.itemView.setOnClickListener(null)
            } else {
                holder.itemView.setOnClickListener {
                    onItemSelected?.onSelected(item)
                    selected = item
                }
            }
        }
    }

    fun highlight(answer: String) {
        this.answer = answer
        this.notifyDataSetChanged()
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.text)

        override fun toString(): String {
            return super.toString() + " '" + textView.text + "'"
        }
    }
}