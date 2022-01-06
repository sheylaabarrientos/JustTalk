package com.jera.justtalk.registerfeature

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.jera.justtalk.R
import com.jera.justtalk.databinding.UserPhotoFragmentBinding
import com.jera.justtalk.ui.utils.Constants
import com.jera.justtalk.ui.utils.ViewState
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class UserPhotoFragment : Fragment() {

    private lateinit var binding: UserPhotoFragmentBinding
    private val viewModel: AuthViewModel by sharedViewModel()
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        sharedPreferences =
            activity?.getSharedPreferences(
            Constants.SHARED_PREFERENCES_NAME,
            Context.MODE_PRIVATE
        ) as SharedPreferences
        binding = UserPhotoFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        insertListeners()
        observeStateFlow()
        observeSharedPrefWriteState()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            val selectedImage = data?.data
            val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, selectedImage)
            if (selectedImage != null) {
                viewModel.updateImage(selectedImage)
            }
            binding.imagePhotoImageView.setImageBitmap(bitmap)
        }
    }

    private fun insertListeners() {
        binding.imagePhotoImageView.setOnClickListener {
            getImageFromGallery()
        }

        binding.photoConcludeAppCompatButton.setOnClickListener {
            viewModel.registerUser()
        }

        binding.photoBackImageButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.enterPhotoTextView.setOnClickListener {
            navigateToLoginFragment()
        }
    }

    private fun getImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    private fun observeStateFlow() {
        lifecycleScope.launchWhenStarted {
            viewModel.registerState.collect { uiState ->
                when (uiState) {
                    ViewState.Loading -> {
                        showProgressBar()
                    }
                    ViewState.Success -> {
                        viewModel.getCurrentUser()
                    }
                    ViewState.Error -> {
                        errorToast()
                        hideProgressBar()
                    }
                    else -> {}
                }
            }
        }
    }

    private fun observeSharedPrefWriteState() {
        lifecycleScope.launchWhenStarted {
            viewModel.sharedPrefWriteState.collect { state ->
                when (state) {
                    ViewState.Loading -> {
                        viewModel.writeOnSharedPrefs(sharedPreferences)
                    }
                    ViewState.Success -> {
                        hideProgressBar()
                        navigateOnSuccess()
                    }
                    ViewState.Error -> {
                        errorToast()
                        hideProgressBar()
                    }
                    else -> {}
                }
            }
        }
    }

    private fun navigateOnSuccess() {
        Toast.makeText(
            activity,
            getString(R.string.register_success_toast),
            Toast.LENGTH_SHORT
        ).show()
        findNavController().navigate(R.id.action_userPhotoFragment_to_dashboardFragment)
    }

    private fun hideProgressBar() {
        binding.loadScreen.visibility = View.GONE
    }

    private fun showProgressBar() {
        binding.loadScreen.visibility = View.VISIBLE
    }

    private fun errorToast() {
        Toast.makeText(context, getString(R.string.register_toast_error), Toast.LENGTH_LONG).show()
    }

    private fun navigateToLoginFragment() {
        findNavController().navigate(R.id.action_global_loginFragment)
    }

    companion object {
        const val GALLERY_REQUEST_CODE = 0
    }
}
