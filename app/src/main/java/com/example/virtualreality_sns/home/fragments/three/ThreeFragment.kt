package com.example.virtualreality_sns.home.fragments.three


import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.virtualreality_sns.LocationData
import com.example.virtualreality_sns.databinding.FragmentThreeBinding
import com.example.virtualreality_sns.util.LocationHelper
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class ThreeFragment : Fragment() , OnMapReadyCallback{
    private var _binding: FragmentThreeBinding? = null
    private val binding get() = _binding ?: error("View를 참조하기 위해 binding이 초기화되지 않았습니다.")
    private lateinit var mMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentThreeBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        binding.mvMap.onCreate(savedInstanceState)
        binding.mvMap.getMapAsync(this)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //함수

        Log.d("Location","들어옴")
        LocationHelper().startListeningUserLocation(requireContext() , object : LocationHelper.MyLocationListener {
            override fun onLocationChanged(location: Location) {
                // Here you got user location :)
                Log.d("Location","" + location.latitude + "," + location.longitude)
                var currentLoc = LocationData(location.latitude,location.longitude,"Current")
                markLoc(currentLoc)
            }
        })
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        var konkukLoc = LocationData(37.54093262179155, 127.07931061173049,"Konkuk")
        markLoc(konkukLoc)
    }

    fun markLoc(loc: LocationData){
        val marker = LatLng(loc.lat, loc.long)
        mMap.addMarker(MarkerOptions().position(marker).title(loc.title))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(marker))
    }

    override fun onStart() {
        super.onStart()
        _binding?.mvMap?.onStart()
    }
    override fun onStop() {
        super.onStop()
        _binding?.mvMap?.onStop()
    }
    override fun onResume() {
        super.onResume()
        _binding?.mvMap?.onResume()
    }
    override fun onPause() {
        super.onPause()
        _binding?.mvMap?.onPause()
    }
    override fun onLowMemory() {
        super.onLowMemory()
        _binding?.mvMap?.onLowMemory()
    }
    override fun onDestroy() {
        _binding?.mvMap?.onDestroy()
        super.onDestroy()
    }
}