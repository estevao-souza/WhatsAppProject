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
import com.estevaosouza.whatsappproject.adapter.ContactsAdapter
import com.estevaosouza.whatsappproject.databinding.FragmentContactsBinding
import com.estevaosouza.whatsappproject.model.User
import com.estevaosouza.whatsappproject.util.Constants
import com.estevaosouza.whatsappproject.viewModel.FirebaseAuthViewModel
import com.estevaosouza.whatsappproject.viewModel.FirebaseFirestoreViewModel
import com.google.firebase.firestore.ListenerRegistration

class ContactsFragment : Fragment() {

    private val firebaseAuthViewModel: FirebaseAuthViewModel by lazy {
        ViewModelProvider(this)[FirebaseAuthViewModel::class.java]
    }

    private val firebaseFirestoreViewModel: FirebaseFirestoreViewModel by lazy {
        ViewModelProvider(this)[FirebaseFirestoreViewModel::class.java]
    }

    private lateinit var binding: FragmentContactsBinding

    private lateinit var snapshotEvent: ListenerRegistration

    private lateinit var contactsAdapter: ContactsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate Fragment Layout
        binding = FragmentContactsBinding.inflate(inflater, container, false)

        // Initialize Contacts RecyclerView
        with(binding) {
            contactsAdapter = ContactsAdapter { user ->
                // Action of Lambda from Adapter
                val intent = Intent(context, MessagesActivity::class.java)

                // Set Chat as Read
                val signedUser = firebaseAuthViewModel.getCurrentUser()
                val data = mapOf(Constants.FIELD_UNREAD to Constants.MESSAGE_READ)
                if (signedUser != null) {
                    firebaseFirestoreViewModel.updateChatData(signedUser.uid, user.id, data)
                }

                // Send Recipient User as Extra Parameter
                intent.putExtra(Constants.RECIPIENT_USER_EXTRAS_PARAMETER, user)

                // Open Message Activity
                startActivity(intent)
            }
            rvContacts.adapter = contactsAdapter
            rvContacts.layoutManager = LinearLayoutManager(context)
            // Add a Divider Between Contacts
            rvContacts.addItemDecoration(
                DividerItemDecoration(
                    context, LinearLayoutManager.VERTICAL
                )
            )
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        // Start Contacts Listener
        initializeListeners()
    }

    override fun onDestroy() {
        super.onDestroy()

        // Finish Contacts Listener
        snapshotEvent.remove()
    }

    private fun initializeListeners() {
        // Start Listener Event (Get All New Contacts from Firestore Database)
        snapshotEvent = firebaseFirestoreViewModel.getAllUsersListener()

            // Listener Action
            .addSnapshotListener { querySnapshot, error ->
                // Get All New Contacts and Send to a List
                val contactsList = mutableListOf<User>()
                val documents = querySnapshot?.documents
                documents?.forEach { documentSnapshot ->
                    // Convert DocumentSnapshot to Contact Object
                    val signedUser = firebaseAuthViewModel.getCurrentUser()
                    val userSnapshot = documentSnapshot.toObject(User::class.java)
                    if (signedUser != null && userSnapshot != null) {
                        // Check If Snapshot User is Not Signed User
                        if (signedUser.uid != userSnapshot.id) {
                            contactsList.add(userSnapshot)
                        }
                    }
                }

                // Populate RecyclerView List
                if (contactsList.isNotEmpty()) {
                    contactsAdapter.populateList(contactsList)
                }
            }
    }
}