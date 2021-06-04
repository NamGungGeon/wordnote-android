package kr.ac.konkuk.wordnote.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kr.ac.konkuk.wordnote.R

class MaterialMenuItemRecyclerView(
    private val values: List<MaterialMenuItem>
) : RecyclerView.Adapter<MaterialMenuItemRecyclerView.ViewHolder>() {
    class MaterialMenuItem(
        @DrawableRes val iconDrawable: Int,
        val title: String,
        val explain: String?,
        val onClick: (() -> Unit)?
    )

    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.material_menu, parent, false)
        context = parent.context

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]

        if (item.iconDrawable == 0) {
            holder.iconView.visibility = View.GONE
        } else {
            holder.iconView.visibility = View.VISIBLE
            holder.iconView.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    item.iconDrawable
                )
            )
        }
        holder.titleView.text = item.title
        if (item.explain == null) {
            holder.explainView.visibility = View.GONE
        } else {
            holder.explainView.text = item.explain
            holder.explainView.visibility = View.VISIBLE
        }

        holder.itemView.setOnClickListener {
            item.onClick?.run {
                this()
            }
        }
    }


    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val iconView: ImageView = view.findViewById(R.id.menuIcon)
        val titleView: TextView = view.findViewById(R.id.menuTitle)
        val explainView: TextView = view.findViewById(R.id.menuExplain)
    }
}