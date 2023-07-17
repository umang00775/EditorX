package com.elementalideas.editorx

import android.Manifest
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import me.ibrahimsn.lib.SmoothBottomBar

class Dashboard : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        replaceFragment(com.elementalideas.editorx.dashboard.Dashboard())


//
//        val bottomNav = this.findViewById<SmoothBottomBar>(R.id.bottomNavigation)
//        bottomNav.visibility = View.INVISIBLE

//        bottomNav.onItemSelected = {
//            if (it == 0) replaceFragment(com.elementalideas.editorx.dashboard.Dashboard())
//            else if (it == 1) replaceFragment(com.elementalideas.editorx.dashboard.Profile())
//        }

    }

    private fun replaceFragment(frag: Fragment){
        val fragManager = supportFragmentManager
        val fragTransaction = fragManager.beginTransaction()
        fragTransaction.replace(R.id.frame, frag)
        fragTransaction.commit()
    }

    // Manage back press
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        replaceFragment(com.elementalideas.editorx.dashboard.Dashboard())
    }




}