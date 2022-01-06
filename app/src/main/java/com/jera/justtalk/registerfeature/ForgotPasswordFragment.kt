package com.jera.justtalk.registerfeature

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.jera.justtalk.R
import com.jera.justtalk.databinding.ForgotPasswordFragmentBinding
import com.jera.justtalk.ui.utils.ViewState
import com.jera.justtalk.ui.utils.hideSoftKeyboard
import com.jera.justtalk.ui.utils.onTextChanged
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ForgotPasswordFragment : Fragment() {
    private lateinit var binding: ForgotPasswordFragmentBinding
    private val viewModel: RetrivePasswordViewModel by viewModel()
    private val parentActivity: AppCompatActivity? by lazy { activity as? AppCompatActivity }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ForgotPasswordFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeStateFlow()
        insertListeners()
    }

    private fun navigateOnSuccess() {
        Toast.makeText(
            activity,
            getString(R.string.retrieve_password_success),
            Toast.LENGTH_SHORT
        ).show()
        findNavController().navigate(R.id.action_forgotPasswordFragment_to_homeFragment)
    }

    private fun hideLoadScreen() {
        binding.loadScreen.visibility = View.GONE
        binding.loginForgotToolbar.visibility = View.VISIBLE
    }

    private fun showLoadScreen() {
        binding.loadScreen.visibility = View.VISIBLE
        binding.loginForgotToolbar.visibility = View.GONE
        parentActivity?.hideSoftKeyboard()
    }

    private fun observeStateFlow() {
        lifecycleScope.launchWhenStarted {
            viewModel.retriveState.collect { uiState ->
                when (uiState) {
                    ViewState.Loading -> {
                        showLoadScreen()
                    }
                    ViewState.Success -> {
                        hideLoadScreen()
                        navigateOnSuccess()
                    }
                    ViewState.Error -> {
                        hideLoadScreen()
                        invalidFormatErrorHandler()
                    }
                    else -> {
                    }
                }
            }
        }
    }

    private fun insertListeners() {
        binding.forgotPasswordEmailEditText.onTextChanged { email ->
            viewModel.updateEmail(email)
        }
        binding.forgotPasswordSendAppCompatButton.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                if (viewModel.isEmailValid()) {
                    viewModel.retrievePassword()
                } else {
                    invalidFormatErrorHandler()
                }
            }
        }
        binding.loginForgotToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun invalidFormatErrorHandler() {
        Toast.makeText(context, R.string.error_toast, Toast.LENGTH_LONG).show()
    }
}
