package com.example.gallerysample.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.gallerysample.R
import com.example.gallerysample.databinding.ActivityMainBinding
import com.example.gallerysample.presentation.gallery.GalleryFragment

class MainActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, GalleryFragment())
                .commit()
        }
    }

}