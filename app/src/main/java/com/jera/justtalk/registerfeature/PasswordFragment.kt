package com.jera.justtalk.registerfeature

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.jera.justtalk.R
import com.jera.justtalk.databinding.PasswordFragmentBinding
import com.jera.justtalk.ui.utils.onTextChanged
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class PasswordFragment : Fragment() {

    private lateinit var binding: PasswordFragmentBinding
    private val viewModel: AuthViewModel by sharedViewModel()
    private lateinit var firstDigit: EditText
    private lateinit var secondDigit: EditText
    private lateinit var thirdDigit: EditText
    private lateinit var fourthDigit: EditText
    private lateinit var fifthDigit: EditText
    private lateinit var sixthDigit: EditText
    private lateinit var seventhDigit: EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = PasswordFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firstDigit = binding.passwordFirstDigitEditText
        secondDigit = binding.passwordSecondDigitEditText
        thirdDigit = binding.passwordThirdDigitEditText
        fourthDigit = binding.passwordFourthDigitEditText
        fifthDigit = binding.passwordFifthDigitEditText
        sixthDigit = binding.passwordSixthDigitEditText
        seventhDigit = binding.passwordSeventhDigitEditText

        insertListeners()
        requestFocusBehavior()
        binding.passwordFirstDigitEditText.requestFocus()
    }

    private fun insertListeners() {
        binding.passwordSixthDigitEditText.onTextChanged {
            updateUserPassword()
        }

        binding.passwordSeventhDigitEditText.onTextChanged {
            updateUserPassword()
        }

        binding.passwordNextAppCompatButton.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                if (viewModel.isPasswordValid()) {
                    navigateToUserPhotoFragment()
                } else {
                    showErrorToast()
                }
            }
        }

        binding.passwordToggleButton.setOnClickListener {
            togglePassword()
        }

        binding.passwordBackImageButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.enterPasswordTextView.setOnClickListener {
            navigateToLoginFragment()
        }
    }

    private fun updateUserPassword() {
        viewModel.updatePassword(
            firstDigit.text.toString(),
            secondDigit.text.toString(),
            thirdDigit.text.toString(),
            fourthDigit.text.toString(),
            fifthDigit.text.toString(),
            sixthDigit.text.toString(),
            seventhDigit.text.toString()
        )
    }

    private fun togglePassword() {

        if (binding.passwordToggleButton.isChecked) {
            firstDigit.transformationMethod = HideReturnsTransformationMethod.getInstance()
            secondDigit.transformationMethod = HideReturnsTransformationMethod.getInstance()
            thirdDigit.transformationMethod = HideReturnsTransformationMethod.getInstance()
            fourthDigit.transformationMethod = HideReturnsTransformationMethod.getInstance()
            fifthDigit.transformationMethod = HideReturnsTransformationMethod.getInstance()
            sixthDigit.transformationMethod = HideReturnsTransformationMethod.getInstance()
            seventhDigit.transformationMethod = HideReturnsTransformationMethod.getInstance()
        } else {
            firstDigit.transformationMethod = PasswordTransformationMethod.getInstance()
            secondDigit.transformationMethod = PasswordTransformationMethod.getInstance()
            thirdDigit.transformationMethod = PasswordTransformationMethod.getInstance()
            fourthDigit.transformationMethod = PasswordTransformationMethod.getInstance()
            fifthDigit.transformationMethod = PasswordTransformationMethod.getInstance()
            sixthDigit.transformationMethod = PasswordTransformationMethod.getInstance()
            seventhDigit.transformationMethod = PasswordTransformationMethod.getInstance()
        }
    }

    private fun requestFocusBehavior() {
        changeFocusEditText(firstDigit, null, secondDigit)
        changeFocusEditText(secondDigit, firstDigit, thirdDigit)
        changeFocusEditText(thirdDigit, secondDigit, fourthDigit)
        changeFocusEditText(fourthDigit, thirdDigit, fifthDigit)
        changeFocusEditText(fifthDigit, fourthDigit, sixthDigit)
        changeFocusEditText(sixthDigit, fifthDigit, seventhDigit)
        changeFocusEditText(seventhDigit, sixthDigit, null)
    }

    private fun changeFocusEditText(
        currentDigit: EditText,
        previousDigit: EditText?,
        nextDigit: EditText?
    ) {

        currentDigit.onTextChanged {
            if (nextDigit != null) {
                next(currentDigit, nextDigit)
            }
        }

        currentDigit.setOnKeyListener(
            View.OnKeyListener { _, keycode, _ ->
                if (keycode == KeyEvent.KEYCODE_DEL) {
                    currentDigit.text = null
                    if (previousDigit != null) {
                        back(previousDigit)
                    } else {
                        binding.passwordFirstDigitEditText.requestFocus()
                    }
                    return@OnKeyListener true
                } else {
                    next(currentDigit, nextDigit)
                }
                false
            }
        )
    }

    private fun next(currentDigit: EditText, nextDigit: EditText?) {
        currentDigit.addTextChangedListener {
            nextDigit?.requestFocus()
        }
    }

    private fun back(previousDigit: EditText?) {
        previousDigit?.requestFocus()
    }

    private fun navigateToUserPhotoFragment() {
        findNavController().navigate(R.id.action_passwordFragment_to_userPhotoFragment)
    }

    private fun navigateToLoginFragment() {
        findNavController().navigate(R.id.action_global_loginFragment)
    }

    private fun showErrorToast() {
        Toast.makeText(activity, getString(R.string.error_password_toast), Toast.LENGTH_LONG).show()
    }
}
