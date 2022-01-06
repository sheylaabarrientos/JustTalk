package com.jera.justtalk.registerfeature

import android.content.SharedPreferences
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.jera.justtalk.firebase.APIState
import com.jera.justtalk.firebase.AuthRepository
import com.jera.justtalk.firebase.StorageRepository
import com.jera.justtalk.firebase.UserRepository
import com.jera.justtalk.model.User
import com.jera.justtalk.model.UserRequest
import com.jera.justtalk.ui.utils.Constants
import com.jera.justtalk.ui.utils.ViewState
import com.jera.justtalk.ui.utils.isFormattedEmail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository,
    private val storageRepository: StorageRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    val registerState = MutableStateFlow<ViewState>(ViewState.Initial)
    val sharedPrefWriteState = MutableStateFlow<ViewState>(ViewState.Initial)
    private val user: UserRequest = UserRequest()
    private var currentUser: User? = User()

    fun isUserNameValid(): Boolean {
        val userNameSplit = user.name?.trim()?.split(" ")
        return (userNameSplit != null && userNameSplit.size >= 2)
    }

    suspend fun isEmailValid(): Boolean {
        val formattedEmail = user.email?.lowercase()?.trim()
        var emailNotUsed = false
        if (formattedEmail != null) {
            repository.verifyUserAlreadyExist(formattedEmail).collect { state ->
                when (state) {
                    is APIState.Success -> {
                        emailNotUsed = state.data
                    }
                    is APIState.Error -> {
                    }
                }
            }
        }
        return formattedEmail?.isFormattedEmail() == true && emailNotUsed
    }

    fun isPasswordValid(): Boolean {
        val password = user.password
        return if (password != null) {
            password.length >= 6
        } else {
            false
        }
    }

    fun updateUsername(userName: String) {
        val formattedUserName = userName.trim()
        user.name = formattedUserName
    }

    fun updateEmail(email: String) {
        val formattedEmail = email.lowercase().trim()
        user.email = formattedEmail
    }

    fun updatePassword(
        firstDigit: String,
        secondDigit: String,
        thirdDigit: String,
        forthDigit: String,
        fifthDigit: String,
        sixthDigit: String,
        seventhDigit: String
    ) {
        val password =
            firstDigit + secondDigit + thirdDigit + forthDigit + fifthDigit + sixthDigit + seventhDigit
        user.password = password
    }

    fun updateImage(image: Uri) {
        user.photoUrl = image
    }

    fun registerUser() {
        viewModelScope.launch {
            registerState.value = ViewState.Loading
            repository.registerUser(user.email.toString(), user.password.toString())
                .collect { state ->
                    when (state) {
                        is APIState.Success -> {
                            if (state.data != null) {
                                uploadPhoto(state.data)
                            } else {
                                registerState.value = ViewState.Error
                            }
                        }
                        is APIState.Error -> {
                            registerState.value = ViewState.Error
                        }
                    }
                }
        }
    }

    private fun uploadPhoto(uid: String) {
        viewModelScope.launch {
            user.photoUrl?.let { uri ->
                storageRepository.uploadUserPhoto(uid, uri).collect { state ->
                    when (state) {
                        is APIState.Success -> {
                            updateUser()
                        }
                        is APIState.Error -> {
                            registerState.value = ViewState.Error
                        }
                    }
                }
            }
        }
    }

    private fun updateUser() {
        viewModelScope.launch {
            repository.updateUserNameAndPhoto(user).collect { state ->
                when (state) {
                    is APIState.Success -> {
                        registerState.value = ViewState.Success
                    }
                    is APIState.Error -> {
                        registerState.value = ViewState.Error
                    }
                }
            }
        }
    }

    fun getCurrentUser() {
        if (currentUser?.uid == "") {
            viewModelScope.launch {
                userRepository.getCurrentUser().collect { state ->
                    when (state) {
                        is APIState.Success -> {
                            currentUser = state.data
                            getUserPhotoBy()
                        }
                        is APIState.Error -> {
                            sharedPrefWriteState.value = ViewState.Error
                        }
                    }
                }
            }
        }
    }

    private fun getUserPhotoBy() {
        viewModelScope.launch {
            storageRepository.retrieveUserPhoto(currentUser?.uid).collect { state ->
                when (state) {
                    is APIState.Success -> {
                        currentUser?.storageReference = state.data
                        sharedPrefWriteState.value = ViewState.Loading
                    }
                    is APIState.Error -> {
                        sharedPrefWriteState.value = ViewState.Error
                    }
                }
            }
        }
    }

    fun writeOnSharedPrefs(sharedPreferences: SharedPreferences) {
        try {
            val gson = Gson()
            val objectToJson = gson.toJson(currentUser)
            sharedPreferences.edit().putString(Constants.SHARED_PREFERENCES_NAME, objectToJson)
                .apply()
            sharedPrefWriteState.value = ViewState.Success
        } catch (e: Exception) {
            sharedPrefWriteState.value = ViewState.Error
        }
    }
}
