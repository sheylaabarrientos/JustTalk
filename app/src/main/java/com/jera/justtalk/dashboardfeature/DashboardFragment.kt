package com.jera.justtalk.dashboardfeature

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.jera.justtalk.R
import com.jera.justtalk.databinding.DashboardFragmentBinding
import com.jera.justtalk.model.ThemeModel
import com.jera.justtalk.ui.utils.AutoScroll
import com.jera.justtalk.ui.utils.Constants.Companion.CHAT_BUNDLE_NAME
import com.jera.justtalk.ui.utils.Constants.Companion.SHARED_PREFERENCES_NAME
import com.jera.justtalk.ui.utils.Constants.Companion.THEME_INFO
import com.jera.justtalk.ui.utils.ViewState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class DashboardFragment : Fragment() {

    private lateinit var binding: DashboardFragmentBinding
    private lateinit var sharedPreferences: SharedPreferences
    private val viewModel: DashboardViewModel by viewModel()
    private var themesAdapter: ThemesAdapter? = null
    private var highlightsAdapter: HighlightsAdapter? = null
    private var currentPosition: Int = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DashboardFragmentBinding.inflate(inflater, container, false)
        sharedPreferences =
            activity?.getSharedPreferences(
            SHARED_PREFERENCES_NAME,
            Context.MODE_PRIVATE
        ) as SharedPreferences
        viewModel.updateUser(sharedPreferences)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHighlightRecyclerView()
        setThemeRecyclerView()
        observeStateFlow()
    }

    override fun onStop() {
        super.onStop()
        themesAdapter?.stopListening()
        highlightsAdapter?.stopListening()
    }

    private fun bindView() {
        val circularProgressDrawable = context?.let { CircularProgressDrawable(it) }
        circularProgressDrawable?.strokeWidth = 5f
        circularProgressDrawable?.centerRadius = 30f
        circularProgressDrawable?.start()

        binding.usernameTextView.text = viewModel.currentUser?.name
        Glide.with(requireActivity())
            .load(viewModel.currentUser?.storageReference)
            .placeholder(circularProgressDrawable).circleCrop()
            .into(binding.imagePhotoProfileImageView)
    }

    private fun observeStateFlow() {
        lifecycleScope.launchWhenStarted {
            viewModel.userStateFlow.collect { userState ->
                when (userState) {
                    ViewState.Loading -> {
                        showProgressBar()
                    }
                    ViewState.Success -> {
                        bindView()
                        hideProgressBar()
                    }
                    ViewState.Error -> {
                        showToastError()
                        hideProgressBar()
                    }
                    else -> {
                    }
                }
            }
        }
    }

    private fun showProgressBar() {
        binding.dashboardProgressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.dashboardProgressBar.visibility = View.GONE
    }

    private fun showToastError() {
        Toast.makeText(
            activity,
            getString(R.string.dashboard_error_toast_message),
            Toast.LENGTH_LONG
        ).show()
    }

    private fun setThemeRecyclerView() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.createThemeRecyclerOptions(THEME_INFO).collect { themeModel ->
                themesAdapter = ThemesAdapter(themeModel, viewModel) { chatNavigation(it) }
                binding.themesRecyclerView.layoutManager = GridLayoutManager(context, 2)
                binding.themesRecyclerView.adapter = themesAdapter
                themesAdapter?.startListening()
            }
        }
    }

    private fun setHighlightRecyclerView() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.createHighlightsRecyclerOptions(THEME_INFO).collect { themeModel ->
                highlightsAdapter = HighlightsAdapter(themeModel, viewModel) { chatNavigation(it) }
                val linearLayout = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                binding.highlightsRecyclerView.layoutManager = linearLayout
                binding.highlightsRecyclerView.adapter = highlightsAdapter
                highlightsAdapter?.startListening()
                binding.themesRecyclerView.post {
                    val timer = Timer()
                    timer.schedule(AutoScroll { scrollToNextItem() }, 5000, 3000)
                }
            }
        }
    }

    private fun scrollToNextItem() {
        if (currentPosition != (highlightsAdapter?.itemCount?.minus(1))) {
            binding.highlightsRecyclerView.smoothScrollToPosition(currentPosition + 1)
            currentPosition++
        } else {
            currentPosition = 0
            binding.highlightsRecyclerView.smoothScrollToPosition(currentPosition)
        }
    }

    private fun chatNavigation(theme: ThemeModel) {
        val bundle = bundleOf(CHAT_BUNDLE_NAME to theme)
        findNavController().navigate(R.id.action_dashboardFragment_to_chatGroupFragment, bundle)
    }
}
