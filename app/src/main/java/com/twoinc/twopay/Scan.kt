package com.twoinc.twopay

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.CaptureActivity
import kotlin.math.log


@Suppress("DEPRECATION")
@SuppressLint("WrongViewCast")
class Scan : AppCompatActivity() {
    companion object{
        var Username = null
        var Balance = null
    }

    private val TAG = "FragmentActivity"
    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)


            val intent = intent
            var Username = intent.getStringExtra("Username")
            var Balance = intent.getStringExtra("Balance")

        fun getTargetUser(): String {
            val qrcodeString = findViewById<TextView>(R.id.qrcode).text.toString()
            if (qrcodeString.contains("twopay>accName=")) {
                val delimiter: String = "twopay>accName="
                return qrcodeString.split(delimiter)[1]
            } else {
                return " "
            }
        }

        fun goTransaction(){
            Log.d("goTransaction","Start")
            val changePage = Intent(this,ConfirmPayment::class.java)
            changePage.putExtra("Username",Username)
            changePage.putExtra("Wallet",Balance)
            changePage.putExtra("TargetUser",getTargetUser())
            Log.d("Intent",changePage.toString())
            startActivity(changePage)
        }

        val button = findViewById<Button>(R.id.button2)
        button.setOnClickListener {
            scanQRCode()
        }

        val transBut  =findViewById<Button>(R.id.transfer)
        transBut.setOnClickListener{
            goTransaction()
        }
    }

    fun scanQRCode() {
        val integrator = IntentIntegrator(this).apply {
            captureActivity = CaptureActivity::class.java
            setOrientationLocked(true)
            setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
            setPrompt("Please show your code")
        }

        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Scanned: " + result.contents, Toast.LENGTH_LONG).show()
                val textHolder = findViewById<TextView>(R.id.qrcode)
                val resultText: String = result.contents
                Log.d("QR RESULT ", "$resultText : {${resultText.toString()}}")
                textHolder.text = "Qrcode : \n" + result.contents
            }

        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

}