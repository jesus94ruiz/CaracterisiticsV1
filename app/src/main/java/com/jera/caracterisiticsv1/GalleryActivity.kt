package com.jera.caracterisiticsv1;

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class GalleryActivity : ComponentActivity() {

    companion object{
        val IMAGE_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera_screen)
        Log.d("GalleryActivity", "Hello, world! ONCREATE")
        pickImageFromGallery()
    }

    private fun pickImageFromGallery(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, GalleryActivity.IMAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            val ivImage = findViewById<ImageView>(R.id.iv_image)
            // thumbNail: Bitmap = data!!.extras!!.get("data") as Bitmap
            ivImage.setImageURI(data?.data)
        }
    }

}
