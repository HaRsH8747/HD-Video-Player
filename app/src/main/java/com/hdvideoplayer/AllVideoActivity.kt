package com.hdvideoplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.hdvideoplayer.databinding.ActivityAllVideoBinding

class AllVideoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAllVideoBinding
    companion object{
        var isGridView: Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvAllVideos.setHasFixedSize(true)
        binding.rvAllVideos.setItemViewCacheSize(10)
        binding.rvAllVideos.layoutManager = LinearLayoutManager(this)
        binding.rvAllVideos.adapter = AllVideoListAdapter(this,VideoActivity.videoList)

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
        binding.viewType.setOnClickListener {
            isGridView = !isGridView
            if (isGridView){
                binding.viewType.setImageResource(R.drawable.ic_grid_view)
                binding.rvAllVideos.layoutManager = GridLayoutManager(this, 2)
                binding.rvAllVideos.adapter = AllVideoGridAdapter(this,VideoActivity.videoList)
            }else{
                binding.viewType.setImageResource(R.drawable.ic_list_view)
                binding.rvAllVideos.layoutManager = LinearLayoutManager(this)
                binding.rvAllVideos.adapter = AllVideoListAdapter(this,VideoActivity.videoList)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}