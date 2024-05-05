package com.estevaosouza.whatsappproject.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.estevaosouza.whatsappproject.activity.MessagesActivity
import com.estevaosouza.whatsappproject.adapter.ChatsAdapter
import com.estevaosouza.whatsappproject.databinding.FragmentChatsBinding
import com.estevaosouza.whatsappproject.model.Chat
import com.estevaosouza.whatsappproject.model.User
import com.estevaosouza.whatsappproject.util.Constants
import com.estevaosouza.whatsappproject.viewModel.FirebaseAuthViewModel
import com.estevaosouza.whatsappproject.viewModel.FirebaseFirestoreViewModel
import com.google.firebase.firestore.ListenerRegistration

class ChatsFragment : Fragment() {

    private val firebaseAuthViewModel: FirebaseAuthViewModel by lazy {
        ViewModelProvider(this)[FirebaseAuthViewModel::class.java]
    }

    private val firebaseFirestoreViewModel: FirebaseFirestoreViewModel by lazy {
        ViewModelProvider(this)[FirebaseFirestoreViewModel::class.java]
    }

    private lateinit var binding: FragmentChatsBinding

    private lateinit var snapshotEvent: ListenerRegistration

    private lateinit var chatsAdapter: ChatsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate Fragment Layout
        binding = FragmentChatsBinding.inflate(inflater, container, false)

        // Initialize Chats RecyclerView
        with(binding) {
            chatsAdapter = ChatsAdapter { chat ->
                // Action of Lambda from Adapter
                val intent = Intent(context, MessagesActivity::class.java)

                // Set Chat as Read
                val signedUser = firebaseAuthViewModel.getCurrentUser()
                val data = mapOf(Constants.FIELD_UNREAD to Constants.MESSAGE_READ)
                if (signedUser != null) {
                    firebaseFirestoreViewModel.updateChatData(signedUser.uid, chat.recipientUserId, data)
                }

                // Send Recipient User as Extra Parameter
                val user = User(id = chat.recipientUserId, name = chat.recipientUserName, photo = chat.recipientUserPhoto)
                intent.putExtra(Constants.RECIPIENT_USER_EXTRAS_PARAMETER, user)

                // Open Message Activity
                startActivity(intent)
            }
            rvChats.adapter = chatsAdapter
            rvChats.layoutManager = LinearLayoutManager(context)
            // Add a Divider Between Chats
            rvChats.addItemDecoration(
                DividerItemDecoration(
                    context, LinearLayoutManager.VERTICAL
                )
            )
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        // Start Chats Listener
        initializeListeners()
    }

    override fun onDestroy() {
        super.onDestroy()

        // Finish Chats Listener
        snapshotEvent.remove()
    }

    private fun initializeListeners() {
        // Get Sender User
        val senderUser = firebaseAuthViewModel.getCurrentUser()

        if (senderUser != null) {
            // Start Listener Event (Get All New Chats from Firestore Database)
            snapshotEvent = firebaseFirestoreViewModel.getAllChatsListener(
                senderUser.uid

            // Listener Action
            ).addSnapshotListener { querySnapshot, error ->
                // Get All New Chats and Send to a List
                val chatsList = mutableListOf<Chat>()
                val documents = querySnapshot?.documents
                documents?.forEach { documentSnapshot ->
                    // Convert DocumentSnapshot to Chat Object
                    val chat = documentSnapshot.toObject(Chat::class.java)
                    if (chat != null) {
                        chatsList.add(chat)
                    }
                }

                // Populate RecyclerView List
                if (chatsList.isNotEmpty()) {
                    chatsAdapter.populateList(chatsList)
                }
            }
        }
    }
}