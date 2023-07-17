package com.elementalideas.editorx.authentication

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.elementalideas.editorx.Dashboard
import com.elementalideas.editorx.Keys
import com.elementalideas.editorx.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlin.math.log

class Login : Fragment(R.layout.fragment_login) {
    lateinit var database: DatabaseReference
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val welcomeImg = view.findViewById<ImageView>(R.id.welcome)
        val opacity: Float = 0.15f
        welcomeImg.alpha = opacity

        val login = view.findViewById<RelativeLayout>(R.id.loginBtn)
        val inputEmail = view.findViewById<TextInputEditText>(R.id.email)
        val inputPassword = view.findViewById<TextInputEditText>(R.id.password)

        login.setOnClickListener {
            val email = inputEmail.text.toString()
            val password = inputPassword.text.toString()

            val validateEmail = isValidEmail(email)

            if(email.isBlank()) Toast.makeText(requireContext(), "Email cannot be empty!", Toast.LENGTH_LONG).show()
            else if(email.isNotBlank() && !validateEmail) Toast.makeText(requireContext(), "Please enter valid email!", Toast.LENGTH_LONG).show()
            else if(password.isBlank()) Toast.makeText(requireContext(), "Password cannot be empty!", Toast.LENGTH_LONG).show()
            else login(email, password)
        }
    }

    // Login
    private fun login(email: String, password: String){
        val username = getEmailPrefix(email)

        database = FirebaseDatabase.getInstance().getReference("EditorX/Authentication")

        database.child(username)
            .get()
            .addOnSuccessListener {
                if (it.exists()){

                    val sharedPref = requireContext().getSharedPreferences(Keys.SHARED_PREF, Context.MODE_PRIVATE)
                    val editor = sharedPref.edit()

                    val nameFromDB = it.child("name").value.toString()
                    val emailFromDB = it.child("email").value.toString()
                    val dobFromDB = it.child("dob").value.toString()
                    val passwordFromDB = it.child("password").value.toString()
                    val dpFromDB = it.child("dp").value.toString()

                    if (password == passwordFromDB){
                        editor.putString(Keys.NAME, nameFromDB)
                        editor.putString(Keys.EMAIL, emailFromDB)
                        editor.putString(Keys.DATE_OF_BIRTH, dobFromDB)
                        editor.putString(Keys.USER_NAME, username)
                        editor.putString(Keys.PROFILE_PICTURE_NAME, dpFromDB)
                        editor.putString(Keys.HAS_LOGGED_IN, "YES")

                        editor.apply()

                        Toast.makeText(requireContext(), "Welcome $nameFromDB!!", Toast.LENGTH_SHORT).show()

                        val intent = Intent(requireContext(), Dashboard::class.java)
                        startActivity(intent)
                    }
                    else{
                        Toast.makeText(requireContext(), "Wrong password!!", Toast.LENGTH_SHORT).show()
                    }
                }
                else{
                    Toast.makeText(requireContext(), "No such user exists!", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { Toast.makeText(requireContext(), "FAILURE!!!", Toast.LENGTH_SHORT).show() }
    }

    //  get User name
    private fun getEmailPrefix(email: String): String {
        return email.substringBefore("@")
    }

    // Validate email
    private fun isValidEmail(email: String): Boolean {
        val pattern = """^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$""".toRegex()
        return pattern.matches(email)
    }
}
