package com.elementalideas.editorx

import android.app.Dialog
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog

class Settings : AppCompatActivity() {

    private lateinit var dialog: Dialog
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)


        val changeProfilePicture = findViewById<ImageView>(R.id.change_dp)
        val changeName  = findViewById<ImageView>(R.id.change_name)

        changeProfilePicture.setOnClickListener { changeDP() }
        changeName.setOnClickListener { changeName() }

        // Set the data
        val sharedPref = getSharedPreferences(Keys.SHARED_PREF, Context.MODE_PRIVATE)
        val dpName = sharedPref.getString(Keys.PROFILE_PICTURE_NAME, getString(R.string.f1))

        val profilePictureName = findViewById<TextView>(R.id.profile_picture_name)
        profilePictureName.text = dpName

    }

    // change name
    private fun changeName(){
        val dialogView = LayoutInflater.from(this).inflate(R.layout.change_name, null)

        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)

        dialog = builder.create()
        dialog.show()

//        val sharedPref = getSharedPreferences(Keys.SHARED_PREF, Context.MODE_PRIVATE)
//        val oldName = sharedPref.getString(Keys.FULL_NAME, getString(R.string.f1))
//        val newNameTV = dialog.findViewById<TextView>(R.id.change_name)
//        newNameTV.hint = oldName

        val mLayoutParams = WindowManager.LayoutParams()
        mLayoutParams.width =  975   /* (mDisplayWidth * 0.9f).toInt()*/
        mLayoutParams.height = 620 /* (mDisplayHeight * 0.6f).toInt() */
        dialog.window?.attributes = mLayoutParams
    }


    // change dp
    private fun changeDP(){
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dp_selector, null)

        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)

        dialog = builder.create()
        dialog.show()

//            val mDisplayMetrics = windowManager.currentWindowMetrics
//            val mDisplayWidth = mDisplayMetrics.bounds.width()
//            val mDisplayHeight = mDisplayMetrics.bounds.height()

        val mLayoutParams = WindowManager.LayoutParams()
        mLayoutParams.width =  975   /* (mDisplayWidth * 0.9f).toInt()*/
        mLayoutParams.height = 1440 /* (mDisplayHeight * 0.6f).toInt() */
        dialog.window?.attributes = mLayoutParams
    }

    // Select DP
    fun onImageOptionClicked(view: View) {
        val selectedImageTag = view.tag.toString()
        val sharedPref = getSharedPreferences(Keys.SHARED_PREF, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        val profilePictureName = findViewById<TextView>(R.id.profile_picture_name)

        // Handle the selected image option
        when (selectedImageTag) {
            "m1" -> {editor.putInt(Keys.PROFILE_PICTURE, R.drawable.me)}
            "m2" -> {editor.putInt(Keys.PROFILE_PICTURE, R.drawable.me)}
            "m3" -> {editor.putInt(Keys.PROFILE_PICTURE, R.drawable.me)}
            "m4" -> {editor.putInt(Keys.PROFILE_PICTURE, R.drawable.me)}
            "m5" -> {editor.putInt(Keys.PROFILE_PICTURE, R.drawable.me)}
            "m6" -> {editor.putInt(Keys.PROFILE_PICTURE, R.drawable.me)}
            "m7" -> {editor.putInt(Keys.PROFILE_PICTURE, R.drawable.me)}
            "m8" -> {editor.putInt(Keys.PROFILE_PICTURE, R.drawable.me)}
            "f1" -> {
                editor.putInt(Keys.PROFILE_PICTURE, R.drawable.f1)
                editor.putString(Keys.PROFILE_PICTURE_NAME, getString(R.string.f1))
                profilePictureName.text = getString(R.string.f1)
            }
            "f2" -> {
                editor.putInt(Keys.PROFILE_PICTURE, R.drawable.f2)
                editor.putString(Keys.PROFILE_PICTURE_NAME, getString(R.string.f2))
                profilePictureName.text = getString(R.string.f2)
            }
            "f3" -> {
                editor.putInt(Keys.PROFILE_PICTURE, R.drawable.f3)
                editor.putString(Keys.PROFILE_PICTURE_NAME, getString(R.string.f3))
                profilePictureName.text = getString(R.string.f3)
            }
            "f4" -> {
                editor.putInt(Keys.PROFILE_PICTURE, R.drawable.f4)
                editor.putString(Keys.PROFILE_PICTURE_NAME, getString(R.string.f4))
                profilePictureName.text = getString(R.string.f4)
            }
            "f5" -> {
                editor.putInt(Keys.PROFILE_PICTURE, R.drawable.f5)
                editor.putString(Keys.PROFILE_PICTURE_NAME, getString(R.string.f5))
                profilePictureName.text = getString(R.string.f5)
            }
            "f6" -> {
                editor.putInt(Keys.PROFILE_PICTURE, R.drawable.f6)
                editor.putString(Keys.PROFILE_PICTURE_NAME, getString(R.string.f6))
                profilePictureName.text = getString(R.string.f6)
            }
            "f7" -> {
                editor.putInt(Keys.PROFILE_PICTURE, R.drawable.f7)
                editor.putString(Keys.PROFILE_PICTURE_NAME, getString(R.string.f7))
                profilePictureName.text = getString(R.string.f7)
            }
            "f8" -> {
                editor.putInt(Keys.PROFILE_PICTURE, R.drawable.f8)
                editor.putString(Keys.PROFILE_PICTURE_NAME, getString(R.string.f8))
                profilePictureName.text = getString(R.string.f8)
            }
        }

        editor.apply()
        dialog.dismiss()
    }

}