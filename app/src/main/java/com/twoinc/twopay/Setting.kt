package com.twoinc.twopay

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Setting : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        val TAG = "Fragment Activity"
        val db = Firebase.firestore
        val intents = intent
        val username = intents.getStringExtra("Username")

        var hpNum = "Not Available"
        var email = "Not Available"

        fun log(msg:String){
            Log.d(TAG,msg)
        }

        fun print(msg: String){
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
        }

        val docRef = db.collection("Users").whereEqualTo("Username", username)
        docRef.get().addOnSuccessListener { doc ->
            for(d in doc){
                val data = d.data
                var hpNum = data?.get("Phone Number").toString()
                var email = data?.get("Email").toString()

                val contactHolder  = findViewById<TextView>(R.id.contactNumSet)
                val emailHolder = findViewById<TextView>(R.id.emailSet)
                val usernameHolder = findViewById<TextView>(R.id.usernameSet)
                usernameHolder.setText(username)
                contactHolder.setText(hpNum)
                emailHolder.setText(email)

                val editSetBut = findViewById<Button>(R.id.edit)
                val privacySetBut = findViewById<Button>(R.id.privacySet)

                fun contectPage(){
                    val changePage = Intent(this, ContactSetting::class.java)
                    changePage.putExtra("Username",username)
                    changePage.putExtra("Email",email)
                    changePage.putExtra("HPNum",hpNum)
                    startActivity(changePage)
                }

                fun privacyPage(){
                    val changePage = Intent(this, PrivacySetting::class.java)
                    changePage.putExtra("Username",username)
                    startActivity(changePage)
                }

                privacySetBut.setOnClickListener{
                    privacyPage()
                }

                editSetBut.setOnClickListener{
                    contectPage()
                }
            }
        }


        val logoutBut = findViewById<Button>(R.id.logout)


        fun logout(){
            val changePage = Intent(this,MainActivity::class.java)
            startActivity(changePage)
        }
        val homeBut = findViewById<Button>(R.id.backToHome)

        fun backToHome(){
            val changPage = Intent(this,HomePage::class.java)
            changPage.putExtra("Username",username)
            startActivity(changPage)
        }

        homeBut.setOnClickListener{
            backToHome()
        }

        logoutBut.setOnClickListener{
            logout()
        }





    }








}

