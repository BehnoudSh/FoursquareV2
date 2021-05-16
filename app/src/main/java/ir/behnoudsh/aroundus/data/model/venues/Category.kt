package ir.behnoudsh.aroundus.data.model.venues

import com.google.gson.annotations.SerializedName

data class Category (
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("pluralName")
    val pluralName: String? = null,

    @SerializedName("shortName")
    val shortName: String? = null,

    @SerializedName("primary")
    val primary: Boolean? = null
)