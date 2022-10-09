package com.example.gallerysample.presentation.auth

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.gallerysample.BuildConfig
import com.example.gallerysample.R
import com.example.gallerysample.data.network.NetworkConstants
import com.example.gallerysample.databinding.FragmentAuthBinding
import com.example.gallerysample.presentation.gallery.GalleryFragment

class AuthFragment : Fragment() {

    private var _binding: FragmentAuthBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentAuthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnAuth.setOnClickListener {
            val token = binding.edToken.text.toString()
            if (token.isNotBlank()) {
                saveToken(token.trim())
                openGalleryScreen()
            }
        }
    }

    private fun openGalleryScreen() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, GalleryFragment())
            .commit()
    }

    private fun saveToken(token: String) {
        val sharedPref = requireContext().getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString(NetworkConstants.TOKEN_KEY, token)
            apply()
        }
    }

}