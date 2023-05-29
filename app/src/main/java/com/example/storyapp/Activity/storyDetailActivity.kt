package com.example.storyapp.Activity

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.example.storyapp.databinding.ActivityStoryDetailBinding
import com.example.storyapp.response.ListStoryItem
import com.example.storyapp.utils.DateFormatter
import java.util.*

class storyDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStoryDetailBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val data = intent.getParcelableExtra<ListStoryItem>("User") as ListStoryItem

        binding.apply {
            nameDS.text = data.name
            descDS.text = data.description
            created.text = DateFormatter.formatDate(data.createdAt, TimeZone.getDefault().id)
        }
        Glide.with(this)
            .load(data.photoUrl)
            .into(binding.detailImage)

    }

    companion object {
        var STORY = ""
        var AVATAR = ""
    }
}