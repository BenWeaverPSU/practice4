package com.example.practice4

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class ItemAdapter(
    private val items: MutableList<Item>,
    private val onItemClick: (Item) -> Unit,
    private val onDelete: (Item) -> Unit
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.textViewItemName)
        val descriptionTextView: TextView = itemView.findViewById(R.id.textViewItemDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_row, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        val context = holder.itemView.context

        holder.nameTextView.text = item.name
        holder.descriptionTextView.text = item.description

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }

        holder.itemView.setOnLongClickListener {
            holder.itemView.setBackgroundColor(
                ContextCompat.getColor(context, R.color.selectedItem)
            )

            AlertDialog.Builder(context)
                .setTitle(R.string.delete_confirm)
                .setMessage(item.name)
                .setPositiveButton(R.string.delete) { _, _ ->
                    onDelete(item)
                }
                .setNegativeButton(R.string.cancel) { _, _ ->
                    holder.itemView.setBackgroundColor(0) // reset
                }
                .show()

            true
        }
    }

    override fun getItemCount() = items.size
}
