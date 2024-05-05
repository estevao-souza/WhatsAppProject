package com.estevaosouza.whatsappproject.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Chat(
    val signedUserId: String = "",
    val recipientUserId: String = "",
    val recipientUserPhoto: String = "",
    val recipientUserName: String = "",
    val lastMessage: String = "",
    val unread: String = "",
    @ServerTimestamp
    val date: Date? = null
)