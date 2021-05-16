package ir.behnoudsh.aroundus.data.api

import io.reactivex.Single
import ir.behnoudsh.aroundus.BuildConfig
import ir.behnoudsh.aroundus.data.model.venue.ResponseVenue
import ir.behnoudsh.aroundus.data.model.venues.ResponseVenues
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET(
        "explore?client_id=" + BuildConfig.CLIENT_ID + "&client_secret=" + BuildConfig.CLIENT_SECRET + "&v=20190218&limit=20&intent=browse&radius=1000"
    )
    fun getPlaces(
        @Query("ll") lng_lat: String?,
        @Query("offset") offset: Int
    ): Single<ResponseVenues>?

    @GET(
        "{venue_id}?client_id=" + BuildConfig.CLIENT_ID + "&client_secret=" + BuildConfig.CLIENT_SECRET + "&v=20190218"
    )
    fun getPlaceDetails(
        @Path(
            value = "venue_id",
            encoded = true
        ) venueId: String?
    ): Single<ResponseVenue>?

}