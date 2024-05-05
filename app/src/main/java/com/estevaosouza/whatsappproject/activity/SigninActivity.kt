package com.estevaosouza.whatsappproject.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.estevaosouza.whatsappproject.databinding.ActivitySigninBinding
import com.estevaosouza.whatsappproject.util.Constants
import com.estevaosouza.whatsappproject.util.showMessage
import com.estevaosouza.whatsappproject.viewModel.FirebaseAuthViewModel
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class SigninActivity : AppCompatActivity() {

    private val firebaseAuthViewModel: FirebaseAuthViewModel by viewModels()

    private val binding by lazy {
        ActivitySigninBinding.inflate(layoutInflater)
    }

    private lateinit var email: String

    private lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initializeClickEvents()
    }

    override fun onStart() {
        super.onStart()
        isUserSigned()
    }

    private fun initializeClickEvents() {
        with(binding) {
            // Sign Up User Button
            textSignUp.setOnClickListener {
                startActivity(
                    Intent(this@SigninActivity, SignupActivity::class.java)
                )
            }

            // Sign In User Button
            btnSignIn.setOnClickListener {
                if (hasSignInValidFields()) {
                    signInUser()
                }
            }
        }
    }

    private fun hasSignInValidFields(): Boolean {

        with(binding) {
            // Get E-mail and Password Values
            email = editSignInEmail.text.toString()
            password = editSignInPassword.text.toString()

            return if (email.isNotEmpty()) {
                // Delete E-mail Error Message
                textInputLayoutSignInEmail.error = null
                if (password.isNotEmpty()) {
                    // Delete Password Error Message
                    textInputLayoutSignInPassword.error = null
                    true
                } else {
                    // Fill Password Error Message
                    textInputLayoutSignInPassword.error = Constants.FILL_PASSWORD_FIELD_ERROR
                    false
                }
            } else {
                // Fill E-mail Error Message
                textInputLayoutSignInEmail.error = Constants.FILL_EMAIL_FIELD_ERROR
                false
            }
        }
    }

    private fun signInUser() {
        // Sign In User
        firebaseAuthViewModel.signInUser(
            email, password

        // If Transaction is Successful
        ).addOnSuccessListener {
            showMessage(Constants.SIGNIN_SUCCESSFUL_MSG)
            startActivity(
                Intent(this, MainActivity::class.java)
            )

        // If Transaction is Failed
        }.addOnFailureListener { error ->
            try {
                throw error
            } catch (invalidUserError: FirebaseAuthInvalidUserException) {
                invalidUserError.printStackTrace()
                showMessage(Constants.INVALID_EMAIL_MSG)
            }
            catch (invalidCredentialsError: FirebaseAuthInvalidCredentialsException) {
                invalidCredentialsError.printStackTrace()
                showMessage(Constants.INVALID_CREDENTIALS_MSG)
            }
        }
    }

    private fun isUserSigned() {
        val currentUser = firebaseAuthViewModel.getCurrentUser()
        if (currentUser != null) {
            startActivity(
                Intent(this, MainActivity::class.java)
            )
        }
    }
}