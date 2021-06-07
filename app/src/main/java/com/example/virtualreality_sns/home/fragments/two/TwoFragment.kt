package com.example.virtualreality_sns.home.fragments.two

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.*
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.virtualreality_sns.databinding.FragmentTwoBinding
import com.example.virtualreality_sns.login_signup.login.LoginViewModel
import com.example.virtualreality_sns.picture.PictureHelper
import com.example.virtualreality_sns.util.PermissionSupport
import com.google.vr.sdk.widgets.pano.VrPanoramaView
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class TwoFragment : Fragment() {
    private var _binding: FragmentTwoBinding? = null
    private val binding get() = _binding ?: error("View를 참조하기 위해 binding이 초기화되지 않았습니다.")
    private val viewModel: TwoViewModel by viewModels() //위임초기화
    private val handler: Handler = Handler(Looper.getMainLooper()) //tmp

    //권한 요청
    private lateinit var permissionSupport :PermissionSupport
    private lateinit var pictureHelper: PictureHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTwoBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        permissionSupport = PermissionSupport(requireContext()) //권한 요청
        pictureHelper = PictureHelper(requireContext())

        goCheckPermissions()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnCamera.setOnClickListener {
            //버튼 클릭 시 카메라 오픈, 사진 저장
            openCamera()
        }

        binding.btnCamera2.setOnClickListener {
            //버튼 클릭 시 이미지 불러와서 스티칭
            chooseImg()
        }
    }

    /**
     * 갤러리에서 이미지 선택하기*/
    private fun chooseImg() {
        val stitchIntent: Intent = Intent().apply {
            action = Intent.ACTION_GET_CONTENT
            type = "image/*"
        }
        //다중선택 가능
        stitchIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)

        val shareIntent = Intent.createChooser(stitchIntent, null)
        chooseActivityLauncher.launch(shareIntent)
    }

    /**
     * 선택 후 activity */
    private val chooseActivityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
            result: ActivityResult ->
        //content://com.android.providers.media.documents/document/image%3A30 형태로 저장.
        //꺼낼 때는 getFullPathFromUri(requireContext(),imageList[n]) 사용
        val imageList = ArrayList<Any>() //URI가 들어 갈 배열

        //한 개 고름
        if (result?.getData()?.clipData == null) {
            Log.i("1. single choice", result?.getData()?.data.toString())
            var source: ImageDecoder.Source? = null
            val imageBitmap = result?.getData()?.data.let { uri ->
                if (uri != null) {
                    imageList.add(uri)
                    source = ImageDecoder.createSource(requireContext().contentResolver, uri)
                }
            }
            lateinit var tmp: ImageDecoder.Source
            imageBitmap?.let { source?.let { img ->
                tmp = img
            } }

            //binding.ivImage.setImageBitmap( ImageDecoder.decodeBitmap(tmp)) //이미지 설정
            panoramaMethod(imageList) //파노라마 뷰 설
        }

        //다중 선택 시
        else{
            val clipData = result?.getData()?.clipData
            Log.i("clipdata", clipData!!.itemCount.toString())

            if (clipData.itemCount > 10) {
                Toast.makeText(requireContext(), "사진은 10개까지 선택가능 합니다.", Toast.LENGTH_SHORT).show()
                return@registerForActivityResult
            }
            //한 개 고름
            else if (clipData.itemCount == 1) {
                Log.i("2. clipdata choice", clipData.getItemAt(0).uri.toString())
                Log.i("2. single choice", clipData.getItemAt(0).uri.path!!)
                imageList.add(clipData.getItemAt(0).uri)
            }
            //1~10개 고름
            else if (clipData.itemCount > 1 && clipData.itemCount < 10) {
                var i = 0
                while (i < clipData.itemCount) {
                    Log.i("3. single choice", clipData.getItemAt(i).uri.toString())
                    imageList.add(clipData.getItemAt(i).uri)
                    i++
                }

//                var mergedImg = mergeMultiple(imageList)
//                binding.ivImage.setImageBitmap(mergedImg)
                panoramaMethod(imageList)
            }
        }
    }


    private fun mergeMultiple(imageList: ArrayList<Any>): Bitmap? {
        val listBmp: ArrayList<Bitmap> = ArrayList<Bitmap>()
        if (listBmp != null) {
            for (i in imageList.indices) {
                listBmp.add(BitmapFactory.decodeFile(getFullPathFromUri(requireContext(),imageList[i] as Uri)))
            }
//            listBmp.add(BitmapFactory.decodeFile(getFullPathFromUri(requireContext(),imageList[0] as Uri)))
//            listBmp.add(BitmapFactory.decodeFile(getFullPathFromUri(requireContext(),imageList[1] as Uri)))
        }
        val result =
            Bitmap.createBitmap(listBmp[0].width * imageList.size, listBmp[0].height, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(result)
        val paint = Paint()
        for (i in listBmp.indices) {
            canvas.drawBitmap(listBmp[i], (listBmp[i].width * (i % imageList.size)).toFloat(), (listBmp[i].height * (i / imageList.size)).toFloat(), paint)
        }
        return result
    }


    private fun panoramaMethod(imageList: ArrayList<Any>) {
        showSpherePanorama(Pair.create(getActivity()?.getIntent()?.getData(), VrPanoramaView.Options()),imageList)
    }

    private fun showSpherePanorama(
        pair: Pair<Uri, VrPanoramaView.Options>,
        imageList: ArrayList<Any>
    ) {
        handler.postDelayed({

            val options = VrPanoramaView.Options()
            options.inputType = VrPanoramaView.Options.TYPE_MONO

            //절대경로 받아오기
//            val realAdd = getFullPathFromUri(requireContext(),imageList[0] as Uri)
//            binding.pvImage.loadImageFromBitmap(BitmapFactory.decodeFile(realAdd), options) //파노라마 설정
//
            //test
            binding.pvImage.loadImageFromBitmap(mergeMultiple(imageList), options)

        }, 0)
    }

    private fun openCamera(){
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireContext().packageManager)?.also {
                // 사진 파일을 만듭니다.
                val photoFile = pictureHelper.photoFile

                // photoUri를 보내는 코드
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireContext(),
                        "com.example.virtualreality_sns.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    cameraActivityLauncher.launch(takePictureIntent)
                }
            }
        }
    }

    private val cameraActivityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        result: ActivityResult ->
        // TODO : 다른 FRAGMENT를 갔다가 들어오면 launch가 제대로 동작하지 않음
        pictureHelper.galleryAddPic()
    }

    //절대경로 반환
    fun getFullPathFromUri(ctx: Context, fileUri: Uri?): String? {
        var fullPath: String? = null
        val column = "_data"
        var cursor: Cursor? =
            fileUri?.let { ctx.getContentResolver().query(it, null, null, null, null) }
        if (cursor != null) {
            cursor.moveToFirst()
            var document_id: String = cursor.getString(0)
            if (document_id == null) {
                for (i in 0 until cursor.getColumnCount()) {
                    if (column.equals(cursor.getColumnName(i), ignoreCase = true)) {
                        fullPath = cursor.getString(i)
                        break
                    }
                }
            }
            else {
                document_id = document_id.substring(document_id.lastIndexOf(":") + 1)
                cursor.close()
                val projection = arrayOf(column)
                try {
                    cursor = ctx.getContentResolver().query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        projection,
                        MediaStore.Images.Media._ID + " = ? ",
                        arrayOf(document_id),
                        null
                    )
                    if (cursor != null) {
                        cursor.moveToFirst()
                        fullPath = cursor.getString(cursor.getColumnIndexOrThrow(column))
                    }
                } finally {
                    if (cursor != null) cursor.close()
                }
            }
        }
        return fullPath
    }

    fun goCheckPermissions(){
        if(!permissionSupport.checkPermissions()){ //권한 요청 거부
            permissionSupport.requestPermission()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(!permissionSupport.permissionResult(requestCode,permissions ,grantResults)){
            permissionSupport.requestPermission()
        }
    }

}