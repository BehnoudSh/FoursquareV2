package ir.behnoudsh.aroundus.data.api

import ir.behnoudsh.aroundus.di.component.ApiComponent
import ir.behnoudsh.aroundus.di.component.DaggerApiComponent
import javax.inject.Inject

class ApiHelper {

    @Inject
    lateinit var apiService: ApiService

    init {
        val apiComponent: ApiComponent = DaggerApiComponent.create()
        apiComponent.inject(this)
    }

    fun getVenues(lng_lat: String, offset: Int) = apiService.getVenues(lng_lat, offset)

    fun getVenueDetails(venueId: String) = apiService.getVenueDetails(venueId)

}