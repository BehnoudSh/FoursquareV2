package ir.behnoudsh.aroundus.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "places")

data class FoursquarePlace(
    @PrimaryKey
    @ColumnInfo(name = "Id")
    var id: String?,
    @ColumnInfo(name = "Name")
    var name: String?,
    @ColumnInfo(name = "Address")
    var address: String?,
    @ColumnInfo(name = "Distance")
    var distance: Int?,
    @ColumnInfo(name = "Lat")
    var lat: String?,
    @ColumnInfo(name = "Long")
    var long: String?,
    @ColumnInfo(name = "Link")
    var link: String?
) {
    constructor() : this("", "", "", 0, "", "", "")
}
