package com.estevaosouza.whatsappproject.adapter

import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.estevaosouza.whatsappproject.databinding.ItemChatsBinding
import com.estevaosouza.whatsappproject.model.Chat
import com.estevaosouza.whatsappproject.util.Constants
import com.squareup.picasso.Picasso

class ChatsAdapter(
    // Generic Lambda to be Defined on Fragment
    private val onClick: (Chat) -> Unit
) : Adapter<ChatsAdapter.ChatsViewHolder>() {

    inner class ChatsViewHolder(
        private val binding: ItemChatsBinding
    ) : ViewHolder(binding.root) {

        fun bind(chat: Chat) {
            with(binding) {
                // Set Respective User Name
                textRecipientName.text = chat.recipientUserName

                // Set Respective Last Message
                textRecipientLastMessage.text = chat.lastMessage

                // Set Respective User Profile Photo
                Picasso.get()
                    .load(chat.recipientUserPhoto)
                    .into(imageRecipientPhoto)

                // Set Unread Message Layout
                if (chat.unread == Constants.MESSAGE_UNREAD) {
                    // Set Message in Color and Bold
                    textRecipientLastMessage.setTextColor(Color.parseColor(Constants.UNREAD_MESSAGE_COLOR))
                    textRecipientLastMessage.setTypeface(null, Typeface.BOLD)

                    // Set Unread Message Image
                    imageUnreadMessages.isVisible = true
                }

                // Call Generic Lambda When Respective Contact is Clicked (Click Event)
                clChatItem.setOnClickListener {
                    onClick(chat)
                }
            }
        }
    }

    // List with RecyclerView Data
    private var chatsList = emptyList<Chat>()

    fun populateList(list: List<Chat>) {
        chatsList = list
        // Notify Adapter that RecyclerView Data Has Changed
        notifyDataSetChanged()
    }

    // Create the Visualization
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = ItemChatsBinding.inflate(inflater, parent, false)

        return ChatsViewHolder(itemView)
    }

    // Vinculate Each List Item for Visualization
    override fun onBindViewHolder(holder: ChatsViewHolder, position: Int) {
        val chat = chatsList[position]
        holder.bind(chat)
    }

    // Return Data List Size
    override fun getItemCount(): Int {
        return  chatsList.size
    }
}