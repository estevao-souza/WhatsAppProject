package com.estevaosouza.whatsappproject.activity

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.estevaosouza.whatsappproject.databinding.ActivityProfileBinding
import com.estevaosouza.whatsappproject.model.Chat
import com.estevaosouza.whatsappproject.util.Constants
import com.estevaosouza.whatsappproject.util.showMessage
import com.estevaosouza.whatsappproject.viewModel.FirebaseAuthViewModel
import com.estevaosouza.whatsappproject.viewModel.FirebaseFirestoreViewModel
import com.estevaosouza.whatsappproject.viewModel.FirebaseStorageViewModel
import com.squareup.picasso.Picasso

class ProfileActivity : AppCompatActivity() {

    private val firebaseAuthViewModel: FirebaseAuthViewModel by viewModels()

    private val firebaseStorageViewModel: FirebaseStorageViewModel by viewModels()

    private val firebaseFirestoreViewModel: FirebaseFirestoreViewModel by viewModels()

    private val getPhotoFromGallery = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            photoUri = uri
            binding.profilePhoto.setImageURI(uri)
        } else {
            showMessage(Constants.NO_SELECTED_IMG_MSG)
        }
    }

    private val binding by lazy {
        ActivityProfileBinding.inflate(layoutInflater)
    }

    private var hasCameraPermission = false

    private var hasGalleryPermission = false

    private var photoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initializeToolbar()
        requestCameraAndGalleryPermissions()
        initializeClickEvents()
    }

    override fun onStart() {
        super.onStart()
        getUserData()
    }

    private fun initializeToolbar() {
        // Profile Toolbar
        val toolbar = binding.includeToolbarProfile.tbMain
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            // Toolbar Title
            title = Constants.UPDATE_ACCOUNT_TOOLBAR_TEXT

            // Back Toolbar Button
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun requestCameraAndGalleryPermissions() {
        // Check If User Has Camera Permission
        hasCameraPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        // Check If User Has Gallery Permission
        hasGalleryPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_MEDIA_IMAGES
        ) == PackageManager.PERMISSION_GRANTED

        // Denied Permissions List
        val deniedPermissionsList = mutableListOf<String>()

        if (!hasCameraPermission)
            deniedPermissionsList.add(Manifest.permission.CAMERA)

        if (!hasGalleryPermission)
            deniedPermissionsList.add(Manifest.permission.READ_MEDIA_IMAGES)

        if (deniedPermissionsList.isNotEmpty()) {
            // Request Permissions If Necessary
            val permissionsManager = registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                hasCameraPermission = permissions[Manifest.permission.CAMERA] ?: hasCameraPermission
                hasGalleryPermission = permissions[Manifest.permission.READ_MEDIA_IMAGES] ?: hasGalleryPermission
            }

            permissionsManager.launch(deniedPermissionsList.toTypedArray())
        }
    }

    private fun initializeClickEvents() {
        with(binding) {
            // Select Profile Photo Button
            fabSelectPhoto.setOnClickListener {
                // Check User Permissions
                if (hasCameraPermission) {
                    getPhotoFromGallery.launch(Constants.ALLOWED_IMG_TYPES)
                } else {
                    showMessage(Constants.GALLERY_PERMISSION_DENIED_MSG)
                }
            }

            // Update Profile Button
            btnUpdate.setOnClickListener {
                // Get Signed User
                val user = firebaseAuthViewModel.getCurrentUser()
                if (user != null) {
                    val userName = editUpdateName.text.toString()
                    if (userName.isNotEmpty()) {
                        // Update Profile Photo
                        if (photoUri != null) {
                            updateProfilePhoto()
                        }

                        // Update User Name
                        updateUserName(user.uid, userName)

                        // Close Activity
                        finish()
                    } else {
                        showMessage(Constants.FILL_NAME_FIELD_MSG)
                    }
                }
            }
        }
    }

    private fun getUserData() {
        // Get Signed User
        val user = firebaseAuthViewModel.getCurrentUser()
        if (user != null) {
            firebaseFirestoreViewModel.getUserData(
                user.uid

            // If Transaction is Successful
            ).addOnSuccessListener { documentSnapshot ->
                // Get User Data
                val userData = documentSnapshot.data
                if (userData != null) {
                    // Set User Name
                    val name = userData[Constants.FIELD_NAME] as String
                    binding.editUpdateName.setText(name)

                    // Set User Profile and URI Photo
                    val photo = userData[Constants.FIELD_PHOTO] as String
                    if (photo.isNotEmpty()) {
                        Picasso.get()
                            .load(photo)
                            .into(binding.profilePhoto)
                    }
                }

            // If Transaction is Failed
            }.addOnFailureListener {
                showMessage(Constants.NO_PROFILE_USER_DATA_MSG)
                finish()
            }
        }
    }

    private fun updateProfilePhoto() {
        // Update Profile Photo
        val user = firebaseAuthViewModel.getCurrentUser()
        if (user != null) {
            // Update Profile Photo to Storage
            firebaseStorageViewModel.uploadPhotoToStorage(
                user.uid, photoUri!!

            // If Transaction is Successful
            ).addOnSuccessListener { task ->
                showMessage(Constants.UPDATE_PROFILE_PHOTO_SUCCESS_MSG)

                // Get Updated Profile Photo URI from Storage Transaction
                task.metadata
                    ?.reference
                    ?.downloadUrl
                    ?.addOnSuccessListener { uri ->
                        // Profile Photo URI Map
                        var data = mapOf(Constants.FIELD_PHOTO to uri.toString())

                        // Update Profile Photo URI on Firestore Database (User)
                        firebaseFirestoreViewModel.updateUserData(
                            user.uid, data
                        )

                        // Update Profile Photo URI on Firestore Database (Chat)
                        data = mapOf(Constants.FIELD_RECIPIENT_USER_PHOTO to uri.toString())
                        // Get All Chats from Sender User
                        firebaseFirestoreViewModel.getAllChats(
                            user.uid

                        // If Transaction is Successful
                        ).addOnSuccessListener { querySnapshot ->
                            // Convert DocumentSnapshot to Chat Object
                            val documents = querySnapshot?.documents
                            documents?.forEach { documentSnapshot ->
                                val chat = documentSnapshot.toObject(Chat::class.java)
                                if (chat != null) {
                                    // Update Sender User Profile Photo URI from All Recipient Chats
                                    firebaseFirestoreViewModel.updateChatData(chat.recipientUserId, user.uid, data)
                                }
                            }
                        }
                    }

            // If Transaction is Failed
            }.addOnFailureListener {
                showMessage(Constants.UPDATE_PROFILE_PHOTO_FAIL_MSG)
            }
        }
    }

    private fun updateUserName(userId: String, userName: String) {
        // User Name Map
        var data = mapOf(Constants.FIELD_NAME to userName)

        // Update User Name on Firestore Database (User)
        firebaseFirestoreViewModel.updateUserData(
            userId, data
        )

        // Update User Name on Firestore Database (Chat)
        data = mapOf(Constants.FIELD_RECIPIENT_USER_NAME to userName)
        // Get All Chats from Sender User
        firebaseFirestoreViewModel.getAllChats(
            userId

        // If Transaction is Successful
        ).addOnSuccessListener { querySnapshot ->
            // Convert DocumentSnapshot to Chat Object
            val documents = querySnapshot?.documents
            documents?.forEach { documentSnapshot ->
                val chat = documentSnapshot.toObject(Chat::class.java)
                if (chat != null) {
                    // Update Sender User Name from All Recipient Chats
                    firebaseFirestoreViewModel.updateChatData(chat.recipientUserId, userId, data)
                }
            }
            showMessage(Constants.UPDATE_USER_PROFILE_SUCCESS_MSG)

        // If Transaction is Failed
        }.addOnFailureListener {
            showMessage(Constants.UPDATE_USER_PROFILE_FAIL_MSG)
        }
    }
}