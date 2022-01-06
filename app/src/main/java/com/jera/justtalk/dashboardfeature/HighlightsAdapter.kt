package com.jera.justtalk.dashboardfeature

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.jera.justtalk.R
import com.jera.justtalk.databinding.ItemHighlightsBinding
import com.jera.justtalk.model.ThemeModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HighlightsAdapter(
    options: FirestoreRecyclerOptions<ThemeModel>,
    val dashboardViewModel: DashboardViewModel,
    val onCLick: (ThemeModel) -> Unit
) : FirestoreRecyclerAdapter<ThemeModel, HighlightsAdapter.HighlightsViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HighlightsViewHolder {
        return HighlightsViewHolder(
            ItemHighlightsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: HighlightsViewHolder, position: Int, model: ThemeModel) {
        holder.bind(model)
    }

    inner class HighlightsViewHolder(val binding: ItemHighlightsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(theme: ThemeModel) {
            dashboardViewModel.viewModelScope.launch {
                dashboardViewModel.getPhotoStorageReferenceFromUrl(theme.bannerurl).collect {
                    Glide.with(itemView)
                        .load(it)
                        .into(binding.highlightsThemeBannerImageView)
                }
            }
            val subsText: String = itemView.context.getString(R.string.themes_subscribers_text)
            binding.highlightsSubscribersTextView.text = String.format(subsText, theme.totalsubscribers)
            binding.highlightsTitleTextView.text = theme.title

            binding.highlightsThemeBannerImageView.setOnClickListener {
                onCLick.invoke(theme)
            }
        }
    }
}
