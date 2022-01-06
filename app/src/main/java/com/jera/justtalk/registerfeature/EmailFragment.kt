package com.jera.justtalk.registerfeature

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.jera.justtalk.R
import com.jera.justtalk.databinding.EmailRegisterBinding
import com.jera.justtalk.ui.utils.onTextChanged
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class EmailFragment : Fragment() {

    private lateinit var binding: EmailRegisterBinding
    private val viewModel: AuthViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = EmailRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        insertListeners()
    }

    private fun insertListeners() {

        binding.emailEditText.onTextChanged { email ->
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.updateEmail(email)
            }
        }

        binding.emailNextAppCompatButton.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                if (viewModel.isEmailValid()) {
                    navigateOnSuccess()
                } else {
                    invalidFormatErrorHandler()
                }
            }
        }

        binding.emailBackImageButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.enterEmailTextView.setOnClickListener {
            navigateToLoginFragment()
        }
    }

    private fun invalidFormatErrorHandler() {
        Toast.makeText(activity, getString(R.string.error_email_toast), Toast.LENGTH_LONG).show()
    }

    private fun navigateOnSuccess() {
        findNavController().navigate(R.id.action_emailFragment_to_passwordFragment)
    }

    private fun navigateToLoginFragment() {
        findNavController().navigate(R.id.action_global_loginFragment)
    }
}
