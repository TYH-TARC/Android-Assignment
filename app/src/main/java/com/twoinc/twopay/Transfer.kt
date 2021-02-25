package com.twoinc.twopay

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Transfer : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transfer)

        val TAG = "Fragment Activity"
        val accountNumber  = findViewById<EditText>(R.id.usernameInput)
        val transferAmount = findViewById<EditText>(R.id.amountInput)

        val intent = intent
        val username = intent.getStringExtra("Username").toString()
        val password = intent.getStringExtra("Password").toString()
        val balance = intent.getStringExtra("Wallet").toString()


        fun backToHomePage(){
            val changePage = Intent(this,HomePage::class.java)
            changePage.putExtra("Username",username)
            changePage.putExtra("Password",password)
            changePage.putExtra("Wallet",balance.toString())
            changePage.putExtra("Result","Success")
            startActivity(changePage)
        }

        fun log(msg:String){
            Log.d(TAG,"$msg")
        }
        log("BALANCE $balance")

        fun printMesaage(msg: String){
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
        }

        fun setData(path:DocumentReference, value: Any){
            log("Set data")
            path.set(value)
                    .addOnSuccessListener { printMesaage("Success") }
                    .addOnFailureListener{ e -> Log.d(TAG,"Set data Error $e")}
        }



        fun checkAccountValidation(): String{
            val db = Firebase.firestore
            val accName = findViewById<EditText>(R.id.confirmAccName)
            val targetAccName = accName.text.toString()
            if(accName.text == null ){
                printMesaage("Account Number Error")
                return "Error"
            }
            else{
                val docRef = db.collection("Users")
                var userList = ArrayList<String>()
                docRef.get().addOnSuccessListener { docs ->
                    for(doc in docs){
                        val data = doc.data
                        val username = data?.get("Username").toString()
                        userList.add(username)
                    }
                }

                for(users in userList){
                    if(users == targetAccName){
                        return users
                    }
                }
            }

            return targetAccName
        }

        fun checkAccountBalance(): Boolean {
            val targetAmount = findViewById<EditText>(R.id.confirmAmount).toString().toFloat()
            return balance.toFloat() >= targetAmount
        }

        fun passcodeValidation(): Boolean {
            val db = Firebase.firestore
            val passcode = findViewById<EditText>(R.id.confirmPasscode).toString()
            val docRef = db.collection("Users").whereEqualTo("Username",username)
            var returnFlag : Boolean = false
            docRef.get().addOnSuccessListener { docs ->
                for(doc in docs){
                    val data = doc.data
                    val userPasscode = data?.get("Passcode").toString()
                    if(userPasscode == passcode){
                        returnFlag = true
                    }
                }
            }
            return returnFlag
        }

        fun transaction(targetUsername: String?) {
            val targetAmount = findViewById<EditText>(R.id.confirmAmount).toString().toFloat()
            val db = Firebase.firestore
            val sender = db.collection("Users").whereEqualTo("Users",username)
            sender.get().addOnSuccessListener { docs ->
                for(doc in docs){
                    val data = doc.data
                    val id = data?.get("id").toString()
                    val balance = data?.get("Wallet").toString().toFloat()
                    val newBalance = (balance - targetAmount)
                    val docRef = db.collection("Users").document(id)
                    val collection = hashMapOf(
                            "Wallet" to newBalance
                    )
                    setData(docRef,collection)
                }
            }
            val receiver = db.collection("Users").whereEqualTo("Users",targetUsername)
            receiver.get().addOnSuccessListener { docs ->
                for(doc in docs){
                    val data = doc.data
                    val id = data?.get("id").toString()
                    val balance = data?.get("Wallet").toString().toFloat()
                    val newBalance = (balance + targetAmount)
                    val docRef = db.collection("Users").document(id)
                    val collection = hashMapOf(
                            "Wallet" to newBalance
                    )
                    setData(docRef,collection)
                }
            }
        }

        fun transfer(){
            val confirmButton = findViewById<Button>(R.id.confirmButton)
            log("Con But")
            confirmButton.setOnClickListener{
                transfer()
            }
            if(passcodeValidation()){
                if (checkAccountValidation() != "Error"){
                    val targetUsername = checkAccountValidation()
                    if(checkAccountBalance()){
                        transaction(targetUsername)
                    }
                }
            }





        }
        fun triggerPopout(){
            val dialog = PaymentDialog()
            dialog.show(supportFragmentManager,"PAYMENT")

            transfer()


        }

        val transferButton = findViewById<Button>(R.id.transferToButton)
        transferButton.setOnClickListener(){
            triggerPopout()

        }

    }

}

