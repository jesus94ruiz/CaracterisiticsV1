package com.jera.caracterisiticsv1

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class CameraActivity : ComponentActivity() {

    companion object{
        private const val CAMERA_PERMISSION_CODE = 1
        private const val CAMERA_REQUEST_CODE = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera_screen)
        Log.d("MyApp", "Hello, world! ONCREATE")

            if (ContextCompat.checkSelfPermission
                    (
                    this,
                    android.Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) //Permiso a camara correcto
            {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, CAMERA_REQUEST_CODE)
            } else {
                ActivityCompat.requestPermissions(  // Solicitamos permiso para acceder a la camara
                    this,
                    arrayOf(android.Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_CODE
                )
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){ //Comprobamos que se tengan los permisos necesarios para acceder a la camara
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, CAMERA_REQUEST_CODE)
            }
            else{
                 Toast.makeText(this,
                     "No se tienen el permiso necesario para acceder a la cámara" + "Cambie la configuración en ajustes",
                     Toast.LENGTH_LONG).show()
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if (requestCode == CAMERA_REQUEST_CODE){
                val ivImage = findViewById<androidx.appcompat.widget.AppCompatImageView>(R.id.iv_image)
                val thumbNail: Bitmap = data!!.extras!!.get("data") as Bitmap
                ivImage.setImageBitmap(thumbNail)
            }
        }
    }

}


