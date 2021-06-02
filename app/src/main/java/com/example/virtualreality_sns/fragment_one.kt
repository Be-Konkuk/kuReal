package com.example.virtualreality_sns

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.virtualreality_sns.databinding.FragmentOneBinding
import com.google.vr.sdk.widgets.pano.VrPanoramaView
import java.io.IOException
import java.io.InputStream


class fragment_one : Fragment() {
    private val handler: Handler = Handler(Looper.getMainLooper())
    private var _binding: FragmentOneBinding? = null
    private val binding get() = _binding ?: error("View를 참조하기 위해 binding이 초기화되지 않았습니다.")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOneBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //함수
        panoramaMethod()

    }

    private fun panoramaMethod() {
        showSpherePanorama(Pair.create(getActivity()?.getIntent()?.getData(), VrPanoramaView.Options()))
    }

    private fun showSpherePanorama(pair: Pair<Uri, VrPanoramaView.Options>) {
        handler.postDelayed({
            var `is`: InputStream? = null
            val assetManager: AssetManager? = context?.getAssets()
            try {
                if (assetManager != null) {
                    `is` = assetManager.open("panorama2.jpeg")
                }
                val options = VrPanoramaView.Options()
                options.inputType = VrPanoramaView.Options.TYPE_MONO
                binding.vrPanoramaView!!.loadImageFromBitmap(BitmapFactory.decodeStream(`is`), options)
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                if (`is` != null) {
                    try {
                        `is`.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }, 0)
    }




//    private fun bitmap(){
//
//        handler.postDelayed({
//            var bitmap1: InputStream? = null
//            var bitmap2: InputStream? = null
//            val assetManager: AssetManager? = context?.getAssets()
//            try {
//                if (assetManager != null) {
//                    bitmap1 = assetManager.open("panorama2.jpeg")
//                    bitmap2 = assetManager.open("panorama2.jpeg")
//                }
//                var listBmp: Bitmap
//
//
//            } catch (e: IOException) {
//                e.printStackTrace()
//            } finally {
//                if (bitmap1 != null) {
//                    try {
//                        bitmap1.close()
//                        bitmap2.close()
//                    } catch (e: IOException) {
//                        e.printStackTrace()
//                    }
//                }
//            }
//        }, 0)
//    }
//
//    private fun mergeMultiple(Bitmap, Bitmap): Bitmap? {
//        val result =
//            Bitmap.createBitmap(parts[0].width * 2, parts[0].height * 2, Bitmap.Config.ARGB_8888)
//        val canvas = Canvas(result)
//        val paint = Paint()
//        for (i in parts.indices) {
//            canvas.drawBitmap(parts[i], parts[i].width * (i % 2), parts[i].height * (i / 2), paint)
//        }
//        return result
//    }
}