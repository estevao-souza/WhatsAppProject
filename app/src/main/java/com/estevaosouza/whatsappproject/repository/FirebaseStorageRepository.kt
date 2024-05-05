package com.estevaosouza.whatsappproject.repository

import android.net.Uri
import com.estevaosouza.whatsappproject.util.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask

class FirebaseStorageRepository(private val storage: FirebaseStorage) {

    fun uploadPhoto(userId: String, uri: Uri): UploadTask {
        return storage
            .getReference(Constants.PHOTOS_REFERENCE_NAME)
            .child(Constants.USERS_COLLECTION_NAME)
            .child(userId)
            .child(Constants.PROFILE_PHOTO_FILE)
            .putFile(uri)
    }
}