package com.jera.justtalk.registerfeature

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.jera.justtalk.R
import com.jera.justtalk.databinding.UsernameFragmentBinding
import com.jera.justtalk.ui.utils.onTextChanged
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class UserNameFragment : Fragment() {
    private val viewModel: AuthViewModel by sharedViewModel()
    private lateinit var binding: UsernameFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = UsernameFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        insertListeners()
    }

    private fun insertListeners() {
        binding.usernameNextAppCompatButton.setOnClickListener {

            if (viewModel.isUserNameValid()) {
                navigateToEmailFragment()
            } else {
                showErrorToast()
            }
        }

        binding.usernameEditText.onTextChanged { username ->
            viewModel.updateUsername(username)
        }

        binding.enterUsernameTextView.setOnClickListener {
            navigateToLoginFragment()
        }
    }

    private fun showErrorToast() {
        Toast.makeText(
            activity,
            getString(R.string.error_username_toast),
            Toast.LENGTH_LONG
        ).show()
    }

    private fun navigateToEmailFragment() {
        findNavController().navigate(R.id.action_userNameFragment_to_emailFragment)
    }

    private fun navigateToLoginFragment() {
        findNavController().navigate(R.id.action_global_loginFragment)
    }
}
