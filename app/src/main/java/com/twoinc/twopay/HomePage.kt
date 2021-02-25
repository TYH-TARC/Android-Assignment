package com.twoinc.twopay

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.util.*
import kotlin.collections.ArrayList


//TODO : Scan > Transfer Function
//TODO :           ^ Transfer > Add Popout
//TODO :                        ^ Payment Popout Fragment

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
        val balance = intent.getStringExtra("Wallet")
        val result = intent.getStringExtra("Result")
        var walletBalance: String = " "
        var userid:String= " "

        fun print(msg: String){
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
        }

        fun log(msg: String){
            Log.d(TAG, "$msg")
        }
        log("Result : $result")
        Log.d("Result Boo : ", (result==null).toString())
        if(result!=null){

            print(result.toString())
        }

        val userIdRef = db.collection("Users").whereEqualTo("Username", username)
        userIdRef.get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        userid = document.id.toString()
                        Log.d(TAG, "Userid : $userid")
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

        val settingBut = findViewById<Button>(R.id.setting)
        fun settingPage(){
            val changePage = Intent(this,Setting::class.java)
            changePage.putExtra("Username",username)
            startActivity(changePage)
        }
        settingBut.setOnClickListener{
            settingPage()
        }

        val transferBut = findViewById<Button>(R.id.transferButton)

        fun transferPage(){
            val changePage = Intent(this, ConfirmPayment::class.java)
            changePage.putExtra("Username", username)
            changePage.putExtra("Wallet", walletBalance)
            startActivity(changePage)
        }

        transferBut.setOnClickListener{
            transferPage()
        }

        val receiveBut = findViewById<Button>(R.id.paybutton)
        fun receivePage(){
            val changePage = Intent(this, Receive::class.java)
            changePage.putExtra("Username", username)
            startActivity(changePage)
        }
        receiveBut.setOnClickListener(){
            receivePage()
        }

        val scanBut = findViewById<Button>(R.id.scanButton)
        fun scanPage(){
            val changePage = Intent(this, Scan::class.java)
            changePage.putExtra("Username",username.toString())
            log("WALLET BALANCE : $walletBalance")
            changePage.putExtra("Balance",walletBalance)
            startActivity(changePage)
        }
        scanBut.setOnClickListener(){
            scanPage()
        }

        val reloadBut = findViewById<Button>(R.id.reloadButton)
        fun reloadPage(){
            val changePage = Intent(this, Reload::class.java)
            changePage.putExtra("Username",username)
            startActivity(changePage)
        }
        reloadBut.setOnClickListener{
            reloadPage()
        }

        fun goToEvent(url: String){
            val docRef = db.collection("Event").whereEqualTo("Image URL",url)
            docRef.get().addOnSuccessListener {doc ->
                for (document in doc){
                    val id = document.id
                    val changePage = Intent(this, EventPage::class.java)
                    changePage.putExtra("Id",id)
                    startActivity(changePage)
                }

            }
        }



        fun getRecentEvents(){
            val docRef = db.collection("Event").orderBy("Date Added", Query.Direction.DESCENDING)
            docRef.addSnapshotListener{ value, e ->
                if(e != null) {
                    Log.w(TAG, "Listen Failed.", e)
                    return@addSnapshotListener
                }
                val urls = ArrayList<String>()
                val deadlines = ArrayList<String>()
                for(doc in value!!){

                    doc.getString("Image URL")?.let{
                        Log.d(TAG, "\n Url : $it \n")
                        urls.add(it)
                    }
                    doc.getDate("Date Ended")?.let{
                        Log.d(TAG, "\n End Date : $it \n")
                        deadlines.add(it.toString())
                    }
                }
                Log.d(TAG, "\n Url : $urls ; End Date : $deadlines \n")

                fun loadImages(url1: String, url2: String, url3: String){
                    val imageView1 = findViewById<ImageButton>(R.id.eventPic1)
                    imageView1.setOnClickListener{
                        goToEvent(url1)
                    }
                    Picasso.with(this).load(url1).into(imageView1);

                    val imageView2 = findViewById<ImageButton>(R.id.eventPic2)
                    imageView2.setOnClickListener{
                        goToEvent(url2)
                    }
                    Picasso.with(this).load(url2).into(imageView2)

                    val imageView3 = findViewById<ImageButton>(R.id.eventPic3)
                    imageView3.setOnClickListener{
                        goToEvent(url3)
                    }
                    Picasso.with(this).load(url3).into(imageView3)
                }

                loadImages(urls[0], urls[1], urls[2])
        }}
        getRecentEvents()




    fun getTime(s: com.google.firebase.Timestamp): String{
        val time = s.seconds
        val dt = Instant.ofEpochSecond(time.toLong()).atZone(ZoneId.systemDefault()).toLocalDateTime().toString()
        return dt
    }

//        fun getRecentTransactions() {
//            val path = "Transactions"
//            val docgrpRef = db.collectionGroup(path).orderBy("Time",Query.Direction.DESCENDING)
//
//            val Type = ArrayList<String>()
//            val Amount = ArrayList<String>()
//            val Date = ArrayList<String>()
//            val Owner = ArrayList<String>()
//
//            val title1 = findViewById<TextView>(R.id.tTitle1)
//            val title2 = findViewById<TextView>(R.id.tTitle2)
//            val subtitle1 = findViewById<TextView>(R.id.tSubtittle1)
//            val subtitle2 = findViewById<TextView>(R.id.tSubtittle2)
//            val amount1 = findViewById<TextView>(R.id.tAmount1)
//            val amount2 = findViewById<TextView>(R.id.tAmount2)
//            val date1 = findViewById<TextView>(R.id.tDate1)
//            val date2 = findViewById<TextView>(R.id.tDate2)
//
//            docgrpRef.get().addOnSuccessListener { documents ->
//                for (document in documents) {
//                    val data = document.data
//
//                    if(data["Type"]!=null && data["Transfer Amount"] != null && data["Account Owner"] != null && data["Time"] != null ){
//                        Type.add(data["Type"].toString())
//                        Amount.add(data["Transfer Amount"].toString())
//                        val timeData = getTime(data["Time"] as com.google.firebase.Timestamp)
//                        Date.add(timeData)
//                        Owner.add(data["Account Owner"].toString())
//                    }
//                }
//                Log.d(TAG, "\n Owner : $Owner ; Time : $Date ; Amount : $Amount; Type: $Type \n")
//                if(Owner.size == 1){
//                    title1.setText(Owner[0])
//                    subtitle1.setText(Type[0])
//                    amount1.setText(Amount[0])
//                    date1.setText(Date[0])
//                    log("Boo : " +(Type[0] == "Send").toString())
//                    if(Type[0] == "Send"){
//                        log((Type[0] == "Send").toString())
//                        val color = getColor(R.color.alert_red)
//                        amount1.setTextColor(color)
//                    }
//                    title2.setText("No More Recent Transactions")
//                    subtitle2.setText(" ")
//                    amount2.setText(" ")
//                    date2.setText(" ")
//                }else if (Owner.size > 1){
//                    title1.setText(Owner[0])
//                    subtitle1.setText(Type[0])
//                    amount1.setText(Amount[0])
//                    date1.setText(Date[0])
//                    if(Type[0] == "Send"){
//                        val color = getColor(R.color.alert_red)
//                        amount1.setTextColor(color)
//                    }
//                    title2.setText(Owner[1])
//                    subtitle2.setText(Type[1])
//                    amount2.setText(Amount[1])
//                    date2.setText(Date[1])
//                    if(Type[1] == "Send"){
//                        val color = getColor(R.color.alert_red)
//                        amount2.setTextColor(color)
//                    }
//                }else if (Owner.size < 1){
//                    title1.setText("No More Recent Transactions")
//                    subtitle1.setText(" ")
//                    amount1.setText(" ")
//                    date1.setText(" ")
//                    title2.setText("No More Recent Transactions")
//                    subtitle2.setText(" ")
//                    amount2.setText(" ")
//                    date2.setText(" ")
//                }
//
//
//
//
//            }.addOnFailureListener { exception ->
//                Log.w(TAG, "Error getting documents: ", exception)
//            }
//
//
//
//
//
//
//
//        }
//            getRecentTransactions()

//        val eventBut1 = findViewById<ImageButton>(R.id.eventPic1)
//        eventBut1.setOnClickListener{
//            Log.d(TAG, "Event Pressed")
//        }





    }





}
