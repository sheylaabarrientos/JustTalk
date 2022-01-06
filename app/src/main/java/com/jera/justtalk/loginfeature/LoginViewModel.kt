package com.jera.justtalk.loginfeature

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.jera.justtalk.firebase.APIState
import com.jera.justtalk.firebase.AuthRepository
import com.jera.justtalk.firebase.StorageRepository
import com.jera.justtalk.firebase.UserRepository
import com.jera.justtalk.model.User
import com.jera.justtalk.ui.utils.Constants
import com.jera.justtalk.ui.utils.ViewState
import com.jera.justtalk.ui.utils.isFormattedEmail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: AuthRepository,
    private val userRepository: UserRepository,
    private val storageRepository: StorageRepository
) : ViewModel() {

    private val loginStateFlow = MutableStateFlow<ViewState>(ViewState.Initial)
    val loginState: StateFlow<ViewState> = loginStateFlow
    private var currentUser: User? = User()
    private var emailInserted: String? = null
    private var passwordInserted: String? = null

    fun userLoginWithEmailAndPassword(sharedPreferences: SharedPreferences) {
        viewModelScope.launch {
            loginStateFlow.value = ViewState.Loading
            if (isEmailValid()) {
                repository.loginWithEmailAndPassword(emailInserted, passwordInserted).collect { state ->
                    when (state) {
                        is APIState.Success -> {
                            getCurrentUser(sharedPreferences)
                        }
                        is APIState.Error -> {
                            loginStateFlow.value = ViewState.Error
                        }
                    }
                }
            } else {
                loginStateFlow.value = ViewState.Error
            }
        }
    }

    private fun getCurrentUser(sharedPreferences: SharedPreferences) {
        if (currentUser?.uid == "") {
            loginStateFlow.value = ViewState.Loading
            viewModelScope.launch {
                userRepository.getCurrentUser().collect { state ->
                    when (state) {
                        is APIState.Success -> {
                            currentUser = state.data
                            getUserPhotoBy(sharedPreferences)
                        }
                        is APIState.Error -> {
                            loginStateFlow.value = ViewState.Error
                        }
                    }
                }
            }
        }
    }

    private fun getUserPhotoBy(sharedPreferences: SharedPreferences) {
        viewModelScope.launch {
            storageRepository.retrieveUserPhoto(currentUser?.uid).collect { state ->
                when (state) {
                    is APIState.Success -> {
                        currentUser?.storageReference = state.data
                        writeOnSharedPrefs(sharedPreferences)
                    }
                    is APIState.Error -> {
                        loginStateFlow.value = ViewState.Error
                    }
                }
            }
        }
    }

    private fun writeOnSharedPrefs(sharedPreferences: SharedPreferences) {
        try {
            val gson = Gson()
            val objectToJson = gson.toJson(currentUser)
            sharedPreferences.edit().putString(Constants.SHARED_PREFERENCES_NAME, objectToJson)
                .apply()
            loginStateFlow.value = ViewState.Success
        } catch (e: Exception) {
            loginStateFlow.value = ViewState.Error
        }
    }

    fun updateEmail(email: String?) {
        loginStateFlow.value = ViewState.Initial
        val formattedEmail = email?.lowercase()?.trim()
        emailInserted = formattedEmail
    }

    fun updatePassword(password: String) {
        loginStateFlow.value = ViewState.Initial
        passwordInserted = password
    }

    private fun isEmailValid(): Boolean {
        val email = emailInserted
        if (email != null) {
            return email.isFormattedEmail()
        }
        return false
    }
}
