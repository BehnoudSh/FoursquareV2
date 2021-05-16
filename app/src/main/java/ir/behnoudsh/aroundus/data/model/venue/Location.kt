package ir.behnoudsh.aroundus.data.model.venue

import com.google.gson.annotations.SerializedName

data class Location(
    @SerializedName("address")
    val address: String? = null,

    @SerializedName("lat")
    val lat: Double? = null,

    @SerializedName("lng")
    val lng: Double? = null,

    @SerializedName("labeledLatLngs")
    val labeledLatLngs: List<LabeledLatLng>? = null,

    @SerializedName("cc")
    val cc: String? = null,

    @SerializedName("city")
    val city: String? = null,

    @SerializedName("state")
    val state: String? = null,

    @SerializedName("country")
    val country: String? = null,

    @SerializedName("formattedAddress")
    val formattedAddress: List<String>? = null
)