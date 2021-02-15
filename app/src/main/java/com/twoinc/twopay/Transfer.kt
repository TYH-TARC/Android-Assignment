package com.twoinc.twopay

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText

class Transfer : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transfer)
//        setSupportActionBar(findViewById(R.id.appbar))
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.setDisplayShowTitleEnabled(false)

        val accountNumber  = findViewById<EditText>(R.id.editTextTextPersonName)
        val transferAmount = findViewById<EditText>(R.id.editTextNumber)
        val receipients = findViewById<EditText>(R.id.editTextTextPersonName2)
        val references = findViewById<EditText>(R.id.editTextTextPersonName3)

        val intent = intent
        val username = intent.getStringExtra("Username")
        val password = intent.getStringExtra("Password")
        val balance = intent.getStringExtra("Wallet")

        fun backToHomePage(view: View){
            val changePage = Intent(this,HomePage::class.java)
            changePage.putExtra("Username",username)
            changePage.putExtra("Password",password)
            changePage.putExtra("Wallet",balance)
            startActivity(changePage)
        }

    }
}