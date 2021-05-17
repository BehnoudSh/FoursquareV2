package ir.behnoudsh.aroundus.data.api

import ir.behnoudsh.aroundus.App
import ir.behnoudsh.aroundus.di.component.DiComponent
import javax.inject.Inject

class ApiHelper {

    @Inject
    lateinit var apiService: ApiService

    init {
        val apiComponent: DiComponent = App.diComponent
        apiComponent.inject(this)
    }

    fun getPlaces(lng_lat: String, offset: Int) = apiService.getPlaces(lng_lat, offset)

    fun getPlaceDetails(venueId: String) = apiService.getPlaceDetails(venueId)

}