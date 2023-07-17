package com.elementalideas.editorx


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment

class Edit : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        // Initial config
        replaceFrag(com.elementalideas.editorx.edit.Edit())
    }

    // Replace fragment
    private fun replaceFrag(frag: Fragment){
        val fragManager = supportFragmentManager
        val fragTransaction = fragManager.beginTransaction()
        fragTransaction.replace(R.id.frame, frag)
        fragTransaction.commit()
    }




}
