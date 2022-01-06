package com.jera.justtalk.firebase

import com.google.firebase.auth.FirebaseAuth
import com.jera.justtalk.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class UserRepository {

    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    fun getCurrentUser(): Flow<APIState<User?>> {
        return flow {
            try {
                emit(
                    APIState.Success(
                        firebaseAuth.currentUser?.let { firebaseUser ->
                            User.mapFromFirebaseUser(firebaseUser)
                        }
                    )
                )
            } catch (e: Exception) {
                emit(APIState.Error(e))
            }
        }.flowOn(Dispatchers.IO)
    }
}
