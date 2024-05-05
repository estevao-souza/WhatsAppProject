package com.estevaosouza.whatsappproject.util

object Constants {

    const val USERS_COLLECTION_NAME = "users"
    const val MESSAGES_COLLECTION_NAME = "messages"
    const val CHATS_COLLECTION_NAME = "chats"
    const val LAST_CHATS_COLLECTION_NAME = "chats"
    const val PHOTOS_REFERENCE_NAME = "photos"
    const val FIELD_NAME = "name"
    const val FIELD_RECIPIENT_USER_NAME = "recipientUserName"
    const val FIELD_PHOTO = "photo"
    const val FIELD_RECIPIENT_USER_PHOTO = "recipientUserPhoto"
    const val FIELD_DATE = "date"
    const val FIELD_UNREAD = "unread"
    const val ALLOWED_IMG_TYPES = "image/*"
    const val PROFILE_PHOTO_FILE = "profile.jpg"
    const val PROFILE_PHOTO_URL = "https://firebasestorage.googleapis.com/v0/b/whatsappproject-6d640.appspot.com/o/photos%2FgenericProfilePhoto%2Fprofile.jpeg?alt=media&token=6e66fb32-47e9-48ca-8441-36a65ddf4dfc"
    const val SENDER_MESSAGE_TYPE = 0
    const val RECIPIENT_MESSAGE_TYPE = 1
    const val MESSAGE_UNREAD = "1"
    const val MESSAGE_READ = "0"
    const val UNREAD_MESSAGE_COLOR = "#0F8068"
    const val RECIPIENT_USER_EXTRAS_PARAMETER = "recipientUser"

    const val REGISTER_TOOLBAR_TEXT = "Create an account"
    const val UPDATE_ACCOUNT_TOOLBAR_TEXT = "Update profile"

    const val CHAT_TAB_TEXT = "Chat"
    const val CONTACTS_TAB_TEXT = "Contacts"

    const val SIGNOUT_BOX_TITLE = "Sign Out"
    const val SIGNOUT_BOX_TEXT = "Are you sure want to sign out?"
    const val SIGNOUT_CONFIRM_TEXT = "Sign out"
    const val SIGNOUT_CANCEL_TEXT = "Cancel"

    const val FILL_NAME_FIELD_MSG = "Please enter your name"
    const val WEAK_PASSWORD_MSG = "Password is too weak"
    const val EXISTENT_EMAIL_MSG = "E-mail already in use"
    const val INVALID_CREDENTIALS_MSG = "Invalid e-mail or password"
    const val SIGNUP_SUCCESSFUL_MSG = "Sign up completed successfully"
    const val SIGNUP_FAILURE_MSG = "Error signing up"
    const val SIGNIN_SUCCESSFUL_MSG = "Sign in was successful"
    const val INVALID_EMAIL_MSG = "E-mail not found"
    const val NO_PROFILE_USER_DATA_MSG = "No user data found"
    const val GALLERY_PERMISSION_DENIED_MSG = "You don't have permission to access gallery"
    const val NO_SELECTED_IMG_MSG = "No image selected"
    const val UPDATE_PROFILE_PHOTO_SUCCESS_MSG = "Profile photo uploaded successfully"
    const val UPDATE_PROFILE_PHOTO_FAIL_MSG = "Error uploading profile photo"
    const val UPDATE_USER_PROFILE_SUCCESS_MSG = "User profile updated successfully"
    const val UPDATE_USER_PROFILE_FAIL_MSG = "Error updating user profile"
    const val SEND_CHAT_MESSAGE_FAIL_MSG = "Error sending message"
    const val GET_MESSAGES_FAIL_MSG = "Error retrieving messages"
    const val SAVE_CHAT_FAIL_MSG = "Error saving chat"

    const val FILL_NAME_FIELD_ERROR = "Please enter your name"
    const val FILL_EMAIL_FIELD_ERROR = "Please enter your e-mail"
    const val FILL_PASSWORD_FIELD_ERROR = "Please enter your password"
    const val PASSWORDS_DONT_MATCH_ERROR = "Passwords do not match"
}