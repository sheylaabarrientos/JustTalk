package com.jera.justtalk.chatfeature

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.jera.justtalk.R
import com.jera.justtalk.databinding.ItemMessageBinding
import com.jera.justtalk.model.Message
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ChatAdapter(
    options: FirestoreRecyclerOptions<Message>,
    val chatViewModel: ChatViewModel
) : FirestoreRecyclerAdapter<Message, ChatAdapter.MessageViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return MessageViewHolder(
            ItemMessageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int, model: Message) {
        holder.bind(model)
    }

    inner class MessageViewHolder(val binding: ItemMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(message: Message) {
            chatViewModel.viewModelScope.launch {
                message.storageReference?.let { messageStorageReference ->
                    chatViewModel.getReferenceFromUrl(messageStorageReference)
                        .collect { firebaseStorageReference ->
                            Glide.with(itemView)
                                .load(firebaseStorageReference).circleCrop()
                                .into(binding.messageProfilePhotoImageView)
                        }
                }
            }
            binding.messageBodyTextView.text = message.message
            if (message.uid == chatViewModel.currentUser?.uid) {
                binding.containerLinearLayout.layoutDirection = View.LAYOUT_DIRECTION_RTL
                binding.messageUsernameTextView.text = itemView.context.getString(R.string.chat_me)
            } else {
                binding.containerLinearLayout.layoutDirection = View.LAYOUT_DIRECTION_LTR
                binding.messageUsernameTextView.text = message.username
            }
        }
    }
}
