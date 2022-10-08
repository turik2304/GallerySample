package com.example.gallerysample

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.gallerysample.data.repository.DropBoxRepository
import com.example.gallerysample.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityMainBinding

    private val repository: DropBoxRepository = get()

    private val allImagesUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

    private val allVideosUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        requestPermissions()
        viewBinding.tvText.setOnClickListener {
            getPaths(allVideosUri)?.let { path ->
                lifecycleScope.launch {
                    val r = repository.uploadFile(File(path))
                    Log.d("qweqwe", r)
                }
            }
        }
    }

    private fun requestPermissions() {
        if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
    }

    private fun getPaths(uri: Uri): String? {
//        val picFolders: ArrayList<imageFolder> = ArrayList<imageFolder>()
//        val picPaths = ArrayList<String>()
        val (projection, dataColumn) = when (uri) {
            allImagesUri -> {
                arrayOf(
                    MediaStore.Images.ImageColumns.DATA,
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                    MediaStore.Images.Media.BUCKET_ID
                ) to MediaStore.Images.ImageColumns.DATA
            }
            allVideosUri -> {
                arrayOf(
                    MediaStore.Video.VideoColumns.DATA,
                    MediaStore.Video.Media.DISPLAY_NAME,
                    MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                    MediaStore.Video.Media.BUCKET_ID
                ) to MediaStore.Video.VideoColumns.DATA
            }
            else -> return null
        }
        val cursor = this.contentResolver.query(uri, projection, null, null, null)
        try {
            cursor?.moveToFirst()
            do {
                val path = cursor?.getString(cursor.getColumnIndexOrThrow(dataColumn))
                if (path != null) {
                    return path
                }
            } while (cursor!!.moveToNext())

//            do {
//                val folds = imageFolder()
//                val name = cursor!!.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME))
//                val folder = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))
//                val datapath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
//
//                //String folderpaths =  datapath.replace(name,"");
//                var folderpaths = datapath.substring(0, datapath.lastIndexOf("$folder/"))
//                folderpaths = "$folderpaths$folder/"
//                if (!picPaths.contains(folderpaths)) {
//                    picPaths.add(folderpaths)
//                    folds.setPath(folderpaths)
//                    folds.setFolderName(folder)
//                    folds.setFirstPic(datapath) //if the folder has only one picture this line helps to set it as first so as to avoid blank image in itemview
//                    folds.addpics()
//                    picFolders.add(folds)
//                } else {
//                    for (i in picFolders.indices) {
//                        if (picFolders[i].getPath().equals(folderpaths)) {
//                            picFolders[i].setFirstPic(datapath)
//                            picFolders[i].addpics()
//                        }
//                    }
//                }
//            } while (cursor!!.moveToNext())
            cursor?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}

class imageFolder {
    var path: String? = null
    var folderName: String? = null
    var numberOfPics = 0
    var firstPic: String? = null

    constructor() {}
    constructor(path: String?, folderName: String?) {
        this.path = path
        this.folderName = folderName
    }

    fun addpics() {
        numberOfPics++
    }
}