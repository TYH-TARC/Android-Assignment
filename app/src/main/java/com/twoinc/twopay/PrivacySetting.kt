package com.twoinc.twopay

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PrivacySetting : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_setting)

        val intent = intent
        val username = intent.getStringExtra("Username")

        val passHolder = findViewById<EditText>(R.id.passSet)
        val codeHolder = findViewById<EditText>(R.id.codeSet)
        val saveBut = findViewById<Button>(R.id.savePriBut)

        fun log(msg:String){
            val TAG = "Privacy Setting"
            Log.d(TAG,"$msg")
        }

        fun print(msg: String){
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
        }

        val db = Firebase.firestore
        val docRef = db.collection("Users").whereEqualTo("Username",username)
        docRef.get().addOnSuccessListener { doc ->
            for(d in doc) {
                val id = d.id
                val docRef = db.collection("Users").document(id)
                docRef.get().addOnSuccessListener { doc ->
                        val data = doc.data
                        val pass = data?.get("Password").toString()
                        val code = data?.get("Passcode").toString()

                        passHolder.setText(pass)
                        codeHolder.setText(code)

                  }
                }

        fun saveData(){
            val pass = passHolder.text.toString()
            if (pass.length < 6){
                print("Password is too short")
                return
            }
            val code = codeHolder.text.toString()
            if (code.length != 6){
                print("Passcode must be 6 digits")
                return
            }
            val db = Firebase.firestore
            val docRef = db.collection("Users").whereEqualTo("Username",username)
            docRef.get().addOnSuccessListener { doc ->
                for(d in doc){
                    val id = d.id
                    val docRef = db.collection("Users").document(id)
                    docRef.update(mapOf(
                        "Password" to pass,
                        "Passcode" to code
                    ))
                }
            }.addOnFailureListener { e ->
                log("Error Occured : $e")
            }
        }
            fun logout(){
                val changePage = Intent(this,MainActivity::class.java)
                startActivity(changePage)
            }

            saveBut.setOnClickListener{
                saveData()
                 print("Please login again with new credential !")
                logout()

            }



    }
}


}