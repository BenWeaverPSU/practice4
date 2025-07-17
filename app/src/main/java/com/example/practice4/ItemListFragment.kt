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

class ItemListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ItemAdapter
    private lateinit var db: FirebaseFirestore

    private val itemList = mutableListOf<Item>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_item_list, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        db = FirebaseFirestore.getInstance()

        adapter = ItemAdapter(
            itemList,
            onItemClick = { item ->
                // Optional: handle regular click
                Toast.makeText(requireContext(), "Clicked: ${item.name}", Toast.LENGTH_SHORT).show()
            },
            onDelete = { item ->
                deleteItem(item)
            }
        )

        recyclerView.adapter = adapter

        view.findViewById<FloatingActionButton>(R.id.fab_add_item).setOnClickListener {
            showAddItemDialog()
        }

        loadItems()

        return view
    }

    private fun loadItems() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        db.collection("users").document(userId).collection("items")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Toast.makeText(requireContext(), "Listen failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                itemList.clear()
                for (doc in snapshots!!) {
                    val item = doc.toObject(Item::class.java)
                    item.id = doc.id
                    itemList.add(item)
                }
                adapter.notifyDataSetChanged()
            }
    }

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
