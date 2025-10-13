package com.teamsx.i230610_i230040

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment

class ProfileFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val edit = view.findViewById<Button>(R.id.editprofilebtn)
        edit.setOnClickListener { startActivity(Intent(requireContext(), socialhomescreen11::class.java)) }

        listOf(
            view.findViewById<ImageView>(R.id.circle2),
            view.findViewById<ImageView>(R.id.circle3),
            view.findViewById<ImageView>(R.id.circle4),
        ).forEach {
            it.setOnClickListener { _ ->
                startActivity(Intent(requireContext(), socialhomescreen13::class.java))
            }
        }
    }
}
