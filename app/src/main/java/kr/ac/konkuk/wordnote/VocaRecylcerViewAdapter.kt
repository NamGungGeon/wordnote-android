package kr.ac.konkuk.wordnote

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class VocaRecylcerViewAdapter(
    private val values: List<Voca>
) : RecyclerView.Adapter<VocaRecylcerViewAdapter.ViewHolder>() {


    lateinit var context: Context
    var onItemSelected: ((voca: Voca) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_voca, parent, false)
        context= parent.context

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.itemView.setOnClickListener {
            onItemSelected?.run {
                this(item)
            }
        }
        holder.wordView.text = item.word
        holder.meaingView.text = item.meaning
        if(item.tryCnt> 0){
            holder.hitRateView.text = "정답률: ${"%.2f".format(item.getHitRate()*100)}%"
            if(item.getHitRate()>= 0.7){
                holder.hitRateView.setTextColor(ContextCompat.getColor(context, R.color.right))
            }else{
                holder.hitRateView.setTextColor(ContextCompat.getColor(context, R.color.wrong))
            }
        }else{
            holder.hitRateView.text = "-"
            holder.hitRateView.setTextColor(ContextCompat.getColor(context, R.color.gray))
        }
    }


    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val wordView: TextView = view.findViewById(R.id.word)
        val meaingView: TextView = view.findViewById(R.id.meaning)
        val hitRateView: TextView = view.findViewById(R.id.hitrate)
    }
}