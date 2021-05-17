package ir.behnoudsh.aroundus.ui.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import ir.behnoudsh.aroundus.R
import ir.behnoudsh.aroundus.data.room.FoursquarePlace
import ir.behnoudsh.aroundus.ui.adapter.CellClickListener
import ir.behnoudsh.aroundus.ui.adapter.PlacesAdapter
import ir.behnoudsh.aroundus.ui.viewmodel.MainViewModel
import ir.behnoudsh.aroundus.ui.viewmodel.ViewModelFactory
import ir.behnoudsh.aroundus.utils.GpsUtils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), CellClickListener {

    private lateinit var placesViewModel: MainViewModel
    private var isGPSEnabled = false
    lateinit var mainViewModel: MainViewModel
    val placesAdapter = PlacesAdapter(this, ArrayList(), this)
    var isLoading = false

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase!!))
    }

    override fun onStart() {
        super.onStart()
        invokeLocationAction()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupViewModel()
        GpsUtils(this).turnGPSOn(object : GpsUtils.OnGpsListener {
            override fun gpsStatus(isGPSEnable: Boolean) {
                this@MainActivity.isGPSEnabled = isGPSEnable
            }
        })
        initRecyclerView()

    }

    fun initRecyclerView() {
        rv_placesList.layoutManager = LinearLayoutManager(this)
        rv_placesList.adapter = placesAdapter
        initScrollListener()
    }

    private fun initScrollListener() {
        rv_placesList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoading) {
                    if (linearLayoutManager != null &&
                        linearLayoutManager.findLastCompletelyVisibleItemPosition() == rv_placesList.adapter!!.itemCount - 1
                    ) {
//                        placesViewModel.loadMore()
                        isLoading = true
                    }
                }
            }
        })
    }

    private fun setupViewModel() {
        val factory = ViewModelFactory()
        mainViewModel = ViewModelProviders.of(this, factory)
            .get(MainViewModel::class.java)

    }

    private fun startLocationUpdate() {
        placesViewModel.getLocationData().observe(this, Observer {
            // placesViewModel.locationChanged(LocationModel(it.longitude, it.latitude));
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GPS_REQUEST) {
                isGPSEnabled = true
                invokeLocationAction()
            }
        }
    }

    private fun invokeLocationAction() {
        when {
            !isGPSEnabled -> message.text = getString(R.string.enable_gps)

            isPermissionsGranted() -> startLocationUpdate()

            shouldShowRequestPermissionRationale() -> message.text =
                getString(R.string.permission_request)

            else -> ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_REQUEST
            )
        }
    }


    private fun isPermissionsGranted() =
        ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

    private fun shouldShowRequestPermissionRationale() =
        ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) && ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_REQUEST -> {
                invokeLocationAction()
            }
        }
    }

    override fun onCellClickListener(place: FoursquarePlace) {

//        placesViewModel.getPlaceDetails(place)

    }
}

const val LOCATION_REQUEST = 100
const val GPS_REQUEST = 101