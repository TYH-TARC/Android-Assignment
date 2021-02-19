package com.twoinc.twopay

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import java.time.Instant
import java.time.ZoneId

//TODO  No Activity found to handle Intent { act=android.intent.action.VIEW dat=null }
//TODO URL Control
//TODO date data type

class EventPage : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_page)

        val intent = intent
        val eventId = intent.getStringExtra("Id").toString()

        val db = Firebase.firestore

        fun printM(msg: String) {
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
        }

        val docRef = db.collection("Event").document(eventId)
        docRef.get().addOnSuccessListener { document ->
            val data = document.data
            @RequiresApi(Build.VERSION_CODES.O)
            fun getTime(s: Timestamp): String {
                return Instant.ofEpochSecond(s.seconds.toLong()).atZone(ZoneId.systemDefault())
                    .toLocalDateTime().toString()
            }
            val endDate = getTime(data?.get("Date Ended") as Timestamp)
            val des = data?.get("Description").toString()
            val title = data?.get("Title").toString()
            val url = data?.get("Image URL").toString()
            val ext = data?.get("External URL").toString()

            fun changeImage(url: String) {
                val imageView = findViewById<ImageView>(R.id.eventBannerView)
                Picasso.with(this).load(url).into(imageView);
            }

            fun goToExternalLink(flag:Boolean,ext: String?) {
                if (ext == "null") {
                    printM("URL Not Available")
                    return
                } else {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(ext))
                    startActivity(browserIntent)
                }
            }

            Log.d(
                "Fragment Activity",
                "title : $title ; des : $des ; img : $url ; ext : $ext; data : $data"
            )
            changeImage(url)

            val titleHolder = findViewById<TextView>(R.id.eventTitleText)
            val dateHolder = findViewById<TextView>(R.id.eventDate)
            val desHolder = findViewById<TextView>(R.id.subtitleContent)
            val button = findViewById<Button>(R.id.ExternalLink)

            titleHolder.setText(title)
            dateHolder.setText(endDate)
            desHolder.setText(des)
            button.setOnClickListener {
                goToExternalLink(ext?.isNullOrEmpty(),ext)
            }
        }



        fun openWebPage(url: String) {
            Log.d("OpenWebPage", "$url")
            var webpage = Uri.parse(url)
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                webpage = Uri.parse("http://$url")
            }
            val intent = Intent(Intent.ACTION_VIEW, webpage)
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }
        }






    }
}
