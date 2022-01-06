package com.jera.justtalk.firebase

import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.jera.justtalk.model.Message
import com.jera.justtalk.model.ThemeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await

class FirebaseFirestoreRepository {

    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    suspend fun createRecyclerOptions(collectionPath: String): Flow<APIState<FirestoreRecyclerOptions<ThemeModel>>> {
        return flow {
            try {
                val query = firestore.collection(collectionPath)
                val options =
                    FirestoreRecyclerOptions.Builder<ThemeModel>()
                        .setQuery(query, ThemeModel::class.java)
                        .build()
                emit(APIState.Success(options))
            } catch (e: Exception) {
                emit(APIState.Error(e))
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun createHighlightRecyclerOptions(collectionPath: String): Flow<APIState<FirestoreRecyclerOptions<ThemeModel>>> {
        return flow {
            try {
                val query = firestore.collection(collectionPath)
                    .orderBy("totalsubscribers")
                val options =
                    FirestoreRecyclerOptions.Builder<ThemeModel>()
                        .setQuery(query, ThemeModel::class.java)
                        .build()
                emit(APIState.Success(options))
            } catch (e: Exception) {
                emit(APIState.Error(e))
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun createGroupsRecyclerOptions(collectionPath: String): Flow<APIState<FirestoreRecyclerOptions<ThemeModel>>> {
        return flow {
            try {
                val query = firestore.collection(collectionPath)
                    .orderBy("totalsubscribers", Query.Direction.DESCENDING).limit(3)
                val options =
                    FirestoreRecyclerOptions.Builder<ThemeModel>()
                        .setQuery(query, ThemeModel::class.java)
                        .build()
                emit(APIState.Success(options))
            } catch (e: Exception) {
                emit(APIState.Error(e))
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun createChatRecyclerOptions(collectionPath: String): Flow<APIState<FirestoreRecyclerOptions<Message>>> {
        return flow {
            try {
                val query = firestore.collection(collectionPath).orderBy("timeStamp")
                val options =
                    FirestoreRecyclerOptions.Builder<Message>()
                        .setQuery(query, Message::class.java)
                        .build()
                emit(APIState.Success(options))
            } catch (e: Exception) {
                emit(APIState.Error(e))
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun sendMessage(collectionPath: String, message: Message): Flow<APIState<Unit>> {
        return flow {
            try {
                val docuId: String = firestore.collection(collectionPath).document().getId()
                val firebaseRef = firestore.collection(collectionPath).document("$docuId")

                firebaseRef.set(message).await()
                firebaseRef.update("timeStamp", FieldValue.serverTimestamp()).await()
                emit(APIState.Success(Unit))
            } catch (e: Exception) {
                emit(APIState.Error(e))
            }
        }.flowOn(Dispatchers.IO)
    }
}
