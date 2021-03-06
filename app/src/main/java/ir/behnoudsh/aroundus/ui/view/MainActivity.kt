package ir.behnoudsh.aroundus.ui.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import ir.behnoudsh.aroundus.R
import ir.behnoudsh.aroundus.data.model.LocationModel
import ir.behnoudsh.aroundus.data.room.FoursquarePlace
import ir.behnoudsh.aroundus.ui.adapter.CellClickListener
import ir.behnoudsh.aroundus.ui.adapter.PlacesAdapter
import ir.behnoudsh.aroundus.ui.viewmodel.MainViewModel
import ir.behnoudsh.aroundus.ui.viewmodel.ViewModelFactory
import ir.behnoudsh.aroundus.utils.GpsUtils
import ir.behnoudsh.aroundus.utils.Status
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), CellClickListener {

    private var isGPSEnabled = false
    lateinit var mainViewModel: MainViewModel
    private val placesAdapter = PlacesAdapter(this, ArrayList(), this)
    var isLoading = false

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase!!))
    }

    override fun onStart() {
        super.onStart()
        invokeLocationAction()
    }

    private fun setupUI() {
        rv_placesList.layoutManager = LinearLayoutManager(this)
        rv_placesList.addItemDecoration(
            DividerItemDecoration(
                rv_placesList.context,
                (rv_placesList.layoutManager as LinearLayoutManager).orientation
            )
        )
        rv_placesList.adapter = placesAdapter
        rv_placesList.setItemViewCacheSize(100)
        rv_placesList.setDrawingCacheEnabled(true)
        rv_placesList.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH)
        initScrollListener()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupUI()
        setupViewModel()
        setupObservers()

        GpsUtils(this).turnGPSOn(object : GpsUtils.OnGpsListener {
            override fun gpsStatus(isGPSEnable: Boolean) {
                this@MainActivity.isGPSEnabled = isGPSEnable
            }
        })

    }

    private fun renderList(places: MutableCollection<FoursquarePlace>) {
        for (item in places) {
            placesAdapter.placesList.add(item)
        }
        placesAdapter.notifyDataSetChanged()
    }

    private fun setupObservers() {
        mainViewModel.getPlaces().observe(this, {
            when (it.status) {
                Status.SUCCESS -> {
                    isLoading = false
                    pb_loading.visibility = View.GONE
                    it.data?.let { foursquarePlaces -> renderList(foursquarePlaces) }
                    ll_noResults.visibility = View.GONE
                }
                Status.ERROR -> {
                    isLoading = false
                    pb_loading.visibility = View.GONE
                    onSNACK(content, it.message.toString())
                    ll_noResults.visibility = View.GONE
                }
                Status.LOADING -> {
                    pb_loading.visibility = View.VISIBLE
                    ll_noResults.visibility = View.GONE
                }

                Status.EMPTY -> {
                    ll_noResults.visibility = View.VISIBLE
                    rv_placesList.visibility = View.GONE
                }
                Status.CLEAR -> {
                    placesAdapter.placesList.clear()
                    placesAdapter.notifyDataSetChanged()
                }
            }
        })

        mainViewModel.getPlaceDetails().observe(this, {

            when (it.status) {
                Status.SUCCESS -> {
                    val dialogFragment = PlaceDetailsDialog(it.data!!)
                    dialogFragment.show(supportFragmentManager, "placeDetails")
                    pb_loading.visibility = View.GONE

                }
                Status.ERROR -> {

                    Toast.makeText(this, "???????? ?????? ???? ???????????? ?????????????? ????????", Toast.LENGTH_LONG)
                        .show()

                }
                Status.LOADING -> {

                    pb_loading.visibility = View.VISIBLE

                }
                Status.EMPTY -> {
                }
                Status.CLEAR -> {
                }
            }


        })

        mainViewModel.getMessage().observe(this, {

            message.text = it


        })
    }

    private fun onSNACK(view: View, message: String) {
        val snackbar = Snackbar.make(
            view, message,
            Snackbar.LENGTH_INDEFINITE
        ).setAction("retry") {

            mainViewModel.loadMore()
            isLoading = true

        }
        val snackbarView = snackbar.view
        val textView =
            snackbarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
        textView.textSize = 14f
        snackbar.show()
    }

    private fun initScrollListener() {
        rv_placesList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoading) {
                    if (linearLayoutManager != null &&
                        linearLayoutManager.findLastCompletelyVisibleItemPosition() == rv_placesList.adapter!!.itemCount - 5
                    ) {
                        mainViewModel.loadMore()
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
        mainViewModel.getLocationData().observe(this, Observer {
            mainViewModel.locationChanged(LocationModel(it.longitude, it.latitude));
        })
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
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

            shouldShowRequestPermissionRationale() -> {
                message.text =
                    getString(R.string.permission_request)

                requestPermission()
            }
            else -> requestPermission()
        }
    }

    private fun requestPermission() {

        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_REQUEST
        )
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
        mainViewModel.fetchPlaceDetails(place)
    }
}

const val LOCATION_REQUEST = 100
const val GPS_REQUEST = 101