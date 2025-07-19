package com.example.practice4

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth

// Displays the currently logged-in user's info.
class AccountFragment : Fragment() {

    // Firebase authentication instance
    private lateinit var auth: FirebaseAuth

    // Called to create and return the view hierarchy associated with this fragment.
    override fun onCreateView(

        inflater: LayoutInflater,  // Used to inflate the layout XML
        container: ViewGroup?, // Parent view that the fragment's UI should attach to
        savedInstanceState: Bundle? // If non-null, this fragment is being re-constructed

    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_account, container, false)

        // Get references to the TextViews for email and UID
        val textViewEmail = view.findViewById<TextView>(R.id.textViewEmail)
        val textViewUid = view.findViewById<TextView>(R.id.textViewUid)

        // Initialize FirebaseAuth instance
        auth = FirebaseAuth.getInstance()

        // Get the currently logged-in user
        val currentUser = auth.currentUser

        if (currentUser != null) {
            // If user is logged in, display their email and UID
            textViewEmail.text = "Email: ${currentUser.email}"
            textViewUid.text = "UID: ${currentUser.uid}"
        } else {
            // If no user is logged in, show a toast message
            Toast.makeText(requireContext(), "No user logged in", Toast.LENGTH_SHORT).show()
        }

        // Return the fully-inflated view
        return view
    }
}
