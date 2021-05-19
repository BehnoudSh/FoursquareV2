package ir.behnoudsh.aroundus.data.model.venues

import com.google.gson.annotations.SerializedName

data class Venue(
    @SerializedName("id")
    val id: String = "",

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("location")
    val location: Location? = null,

    @SerializedName("categories")
    val categories: List<Category>? = null
)