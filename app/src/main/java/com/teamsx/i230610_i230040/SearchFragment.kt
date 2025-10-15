package com.teamsx.i230610_i230040

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.*

class SearchFragment : Fragment() {

    private lateinit var searchBox: AutoCompleteTextView
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var dbRef: DatabaseReference
    private val usernameList = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        searchBox = view.findViewById(R.id.searchfield)
        searchBox.threshold = 1
        searchBox.showDropDown()

        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, usernameList)
        searchBox.setAdapter(adapter)

        dbRef = FirebaseDatabase.getInstance().getReference("users")

        // Load all usernames from DB once
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                usernameList.clear()
                for (child in snapshot.children) {
                    val username = child.child("username").getValue(String::class.java)
                    if (!username.isNullOrBlank()) usernameList.add(username)
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to load users", Toast.LENGTH_SHORT).show()
            }
        })

        // Show dropdown suggestions while typing
        searchBox.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter.filter(s)
                if (s.isNullOrEmpty() || adapter.count == 0)
                    searchBox.error = "No accounts found"
                else
                    searchBox.error = null
            }
        })

        return view
    }
}
