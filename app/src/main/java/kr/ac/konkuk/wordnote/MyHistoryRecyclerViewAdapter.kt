package kr.ac.konkuk.wordnote

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

import kr.ac.konkuk.wordnote.databinding.ListMyHistoryBinding

class MyHistoryRecyclerViewAdapter(
    private val values: List<MyHistory>
) : RecyclerView.Adapter<MyHistoryRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            ListMyHistoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.nameView.text = item.name
        holder.explainView.text = item.explain
        if (item.getDateString() != null)
            holder.moreInfoView.text = item.getDateString()
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: ListMyHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val nameView = binding.name
        val explainView = binding.explain
        val moreInfoView = binding.moreInfoView
    }

}