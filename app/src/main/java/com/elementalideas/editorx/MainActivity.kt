package com.elementalideas.editorx

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        animator()

//        Handler().postDelayed({
//            val sharedPrefs = this.getSharedPreferences(Keys.SHARED_PREF, Context.MODE_PRIVATE)
//            val hasLoggedIn = sharedPrefs.getString(Keys.HAS_LOGGED_IN, "NO")
//
//            if (hasLoggedIn == "YES"){
//                intent = Intent(this, Dashboard::class.java)
//                startActivity(intent)
//            }
//            else{
//                intent = Intent(this, Authentication::class.java)
//                startActivity(intent)
//            }
//        },2500)

        Handler().postDelayed({
            intent = Intent(this, Dashboard::class.java)
            startActivity(intent)
        }, 2500)
    }


    // Applying animation
    private fun animator(){
        val animate: Animation = AnimationUtils.loadAnimation(this, R.anim.logo_animation)
        val appLogo = findViewById<ImageView>(R.id.logo)
        appLogo.startAnimation(animate)
    }
}