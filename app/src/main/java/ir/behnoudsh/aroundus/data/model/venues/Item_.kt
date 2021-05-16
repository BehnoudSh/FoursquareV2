package ir.behnoudsh.aroundus.data.model.venues

import com.google.gson.annotations.SerializedName

data class Item_(
    @SerializedName("summary")
    val summary: String? = null,

    @SerializedName("type")
    val type: String? = null,

    @SerializedName("reasonName")
    val reasonName: String? = null
)