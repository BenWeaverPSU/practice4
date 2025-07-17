package com.example.practice4

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ItemAdapter
    private lateinit var db: FirebaseFirestore
    private val recentItems = mutableListOf<Item>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewRecent)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        db = FirebaseFirestore.getInstance()

        adapter = ItemAdapter(
            recentItems,
            onItemClick = { /* optional */ },
            onDelete = { /* no delete on home */ }
        )
        recyclerView.adapter = adapter

        loadRecentItems()

        return view
    }

    private fun loadRecentItems() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        db.collection("users").document(userId).collection("items")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(10)
            .get()
            .addOnSuccessListener { result ->
                recentItems.clear()
                for (doc in result) {
                    val item = doc.toObject(Item::class.java)
                    item.id = doc.id
                    recentItems.add(item)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load recent items", Toast.LENGTH_SHORT).show()
            }
    }
}
