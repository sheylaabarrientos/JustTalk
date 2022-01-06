package com.jera.justtalk.chatfeature

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.jera.justtalk.R
import com.jera.justtalk.databinding.ChatGroupFragmentBinding
import com.jera.justtalk.ui.utils.Constants.Companion.SHARED_PREFERENCES_NAME
import com.jera.justtalk.ui.utils.ViewState
import com.jera.justtalk.ui.utils.onTextChanged
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChatGroupFragment : Fragment() {

    private lateinit var binding: ChatGroupFragmentBinding
    private lateinit var sharedPreferences: SharedPreferences
    private val args: ChatGroupFragmentArgs by navArgs()
    private val viewModel: ChatViewModel by viewModel()
    private var chatAdapter: ChatAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ChatGroupFragmentBinding.inflate(inflater, container, false)
        sharedPreferences =
            activity?.getSharedPreferences(
            SHARED_PREFERENCES_NAME,
            Context.MODE_PRIVATE
        ) as SharedPreferences
        viewModel.updateUser(sharedPreferences)
        viewModel.prepareMessage()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeChatStateFlow()
        setRecyclerView()
        insertListeners()
        bindView()
    }

    private fun bindView() {
        viewLifecycleOwner.lifecycleScope.launch {
            binding.titleThemeTextView.text = args.theme?.title
            args.theme?.bannerurl?.let { bannerUrl ->
                viewModel.getReferenceFromUrl(bannerUrl).collect { storageReference ->
                    Glide.with(requireActivity())
                        .load(storageReference)
                        .into(binding.chatGroupBannerImageView)
                }
            }
            viewModel.currentUser?.storageReference?.let { userStorageReference ->
                viewModel.getReferenceFromUrl(userStorageReference)
                    .collect { firebaseStorageReference ->
                        Glide.with(requireActivity())
                            .load(firebaseStorageReference)
                            .circleCrop()
                            .into(binding.photoProfileChatImageView)
                    }
            }
        }
    }

    private fun insertListeners() {
        binding.chatToolbar.setNavigationOnClickListener {
            backToGroups()
        }

        binding.chatGroupAddTextEditText.onTextChanged { message ->
            viewModel.updateMessageText(message)
        }

        binding.chatGroupSendMessageButton.setOnClickListener {
            if (viewModel.isMessageNull()) {
                showMessageError()
            } else {
                args.theme?.let { theme -> viewModel.sendMessage(theme.title) }
            }
        }
    }

    private fun setRecyclerView() {
        viewLifecycleOwner.lifecycleScope.launch {
            args.theme?.title?.let { title ->
                viewModel.createChatRecyclerOptions(title).collect { options ->
                    chatAdapter = ChatAdapter(options, viewModel)
                    binding.chatGroupMessageRecyclerView.layoutManager =
                        LinearLayoutManager(context)
                    binding.chatGroupMessageRecyclerView.adapter = chatAdapter
                    chatAdapter?.startListening()
                }
            }
        }
    }

    private fun observeChatStateFlow() {
        lifecycleScope.launchWhenStarted {
            viewModel.chatStateFlow.collect { chatState ->
                when (chatState) {
                    ViewState.Loading -> {
                        showSendingToast()
                        hideSendButton()
                    }
                    ViewState.Success -> {
                        showSendButton()
                        resetMessageField()
                        scrollToLastMessage()
                    }
                    ViewState.Error -> {
                        showMessageError()
                    }
                    else -> {
                    }
                }
            }
        }
    }

    private fun scrollToLastMessage() {
        val messageCount = chatAdapter?.itemCount
        if (messageCount != null && messageCount > 1) {
            binding.chatGroupMessageRecyclerView.smoothScrollToPosition(messageCount - 1)
        }
    }

    private fun resetMessageField() {
        binding.chatGroupAddTextEditText.text.clear()
    }

    private fun showSendingToast() {
        val toast = Toast.makeText(context, getString(R.string.sending_toast), Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }

    private fun hideSendButton() {
        binding.chatGroupSendMessageButton.visibility = View.GONE
    }

    private fun showSendButton() {
        binding.chatGroupSendMessageButton.visibility = View.VISIBLE
    }

    private fun showMessageError() {
        Toast.makeText(context, R.string.insert_a_message, Toast.LENGTH_LONG).show()
    }

    private fun backToGroups() {
        findNavController().popBackStack()
    }
}
