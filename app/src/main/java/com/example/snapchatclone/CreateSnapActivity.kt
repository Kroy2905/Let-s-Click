package com.example.snapchatclone

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.util.*


class CreateSnapActivity : AppCompatActivity() {


    var createSnapImageView: ImageView? = null
    var messageEdittext: EditText? = null
    val imagename = UUID.randomUUID().toString() + ".jpeg"
    lateinit var storageReference: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_snap)
        storageReference = FirebaseStorage.getInstance().getReference()

        createSnapImageView = findViewById(R.id.snapIV)
        messageEdittext = findViewById(R.id.snapMessageET)
        setTitle("Take a pic")

    }


    fun getpic() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 1)
    }

    fun submitsnapclciked(view: View) {  // Submit Snap button function

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        } else {
            getpic()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val selectedImage = data!!.data
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImage)

                createSnapImageView?.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getpic()
            }
        }
    }


    fun nextclicked(view: View) {

        // Get the data from an ImageView as bytes
        createSnapImageView?.isDrawingCacheEnabled = true
        createSnapImageView?.buildDrawingCache()
        val bitmap = (createSnapImageView?.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()


        var uploadTask = FirebaseStorage.getInstance().getReference().child("images").child(
            imagename
        ).putBytes(data)
        uploadTask.addOnFailureListener {

            Toast.makeText(this, "Upload failed!", Toast.LENGTH_LONG).show();
            // Handle unsuccessful uploads
        }.addOnSuccessListener { taskSnapshot ->
            Toast.makeText(
                baseContext, "Image uploaded",
                Toast.LENGTH_LONG
            ).show()
            var downloadUrl: Uri? = null
            FirebaseStorage.getInstance().reference.child("images")
                .child(imagename).downloadUrl.addOnSuccessListener { it1 ->
                    downloadUrl = it1


                    Log.i("URL", downloadUrl.toString())

                    val intent = Intent(this,ChooseUser::class.java)
                    intent.putExtra("imageurl",downloadUrl.toString())
                    intent.putExtra("imagename",imagename)
                    intent.putExtra("message",messageEdittext?.text.toString())
                    startActivity(intent)



                }
           /* val intent = Intent(this,ChooseUser::class.java)
            intent.putExtra("imageurl",downloadUrl.toString())
            intent.putExtra("imagename",imagename)
            intent.putExtra("message",messageEdittext?.text.toString())

            startActivity(intent)*/



        }
    }
}





