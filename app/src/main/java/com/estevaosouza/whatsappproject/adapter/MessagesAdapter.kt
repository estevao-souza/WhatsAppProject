package com.estevaosouza.whatsappproject.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.estevaosouza.whatsappproject.databinding.ItemMessageRecipientBinding
import com.estevaosouza.whatsappproject.databinding.ItemMessageSenderBinding
import com.estevaosouza.whatsappproject.model.Message
import com.estevaosouza.whatsappproject.util.Constants
import com.google.firebase.auth.FirebaseAuth

class MessagesAdapter : Adapter<ViewHolder>() {

    class SenderMessagesViewHolder(
        private val binding: ItemMessageSenderBinding
    ) : ViewHolder(binding.root) {

        fun bind(message: Message) {
            // Set Sender Message
            binding.textSenderMessage.text = message.message
        }

        // Inflate Layout
        companion object {
            fun inflateLayout(parent: ViewGroup) : SenderMessagesViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val itemView = ItemMessageSenderBinding.inflate(
                    inflater, parent, false
                )
                return SenderMessagesViewHolder(itemView)
            }
        }
    }

    class RecipientMessagesViewHolder(
        private val binding: ItemMessageRecipientBinding
    ) : ViewHolder(binding.root) {

        fun bind(message: Message) {
            // Set Recipient Message
            binding.textRecipientMessage.text = message.message
        }

        // Inflate Layout
        companion object {
            fun inflateLayout(parent: ViewGroup) : RecipientMessagesViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val itemView = ItemMessageRecipientBinding.inflate(
                    inflater, parent, false
                )
                return RecipientMessagesViewHolder(itemView)
            }
        }
    }

    // List with RecyclerView Data
    private var messagesList = emptyList<Message>()

    fun populateList(list: List<Message>) {
        messagesList = list
        // Notify Adapter that RecyclerView Data Has Changed
        notifyDataSetChanged()
    }

    // Distinguish Layout Messages Type (User or Recipient Messages)
    override fun getItemViewType(position: Int): Int {
        // Get Respective Message
        val message = messagesList[position]

        // Get Signed User ID
        val signedUserId = FirebaseAuth.getInstance().currentUser?.uid.toString()

        // Check If Message is "To User" or "From User"
        return if (signedUserId == message.userId) {
            Constants.SENDER_MESSAGE_TYPE
        } else {
            Constants.RECIPIENT_MESSAGE_TYPE
        }
    }

    // Create the Visualization
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == Constants.SENDER_MESSAGE_TYPE) {
            SenderMessagesViewHolder.inflateLayout(parent)
        } else {
            RecipientMessagesViewHolder.inflateLayout(parent)
        }
    }

    // Vinculate Each List Item for Visualization
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messagesList[position]
        // Show Different Layout Messages Depending on ViewHolder Type
        when (holder) {
            is SenderMessagesViewHolder -> holder.bind(message)
            is RecipientMessagesViewHolder -> holder.bind(message)
        }
    }

    // Return Data List Size
    override fun getItemCount(): Int {
        return messagesList.size
    }
}