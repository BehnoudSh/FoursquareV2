package ir.behnoudsh.aroundus.data.model.venue

import com.google.gson.annotations.SerializedName

data class ResponseVenue(
    @SerializedName("response")
    val response: VenueResponse? = null
)