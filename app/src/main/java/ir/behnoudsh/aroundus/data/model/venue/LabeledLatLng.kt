package ir.behnoudsh.aroundus.data.model.venue

import com.google.gson.annotations.SerializedName

data class LabeledLatLng(
    @SerializedName("label")
    val label: String? = null,

    @SerializedName("lat")
    val lat: Double? = null,

    @SerializedName("lng")
    val lng: Double? = null
)