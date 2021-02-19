package com.twoinc.twopay

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.CollectionReference
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
        val references = findViewById<EditText>(R.id.referenceInput)

        val intent = intent
        val username = intent.getStringExtra("Username").toString()
        val password = intent.getStringExtra("Password").toString()
        val balance = intent.getStringExtra("Wallet").toString()


        fun backToHomePage(view: View){
            val changePage = Intent(this,HomePage::class.java)
            changePage.putExtra("Username",username)
            changePage.putExtra("Password",password)
            changePage.putExtra("Wallet",balance.toString())
            startActivity(changePage)
        }

        fun log(msg:String){
            Log.d(TAG,"$msg")
        }
        log(balance)

        fun printMesaage(msg: String){
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
        }

        fun setData(path:DocumentReference, value: Any){
            log("Set data")
            path.set(value)
                    .addOnSuccessListener { printMesaage("Success") }
                    .addOnFailureListener{ e -> Log.d(TAG,"Error $e")}

        }

        fun addData(path:CollectionReference,value: Any){
            log("Add data")
            path.add(value)
                    .addOnSuccessListener { printMesaage("Success") }
                    .addOnFailureListener{ e -> Log.d(TAG,"Error $e")}
        }

        fun accountBalance(username: String): Float {
            log("Check Account Balance")
            val db = Firebase.firestore
            val path = db.collection("Users").whereEqualTo("Username",username)
            var walletBalance: Float = 0F;
            path.get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            val data = document.data
                            walletBalance = data["Wallet"].toString().toFloat()
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.w(TAG, "Error getting documents: ", exception)
                    }
            return walletBalance
        }

        fun changeAccountBalance(username1: String, username2: String, payment: Float) {
            log("Change Account Balance")
            val senderBalance = accountBalance(username1)
            val receiverBalance = accountBalance(username2)
            val newSenderBalance = senderBalance - payment
            val newReceiverBalance = receiverBalance + payment
            val db = Firebase.firestore
            val path1 = db.collection("Users").whereEqualTo("Username",username1)


            path1.get()
                    .addOnSuccessListener { documents ->
                        for (x in documents) {
                            log(x.toString())
                            log(documents.toString())
                            var id:String = x["id"].toString()
                            val newSender = hashMapOf(
                                    "Wallet" to newSenderBalance
                            )
                            val pathSend = db.collection("Users").document(id)
                            setData(pathSend,newSender)
                        }
                    }
                    .addOnFailureListener { e -> Log.w(TAG, "Error getting documents: ", e)}

            val path2 = db.collection("Users").whereEqualTo("Username",username2)
            path2.get()
                    .addOnSuccessListener { documents ->
                        for (x in documents) {
                            var id:String = x["id"].toString()
                            val newReceive = hashMapOf(
                                    "Wallet" to newReceiverBalance
                            )
                            val pathSend = db.collection("Users").document(id)
                            setData(pathSend,newReceive)
                        }
                    }
                    .addOnFailureListener { e -> Log.w(TAG, "Error getting documents: ", e)}



        }

        fun addTransactions(username1: String,username2: String, amount: Float) {
            log("Add transactions")
            val db = Firebase.firestore
            val ref = references.text.toString()
            val data1 = hashMapOf(
                    "Sender" to username1,
                    "Receiver" to username2,
                    "Amount" to amount,
                    "Reference" to ref,
                    "Type" to "Send"
            )
            val data2 = hashMapOf(
                    "Sender" to username1,
                    "Receiver" to username2,
                    "Amount" to amount,
                    "Reference" to ref,
                    "Type" to "Rececive"
            )
            val path1 = db.collection("Users").document(username1).collection("Transactions")
            val path2 = db.collection("Users/$username2/Transactions")

            addData(path1,data1)
            addData(path2,data2)

        }

        fun checkAccValid(targetAccountName: String,payment: Float){
            log("Check Account Valid")
            val db = Firebase.firestore
            val colRef = db.collection("Users")
            var returnValue: Boolean = false

            colRef.get().addOnSuccessListener { docs ->
                log("Fetching Data")
                val users = ArrayList<String>()
                for (doc in docs) {
                    val data = doc.data
                    users.add(data?.get("Username").toString())
                }

                for (username in users) {
                    log("tan : user ; $targetAccountName : $username ; valid : ${targetAccountName == username}")
                    if (targetAccountName == username) {
                        val username = targetAccountName
                        Log.d(TAG, "User available")
                        changeAccountBalance(username.toString() , targetAccountName, payment)
                        addTransactions(username.toString(),targetAccountName,payment)
                    }else{
                        printMesaage("User not available")
                    }
                }
            }.addOnFailureListener { e->
                log("ERROR : $e")
            }


        }








        fun paymentConfirm(payment: Float) {
            log("Payment Confirm")
            val db = Firebase.firestore
            val targetAccountName = findViewById<EditText>(R.id.usernameInput).text.toString()
            log("tan $targetAccountName")
            checkAccValid(targetAccountName,payment)
        }

        fun checkBalance(payment: Float){
            log("Check Balance + $balance")
            if(payment < balance.toString().toFloat()){
                log("pass if")
                paymentConfirm(payment)
            }else{
                log("pass else")
                printMesaage("Insufficient Balance")

            }
        }

        val transferButton = findViewById<Button>(R.id.transferToButton)
        transferButton.setOnClickListener(){
            log("Button Clicked")
            val paymentAmount = findViewById<EditText>(R.id.amountInput).text.toString().toFloat()
            log("Payment Val : $paymentAmount")
            if(paymentAmount != null){
                log("Clicked + $paymentAmount")
                checkBalance(paymentAmount)
            }else{
                printMesaage("Please enter a valid amount")
            }
        }



    }
}