package com.example.storyapp.Activity

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import com.example.storyapp.R
import com.example.storyapp.ViewModel.ViewModelFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.example.storyapp.response.ListStoryItem
import com.example.storyapp.data.UserPreference
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.location.LocationServices
import com.example.storyapp.databinding.ActivityMapsBinding
import com.example.storyapp.ViewModel.MainViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

val Context.MapdataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, CoroutineScope {


    private lateinit var binding: ActivityMapsBinding
    private lateinit var ggmap: GoogleMap


    private val mvm: MainViewModel by viewModels { ViewModelFactory(UserPreference.getInstance(MapdataStore),this)
    }
    lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        job = Job()
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mvm.cariUser().observe(this) { UserModel ->
            launch {
            mvm.getLocation(UserModel.token)
            }
            println("Token: " + UserModel.token)
        }
        mvm.liststory.observe(this) { data ->
            storiesset(data)
        }


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        ggmap = googleMap
        ggmap.uiSettings.isZoomControlsEnabled = true
        ggmap.uiSettings.isIndoorLevelPickerEnabled = true
        ggmap.uiSettings.isCompassEnabled = true
        ggmap.uiSettings.isMapToolbarEnabled = true
        val Jakarta = LatLng(-6.3, 106.82)
        ggmap.moveCamera(CameraUpdateFactory.newLatLng(Jakarta))
    }

    private fun storiesset(itemsItem: List<ListStoryItem>) {
            for (item in itemsItem) {
                val location = LatLng(item.lat.toDouble(), item.lon.toDouble())
                ggmap.addMarker(
                    MarkerOptions().position(location).title(item.name).snippet(item.description))
            }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.maps_type, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.normal_type -> {
                ggmap.mapType = GoogleMap.MAP_TYPE_NORMAL
                true
            }
            R.id.satellite_type -> {
                ggmap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                true
            }
            R.id.terrain_type -> {
                ggmap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                true
            }
            R.id.hybrid_type -> {
                ggmap.mapType = GoogleMap.MAP_TYPE_HYBRID
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}