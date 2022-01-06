package com.jera.justtalk.model

import com.google.gson.annotations.SerializedName

data class Message(
    @SerializedName("username") var username: String?,
    @SerializedName("storageReference") var storageReference: String?,
    @SerializedName("uid") var uid: String?,
    @SerializedName("message") var message: String?
) {
    constructor() : this(
        "",
        "",
        "",
        ""
    )
}
