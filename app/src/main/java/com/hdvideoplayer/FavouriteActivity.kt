package com.hdvideoplayer

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.hdvideoplayer.VideoActivity.Companion.favouriteVideos
import com.hdvideoplayer.databinding.ActivityFavouriteBinding
import java.io.File

class FavouriteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavouriteBinding
    private lateinit var adapter: FavouriteAdapterAll

    companion object{
//        var favouriteVideos: ArrayList<Video> = ArrayList()
        var favouritesChanged: Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavouriteBinding.inflate(layoutInflater)
        favouriteVideos = checkPlaylist(favouriteVideos)
        binding.rvFavouriteVideos.layoutManager = GridLayoutManager(this, 2)
        adapter = FavouriteAdapterAll(this, favouriteVideos)
        binding.rvFavouriteVideos.adapter = adapter

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

//        binding.viewType.setOnClickListener {
//            AllVideoActivity.isGridView = !AllVideoActivity.isGridView
//            if (AllVideoActivity.isGridView){
//                binding.viewType.setImageResource(R.drawable.ic_grid_view)
//                binding.rvFavouriteVideos.layoutManager = GridLayoutManager(this, 2)
//                binding.rvFavouriteVideos.adapter = AllVideoGridAdapter(this,VideoActivity.videoList)
//            }else{
//                binding.viewType.setImageResource(R.drawable.ic_list_view)
//                binding.rvFavouriteVideos.layoutManager = LinearLayoutManager(this)
//                binding.rvFavouriteVideos.adapter = AllVideoListAdapter(this,VideoActivity.videoList)
//            }
//        }

        setContentView(binding.root)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        if(favouriteVideos.isNotEmpty()){
            binding.instructionFV.visibility = View.GONE
        }else{
            binding.instructionFV.visibility = View.VISIBLE
        }
        if(favouritesChanged) {
            adapter.updateFavourites(favouriteVideos)
            favouritesChanged = false
        }
    }
}