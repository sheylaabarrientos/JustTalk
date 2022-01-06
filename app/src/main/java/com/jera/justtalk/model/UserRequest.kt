package com.jera.justtalk.model

import android.net.Uri
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UserRequest(
    @SerializedName("name") var name: String? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("password") var password: String? = null,
    @SerializedName("photoUrl") var photoUrl: Uri? = null
) : Serializable
