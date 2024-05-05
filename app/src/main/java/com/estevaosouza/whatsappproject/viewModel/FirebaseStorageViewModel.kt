package com.estevaosouza.whatsappproject.viewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.estevaosouza.whatsappproject.repository.FirebaseStorageRepository
import com.google.firebase.storage.FirebaseStorage

class FirebaseStorageViewModel (
    private val repository:FirebaseStorageRepository
) : ViewModel() {

    constructor():this(FirebaseStorageRepository(FirebaseStorage.getInstance()))

    fun uploadPhotoToStorage(
        userId: String, photoUri: Uri
    ) = repository.uploadPhoto(userId, photoUri)
}