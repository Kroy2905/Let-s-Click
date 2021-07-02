package com.example.snapchatclone

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.net.HttpURLConnection
import java.net.URL
import com.example.snapchatclone.ViewSnap.downloadimage as downloadimage1
import com.example.snapchatclone.ViewSnap.downloadimage as downloadimage2


class ViewSnap : AppCompatActivity() {
    val mAuth = FirebaseAuth.getInstance()
    var msgTextview: TextView? = null
    var snapimageview: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_snap)
        setTitle("Message:")


        msgTextview = findViewById(R.id.messageTV)
        snapimageview = findViewById(R.id.snapIV)
        var str = " "+intent.getStringExtra("message")

        msgTextview?.text = str

        val task = downloadimage()
        val myimage: Bitmap

        try {
            myimage =
                task.execute(intent.getStringExtra("imageurl")).get()!!
                    snapimageview?.setImageBitmap(myimage)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }


    }

           inner  class downloadimage :
           AsyncTask<String?, Void?, Bitmap?>() {
               override fun doInBackground(vararg params: String?): Bitmap? {
                   return try {
                       val url = URL(params[0])
                       val connection = url.openConnection() as HttpURLConnection
                       connection.connect()
                       val `in` = connection.inputStream
                       BitmapFactory.decodeStream(`in`)
                   } catch (e: Exception) {
                       e.printStackTrace()
                       null
                   }
               }


           }

    override fun onBackPressed() {

        super.onBackPressed()

        FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.currentUser!!.uid)
            .child("snaps").child(intent.getStringExtra("snapkey")!!).removeValue()

        FirebaseStorage.getInstance().getReference().child("images").child(intent.getStringExtra("imagename")!!).delete()
    }




}