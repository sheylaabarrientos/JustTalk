package com.jera.justtalk.loginfeature

import android.content.Context
import android.content.SharedPreferences
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
import com.jera.justtalk.databinding.LoginFragmentBinding
import com.jera.justtalk.ui.utils.Constants
import com.jera.justtalk.ui.utils.ViewState
import com.jera.justtalk.ui.utils.hideSoftKeyboard
import com.jera.justtalk.ui.utils.onTextChanged
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : Fragment() {

    private val viewModel: LoginViewModel by viewModel()
    private lateinit var binding: LoginFragmentBinding
    private lateinit var sharedPreferences: SharedPreferences
    private val parentActivity: AppCompatActivity? by lazy { activity as? AppCompatActivity }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LoginFragmentBinding.inflate(inflater, container, false)
        sharedPreferences =
            activity?.getSharedPreferences(
            Constants.SHARED_PREFERENCES_NAME,
            Context.MODE_PRIVATE
        ) as SharedPreferences
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeStateFlow()
        insertListeners()
    }

    private fun insertListeners() {
        binding.loginEmailAppCompatEditText.onTextChanged { email ->
            viewModel.updateEmail(email)
        }

        binding.loginPasswordAppCompatEditText.onTextChanged { password ->
            viewModel.updatePassword(password)
        }

        binding.loginEnterAppCompatButton.setOnClickListener {
            viewModel.userLoginWithEmailAndPassword(sharedPreferences)
        }

        binding.loginForgotToolbar.setNavigationOnClickListener {
            navigateHome()
        }

        binding.loginForgotPasswordTextView.setOnClickListener {
            navigateToForgotPasswordFragment()
        }
    }

    private fun observeStateFlow() {
        lifecycleScope.launchWhenStarted {
            viewModel.loginState.collect { uiState ->
                when (uiState) {
                    ViewState.Loading -> {
                        showLoadScreen()
                    }
                    ViewState.Success -> {
                        hideLoadScreen()
                        navigateToDashboard()
                    }
                    ViewState.Error -> {
                        showToastError()
                        hideLoadScreen()
                    }
                    else -> {
                    }
                }
            }
        }
    }

    private fun showToastError() {
        Toast.makeText(
            context,
            getString(R.string.login_error_toast),
            Toast.LENGTH_LONG
        ).show()
    }

    private fun navigateHome() {
        findNavController().navigate(R.id.navgraph)
    }

    private fun navigateToDashboard() {
        findNavController().navigate(R.id.action_loginFragment_to_dashboardFragment)
    }

    private fun navigateToForgotPasswordFragment() {
        findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
    }

    private fun hideLoadScreen() {
        binding.loadScreen.visibility = View.GONE
        binding.loginToolbar.visibility = View.VISIBLE
    }

    private fun showLoadScreen() {
        binding.loadScreen.visibility = View.VISIBLE
        binding.loginToolbar.visibility = View.GONE
        parentActivity?.hideSoftKeyboard()
    }
}
