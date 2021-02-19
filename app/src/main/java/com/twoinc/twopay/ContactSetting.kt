package com.twoinc.twopay

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ContactSetting : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_setting)

        val intent = intent
        val username = intent.getStringExtra("Username")
        val email  = intent.getStringExtra("Email")
        val hpnum = intent.getStringExtra("HPNum")

        val hpHolder = findViewById<EditText>(R.id.hpSet)
        val mailHolder = findViewById<EditText>(R.id.mailSet)
        val saveBut  =findViewById<Button>(R.id.savePriBut)

        hpHolder.setText(hpnum)
        mailHolder.setText(email)

        fun saveData(){
            val phoneNum = hpHolder.text.toString()
            val mailAddr = mailHolder.text.toString()
            val db = Firebase.firestore
            val docRef = db.collection("Users").whereEqualTo("Username",username)
           docRef.get().addOnSuccessListener { doc ->
               for(d in doc){
                    val id = d.id
                    val docRef = db.collection("Users").document(id)
                    docRef.update(mapOf(
                        "Phone Number" to phoneNum,
                        "Email" to mailAddr
                    ))
               }
           }
        }

        fun backToSetting(){
            val changePage = Intent(this,Setting::class.java)
            changePage.putExtra("Username",username)
            startActivity(changePage)
        }

        saveBut.setOnClickListener{
            saveData()
            backToSetting()
        }


    }
}