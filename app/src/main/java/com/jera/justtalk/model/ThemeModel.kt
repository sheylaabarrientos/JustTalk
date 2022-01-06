package com.jera.justtalk.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ThemeModel(
    @SerializedName("title") var title: String,
    @SerializedName("totalsubscribers") var totalsubscribers: Int?,
    @SerializedName("bannerurl") var bannerurl: String
) : Serializable {
    constructor() : this("", null, "")
}
