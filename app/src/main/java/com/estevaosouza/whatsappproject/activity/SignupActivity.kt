package com.estevaosouza.whatsappproject.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.estevaosouza.whatsappproject.databinding.ActivitySignupBinding
import com.estevaosouza.whatsappproject.model.User
import com.estevaosouza.whatsappproject.viewModel.FirebaseFirestoreViewModel
import com.estevaosouza.whatsappproject.util.Constants
import com.estevaosouza.whatsappproject.util.showMessage
import com.estevaosouza.whatsappproject.viewModel.FirebaseAuthViewModel
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

class SignupActivity : AppCompatActivity() {

    private val firebaseAuthViewModel: FirebaseAuthViewModel by viewModels()

    private val firebaseFirestoreViewModel: FirebaseFirestoreViewModel by viewModels()

    private val binding by lazy {
        ActivitySignupBinding.inflate(layoutInflater)
    }

    private lateinit var name: String

    private lateinit var email: String

    private lateinit var password: String

    private lateinit var confirmPassword: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initializeToolbar()
        initializeClickEvents()
    }

    private fun initializeToolbar() {
        // Sign Up Toolbar
        val toolbar = binding.includeToolbar.tbMain
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            // Toolbar Title
            title = Constants.REGISTER_TOOLBAR_TEXT

            // Back Toolbar Button
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun initializeClickEvents() {
        // Sign Up User Button
        binding.btnSignUp.setOnClickListener {
            if (hasSignUpValidFields()) {
                signUpUser(name, email, password)
            }
        }
    }

    private fun hasSignUpValidFields(): Boolean {

        with(binding) {
            // Get Name, E-mail and Passwords Values
            name = editSignUpName.text.toString()
            email = editSignUpEmail.text.toString()
            password = editSignUpPassword.text.toString()
            confirmPassword = editSignUpConfirmPassword.text.toString()

            if (name.isNotEmpty()) {
                // Delete Name Error Message
                textInputLayoutSignUpName.error = null
                if (email.isNotEmpty()) {
                    // Delete E-mail Error Message
                    textInputLayoutSignUpEmail.error = null
                    if (password.isNotEmpty()) {
                        // Delete Password Error Message
                        textInputLayoutSignUpPassword.error = null
                        if (confirmPassword.isNotEmpty()) {
                            // Delete Confirm Password Error Message
                            textInputLayoutSignUpConfirmPassword.error = null
                            return if (password == confirmPassword) {
                                // Delete Passwords Error Messages
                                textInputLayoutSignUpPassword.error = null
                                textInputLayoutSignUpConfirmPassword.error = null
                                true
                            } else {
                                // Passwords Don't Match Error Message
                                textInputLayoutSignUpPassword.error = Constants.PASSWORDS_DONT_MATCH_ERROR
                                textInputLayoutSignUpConfirmPassword.error = Constants.PASSWORDS_DONT_MATCH_ERROR
                                false
                            }
                        } else {
                            // Fill Password Error Message
                            textInputLayoutSignUpConfirmPassword.error = Constants.FILL_PASSWORD_FIELD_ERROR
                            return false
                        }
                    } else {
                        // Fill Password Error Message
                        textInputLayoutSignUpPassword.error = Constants.FILL_PASSWORD_FIELD_ERROR
                        return false
                    }
                } else {
                    // Fill E-mail Error Message
                    textInputLayoutSignUpEmail.error = Constants.FILL_EMAIL_FIELD_ERROR
                    return false
                }
            } else {
                // Fill Name Error Message
                textInputLayoutSignUpName.error = Constants.FILL_NAME_FIELD_ERROR
                return false
            }
        }
    }

    private fun signUpUser(name: String, email: String, password: String) {
        // Sign Up User
        firebaseAuthViewModel.signUpUser(
            email, password

        // If Transaction is Completed
        ).addOnCompleteListener { result ->
            if (result.isSuccessful) {
                // Save User to Firestore Database
                val userId = result.result.user?.uid
                if (userId != null) {
                    val user = User(userId, name, email, Constants.PROFILE_PHOTO_URL)
                    saveUserToFirestore(user)
                }
            }

        // If Transaction is Failed
        }.addOnFailureListener { error ->
            try {
                throw error
            } catch (weakPasswordError: FirebaseAuthWeakPasswordException) {
                weakPasswordError.printStackTrace()
                showMessage(Constants.WEAK_PASSWORD_MSG)
            } catch (existentEmailError: FirebaseAuthUserCollisionException) {
                existentEmailError.printStackTrace()
                showMessage(Constants.EXISTENT_EMAIL_MSG)
            } catch (invalidCredentialsError: FirebaseAuthInvalidCredentialsException) {
                invalidCredentialsError.printStackTrace()
                showMessage(Constants.INVALID_CREDENTIALS_MSG)
            }
        }
    }

    private fun saveUserToFirestore(user: User) {
        // Save User to Firestore Database
        firebaseFirestoreViewModel.saveUserToFirestore(
            user

        // If Transaction is Successful
        ).addOnSuccessListener {
            showMessage(Constants.SIGNUP_SUCCESSFUL_MSG)
            val intent = Intent(applicationContext, MainActivity::class.java)
            finish()
            startActivity(intent)

        // If Transaction is Failed
        }.addOnFailureListener {
            showMessage(Constants.SIGNUP_FAILURE_MSG)
        }
    }
}