package com.example.gallerysample.presentation.share

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.example.gallerysample.databinding.FragmentShareBinding

class ShareDialogFragment : DialogFragment() {

    private var _binding: FragmentShareBinding? = null

    private val binding get() = _binding!!

    private val link: String by lazy { arguments?.getString(ARG_LINK).orEmpty() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentShareBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvLink.text = link
        binding.btnShare.setOnClickListener { onShareClick(link) }
        binding.btnCopy.setOnClickListener { onCopyClick(link) }
    }

    private fun onCopyClick(url: String) {
        val clipboard = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip: ClipData = ClipData.newPlainText("Link", url)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(requireContext(), "Link copied!", Toast.LENGTH_SHORT).show()
    }

    private fun onShareClick(url: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, url)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    companion object {

        const val TAG: String = "ShareFrag"

        private const val ARG_LINK: String = "ARG_LINK"

        fun newInstance(link: String): ShareDialogFragment {
            return ShareDialogFragment().apply {
                arguments = bundleOf(ARG_LINK to link)
            }
        }
    }
}