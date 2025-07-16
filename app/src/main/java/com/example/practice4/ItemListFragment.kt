package com.example.practice4

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

/**
 * ItemListFragment
 * - Displays a RecyclerView of items from Firebase Realtime Database.
 * - Allows deleting an item by clicking on it.
 */
class ItemListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ItemAdapter
    private val itemList = mutableListOf<Item>()

    private lateinit var databaseRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_item_list, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewItems)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ItemAdapter(itemList) { item ->
            deleteItem(item)
        }
        recyclerView.adapter = adapter

        // Initialize Firebase database reference
        databaseRef = FirebaseDatabase.getInstance().getReference("items")

        loadItems()

        return view
    }

    // Read items from Firebase
    private fun loadItems() {
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                itemList.clear()
                for (data in snapshot.children) {
                    val item = data.getValue(Item::class.java)
                    item?.let { itemList.add(it) }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to load items", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Delete an item
    private fun deleteItem(item: Item) {
        databaseRef.child(item.id).removeValue()
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Item deleted", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to delete", Toast.LENGTH_SHORT).show()
            }
    }
}
