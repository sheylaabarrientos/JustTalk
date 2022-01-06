package com.jera.justtalk.navigationbarfeature

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.jera.justtalk.R
import com.jera.justtalk.databinding.GroupsSearchFragmentBinding
import com.jera.justtalk.model.ThemeModel
import com.jera.justtalk.ui.utils.Constants
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class GroupsSearchFragment : Fragment() {

    private lateinit var binding: GroupsSearchFragmentBinding
    private val viewModel: GroupsViewModel by viewModel()
    private var groupsAdapter: GroupsAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = GroupsSearchFragmentBinding.inflate(inflater, container, false)
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
        binding.searchAppBar.setOnClickListener {
            goToGroups()
        }
    }

    private fun goToGroups() {
        findNavController().popBackStack()
    }

    private fun setGroupsRecyclerView() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.createGroupsRecyclerOptions(Constants.THEME_INFO).collect { themeModel ->
                groupsAdapter = GroupsAdapter(themeModel, viewModel) { chatNavigation(it) }
                val linearLayout =
                    LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                binding.searchRecyclerView.layoutManager = linearLayout
                binding.searchRecyclerView.adapter = groupsAdapter
                groupsAdapter?.startListening()
            }
        }
    }

    private fun chatNavigation(theme: ThemeModel) {
        val bundle = bundleOf(Constants.CHAT_BUNDLE_NAME to theme)
        findNavController().navigate(R.id.action_dashboardFragment_to_chatGroupFragment, bundle)
    }
}
