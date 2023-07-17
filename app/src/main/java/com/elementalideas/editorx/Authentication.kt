package com.elementalideas.editorx

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.elementalideas.editorx.authentication.Login
import com.elementalideas.editorx.authentication.Signup

class Authentication : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)

        /*
        msg1 --> Already have an account? Login
        msg2 --> First time here? Sign up
        */

        val message1 = findViewById<LinearLayout>(R.id.msg1)
        val message2 = findViewById<LinearLayout>(R.id.msg2)
        val login = findViewById<TextView>(R.id.loginBtn)
        val signup = findViewById<TextView>(R.id.signupBtn)

        /* Default */
        replaceFragment(Login())
        message1.visibility = View.INVISIBLE

        login.setOnClickListener {
            replaceFragment(Login())
            message1.visibility = View.INVISIBLE
            message2.visibility = View.VISIBLE
        }

        signup.setOnClickListener {
            replaceFragment(Signup())
            message1.visibility = View.VISIBLE
            message2.visibility = View.INVISIBLE
        }

    }

    private fun replaceFragment(frag: Fragment){
        val fragManager = supportFragmentManager
        val fragTransaction = fragManager.beginTransaction()
        fragTransaction.replace(R.id.frame, frag)
        fragTransaction.commit()
    }

    // Handle back press
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val message1 = findViewById<LinearLayout>(R.id.msg1)
        val message2 = findViewById<LinearLayout>(R.id.msg2)
        replaceFragment(Login())
        message1.visibility = View.INVISIBLE
        message2.visibility = View.VISIBLE
    }
}