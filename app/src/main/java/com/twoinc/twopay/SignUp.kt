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
import androidx.fragment.app.DialogFragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SignUp : AppCompatActivity() {
    private val TAG = "FragmentActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        setSupportActionBar(findViewById(R.id.appbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val loginBut = findViewById<Button>(R.id.loginButton)
        loginBut.setOnClickListener(){
            backToPreviousPage()
        }

        val signUpBut = findViewById<Button>(R.id.signUpButton)
        signUpBut.setOnClickListener(){
            signUp()
        }
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
                    Log.d(TAG, "Username too short")
                    return false
                }
                username in usernamelist.toString() -> {
                    Log.d(TAG, "Username invalid")
                    return false
                }
                else -> {
                    finalData["Username"] = username
                }
            }

            when {
                password.count() < 6 -> {
                    Log.d(TAG, "Password too short !")
                    return false
                }

                else -> {
                    finalData["Password"] = password
                }
            }

            when {
                passcode.count() != 6 -> {
                    Log.d(TAG, "Passcode incorrect length")
                    return false
                }
            
                else ->{
                    finalData["Passcode"] = passcode
                }
            }
            return true
        }

            class FireMissilesDialogFragment : DialogFragment() {

                override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
                    Log.d(TAG, "dialog works")
                    return activity?.let {
                        Log.d(TAG, "returns work")
                        // Use the Builder class for convenient dialog construction
                        val builder = AlertDialog.Builder(it)
                        builder.setMessage("Title")
                                .setPositiveButton("R.string.fire",
                                        DialogInterface.OnClickListener { dialog, id ->
                                            // FIRE ZE MISSILES!
                                        })
                                .setNegativeButton("R.string.cancel",
                                        DialogInterface.OnClickListener { dialog, id ->
                                            // User cancelled the dialog
                                        })
                        // Create the AlertDialog object and return it
                        builder.create()
                    } ?: throw IllegalStateException("Activity cannot be null")
                }
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

            }
            .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document ", e)
            }
        }
        

    }

}}