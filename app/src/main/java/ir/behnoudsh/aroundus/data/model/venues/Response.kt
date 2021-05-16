package ir.behnoudsh.aroundus.data.model.venues

import com.google.gson.annotations.SerializedName

data class Response(
    @SerializedName("headerLocation")
    val headerLocation: String? = null,

    @SerializedName("headerFullLocation")
    val headerFullLocation: String? = null,

    @SerializedName("headerLocationGranularity")
    val headerLocationGranularity: String? = null,

    @SerializedName("totalResults")
    val totalResults: Int? = null,

    @SerializedName("groups")
    val groups: List<Group>? = null
)