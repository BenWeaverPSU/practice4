package com.example.practice4

import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


// ItemListFragment
// Displays a real-time list of items from Firestore.
// Allows adding new items and deleting existing ones.

class ItemListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView   // RecyclerView to show items
    private lateinit var adapter: ItemAdapter         // Adapter for RecyclerView
    private lateinit var db: FirebaseFirestore        // Firestore instance

    private val itemList = mutableListOf<Item>()      // List of items to display

    // Creates and returns the view hierarchy for the fragment.
    override fun onCreateView(
        inflater: LayoutInflater, // Inflater to inflate XML
        container: ViewGroup?,    // Parent view
        savedInstanceState: Bundle? // Saved state
    ): View? {
        val view = inflater.inflate(R.layout.fragment_item_list, container, false)

        // Initialize RecyclerView with LinearLayoutManager
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize Firestore
        db = FirebaseFirestore.getInstance()

        // Set up adapter with click and delete handlers
        adapter = ItemAdapter(
            itemList,
            onItemClick = { item ->
                // Handle regular click
                Toast.makeText(requireContext(), "Clicked: ${item.name}", Toast.LENGTH_SHORT).show()
            },
            onDelete = { item ->
                // Delete item from database
                deleteItem(item)
            }
        )

        recyclerView.adapter = adapter

        // Set up FloatingActionButton to add new items
        view.findViewById<FloatingActionButton>(R.id.fab_add_item).setOnClickListener {
            showAddItemDialog()
        }

        // Load and listen to real-time updates from Firestore
        loadItems()

        return view
    }

    // Loads items from Firestore and listens for real-time updates.
    private fun loadItems() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        db.collection("users").document(userId).collection("items")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Toast.makeText(requireContext(), "Listen failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                // Clear the list and repopulate with current data
                itemList.clear()
                for (doc in snapshots!!) {
                    val item = doc.toObject(Item::class.java)
                    item.id = doc.id
                    itemList.add(item)
                }
                adapter.notifyDataSetChanged()
            }
    }

    // Displays a dialog to enter the name of a new item.
    private fun showAddItemDialog() {
        val editText = EditText(requireContext())
        AlertDialog.Builder(requireContext())
            .setTitle("Add Item")
            .setView(editText)
            .setPositiveButton("Add") { _, _ ->
                val name = editText.text.toString()
                if (name.isNotEmpty()) {
                    addItem(name)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // Adds a new item with the given name to Firestore.
    private fun addItem(name: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val newItem = hashMapOf(
            "name" to name,
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("users").document(userId).collection("items")
            .add(newItem)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Item added", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to add item", Toast.LENGTH_SHORT).show()
            }
    }


    // Deletes the given item from Firestore.
    private fun deleteItem(item: Item) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        db.collection("users").document(userId).collection("items")
            .document(item.id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Item deleted", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to delete item", Toast.LENGTH_SHORT).show()
            }
    }

}
