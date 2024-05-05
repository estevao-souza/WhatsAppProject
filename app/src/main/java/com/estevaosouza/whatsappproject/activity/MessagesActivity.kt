package com.estevaosouza.whatsappproject.activity

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.estevaosouza.whatsappproject.adapter.MessagesAdapter
import com.estevaosouza.whatsappproject.databinding.ActivityMessagesBinding
import com.estevaosouza.whatsappproject.model.Chat
import com.estevaosouza.whatsappproject.model.Message
import com.estevaosouza.whatsappproject.model.User
import com.estevaosouza.whatsappproject.util.Constants
import com.estevaosouza.whatsappproject.util.showMessage
import com.estevaosouza.whatsappproject.viewModel.FirebaseAuthViewModel
import com.estevaosouza.whatsappproject.viewModel.FirebaseFirestoreViewModel
import com.google.firebase.firestore.ListenerRegistration
import com.squareup.picasso.Picasso

class MessagesActivity : AppCompatActivity() {

    private val firebaseAuthViewModel: FirebaseAuthViewModel by viewModels()

    private val firebaseFirestoreViewModel: FirebaseFirestoreViewModel by viewModels()

    private val binding by lazy {
        ActivityMessagesBinding.inflate(layoutInflater)
    }

    private lateinit var snapshotEvent: ListenerRegistration

    private lateinit var messagesAdapter: MessagesAdapter

    private var senderUser: User? = null

    private var recipientUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        getUsersData()
        initializeToolbar()
        initializeClickEvents()
        initializeRecyclerView()
        initializeListeners()
    }

    override fun onDestroy() {
        super.onDestroy()

        // Finish Messages Listener
        snapshotEvent.remove()
    }

    private fun initializeToolbar() {
        // Messages Toolbar
        val toolbar = binding.tbChat
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            // Toolbar Title
            title = ""

            if (recipientUser != null) {
                // Set User Name
                binding.textName.text = recipientUser!!.name

                // Set User Profile Photo
                Picasso.get()
                    .load(recipientUser!!.photo)
                    .into(binding.imageProfilePhoto)
            }

            // Back Toolbar Button
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun initializeClickEvents() {
        // Send Message Button
        binding.fabSend.setOnClickListener {
            val message = binding.editMessage.text.toString()
            sendMessage(message)
        }
    }

    private fun initializeRecyclerView() {
        // Initialize Messages RecyclerView
        with (binding) {
            messagesAdapter = MessagesAdapter()
            rvMessages.adapter = messagesAdapter
            rvMessages.layoutManager = LinearLayoutManager(this@MessagesActivity)
        }
    }

    private fun initializeListeners() {
        // Get Users
        val senderUser = firebaseAuthViewModel.getCurrentUser()
        val recipientUser = recipientUser

        if (senderUser != null && recipientUser != null) {
            // Start Listener Event (Get All New Messages from Firestore Database)
            snapshotEvent = firebaseFirestoreViewModel.getAllMessagesListener(
                senderUser.uid, recipientUser.id

            // Listener Action
            ).addSnapshotListener { querySnapshot, error ->
                // Retrieving Messages Error Message
                if (error != null) {
                    showMessage(Constants.GET_MESSAGES_FAIL_MSG)
                }

                // Get All New Messages and Send to a List
                val messagesList = mutableListOf<Message>()
                val documents = querySnapshot?.documents
                documents?.forEach { documentSnapshot ->
                    // Convert DocumentSnapshot to Message Object
                    val message = documentSnapshot.toObject(Message::class.java)
                    if (message != null) {
                        messagesList.add(message)
                    }
                }

                // Populate RecyclerView List
                if (messagesList.isNotEmpty()) {
                    messagesAdapter.populateList(messagesList)
                    // Scroll to Bottom of RecyclerView
                    binding.rvMessages.smoothScrollToPosition(messagesList.size-1)
                }
            }
        }
    }

    private fun getUsersData() {
        // Get Sender User
        val senderUser = firebaseAuthViewModel.getCurrentUser()
        if (senderUser != null) {
            firebaseFirestoreViewModel.getUserData(
                senderUser.uid
            ).addOnSuccessListener { documentSnapshot ->
                // Convert DocumentSnapshot to User Object
                val user = documentSnapshot.toObject(User::class.java)
                this.senderUser = user
            }
        }

        // Get Recipient User
        val extras = intent.extras
        if (extras != null) {
            // Convert Extras Value to User Object (Depending on Android Version)
            recipientUser = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                extras.getParcelable(Constants.RECIPIENT_USER_EXTRAS_PARAMETER, User::class.java)
            } else {
                extras.getParcelable(Constants.RECIPIENT_USER_EXTRAS_PARAMETER)
            }
        }
    }

    private fun sendMessage(messageText: String) {
        // Don't Send Empty Messages
        if (messageText.isNotEmpty()) {
            if (senderUser?.id != null && recipientUser?.id != null) {
                // Get Sent Message
                val message = Message(senderUser!!.id, messageText)

                // Save Message to Firestore for Sender
                saveMessageToFirestore(senderUser!!.id, recipientUser!!.id, message)
                // Save Chat to Firestore for Sender
                val senderChat = Chat(senderUser!!.id, recipientUser!!.id, recipientUser!!.photo, recipientUser!!.name, messageText, Constants.MESSAGE_READ)
                saveChatToFirestore(senderChat)

                // Save Message to Firestore for Recipient
                saveMessageToFirestore(recipientUser!!.id, senderUser!!.id, message)
                // Save Chat to Firestore for Recipient
                val recipientChat = Chat(recipientUser!!.id, senderUser!!.id, senderUser!!.photo, senderUser!!.name, messageText, Constants.MESSAGE_UNREAD)
                saveChatToFirestore(recipientChat)

                // Clean Message Text Box
                binding.editMessage.setText("")
            }
        }
    }

    private fun saveMessageToFirestore(senderUserId: String, recipientUserId: String, message: Message) {
        // Save Message to Firestore Database
        firebaseFirestoreViewModel.saveMessageToFirestore(
            senderUserId, recipientUserId, message

            // If Transaction is Failed
        ).addOnFailureListener {
            showMessage(Constants.SEND_CHAT_MESSAGE_FAIL_MSG)
        }
    }

    private fun saveChatToFirestore(chat: Chat) {
        // Save Chat to Firestore Database
        firebaseFirestoreViewModel.saveChatToFirestore(
            chat

            // If Transaction is Failed
        ).addOnFailureListener {
            showMessage(Constants.SAVE_CHAT_FAIL_MSG)
        }
    }
}