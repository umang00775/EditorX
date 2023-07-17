package com.elementalideas.editorx.authentication

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.elementalideas.editorx.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Signup: Fragment(R.layout.fragment_signup) {
    private lateinit var database: DatabaseReference
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val helloImg = view.findViewById<ImageView>(R.id.hello)
        val opacity: Float = 0.15f
        helloImg.alpha = opacity

        val signup = view.findViewById<RelativeLayout>(R.id.signupBtn)
        val inputName = view.findViewById<TextInputEditText>(R.id.full_name)
        val inputEmail = view.findViewById<TextInputEditText>(R.id.email)
        val inputPassword = view.findViewById<TextInputEditText>(R.id.password)
        val inputDate = view.findViewById<TextInputEditText>(R.id.date_of_birth)

        signup.setOnClickListener {
            val name = inputName.text.toString()
            val email = inputEmail.text.toString()
            val password = inputPassword.text.toString()
            val date = inputDate.text.toString()

            val validateDate = isNotValidDateFormat(date)
            val validateEmail = isValidEmail(email)

            if(name.isBlank()) Toast.makeText(requireContext(), "Name cannot be empty!", Toast.LENGTH_LONG).show()
            else if(email.isBlank()) Toast.makeText(requireContext(), "Email cannot be empty!", Toast.LENGTH_LONG).show()
            else if(email.isNotBlank() && !validateEmail) Toast.makeText(requireContext(), "Please enter valid email!", Toast.LENGTH_LONG).show()
            else if(password.isBlank()) Toast.makeText(requireContext(), "Password cannot be empty!", Toast.LENGTH_LONG).show()
            else if(date.isBlank()) Toast.makeText(requireContext(), "Date cannot be empty!", Toast.LENGTH_LONG).show()
            else if(date.isNotBlank() && validateDate) Toast.makeText(requireContext(), "Please enter valid date!", Toast.LENGTH_LONG).show()
            else storeData(name, email, password, date)
        }
    }

    // Store the data into database
    private fun storeData(name: String, email: String, password: String, date: String){
        database = FirebaseDatabase.getInstance().getReference("EditorX/Authentication")

        val userData = com.elementalideas.editorx.modal.User(name, date, email, password, "DEFAULT")

        val username = getEmailPrefix(email)

        database.child(username).setValue(userData)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "DONE!!!", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Uhhh!!!", Toast.LENGTH_LONG).show()
            }
    }

    //  get User name
    fun getEmailPrefix(email: String): String {
        return email.substringBefore("@")
    }

    // Validate birthday
    fun isNotValidDateFormat(dateString: String): Boolean {
        val pattern = """^\d{2}/\d{2}/\d{4}$""".toRegex()
        return !pattern.matches(dateString)
    }

    // Validate email
    fun isValidEmail(email: String): Boolean {
        val pattern = """^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$""".toRegex()
        return pattern.matches(email)
    }



}