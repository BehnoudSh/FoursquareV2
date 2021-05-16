package ir.behnoudsh.aroundus.data.model.venue

import com.google.gson.annotations.SerializedName

data class VenueResponse (
    @SerializedName("venue")
    val venue: VenueDetail? = null
)