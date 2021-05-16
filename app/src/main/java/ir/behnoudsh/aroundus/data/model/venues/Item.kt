package ir.behnoudsh.aroundus.data.model.venues

import com.google.gson.annotations.SerializedName

data class Item(
    @SerializedName("venue")
    val venue: Venue? = null,

    @SerializedName("referralId")
    val referralId: String? = null
)