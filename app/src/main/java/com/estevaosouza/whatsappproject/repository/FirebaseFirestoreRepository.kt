package com.estevaosouza.whatsappproject.repository

import com.estevaosouza.whatsappproject.model.Chat
import com.estevaosouza.whatsappproject.model.Message
import com.estevaosouza.whatsappproject.model.User
import com.estevaosouza.whatsappproject.util.Constants
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot

class FirebaseFirestoreRepository(private val firestore: FirebaseFirestore) {

    fun saveUser(user: User): Task<Void> {
        return firestore
            .collection(Constants.USERS_COLLECTION_NAME)
            .document(user.id)
            .set(user)
    }

    fun getUser(userId: String): Task<DocumentSnapshot> {
        return firestore
            .collection(Constants.USERS_COLLECTION_NAME)
            .document(userId)
            .get()
    }

    fun updateUser(userId: String, data: Map<String, String>): Task<Void> {
        return firestore
            .collection(Constants.USERS_COLLECTION_NAME)
            .document(userId)
            .update(data)
    }

    fun saveMessage(senderUserId: String, recipientUserId: String, message: Message): Task<DocumentReference> {
        return firestore
            .collection(Constants.MESSAGES_COLLECTION_NAME)
            .document(senderUserId)
            .collection(recipientUserId)
            .add(message)
    }

    fun saveChat(chat: Chat): Task<Void> {
        return firestore
            .collection(Constants.CHATS_COLLECTION_NAME)
            .document(chat.signedUserId)
            .collection(Constants.LAST_CHATS_COLLECTION_NAME)
            .document(chat.recipientUserId)
            .set(chat)
    }

    fun getChats(userId: String): Task<QuerySnapshot> {
        return firestore
            .collection(Constants.CHATS_COLLECTION_NAME)
            .document(userId)
            .collection(Constants.LAST_CHATS_COLLECTION_NAME)
            .get()
    }

    fun updateChat(recipientUserId: String, senderUserId: String, data: Map<String, String>): Task<Void> {
        return firestore
            .collection(Constants.CHATS_COLLECTION_NAME)
            .document(recipientUserId)
            .collection(Constants.LAST_CHATS_COLLECTION_NAME)
            .document(senderUserId)
            .update(data)
    }

    fun getChatsListener(userId: String): Query {
        return firestore
            .collection(Constants.CHATS_COLLECTION_NAME)
            .document(userId)
            .collection(Constants.LAST_CHATS_COLLECTION_NAME)
            .orderBy(Constants.FIELD_DATE, Query.Direction.DESCENDING)
    }

    fun getUsersListener(): Query {
        return firestore
            .collection(Constants.USERS_COLLECTION_NAME)
            .orderBy(Constants.FIELD_NAME, Query.Direction.ASCENDING)
    }

    fun getMessagesListener(senderUserId: String, recipientUserId: String): Query {
        return firestore
            .collection(Constants.MESSAGES_COLLECTION_NAME)
            .document(senderUserId)
            .collection(recipientUserId)
            .orderBy(Constants.FIELD_DATE, Query.Direction.ASCENDING)
    }
}