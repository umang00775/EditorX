package com.elementalideas.editorx.dashboard

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ScrollView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.elementalideas.editorx.Keys
import com.elementalideas.editorx.R
import com.elementalideas.editorx.Settings

class Profile: Fragment(R.layout.fragment_profile) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.main_color)

        // Show DP
        val sharedPref = requireActivity().getSharedPreferences(Keys.SHARED_PREF, Context.MODE_PRIVATE)
        val retrievedImageId = sharedPref.getInt(Keys.PROFILE_PICTURE, R.drawable.me)
        val dp = ContextCompat.getDrawable(requireContext(), retrievedImageId)
        val profilePicture = view.findViewById<ImageView>(R.id.profile_picture)
        profilePicture.setImageDrawable(dp)

        // Scroll behaviour
        val scrollView = view.findViewById<ScrollView>(R.id.wholeScreen)
        var isScrolled = false
        scrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            if (scrollY >= dpToPx(150) && !isScrolled) {
                activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.dark_theme)
                isScrolled = true
            } else if (scrollY < dpToPx(150) && isScrolled) {
                activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.main_color)
                isScrolled = false
            }
        }

        // Background image opacity
        val dots = view.findViewById<ImageView>(R.id.dotsImg)
        val opacity: Float = 0.10f
        dots.alpha = opacity


        // Open settings
        val settingBtn = view.findViewById<ImageView>(R.id.setting)
        settingBtn.setOnClickListener {
            startActivity(Intent(requireContext(), Settings::class.java))
        }

    }


    // dp to px
    private fun dpToPx(dp: Int): Int {
        val density = Resources.getSystem().displayMetrics.density
        return (dp * density + 0.5f).toInt()
    }

}