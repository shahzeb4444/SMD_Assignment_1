package com.teamsx.i230610_i230040

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // KEEP your existing listeners that open other activities
        val messages = view.findViewById<ImageView>(R.id.messangerlogo)
        val cameraicon = view.findViewById<ImageView>(R.id.cameralogo)
        val otheruserprofile = view.findViewById<ImageView>(R.id.mainprofile)
        val p2 = view.findViewById<ImageView>(R.id.p2)
        val p3 = view.findViewById<ImageView>(R.id.p3)
        val p4 = view.findViewById<ImageView>(R.id.p4)
        val p5 = view.findViewById<ImageView>(R.id.p5)
        val p6 = view.findViewById<ImageView>(R.id.p6)
        val p1 = view.findViewById<ImageView>(R.id.p1)

        cameraicon.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).apply { type = "image/*" }
            startActivity(intent)
        }
        messages.setOnClickListener { startActivity(Intent(requireContext(), socialhomescreen4::class.java)) }
        listOf(p2, p3, p4, p5, p6).forEach {
            it.setOnClickListener { startActivity(Intent(requireContext(), socialhomescreen12::class.java)) }
        }
        p1.setOnClickListener { startActivity(Intent(requireContext(), socialhomescreen14::class.java)) }
        otheruserprofile.setOnClickListener { startActivity(Intent(requireContext(), socialhomescreen10::class.java)) }
    }
}
