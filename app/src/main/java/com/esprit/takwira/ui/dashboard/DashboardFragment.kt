package com.esprit.takwira.ui.dashboard

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.esprit.takwira.databinding.FragmentDashboardBinding


import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.CameraUpdateFactory

import com.google.android.gms.maps.model.MarkerOptions

import com.google.android.gms.maps.model.LatLng

import com.google.android.gms.maps.GoogleMap

import com.google.android.gms.maps.OnMapReadyCallback

import android.R
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.esprit.takwira.models.Stade
import com.esprit.takwira.ui.home.adapter
import com.esprit.takwira.viewmodels.mainActitvityViewModel
import com.google.android.gms.maps.GoogleMap.OnMapClickListener


class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private var _binding: FragmentDashboardBinding? = null
    lateinit var googleMap: GoogleMap

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!



    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {





        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root




        val supportMapFragment =
            childFragmentManager.findFragmentById(com.esprit.takwira.R.id.map) as SupportMapFragment?

        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    // Precise location access granted.
                    supportMapFragment!!.getMapAsync(OnMapReadyCallback {

                        googleMap = it
                        googleMap.isMyLocationEnabled = true
                        val zoomloc = LatLng(36.858994,10.187022)

                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(zoomloc,9f))
                        val stadeList = adapter.getStadeList()
                        if (stadeList?.isEmpty() == false) {

                            for (stade in stadeList!!) {
                                val lat = stade.location?.substringBefore(",")
                                val long = stade.location?.substringAfter(",")
                                val location = lat?.let { it1 -> long?.let { it2 ->
                                    LatLng(it1.toDouble(),
                                        it2.toDouble())
                                } }
                                googleMap.addMarker(MarkerOptions().position(location!!).title(stade.name))
                            }
                        }


                    })
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    supportMapFragment!!.getMapAsync(OnMapReadyCallback {

                        googleMap = it
                        googleMap.isMyLocationEnabled = true
                        val zoomloc = LatLng(36.858994,10.187022)
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(zoomloc,8f))
                        val stadeList = adapter.getStadeList()
                        if (stadeList?.isEmpty() == false) {

                            for (stade in stadeList!!) {
                                val lat = stade.location?.substringBefore(",")
                                val long = stade.location?.substringAfter(",")
                                val location = lat?.let { it1 -> long?.let { it2 ->
                                    LatLng(it1.toDouble(),
                                        it2.toDouble())
                                } }
                                googleMap.addMarker(MarkerOptions().position(location!!).title(stade.name))
                            }
                        }
                    })
                } else -> {
                // No location access granted.
            }
            }
        }
        // Before you perform the actual permission request, check whether your app
// already has the permissions, and whether your app needs to show a permission
// rationale dialog. For more details, see Request permissions.
        locationPermissionRequest.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION))




        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}