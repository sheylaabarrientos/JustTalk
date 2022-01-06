package com.jera.justtalk.chatfeature

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import com.jera.justtalk.firebase.APIState
import com.jera.justtalk.firebase.FirebaseFirestoreRepository
import com.jera.justtalk.firebase.StorageRepository
import com.jera.justtalk.model.Message
import com.jera.justtalk.model.User
import com.jera.justtalk.ui.utils.Constants
import com.jera.justtalk.ui.utils.ViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class ChatViewModel(
    private val storageRepository: StorageRepository,
    private val firebaseFirestore: FirebaseFirestoreRepository
) : ViewModel() {

    val chatStateFlow = MutableStateFlow<ViewState>(ViewState.Initial)

    var currentUser: User? = null
    private var message: Message = Message()

    fun createChatRecyclerOptions(collectionPath: String): Flow<FirestoreRecyclerOptions<Message>> {
        return flow {
            chatStateFlow.value = ViewState.Loading
            firebaseFirestore.createChatRecyclerOptions(collectionPath).collect { state ->
                when (state) {
                    is APIState.Success -> {
                        chatStateFlow.value = ViewState.Success
                        emit(state.data)
                    }
                    is APIState.Error -> {
                        chatStateFlow.value = ViewState.Error
                    }
                }
            }
        }.flowOn(Dispatchers.IO)
    }

    fun getReferenceFromUrl(url: String): Flow<StorageReference?> {
        return flow {
            storageRepository.getReferenceFromUrl(url).collect {
                when (it) {
                    is APIState.Success -> {
                        emit(it.data)
                    }
                    is APIState.Error -> {
                    }
                }
            }
        }.flowOn(Dispatchers.IO)
    }

    fun sendMessage(collectionPath: String) {
        viewModelScope.launch {
            chatStateFlow.value = ViewState.Loading
            firebaseFirestore.sendMessage(collectionPath, message).collect {
                when (it) {
                    is APIState.Success -> {
                        chatStateFlow.value = ViewState.Success
                    }
                    is APIState.Error -> {
                        chatStateFlow.value = ViewState.Error
                    }
                }
            }
        }
    }

    fun updateUser(sharedPrefs: SharedPreferences) {
        currentUser = getUserFromPreferences(sharedPrefs)
    }

    fun prepareMessage() {
        message.username = currentUser?.name
        message.uid = currentUser?.uid
        message.storageReference = currentUser?.storageReference.toString()
    }

    fun updateMessageText(insertedMessage: String) {
        message.message = insertedMessage.trim()
    }

    fun isMessageNull(): Boolean {
        return message.message.isNullOrEmpty()
    }

    private fun getUserFromPreferences(sharedPreferences: SharedPreferences): User? {
        val gson = Gson()
        val json = sharedPreferences.getString(Constants.SHARED_PREFERENCES_NAME, "")
        return gson.fromJson(json, User::class.java)
    }
}
