package com.example.virtualreality_sns

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.virtualreality_sns.databinding.FragmentOneBinding
import com.example.virtualreality_sns.databinding.FragmentThreeBinding
import com.example.virtualreality_sns.util.LocationHelper
import com.google.vr.sdk.widgets.pano.VrPanoramaView
import java.io.IOException
import java.io.InputStream


class fragment_three : Fragment() {
    private val handler: Handler = Handler(Looper.getMainLooper())
    private var _binding: FragmentThreeBinding? = null
    private val binding get() = _binding ?: error("View를 참조하기 위해 binding이 초기화되지 않았습니다.")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentThreeBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
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
            }
        })
    }



}