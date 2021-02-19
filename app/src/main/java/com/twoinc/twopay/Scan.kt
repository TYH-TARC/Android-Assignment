package com.twoinc.twopay

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.CaptureActivity

@SuppressLint("WrongViewCast")
class Scan : AppCompatActivity() {

    private val TAG = "FragmentActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)
//        scanQRCode()
        val button = findViewById<Button>(R.id.button2)
        button.setOnClickListener{
            scanQRCode()
        }
    }

    fun scanQRCode(){
        val integrator = IntentIntegrator(this).apply {
            captureActivity = CaptureActivity::class.java
            setOrientationLocked(false)
            setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
            setPrompt("Please show your code")
        }

        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
            else {
                Toast.makeText(this, "Scanned: " + result.contents, Toast.LENGTH_LONG).show()
                val textHolder = findViewById<TextView>(R.id.qrcode)
                textHolder.setText("Qrcode : \n"+result.contents)
                transferTo(result.contents)
            }

        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    fun transferTo(qrcodeString: String){
        if(qrcodeString.contains("twopay>accName=")){
            val delimiter: String = "twopay>accName="
            val username = qrcodeString.split(delimiter)[1]
            Log.d(TAG,"Username : $username")
        }else{
            Log.d(TAG,"Wrong Format")
        }

    }



}