package ir.behnoudsh.aroundus.data.model.venue

import com.google.gson.annotations.SerializedName

class VenueDetail(

    @SerializedName("id")
    val id: String? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("location")
    val location: Location? = null,

    @SerializedName("canonicalUrl")
    val canonicalUrl: String? = null,

    @SerializedName("categories")
    val categories: List<Category?>? = null,

    @SerializedName("shortUrl")
    val shortUrl: String? = null,

    @SerializedName("timeZone")
    val timeZone: String? = null

)