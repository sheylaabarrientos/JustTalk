package com.jera.justtalk.navigationbarfeature

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.jera.justtalk.R
import com.jera.justtalk.databinding.ItemThemeGroupsBinding
import com.jera.justtalk.model.ThemeModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class GroupsAdapter(
    options: FirestoreRecyclerOptions<ThemeModel>,
    val groupsViewModel: GroupsViewModel,
    val onCLick: (ThemeModel) -> Unit
) : FirestoreRecyclerAdapter<ThemeModel, GroupsAdapter.GroupsViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupsViewHolder {
        return GroupsViewHolder(
            ItemThemeGroupsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: GroupsViewHolder, position: Int, model: ThemeModel) {
        holder.bind(model)
    }

    inner class GroupsViewHolder(val binding: ItemThemeGroupsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(theme: ThemeModel) {
            groupsViewModel.viewModelScope.launch {
                groupsViewModel.getPhotoStorageReferenceFromUrl(theme.bannerurl).collect {
                    Glide.with(itemView)
                        .load(it)
                        .into(binding.sleepThemeBannerImageView)
                }
            }

            val subsText: String = itemView.context.getString(R.string.themes_subscribers_text)
            binding.themesSubscribersTextView.text = String.format(subsText, theme.totalsubscribers)
            binding.themeTitleTextView.text = theme.title

            binding.sleepThemeBannerImageView.setOnClickListener {
                onCLick.invoke(theme)
            }
        }
    }
}
