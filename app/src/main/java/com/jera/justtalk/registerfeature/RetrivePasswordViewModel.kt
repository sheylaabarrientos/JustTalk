package com.jera.justtalk.registerfeature

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jera.justtalk.firebase.APIState
import com.jera.justtalk.firebase.AuthRepository
import com.jera.justtalk.ui.utils.ViewState
import com.jera.justtalk.ui.utils.isFormattedEmail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class RetrivePasswordViewModel(private val repository: AuthRepository) : ViewModel() {

    private var email: String? = null
    private val retriveStateFlow = MutableStateFlow<ViewState>(ViewState.Initial)
    val retriveState: StateFlow<ViewState> = retriveStateFlow

    suspend fun isEmailValid(): Boolean {
        val formattedEmail = email?.trim()
        var emailUsed = false
        if (formattedEmail != null) {
            repository.verifyUserAlreadyExist(formattedEmail).collect { state ->
                when (state) {
                    is APIState.Success -> {
                        emailUsed = !state.data
                    }
                    is APIState.Error -> {
                    }
                }
            }
        }
        return formattedEmail?.isFormattedEmail() == true && emailUsed
    }

    fun retrievePassword() {
        viewModelScope.launch {
            retriveStateFlow.value = ViewState.Loading
            val email = email
            if (email != null) {
                repository.retrievePassword(email).collect {
                    when (it) {
                        is APIState.Success -> {
                            retriveStateFlow.value = ViewState.Success
                        }
                        is APIState.Error -> {
                            retriveStateFlow.value = ViewState.Error
                        }
                    }
                }
            } else {
                retriveStateFlow.value = ViewState.Error
            }
        }
    }

    fun updateEmail(email: String) {
        val formattedEmail = email.trim()
        this.email = formattedEmail
    }
}
