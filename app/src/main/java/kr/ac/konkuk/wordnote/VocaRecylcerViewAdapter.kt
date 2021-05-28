package kr.ac.konkuk.wordnote

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class VocaRecylcerViewAdapter(
    private val values: List<Voca>
) : RecyclerView.Adapter<VocaRecylcerViewAdapter.ViewHolder>() {


    var onItemSelected: ((voca: Voca)-> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_voca, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.itemView.setOnClickListener {
            onItemSelected?.run{
                this(item)
            }
        }
        holder.wordView.text= item.word
        holder.meaingView.text= item.meaning
    }


    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val wordView: TextView = view.findViewById(R.id.word)
        val meaingView: TextView = view.findViewById(R.id.meaning)
    }
}