package kr.ac.konkuk.wordnote.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kr.ac.konkuk.wordnote.R
import kr.ac.konkuk.wordnote.bean.Voca

class VocaRecylcerViewAdapter(
    var values: List<Voca>
) : RecyclerView.Adapter<VocaRecylcerViewAdapter.ViewHolder>() {
    companion object {
        val MODE_CHECK = "MODE_CHECK"
    }

    lateinit var context: Context
    var onItemSelected: ((voca: Voca) -> Unit)? = null
    var onItemLongSelected: ((voca: Voca) -> Unit)? = null

    val selectedVocaList = ArrayList<Voca>()
    var onUpdateSelectedVoca: ((ArrayList<Voca>) -> Unit)? = null

    var mode: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_voca, parent, false)
        context = parent.context

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.itemView.setOnClickListener {
            if (mode == MODE_CHECK) {
                holder.checkBox.isChecked= !holder.checkBox.isChecked
            } else {
                onItemSelected?.run {
                    this(item)
                }
            }
        }
        holder.itemView.setOnLongClickListener() {
            onItemLongSelected?.run {
                this(item)
            }
            false
        }
        holder.wordView.text = item.word
        holder.meaingView.text = item.meaning
        if (item.tryCnt > 0) {
            holder.hitRateView.text = "정답률: ${"%.2f".format(item.getHitRate() * 100)}%"
            if (item.getHitRate() >= 0.7) {
                holder.hitRateView.setTextColor(ContextCompat.getColor(context, R.color.right))
            } else {
                holder.hitRateView.setTextColor(ContextCompat.getColor(context, R.color.wrong))
            }
        } else {
            holder.hitRateView.text = "-"
            holder.hitRateView.setTextColor(ContextCompat.getColor(context, R.color.gray))
        }

        if (mode == MODE_CHECK) {
            holder.checkBox.visibility = View.VISIBLE
            holder.checkBox.isChecked = false
            holder.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    selectedVocaList.add(item)
                } else {
                    selectedVocaList.remove(item)
                }
                onUpdateSelectedVoca?.run {
                    this(selectedVocaList)
                }
            }
        } else {
            holder.checkBox.visibility = View.GONE
        }
    }

    fun updateMode(mode: String?) {
        this.mode = mode
        notifyDataSetChanged()
    }

    fun setSelectMode(onUpdate: ((ArrayList<Voca>) -> Unit)) {
        selectedVocaList.clear()
        updateMode(MODE_CHECK)
        this.onUpdateSelectedVoca = onUpdate
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val wordView: TextView = view.findViewById(R.id.word)
        val meaingView: TextView = view.findViewById(R.id.meaning)
        val hitRateView: TextView = view.findViewById(R.id.hitrate)
        val checkBox: CheckBox = view.findViewById(R.id.voca_checkbox)
    }
}