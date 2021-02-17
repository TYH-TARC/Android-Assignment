package com.twoinc.twopay

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class HomePage : AppCompatActivity(){
    private val TAG = "FragmentActivity"

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        val db = Firebase.firestore
        val accountBalance = findViewById<TextView>(R.id.walletBalance)

        val intent = intent
        val username = intent.getStringExtra("Username")
        val password = intent.getStringExtra("Password")
        val balance = intent.getStringExtra("Wallet")
        var walletBalance: String = " "
        var userid:String= " "

        val userIdRef = db.collection("Users").whereEqualTo("Username",username)
        userIdRef.get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        userid = document.id.toString()
                        Log.d(TAG,"Userid : $userid")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }

        if(balance == null){
            val docRef = db.collection("Users").whereEqualTo("Username", username)
            docRef.get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            val data = document.data
                            var wallet = document.data["Wallet"].toString()
                            walletBalance = wallet
                            accountBalance.setText(wallet)
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.w(TAG, "Error getting documents: ", exception)
                    }
        }else{
            accountBalance.setText(balance)
        }

        val transferBut = findViewById<Button>(R.id.transferButton)

        fun transferPage(){
            val changePage = Intent(this,Transfer::class.java)
            changePage.putExtra("Username",username)
            changePage.putExtra("Password",password)
            changePage.putExtra("Wallet",walletBalance)
            startActivity(changePage)
        }

        transferBut.setOnClickListener{
            transferPage()
        }

        val receiveBut = findViewById<Button>(R.id.paybutton)
        fun receivePage(){
            val changePage = Intent(this,Receive::class.java)
            changePage.putExtra("Username",username)
            startActivity(changePage)
        }
        receiveBut.setOnClickListener(){
            receivePage()
        }

        val scanBut = findViewById<Button>(R.id.scanButton)
        fun scanPage(){
            val changePage = Intent(this,Scan::class.java)
            startActivity(changePage)
        }
        scanBut.setOnClickListener(){
            scanPage()
        }

        val reloadBut = findViewById<Button>(R.id.reloadButton)
        fun reloadPage(){
            val changePage = Intent(this,Reload::class.java)
            startActivity(changePage)
        }
        reloadBut.setOnClickListener{
            reloadPage()
        }



        fun getRecentEvents(){
            val docRef = db.collection("Event").orderBy("Date Added",Query.Direction.DESCENDING)
            docRef.addSnapshotListener{ value, e ->
                if(e != null) {
                    Log.w(TAG,"Listen Failed." , e)
                    return@addSnapshotListener
                }
                val urls = ArrayList<String>()
                val deadlines = ArrayList<String>()
                for(doc in value!!){

                    doc.getString("Image URL")?.let{
                        Log.d(TAG,"\n Url : $it \n")
                        urls.add(it)
                    }
                    doc.getDate("Date Ended")?.let{
                        Log.d(TAG,"\n End Date : $it \n")
                        deadlines.add(it.toString())
                    }
                }
                Log.d(TAG,"\n Url : $urls ; End Date : $deadlines \n")

                fun loadImages(url1:String, url2: String, url3: String){
                    val imageView1 = findViewById<ImageButton>(R.id.eventPic1)
                    Picasso.with(this).load(url1).into(imageView1);

                    val imageView2 = findViewById<ImageButton>(R.id.eventPic2)
                    Picasso.with(this).load(url2).into(imageView2)

                    val imageView3 = findViewById<ImageButton>(R.id.eventPic3)
                    Picasso.with(this).load(url3).into(imageView3)
                }

                loadImages(urls[0],urls[1],urls[2])
        }}
        getRecentEvents()

        fun getRecentTransactions(){

            val path = "Users/"+userid+"/Transactions"

            val docRef = db.collection(path)
            docRef.addSnapshotListener{ value, e ->

                if(e != null) {
                    Log.w(TAG,"Listen Failed." , e)
                    return@addSnapshotListener
                }

                val Type = ArrayList<String>()
                val Amount = ArrayList<String>()
                val Date = ArrayList<String>()
                val Owner = ArrayList<String>()

                for(doc in value!!){

                    Log.d(TAG,"aa ${doc.getString("Type")}")
                    doc.getString("Type")?.let{
                        Log.d(TAG,"\n Type : $it \n")
                        Type.add(it)
                    }
                    doc.getString("Transfer Amount")?.let{
                        Log.d(TAG,"\n Url : $it \n")
                        Amount.add(it)
                    }
                    doc.getDate("Time")?.let{
                        Log.d(TAG,"\n End Date : $it \n")
                        Date.add(it.toString())
                    }
                    doc.getString("Account Owner")?.let{
                        Log.d(TAG,"\n Url : $it \n")
                        Owner.add(it)
                    }
                }
                Log.d(TAG,"\n Owner : $Owner ; Time : $Date ; Amount : $Amount; Type: $Type \n")
        }}
            getRecentTransactions()

        val eventBut1 = findViewById<ImageButton>(R.id.eventPic1)
        eventBut1.setOnClickListener{
            Log.d(TAG,"Event Pressed")
        }





    }





}
