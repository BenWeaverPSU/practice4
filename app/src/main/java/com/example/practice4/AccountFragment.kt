package com.example.practice4

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth

/**
 * AccountFragment
 * - Displays the currently logged-in user's info.
 */
class AccountFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_account, container, false)

        val textViewEmail = view.findViewById<TextView>(R.id.textViewEmail)
        val textViewUid = view.findViewById<TextView>(R.id.textViewUid)

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            textViewEmail.text = "Email: ${currentUser.email}"
            textViewUid.text = "UID: ${currentUser.uid}"
        } else {
            Toast.makeText(requireContext(), "No user logged in", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}
