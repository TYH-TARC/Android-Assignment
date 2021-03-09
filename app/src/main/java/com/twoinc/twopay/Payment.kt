package com.twoinc.twopay

import android.content.Intent
import android.icu.number.Precision.increment
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Text

class Payment : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        val intent = intent
        val gateway = intent.getStringExtra("Gateway")
        val amount = intent.getStringExtra("Amount")
        val username = intent.getStringExtra("Username")

        val gatewayHolder = findViewById<TextView>(R.id.gatewayText)
        val amountHolder = findViewById<TextView>(R.id.amountText)
        val successBut = findViewById<Button>(R.id.successBut)
        val deniedBut = findViewById<Button>(R.id.deniedBut)
        
        gatewayHolder.setText("Reload Gateway : $gateway")
        amountHolder.setText("Reload Amount : RM $amount")
        
        fun backToHome(res: String){
            val changePage = Intent(this,HomePage::class.java)
            changePage.putExtra("Username",username)
            changePage.putExtra("Result",res)
            startActivity(changePage)
        }

        fun updateData(id: String, newBalance: Float) {
            val db = Firebase.firestore
            val docRef = db.collection("Users").document(id)
            val data = hashMapOf(
                    "Wallet" to newBalance
            )
            docRef.update(data as Map<String, Any>).addOnSuccessListener{
                Log.d("UpdateData","Update Success")
            }.addOnFailureListener { e->
                Log.d("Error","$e")
            }

        }

        fun addCash(username: String?, amount: String?) {
            val db = Firebase.firestore
            val docRef = db.collection("Users").whereEqualTo("Username",username)
            docRef.get().addOnSuccessListener { doc -> 
                for(d in doc){
                    val data = d.data
                    val walletBalance = data?.get("Wallet").toString().toFloat()
                    val newBalance = walletBalance + amount.toString().toInt()
                    val id = d.id.toString()
                    updateData(id,newBalance)
                }
            }
        }
        
        successBut.setOnClickListener{
            addCash(username,amount)
            backToHome("Success")
        }
        
        deniedBut.setOnClickListener{
            backToHome("Denied")
        }
        
        
    }

    
}