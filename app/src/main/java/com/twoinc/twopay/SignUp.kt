package com.twoinc.twopay

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SignUp : AppCompatActivity() {
    private val TAG = "Sign Up"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        setSupportActionBar(findViewById(R.id.appbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        fun print(msg: String){
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
        }

        fun backToPreviousPage() {
            val changePage = Intent(this,MainActivity::class.java)
            startActivity(changePage)
        }



        fun signUp(){
            val db = Firebase.firestore

            val username = findViewById<EditText>(R.id.usernameField).text.toString()
            val password = findViewById<EditText>(R.id.passwordField).text.toString()
            val passcode = findViewById<EditText>(R.id.passcodeField).text.toString()

            val finalData = mutableMapOf("Username" to "null", "Password" to "null", "Passcode" to "null")

            val colRef = db.collection("Users")
            colRef.addSnapshotListener{ value, e ->
                if(e != null) {
                    Log.w(TAG,"Listen Failed." , e)
                    return@addSnapshotListener
                }

                val users = ArrayList<String>()
                for(doc in value!!){
                    doc.getString("Username")?.let{
                        users.add(it)
                    }
                }
                Log.d(TAG,"User list : $users")
                val usernamelist = users

                fun valid():Boolean{
                    when {
                        username.count() < 6 -> {
                            print("Username too short")
                            return false
                        }
                        username in usernamelist.toString() -> {
                            print("Username has been taken")
                            return false
                        }
                        else -> {
                            finalData["Username"] = username
                        }
                    }

                    when {
                        password.count() < 6 -> {
                            print( "Password too short !")
                            return false
                        }

                        else -> {
                            finalData["Password"] = password
                        }
                    }

                    when {
                        passcode.count() != 6 -> {
                            print( "Passcode must be 6 digits")
                            return false
                        }

                        else ->{
                            finalData["Passcode"] = passcode
                        }
                    }
                    return true
                }

                val newUser = hashMapOf(
                        "Username" to username,
                        "Password" to password,
                        "Passcode" to passcode,
                        "Wallet" to 0
                )

                if(valid()){
                    db.collection("Users"
                    )
                            .add(newUser)
                            .addOnSuccessListener { documentReference ->
                                backToPreviousPage()
                                Log.d(TAG,"DocumentSnapshot added with ID: $documentReference")
                                print("Success, please sign in with your new credential")

                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error adding document ", e)
                            }
                }


            }
    }

        val loginBut = findViewById<Button>(R.id.loginButton)
        loginBut.setOnClickListener(){
            backToPreviousPage()
        }

        val signUpBut = findViewById<Button>(R.id.signUpButton)
        signUpBut.setOnClickListener(){
            signUp()
        }



}}