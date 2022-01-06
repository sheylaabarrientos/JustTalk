package com.jera.justtalk.navigationbarfeature

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.jera.justtalk.databinding.PrivacyPolicyFragmentBinding

class PrivacyPolicyFragment : Fragment() {

    private lateinit var binding: PrivacyPolicyFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = PrivacyPolicyFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        insertListeners()
    }

    private fun insertListeners() {
        binding.loginForgotToolbar.setOnClickListener {
            goToMoreOptions()
        }
    }

    private fun goToMoreOptions() {
        findNavController().popBackStack()
    }
}
