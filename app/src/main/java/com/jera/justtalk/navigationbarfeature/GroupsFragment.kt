package com.jera.justtalk.navigationbarfeature

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.jera.justtalk.R
import com.jera.justtalk.databinding.GroupsFragmentBinding
import com.jera.justtalk.model.ThemeModel
import com.jera.justtalk.ui.utils.Constants
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class GroupsFragment : Fragment() {

    private lateinit var binding: GroupsFragmentBinding
    private val viewModel: GroupsViewModel by viewModel()
    private var groupsAdapter: GroupsAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = GroupsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        insertListeners()
        setGroupsRecyclerView()
    }

    override fun onStop() {
        super.onStop()
        groupsAdapter?.stopListening()
    }

    private fun insertListeners() {
        binding.loginForgotToolbar.setOnClickListener {
            goToMoreOptions()
        }

        binding.yourGroupsTextView.setOnClickListener {
            goChatGroup()
        }

        binding.loginForgotToolbar.menu.findItem(R.id.search).setOnMenuItemClickListener { item: MenuItem? ->
            when (item!!.itemId) {
                R.id.search -> {
                    findNavController().navigate(R.id.action_groupsFragment_to_groupsSearchFragment)
                }
            }
            true
        }
    }

    private fun goChatGroup() {
        findNavController().navigate(R.id.action_groupsFragment_to_chatGroupFragment)
    }

    private fun goToMoreOptions() {
        findNavController().popBackStack()
    }

    private fun setGroupsRecyclerView() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.createGroupsRecyclerOptions(Constants.THEME_INFO).collect { themeModel ->
                groupsAdapter = GroupsAdapter(themeModel, viewModel) { chatNavigation(it) }
                val linearLayout =
                    LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                binding.themesGroupsRecyclerView.layoutManager = linearLayout
                binding.themesGroupsRecyclerView.adapter = groupsAdapter
                groupsAdapter?.startListening()
            }
        }
    }

    private fun chatNavigation(theme: ThemeModel) {
        val bundle = bundleOf(Constants.CHAT_BUNDLE_NAME to theme)
        findNavController().navigate(R.id.action_dashboardFragment_to_chatGroupFragment, bundle)
    }
}
