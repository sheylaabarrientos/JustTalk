package com.jera.justtalk.firebase

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await

class StorageRepository {

    private val firebaseStorage: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }

    suspend fun uploadUserPhoto(uid: String, image: Uri): Flow<APIState<Unit>> {
        return flow {
            try {
                val storageReference = firebaseStorage.getReference("profilePics/$uid")
                storageReference.putFile(image).await()
                emit(APIState.Success(Unit))
            } catch (e: Exception) {
                emit(APIState.Error(e))
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun retrieveUserPhoto(uid: String?): Flow<APIState<String>> {
        return flow {
            try {
                val storageRef = firebaseStorage.reference
                val pathReference = storageRef.child("profilePics/$uid").downloadUrl.await()
                emit(APIState.Success(pathReference.toString()))
            } catch (e: Exception) {
                emit(APIState.Error(e))
            }
        }.flowOn(Dispatchers.IO)
    }

    fun getReferenceFromUrl(url: String): Flow<APIState<StorageReference>> {
        return flow {
            try {
                val uri = firebaseStorage.getReferenceFromUrl(url)
                emit(APIState.Success(uri))
            } catch (e: Exception) {
                emit(APIState.Error(e))
            }
        }.flowOn(Dispatchers.IO)
    }
}
