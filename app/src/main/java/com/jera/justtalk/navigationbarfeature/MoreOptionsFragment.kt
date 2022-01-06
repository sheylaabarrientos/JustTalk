package com.jera.justtalk.navigationbarfeature

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.jera.justtalk.R
import com.jera.justtalk.databinding.MoreOptionsFragmentBinding

class MoreOptionsFragment : Fragment() {

    private lateinit var binding: MoreOptionsFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MoreOptionsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        insertListeners()
    }

    private fun insertListeners() {
        binding.useTermsLinearLayout.setOnClickListener {
            goToUseTermns()
        }

        binding.privacyPolicyLinearLayout.setOnClickListener {
            goToPrivacyPolicy()
        }
    }

    private fun goToUseTermns() {
        findNavController().navigate(R.id.action_moreOptionsFragment_to_useTermsFragment)
    }

    private fun goToPrivacyPolicy() {
        findNavController().navigate(R.id.action_moreOptionsFragment_to_privacyPolicyFragment)
    }
}
