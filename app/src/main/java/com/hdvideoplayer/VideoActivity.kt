package com.hdvideoplayer

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.hdvideoplayer.databinding.ActivityVideoBinding
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class VideoActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    private lateinit var binding: ActivityVideoBinding
    private val REQUEST_CODE_READ_EXTERNAL_STORAGE = 10
    private val REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 11
    private var hasReadPermissions: Boolean = false
    private var hasWritePermissions: Boolean = false
    lateinit var adapter: VideoAdapter
    lateinit var favouriteAdapter: FavouriteAdapter

    companion object {
        lateinit var videoList: ArrayList<Video>
        lateinit var folderList: ArrayList<Folder>
        lateinit var searchList: ArrayList<Video>
        lateinit var videoActivity: VideoActivity
        var favourite: ArrayList<Video> = ArrayList()
        var search: Boolean = false
        var sortValue: Int = 0
        var favouriteVideos: ArrayList<Video> = ArrayList()

        val sortList = arrayOf(
            MediaStore.Video.Media.DATE_MODIFIED + " DESC",
            MediaStore.Video.Media.DATE_MODIFIED,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DISPLAY_NAME + " DESC",
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.SIZE + " DESC"
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoBinding.inflate(layoutInflater)
        setTheme(R.style.Theme_HDVideoPlayer)
        setContentView(binding.root)
        videoActivity = this
        Log.d("CLEAR","list: ")
        favouriteVideos = ArrayList()
        val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE)
        val jsonString = editor.getString("FavouriteVideos", null)
        Log.d("CLEAR","json: $jsonString")
        val typeToken = object : TypeToken<ArrayList<Video>>(){}.type
        if(jsonString != null){
            val data: ArrayList<Video> = GsonBuilder().create().fromJson(jsonString, typeToken)
            favouriteVideos.addAll(data)
        }
        if (EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            hasWritePermissions = true
        }
//        if (hasWritePermissions){
//            videoList = getAllVideos(this)
//            Log.d("CLEAR","list: ${videoList.size}")
//            binding.rvAllVideos.setItemViewCacheSize(10)
//            adapter = VideoAdapter(this, videoList)
//            binding.rvAllVideos.adapter = adapter
//
//            favouriteVideos = checkPlaylist(favouriteVideos)
//            if (favouriteVideos.isEmpty()){
//                binding.tvFavInfo.visibility = View.VISIBLE
//            }else{
//                binding.tvFavInfo.visibility = View.GONE
//                binding.rvFavourites.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
//                favouriteAdapter = FavouriteAdapter(this, favouriteVideos)
//                binding.rvFavourites.adapter = favouriteAdapter
//            }
//        }else{
//            requestPermissions()
//        }
        binding.tvAllFavourites.setOnClickListener {
            val intent = Intent(this, FavouriteActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
        binding.tvAllVideos.setOnClickListener {
            val intent = Intent(this, AllVideoActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        binding.fvStar.setOnClickListener {
            goToPlayStore()
        }
    }

    private fun goToPlayStore() {
        val uri = Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID)
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        try {
            startActivity(goToMarket)
        } catch (e: Exception) {
            startActivity(Intent(Intent.ACTION_VIEW,
                Uri.parse("http://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)))
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        Log.d("CLEAR","fav: ${favouriteVideos.size}")
        val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE).edit()
        val jsonString = GsonBuilder().create().toJson(favouriteVideos)
        Log.d("CLEAR","json put: $jsonString")
        editor.putString("FavouriteVideos", jsonString)
        editor.apply()
        if (hasWritePermissions){
            videoList = getAllVideos(this)
            Log.d("CLEAR","list: ${videoList.size}")
            binding.rvAllVideos.setItemViewCacheSize(10)
            adapter = VideoAdapter(this, videoList)
            binding.rvAllVideos.adapter = adapter

            favouriteVideos = checkPlaylist(favouriteVideos)
            if (favouriteVideos.isEmpty()){
                binding.tvFavInfo.visibility = View.VISIBLE
                binding.rvFavourites.visibility = View.INVISIBLE
            }else{
                binding.rvFavourites.visibility = View.VISIBLE
                binding.tvFavInfo.visibility = View.GONE
                binding.rvFavourites.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                favouriteAdapter = FavouriteAdapter(this, favouriteVideos)
                binding.rvFavourites.adapter = favouriteAdapter
            }
            binding.rvFavourites.adapter?.notifyDataSetChanged()
        }else{
            requestPermissions()
        }
    }

    private fun requestPermissions() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            hasWritePermissions = true
        }else{
            EasyPermissions.requestPermissions(
                this,
                "You need to accept Write permissions to use this app",
                REQUEST_CODE_WRITE_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
        val minSdk29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
        hasWritePermissions = hasWritePermissions || minSdk29
//        if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
//            hasReadPermissions = true
//        }else{
//            EasyPermissions.requestPermissions(
//                this,
//                "You need to accept Read permissions to use this app",
//                REQUEST_CODE_READ_EXTERNAL_STORAGE,
//                Manifest.permission.READ_EXTERNAL_STORAGE
//            )
//        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == REQUEST_CODE_WRITE_EXTERNAL_STORAGE){
            lifecycleScope.launch {
                val minSdk30 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
                hasWritePermissions = true || minSdk30
            }
        }
        if (requestCode == REQUEST_CODE_READ_EXTERNAL_STORAGE){
            lifecycleScope.launch {
                hasReadPermissions = true
            }
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestPermissions()
        }
    }
}
