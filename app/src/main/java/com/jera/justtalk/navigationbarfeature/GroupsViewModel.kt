package com.jera.justtalk.navigationbarfeature

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import com.jera.justtalk.firebase.APIState
import com.jera.justtalk.firebase.FirebaseFirestoreRepository
import com.jera.justtalk.firebase.StorageRepository
import com.jera.justtalk.model.ThemeModel
import com.jera.justtalk.model.User
import com.jera.justtalk.ui.utils.Constants
import com.jera.justtalk.ui.utils.ViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

class GroupsViewModel(
    private val storageRepository: StorageRepository,
    private val firestoreRepository: FirebaseFirestoreRepository
) : ViewModel() {

    val userStateFlow = MutableStateFlow<ViewState>(ViewState.Initial)
    var currentUser: User? = User()

    fun createGroupsRecyclerOptions(collectionPath: String): Flow<FirestoreRecyclerOptions<ThemeModel>> {
        return flow {
            userStateFlow.value = ViewState.Loading
            firestoreRepository.createGroupsRecyclerOptions(collectionPath).collect { state ->
                when (state) {
                    is APIState.Success -> {
                        userStateFlow.value = ViewState.Success
                        emit(state.data)
                    }
                    is APIState.Error -> {
                        userStateFlow.value = ViewState.Error
                    }
                }
            }
        }.flowOn(Dispatchers.IO)
    }

    fun getPhotoStorageReferenceFromUrl(url: String): Flow<StorageReference> {
        return flow {
            userStateFlow.value = ViewState.Loading
            storageRepository.getReferenceFromUrl(url).collect { state ->
                when (state) {
                    is APIState.Success -> {
                        userStateFlow.value = ViewState.Success
                        emit(state.data)
                    }
                    is APIState.Error -> {
                        userStateFlow.value = ViewState.Error
                    }
                }
            }
        }.flowOn(Dispatchers.IO)
    }

    private fun getUserFromPreferences(sharedPreferences: SharedPreferences): User? {
        val gson = Gson()
        val json = sharedPreferences.getString(Constants.SHARED_PREFERENCES_NAME, "")
        return gson.fromJson(json, User::class.java)
    }

    fun updateUser(sharedPrefs: SharedPreferences) {
        currentUser = getUserFromPreferences(sharedPrefs)
    }
}
