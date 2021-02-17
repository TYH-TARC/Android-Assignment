package com.twoinc.twopay

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import java.lang.NumberFormatException

class Reload : AppCompatActivity() {

    val TAG = "FragmentActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reload)

        val reloadButton = findViewById<Button>(R.id.reloadBut)
        reloadButton.setOnClickListener(){
            reloadValue("CheckBut")
        }

        val moneyArray = arrayOf<Int>(R.id.rm10but,R.id.rm20but,R.id.rm50but,R.id.rm100but,R.id.rm150but,R.id.rm200but)
        val valueArray = arrayOf<String>("10","20","50","100","150","200")
        val iterationCount : Int = moneyArray.size - 1
        for(i in 0..iterationCount){
            val button = findViewById<Button>(moneyArray[i])
            button.setOnClickListener(){
                val reloadVal = valueArray[i]
                reloadValue(reloadVal)

            }

        }
    }

    fun reloadValue(value: String){
        if(value == "CheckBut"){
            val valueInput = findViewById<EditText>(R.id.reloadValue)
            val reloadVal: String = valueInput.text.toString()
            Log.d(TAG,"Value : $reloadVal")
            reloadGatewayPage(reloadVal)
        }else{
            Log.d(TAG,"Value : $value")
            reloadGatewayPage(value)
        }
    }

    fun reloadGatewayPage(reloadVal: String){
        val changePage = Intent(this,reloadGateway::class.java)
        changePage.putExtra("Value",reloadVal)
        startActivity(changePage)
    }

    fun butListeners(){

    }



}

