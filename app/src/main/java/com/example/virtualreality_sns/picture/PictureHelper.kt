package com.example.virtualreality_sns.picture

import android.content.Context
import android.media.MediaScannerConnection
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class PictureHelper (context:Context){

    var mContext = context
    lateinit var currentPhotoPath: String

    val photoFile: File? = try {
        createImageFile()
    } catch (ex: IOException) {
        // Error occurred while creating the File
        Log.d("test", "error: $ex")
        null
    }


    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
            Log.d("test", "currentPhotoPath : $currentPhotoPath")
        }
    }

    fun galleryAddPic() {
        Log.d("test", "currentPhotoPath2 : $currentPhotoPath")

        val file = File(currentPhotoPath)
        MediaScannerConnection.scanFile(mContext, arrayOf(file.toString()), null, null)
    }
}