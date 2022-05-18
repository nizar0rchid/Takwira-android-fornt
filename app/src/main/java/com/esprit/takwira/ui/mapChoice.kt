package com.esprit.takwira.ui

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.esprit.takwira.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap.OnMapClickListener

import com.google.android.gms.maps.model.MarkerOptions

import com.google.android.gms.maps.model.LatLng

import com.google.android.gms.maps.OnMapReadyCallback

import com.sendbird.android.constant.StringSet.core
import kotlinx.coroutines.delay
import java.util.*
import kotlin.concurrent.schedule


class mapChoice : AppCompatActivity() {
    lateinit var mapFragment : SupportMapFragment
    lateinit var googleMap: GoogleMap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_choice)

        mapFragment = supportFragmentManager.findFragmentById(R.id.mapChoice) as SupportMapFragment


        mapFragment.getMapAsync(OnMapReadyCallback {
            // When map is loaded
            googleMap = it
            val zoomloc = LatLng(36.858994,10.187022)
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(zoomloc,8f))


            googleMap.setOnMapClickListener { latLng -> // When clicked on map
                // Initialize marker options
                val markerOptions = MarkerOptions()
                // Set position of marker
                markerOptions.position(latLng)
                // Set title of marker
                markerOptions.title(latLng.latitude.toString() + " : " + latLng.longitude)
                // Remove all marker
                googleMap.clear()
                // Animating to zoom the marker
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
                // Add marker on map
                googleMap.addMarker(markerOptions)

                sendDataBackToPreviousActivity(latLng.latitude.toString(),latLng.longitude.toString())
                Timer().schedule(1000){
                    finish()
                }

            }
        })

    }

    private fun sendDataBackToPreviousActivity(latitude : String,longitude: String ) {
        val intent = Intent().apply {
            putExtra("latitude", latitude)
            putExtra("longitude", longitude)
            // Put your data here if you want.
        }
        setResult(Activity.RESULT_OK, intent)
    }
}
