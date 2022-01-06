package com.jera.justtalk.dashboardfeature

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.jera.justtalk.R
import com.jera.justtalk.databinding.ItemThemeBinding
import com.jera.justtalk.model.ThemeModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ThemesAdapter(
    options: FirestoreRecyclerOptions<ThemeModel>,
    val dashboardViewModel: DashboardViewModel,
    val onCLick: (ThemeModel) -> Unit
) : FirestoreRecyclerAdapter<ThemeModel, ThemesAdapter.ThemesViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThemesViewHolder {
        return ThemesViewHolder(
            ItemThemeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ThemesViewHolder, position: Int, model: ThemeModel) {
        holder.bind(model)
    }

    inner class ThemesViewHolder(val binding: ItemThemeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(theme: ThemeModel) {
            dashboardViewModel.viewModelScope.launch {
                dashboardViewModel.getPhotoStorageReferenceFromUrl(theme.bannerurl).collect {
                    Glide.with(itemView)
                        .load(it)
                        .into(binding.motivationThemeBannerImageView)
                }
            }
            val subsText: String = itemView.context.getString(R.string.themes_subscribers_text)
            binding.themesSubscribersTextView.text = String.format(subsText, theme.totalsubscribers)
            binding.themeTitleTextView.text = theme.title

            binding.motivationThemeBannerImageView.setOnClickListener {
                onCLick.invoke(theme)
            }
        }
    }
}
