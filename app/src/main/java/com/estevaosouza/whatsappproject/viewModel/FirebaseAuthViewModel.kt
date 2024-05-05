package com.estevaosouza.whatsappproject.viewModel

import androidx.lifecycle.ViewModel
import com.estevaosouza.whatsappproject.repository.FirebaseAuthRepository
import com.google.firebase.auth.FirebaseAuth

class FirebaseAuthViewModel (
    private val repository: FirebaseAuthRepository
) : ViewModel() {

    constructor():this(FirebaseAuthRepository(FirebaseAuth.getInstance()))

    fun getCurrentUser() = repository.currentUser

    fun signUpUser(
        email: String, password: String
    ) = repository.signUp(email, password)

    fun signInUser(
        email: String, password: String
    ) = repository.signIn(email, password)

    fun signOutUser() = repository.signOut()
}