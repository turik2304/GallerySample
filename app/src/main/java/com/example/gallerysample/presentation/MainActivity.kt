package com.example.gallerysample.presentation

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.gallerysample.BuildConfig
import com.example.gallerysample.R
import com.example.gallerysample.data.network.NetworkConstants
import com.example.gallerysample.databinding.ActivityMainBinding
import com.example.gallerysample.presentation.auth.AuthFragment
import com.example.gallerysample.presentation.gallery.GalleryFragment

class MainActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        if (savedInstanceState == null) {
            val fragment = if (isTokenValid()) {
                GalleryFragment()
            } else {
                AuthFragment()
            }
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, fragment)
                .commit()
        }
    }

    private fun isTokenValid(): Boolean {
        val token = getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE).getString(NetworkConstants.TOKEN_KEY, null)
        return !token.isNullOrEmpty()
    }

}