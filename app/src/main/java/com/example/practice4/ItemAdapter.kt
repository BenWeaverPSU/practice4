package com.example.practice4

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView


// ItemAdapter
// RecyclerView adapter to display a list of Item objects.
// Supports item click and long-press delete confirmation.

class ItemAdapter(
    private val items: MutableList<Item>,
    private val onItemClick: (Item) -> Unit,
    private val onDelete: (Item) -> Unit
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    // ViewHolder for an individual item row.
    // Holds references to the views in the item layout.
    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.textViewItemName)
        val descriptionTextView: TextView = itemView.findViewById(R.id.textViewItemDescription)
    }

    //  Called when RecyclerView needs a new ViewHolder to represent an item.
    //  Inflates the item_row layout and wraps it in a ViewHolder.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_row, parent, false)
        return ItemViewHolder(view)
    }


    //  Binds data to the views in the ViewHolder for a given position.
    //  Sets up click and long-click listeners for the item.
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        val context = holder.itemView.context

        // Set item name and description
        holder.nameTextView.text = item.name
        holder.descriptionTextView.text = item.description

        // Handle single click event
        holder.itemView.setOnClickListener {
            onItemClick(item)
        }

        // Handle long press event for delete confirmation
        holder.itemView.setOnLongClickListener {
            // Highlight the selected item
            holder.itemView.setBackgroundColor(
                ContextCompat.getColor(context, R.color.selectedItem)
            )

            // Show an AlertDialog to confirm deletion
            AlertDialog.Builder(context)
                .setTitle(R.string.delete_confirm)  // Dialog title
                .setMessage(item.name)          // Show item name in message
                .setPositiveButton(R.string.delete) { _, _ ->
                    onDelete(item)              // Perform deletion
                }
                .setNegativeButton(R.string.cancel) { _, _ ->
                    holder.itemView.setBackgroundColor(0) // Reset background if cancelled
                }
                .show()

            true
        }
    }

    // Returns the total number of items in the data set.
    override fun getItemCount() = items.size
}
