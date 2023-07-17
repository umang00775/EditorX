package com.elementalideas.editorx.dashboard

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.elementalideas.editorx.Edit
import com.elementalideas.editorx.Keys
import com.elementalideas.editorx.MainActivity
import com.elementalideas.editorx.R
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

class Dashboard: Fragment(R.layout.fragment_dashboard) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.dark_theme)

        // Ads
//        MobileAds.initialize(requireContext()) {}
//        val mAdView = view.findViewById<AdView>(R.id.adView)
//        val adRequest = AdRequest.Builder().build()
//        mAdView.loadAd(adRequest)


        val sharpImg = view.findViewById<CardView>(R.id.sharpImage)
//        val colorizeImg = view.findViewById<CardView>(R.id.colorizeImage)


        // Sharp image
        sharpImg.setOnClickListener {
            openGallery()
        }


    }


    // Open Gallery
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(intent, 2) // Use any unique value for the request code
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            val selectedImageUri = data?.data
            val intent = Intent(requireContext(), Edit::class.java)
            val sharedPrefs = requireContext().getSharedPreferences(Keys.SHARED_PREF, Context.MODE_PRIVATE)
            val editor = sharedPrefs.edit()
            editor.putString(Keys.SELECTED_IMG_URI, selectedImageUri.toString())
            editor.apply()
            startActivity(intent)
        }
    }

}