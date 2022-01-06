package com.jera.justtalk.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.jera.justtalk.model.UserRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    suspend fun registerUser(email: String, password: String): Flow<APIState<String?>> {
        return flow {
            try {
                val firebaseResult = firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .await().user?.uid
                emit(APIState.Success(firebaseResult))
            } catch (e: Exception) {
                emit(APIState.Error(e))
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun verifyUserAlreadyExist(email: String): Flow<APIState<Boolean>> {
        return flow {
            try {
                val isNewUser = firebaseAuth.fetchSignInMethodsForEmail(email).await()
                val verificationResponse = isNewUser.signInMethods?.isEmpty() == true
                emit(APIState.Success(verificationResponse))
            } catch (e: Exception) {
                emit(APIState.Error(e))
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun updateUserNameAndPhoto(user: UserRequest): Flow<APIState<Unit>> {
        return flow {
            try {
                val profileUpdate = UserProfileChangeRequest.Builder().setDisplayName(user.name)
                    .setPhotoUri(user.photoUrl).build()
                val currentUser = firebaseAuth.currentUser
                currentUser?.updateProfile(profileUpdate)?.await()
                emit(APIState.Success(Unit))
            } catch (e: Exception) {
                emit(APIState.Error(e))
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun loginWithEmailAndPassword(
        email: String?,
        password: String?
    ): Flow<APIState<Unit>> {
        return flow {
            try {
                if (email != null && password != null) {
                    firebaseAuth.signInWithEmailAndPassword(email, password).await()
                }
                emit(APIState.Success(Unit))
            } catch (e: Exception) {
                emit(APIState.Error(e))
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun retrievePassword(email: String): Flow<APIState<Unit>> {
        return flow {
            try {
                firebaseAuth.sendPasswordResetEmail(email).await()
                emit(APIState.Success(Unit))
            } catch (e: Exception) {
                emit(APIState.Error(e))
            }
        }.flowOn(Dispatchers.IO)
    }
}
