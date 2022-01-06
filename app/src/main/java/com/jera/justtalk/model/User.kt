package com.jera.justtalk.model

import com.google.firebase.auth.FirebaseUser
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class User(
    @SerializedName("uid") var uid: String?,
    @SerializedName("name") var name: String?,
    @SerializedName("email") var email: String?,
    @SerializedName("storageReference") var storageReference: String?
) : Serializable {
    constructor() : this("", "", "", "")
    companion object {
        fun mapFromFirebaseUser(firebaseUser: FirebaseUser): User {
            return User(
                firebaseUser.uid,
                firebaseUser.displayName,
                firebaseUser.email,
                null
            )
        }
    }
}
