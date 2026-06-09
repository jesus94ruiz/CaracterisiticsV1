package com.jera.caracterisiticsv1.repository

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StorageRepository @Inject constructor(
    private val storage: FirebaseStorage
) {

    /**
     * Sube la foto de un coche del expositor a Firebase Storage.
     * Ruta: showcase/{uid}/{carId}.jpg
     * Devuelve la URL pública de descarga o null si falla.
     */
    suspend fun uploadShowcasePhoto(uid: String, carId: String, localPath: String): String? {
        return try {
            val file = File(localPath)
            if (!file.exists()) return null

            val ref = storage.reference
                .child("showcase")
                .child(uid)
                .child("$carId.jpg")

            ref.putFile(Uri.fromFile(file)).await()
            ref.downloadUrl.await().toString()
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Elimina la foto de un coche del expositor de Firebase Storage.
     */
    suspend fun deleteShowcasePhoto(uid: String, carId: String) {
        try {
            storage.reference
                .child("showcase")
                .child(uid)
                .child("$carId.jpg")
                .delete()
                .await()
        } catch (e: Exception) {
            // Ignorar si no existe
        }
    }
}
