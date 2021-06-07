package com.example.virtualreality_sns.home.fragments.two

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.virtualreality_sns.databinding.FragmentTwoBinding
import com.example.virtualreality_sns.picture.GalleryHelper
import com.example.virtualreality_sns.picture.PictureHelper
import com.example.virtualreality_sns.util.PermissionHelper
import com.google.vr.sdk.widgets.pano.VrPanoramaView
import java.util.*


class TwoFragment : Fragment() {
    private var _binding: FragmentTwoBinding? = null
    private val binding get() = _binding ?: error("View를 참조하기 위해 binding이 초기화되지 않았습니다.")
    private val viewModel: TwoViewModel by viewModels() //위임초기화
    private lateinit var mContext: Context

    //권한 요청
    private lateinit var permissionHelper :PermissionHelper
    private lateinit var galleryHelper: GalleryHelper
    private lateinit var pictureHelper: PictureHelper

    private val handler: Handler = Handler(Looper.getMainLooper()) //tmp

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTwoBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        mContext = requireContext()

        permissionHelper = PermissionHelper(mContext) //권한 요청
        galleryHelper = GalleryHelper(mContext) //사진 촬영 및 저장
        pictureHelper = PictureHelper(mContext) //사진 합치기 및 절대경로 변환

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
        var imageList = ArrayList<Any>() //URI가 들어 갈 배열
        imageList = galleryHelper.galleryChoosePic(result)
        if(imageList.size > 0){
            showSpherePanorama(imageList) //파노라마 뷰 설정
        }
    }


    private fun showSpherePanorama( imageList: ArrayList<Any> ) {
        handler.postDelayed({
            val options = VrPanoramaView.Options()
            options.inputType = VrPanoramaView.Options.TYPE_MONO

            val mergedBitmap = pictureHelper.mergeMultiple(imageList)
            //test
            binding.pvImage.loadImageFromBitmap(mergedBitmap, options)
        }, 0)
    }


    private fun openCamera(){
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(mContext.packageManager)?.also {
                // 사진 파일을 만듭니다.
                val photoFile = galleryHelper.photoFile

                // photoUri를 보내는 코드
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        mContext,
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
        galleryHelper.galleryAddPic()
    }


    fun goCheckPermissions(){
        if(!permissionHelper.checkPermissions()){ //권한 요청 거부
            permissionHelper.requestPermission()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(!permissionHelper.permissionResult(requestCode,permissions ,grantResults)){
            permissionHelper.requestPermission()
        }
    }

}