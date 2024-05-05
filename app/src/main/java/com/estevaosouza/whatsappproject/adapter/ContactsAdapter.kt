package com.estevaosouza.whatsappproject.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.estevaosouza.whatsappproject.databinding.ItemContactsBinding
import com.estevaosouza.whatsappproject.model.User
import com.squareup.picasso.Picasso

class ContactsAdapter(
    // Generic Lambda to be Defined on Fragment
    private val onClick: (User) -> Unit
) : Adapter<ContactsAdapter.ContactsViewHolder>() {

    inner class ContactsViewHolder(
        private val binding: ItemContactsBinding
    ) : ViewHolder(binding.root) {

        fun bind(user: User) {
            with(binding) {
                // Set Respective User Name
                textNameContact.text = user.name

                // Set Respective User Profile Photo
                Picasso.get()
                    .load(user.photo)
                    .into(imagePhotoContact)

                // Call Generic Lambda When Respective Contact is Clicked (Click Event)
                clContactItem.setOnClickListener {
                    onClick(user)
                }
            }
        }
    }

    // List with RecyclerView Data
    private var contactsList = emptyList<User>()

    fun populateList(list: List<User>) {
        contactsList = list
        // Notify Adapter that RecyclerView Data Has Changed
        notifyDataSetChanged()
    }

    // Create the Visualization
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = ItemContactsBinding.inflate(inflater, parent, false)

        return ContactsViewHolder(itemView)
    }

    // Vinculate Each List Item for Visualization
    override fun onBindViewHolder(holder: ContactsViewHolder, position: Int) {
        val user = contactsList[position]
        holder.bind(user)
    }

    // Return Data List Size
    override fun getItemCount(): Int {
        return contactsList.size
    }
}