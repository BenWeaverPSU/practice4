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


// Displays a list of the 10 most recent items for the logged-in user.
// Data is loaded from Firestore and shown in a RecyclerView.
class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView   // RecyclerView to show items
    private lateinit var adapter: ItemAdapter  // Adapter for RecyclerView
    private lateinit var db: FirebaseFirestore  // Firestore instance
    private val recentItems = mutableListOf<Item>()  // List to hold recent items


    // Called to create and return the view hierarchy for this fragment.
    override fun onCreateView(
        inflater: LayoutInflater,  // Inflater to create view from XML
        container: ViewGroup?,  // Parent view that the fragment's UI attaches to
        savedInstanceState: Bundle?  // Saved state if fragment is recreated
    ): View? {
        // Inflate layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize RecyclerView and set its layout manager
        recyclerView = view.findViewById(R.id.recyclerViewRecent)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize Firestore instance
        db = FirebaseFirestore.getInstance()

        // Set up the adapter with empty list and optional click handlers
        adapter = ItemAdapter(
            recentItems,
            onItemClick = { /* optional */ },
            onDelete = { /* no delete on home */ }
        )
        recyclerView.adapter = adapter

        // Set up the adapter with empty list and optional click handlers
        loadRecentItems()

        return view
    }

    // Fetches the 10 most recent items from Firestore for the logged-in user.
    private fun loadRecentItems() {

        // Get the current user's UID
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        // Query Firestore: user
        //items collection
        // order by timestamp descending
        // limit 10
        db.collection("users").document(userId).collection("items")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(10)
            .get()
            .addOnSuccessListener { result ->
                // Clear the list and add fetched items
                recentItems.clear()
                for (doc in result) {
                    val item = doc.toObject(Item::class.java)
                    item.id = doc.id // Set the Firestore document ID
                    recentItems.add(item)
                }
                // Notify adapter that data has changed
                adapter.notifyDataSetChanged()
            }

            // Show an error message if fetching fails
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load recent items", Toast.LENGTH_SHORT).show()
            }
    }
}
