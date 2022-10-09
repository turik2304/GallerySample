package com.example.gallerysample.presentation.gallery

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.gallerysample.R
import com.example.gallerysample.databinding.FragmentGalleryBinding
import com.example.gallerysample.presentation.auth.AuthFragment
import com.example.gallerysample.presentation.gallery.adapter.GalleryAdapter
import com.example.gallerysample.presentation.share.ShareDialogFragment
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class GalleryFragment : Fragment() {

    private val permissionLauncher: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            if (result.values.all { it }) {
                viewModel.dispatch(Action.LoadMediaFiles)
            } else {
                Toast.makeText(requireContext(), "Permission not granted! Reload App!", Toast.LENGTH_SHORT).show()
            }
        }

    private var _binding: FragmentGalleryBinding? = null

    private val binding get() = _binding!!

    private val viewModel: GalleryViewModel by viewModel()

    private val adapter: GalleryAdapter = GalleryAdapter(
        onItemClick = { filePath, folderName, url ->
            when {
                filePath != null && url != null -> {
                    showShareScreen(url)
                }
                filePath != null && url == null -> {
                    viewModel.dispatch(Action.UploadFile(folderName, filePath))
                }
                else -> {
                    viewModel.dispatch(Action.OpenFolder(folderName))
                }
            }
        }
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initBackPress()
        requestPermissions()
        viewModel.observableStates.onEach(::renderState).launchIn(viewLifecycleOwner.lifecycleScope)
        viewModel.observableSideEffects.onEach(::renderEffect).launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun initViews() {
        with(binding) {
            rvContent.layoutManager = GridLayoutManager(requireContext(), 3)
            rvContent.adapter = adapter
            rvContent.setHasFixedSize(true)
        }
    }

    private fun initBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(this.viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                viewModel.dispatch(Action.CloseFolder)
            }
        })
    }

    private fun requestPermissions() {
        val permissions = if (Build.VERSION.SDK_INT >= 33) {
            arrayOf(Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        permissionLauncher.launch(permissions)
    }

    private fun showShareScreen(url: String) {
        if (childFragmentManager.findFragmentByTag(ShareDialogFragment.TAG) == null) {
            ShareDialogFragment.newInstance(url).show(childFragmentManager, ShareDialogFragment.TAG)
        }
    }

    private fun renderState(state: State) {
        with(binding) {
            when (state) {
                is State.Content -> {
                    rvContent.isVisible = true
                    progressBar.isGone = true
                    if (state.openedFolderName != null) {
                        adapter.updateItems(state.openedFolder!!.files)
                    } else {
                        adapter.updateItems(state.folders)
                    }
                }
                State.Error -> {
                    rvContent.isGone = true
                    progressBar.isGone = true
                }
                State.Loading -> {
                    rvContent.isGone = true
                    progressBar.isVisible = true
                }
            }
        }
    }

    private fun renderEffect(effect: SideEffect) {
        when (effect) {
            SideEffect.AuthError -> {
                Toast.makeText(requireContext(), "Auth Error!", Toast.LENGTH_SHORT).show()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, AuthFragment())
                    .commit()
            }
            SideEffect.BackPress -> {
                requireActivity().finish()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}