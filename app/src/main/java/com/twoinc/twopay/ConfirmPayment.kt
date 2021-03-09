package com.twoinc.twopay

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlin.concurrent.thread

class ConfirmPayment : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_payment)
        val db = Firebase.firestore
        fun log(msg: String){
            Log.d("LOG FUNCTION",msg)
        }
        val intent = intent
        val username = intent.getStringExtra("Username")
        val wallet = intent.getStringExtra("Wallet")
        val targetUsername = intent.getStringExtra("TargetUser")

        val targetHolder = findViewById<EditText>(R.id.confirmAccName)
        val targetAmount = findViewById<EditText>(R.id.confirmAmount)
        val passcode = findViewById<EditText>(R.id.confirmPasscode)
        val button  = findViewById<Button>(R.id.confirmButton)
        val passFlag = findViewById<TextView>(R.id.passFlag)

        if(targetUsername!=null||targetUsername=="Error"){
            targetHolder.setText(targetUsername)
        }

        fun printMessage(msg: String){
            Toast.makeText(this,msg,Toast.LENGTH_LONG).show()
        }

        fun setData(path:DocumentReference, value:Any){
            log("Set Data : PATH : $path ; VALUE :$value")
            path.update(value as Map<String, Any>)
                .addOnSuccessListener { printMessage("Success") }
                .addOnFailureListener { e -> log("Set data err") }
        }

        fun checkAccValid(): Boolean {
            val accFlag = findViewById<TextView>(R.id.accountFlag).text
            if(accFlag != "/"){
                printMessage("User Not Found")
            }
            return accFlag == "/"
        }

        fun checkAccountBalance(): Boolean{
            val amount = targetAmount.text.toString().toFloat()
            log("Check Account Balance : $amount")
            if(amount?.isNaN()){
                return false
            }
            val balance = wallet.toString().toFloat()
            log("Balance : $balance")
            log("Amount : $amount")
            val returnFlag = (amount<=balance)
            log("Check Acc Result : $returnFlag")
            return returnFlag
        }

        fun listenerReturn(flag: Boolean):Boolean{
            log("Listener Return : $flag")
            return flag
        }

        fun passcodeValidation(): Boolean{
            log("Passcode Validation")
            val code = passcode.text.toString()
            if(code == null){
                printMessage("Passcode cannot be empty! ")
                return false
            }
            var returnFlag = false
            if(passFlag.text.toString()=="/"){
                log("Enter IF")
                returnFlag = true
                log("Passcode Valid : $returnFlag")
                return returnFlag
            }else{
                log("FLAG : "+passFlag.text.toString())
                log("Passcode Valid : $returnFlag")
                return returnFlag
            }

        }

        fun backToHome(){
            val changePage = Intent(this,HomePage::class.java)
            changePage.putExtra("Username",username)
            startActivity(changePage)
        }

        fun transaction(){
            log("Transaction")
            val amount = targetAmount.text.toString().toFloat()
            val targetUser = targetHolder.text.toString()
            val user = username.toString()
            val sender = db.collection("Users").whereEqualTo("Username",user)
            log("Start Sender : $user")
            sender.get().addOnSuccessListener { docs ->
                log("ENTER LISTENER S : ${docs.size()}")
                for(doc in docs){
                    log("ENTER LOOP S")
                    val data = doc.data
                    log("ENTER DATA S : $data")
                    val id = doc.id
                    log("Sender ID : $id")
                    val balance = data?.get("Wallet").toString().toFloat()
                    val newBalance = (balance - amount)
                    log("Sender New Balance : $newBalance")
                    val docRef = db.collection("Users").document(id)
                    val collection = hashMapOf(
                        "Wallet" to newBalance
                    )
                    setData(docRef,collection)
                    log("Sender set complete")

                }
            }.addOnFailureListener{e -> "Sender Error : $e"}
            log("Start Receiver")
            val receiver = db.collection("Users").whereEqualTo("Username",targetUser)
            receiver.get().addOnSuccessListener { docs ->
                log("ENTER LISTENER R : ${docs.size()}")
                for(doc in docs){
                    log("ENTER LOOP R")
                    val data = doc.data
                    log("ENTER DATA R : $data")
                    val id = doc.id
                    log("R ID : $id")
                    val balance = data?.get("Wallet").toString().toFloat()
                    val newBalance = (balance + amount)
                    log("R New Balance : $newBalance")
                    val docRef = db.collection("Users").document(id)
                    val collection = hashMapOf(
                        "Wallet" to newBalance
                    )
                    setData(docRef,collection)
                    log("R set complete")
                }
            }.addOnFailureListener{e -> log("Receiver Error : $e")}
            finish()
            backToHome()
        }

        fun transfer(){
            log("Transfer")
            if( checkAccValid() && checkAccountBalance() && passcodeValidation() ){
                log("Pass checking")
                transaction()
            }
        }

        fun checkFlag() {
            log("CHECK FLAG")
            val code = passcode.text.toString()
            val user = username.toString()
            val docRef = db.collection("Users").whereEqualTo("Username",user)
            docRef.get().addOnSuccessListener { docs ->
                log("RUNNING PASSCODE CHECK")
                for(doc in docs){
                    val data = doc.data
                    val userPasscode = data?.get("Passcode").toString()
                    if(userPasscode == code){
                        passFlag.text = "/"
                        log("CHECKFLAG : " + passFlag.text.toString())
                        transfer()

                    }else{
                        log("Passcode X")
                        printMessage("Passcode incorrect")
                    }

                }
            }.addOnFailureListener{e -> "Passcode Error : $e"}
        }

        fun accountFlag(){
            val accName = targetHolder.text.toString()
            val accFlag = findViewById<TextView>(R.id.accountFlag)
            log("ACCNAME:"+(accName).isBlank().toString())
            if (accName.isNullOrBlank()){
                printMessage("Account Name Error")
            }
            else{
                val docRef = db.collection("Users")
                var userList = ArrayList<String>()
                docRef.get().addOnSuccessListener { docs ->
                    for(doc in docs){
                        val data = doc.data
                        val username = data?.get("Username").toString()
                        log("DOCREF:"+username)
                        userList.add(username)
                        val index = userList.indexOf(accName)
                        log("INDEX "+index)
                        if (index == -1){
                        }else{
                            accFlag.text = "/"
                            checkFlag()
                        }
                    }
                }}
        }
        button.setOnClickListener{
            log("Button Clicked")
            accountFlag()
        }

    }
}