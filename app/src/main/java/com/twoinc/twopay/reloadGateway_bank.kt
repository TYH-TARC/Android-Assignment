package com.twoinc.twopay

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView

class reloadGateway_bank : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reload_gateway_bank)

        val intent = intent
        val reloadVal = intent.getStringExtra("Value")
        val username = intent.getStringExtra("Username")

        val homeBut = findViewById<Button>(R.id.backToHome)

        fun backToHome(){
            val changPage = Intent(this,HomePage::class.java)
            changPage.putExtra("Username",username)
            startActivity(changPage)
        }

        homeBut.setOnClickListener{
            backToHome()
        }

//        val cardBut = findViewById<Button>(R.id.card_Frag)
//        fun chgFragment(){
//            val changePage = Intent(this,reloadGateway::class.java)
//            changePage.putExtra("Value",reloadVal)
//            changePage.putExtra("Username",username)
//            startActivity(changePage)
//        }
//        cardBut.setOnClickListener{
//            chgFragment()
//        }

        val amountHolder = findViewById<TextView>(R.id.valueHolder)
        amountHolder.setText("RM $reloadVal")

        fun goToPayment(gateway: String){
            val changePage = Intent(this,Payment::class.java)
            changePage.putExtra("Gateway",gateway)
            changePage.putExtra("Amount",reloadVal)
            changePage.putExtra("Username",username)
            startActivity(changePage)
        }

        val bankButtonList =  arrayOf<Int>(R.id.maybankBut,R.id.pubBut,R.id.hongBut,R.id.cimbBut)
        val gatewayList = arrayOf<String>("Bank_Maybank","Bank_PublicBank","Bank_HongLeongBank","Bank_CIMBBAnk")
        val iterationCount: Int = bankButtonList.size - 1
        for(x in 0..iterationCount){
            Log.d("Button ID",x.toString())
            val button = findViewById<Button>(bankButtonList[x])
            Log.d("Button", button.toString())
            button.setOnClickListener{
                val gateway = gatewayList[x]
                goToPayment(gateway)
            }
        }


    }
}