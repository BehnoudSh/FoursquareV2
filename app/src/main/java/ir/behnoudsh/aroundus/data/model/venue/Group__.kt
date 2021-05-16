package ir.behnoudsh.aroundus.data.model.venue

import com.google.gson.annotations.SerializedName

data class Group__(
    @SerializedName("type")
    val type: String? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("count")
    val count: Int? = null,

    @SerializedName("items")
    val items: List<Any>? = null
)