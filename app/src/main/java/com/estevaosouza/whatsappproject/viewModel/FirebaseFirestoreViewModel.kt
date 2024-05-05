package com.estevaosouza.whatsappproject.viewModel

import androidx.lifecycle.ViewModel
import com.estevaosouza.whatsappproject.model.Chat
import com.estevaosouza.whatsappproject.model.Message
import com.estevaosouza.whatsappproject.model.User
import com.estevaosouza.whatsappproject.repository.FirebaseFirestoreRepository
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseFirestoreViewModel (
    private val repository: FirebaseFirestoreRepository
) : ViewModel() {

    constructor():this(FirebaseFirestoreRepository(FirebaseFirestore.getInstance()))

    fun saveUserToFirestore(
        user: User
    ) = repository.saveUser(user)

    fun getUserData(
        userId: String
    ) = repository.getUser(userId)

    fun updateUserData(
        userId: String, data: Map<String, String>
    ) = repository.updateUser(userId, data)

    fun saveMessageToFirestore(
        senderUserId: String, recipientUserId: String, message: Message
    ) = repository.saveMessage(senderUserId, recipientUserId, message)

    fun saveChatToFirestore(
        chat: Chat
    ) = repository.saveChat(chat)

    fun getAllChats(
        userId: String
    ) = repository.getChats(userId)

    fun updateChatData(
        recipientUserId: String, senderUserId: String, data: Map<String, String>
    ) = repository.updateChat(recipientUserId, senderUserId, data)

    fun getAllChatsListener(
        userId: String
    ) = repository.getChatsListener(userId)

    fun getAllUsersListener() = repository.getUsersListener()

    fun getAllMessagesListener(
        senderUserId: String, recipientUserId: String
    ) = repository.getMessagesListener(senderUserId, recipientUserId)
}