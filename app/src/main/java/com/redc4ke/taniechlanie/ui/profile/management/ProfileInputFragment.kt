package com.redc4ke.taniechlanie.ui.profile.management

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.internal.TextWatcherAdapter
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseUser
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.viewmodels.UserViewModel
import com.redc4ke.taniechlanie.databinding.FragmentProfileInputBinding
import com.redc4ke.taniechlanie.ui.MainActivity
import com.redc4ke.taniechlanie.ui.base.BaseDialogFragment
import java.lang.Exception

class ProfileInputFragment(private val userViewModel: UserViewModel, private val action: Int):
    BaseDialogFragment<FragmentProfileInputBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) ->
    FragmentProfileInputBinding
        get() = FragmentProfileInputBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            val user = userViewModel.getUser().value!!
            step1(user)
        } catch (e: Exception) {
            this.dismiss()
        }

    }

    private fun step1(user: FirebaseUser) {

        with (binding) {
            cancelBT.setOnClickListener {
                this@ProfileInputFragment.dismiss()
            }
            when (action) {
                0 -> {
                    headerTV.text = getString(R.string.profile_editDisplayName)
                    displayNameTIL.visibility = View.VISIBLE
                    displaynameET.setText(user.displayName)
                    addWatcher(displaynameET, 0, 3, 30)
                }
                1 -> {
                    headerTV.text = getString(R.string.profile_editEmail)
                    emailTIL.visibility = View.VISIBLE
                    emailET.setText(user.email)
                    addWatcher(emailET, 1, 0, 128)
                }
                2 -> {
                    headerTV.text = getString(R.string.profile_editPassword)
                    passwordTIL.visibility = View.VISIBLE
                    passwordET.hint = getString(R.string.profile_hint_newpwd)
                    addWatcher(passwordET, 2, 6,128)
                }
                3 -> {
                    step2("")
                }
            }
        }
    }

    private fun addWatcher(editText: TextInputEditText, action: Int, minLength: Int,
                           maxLength: Int) {
        editText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                binding.forwardBT.setOnClickListener {}
                when {
                    s.toString().trim().isEmpty() -> editText.error =
                        getString(R.string.err_emptyField)
                    s!!.length < minLength -> editText.error =
                        getString(R.string.err_tooShort, minLength.toString())
                    s.length > maxLength -> editText.error =
                        getString(R.string.err_tooLong, maxLength.toString())
                    else -> when (action) {
                        0 -> {
                            binding.forwardBT.setOnClickListener {
                                step2(editText.text.toString())
                            }
                        }
                        1 -> {
                            if (s.matches(Regex("${Patterns.EMAIL_ADDRESS}"))) {
                                binding.forwardBT.setOnClickListener {
                                    step2(editText.text.toString())
                                }
                            } else {
                                editText.error = getString(R.string.err_wrongEmail)
                            }
                        }
                        2 -> {
                            binding.forwardBT.setOnClickListener {
                                step2(editText.text.toString())
                            }
                        }
                    }
                }
            }
        })
    }

    internal fun step2(value: String) {
        val user = userViewModel.getUser().value!!
        with (binding) {
            when (action) {
                0 -> {
                    userViewModel.setDisplayName(value)
                    this@ProfileInputFragment.dismiss()
                }
                1, 2, 3 -> {
                    step1FL.visibility = View.GONE
                    step2FL.visibility = View.VISIBLE
                    forwardBT.visibility = View.GONE
                    saveBT.visibility = View.VISIBLE
                    confirmET.hint = getString(R.string.profile_hint_currentpwd)
                    saveBT.setOnClickListener {
                        val credentials = EmailAuthProvider.getCredential(
                            user.email!!, confirmET.text.toString())
                        user.reauthenticate(credentials)
                            .addOnSuccessListener {
                                when (action) {
                                    1 -> {
                                        userViewModel.setEmail(value, requireContext())
                                    }
                                    2 -> {
                                        userViewModel.setPassword(value, requireContext())
                                    }
                                    3 -> {
                                        userViewModel
                                            .deleteAccount(requireActivity() as MainActivity)
                                    }
                                }
                                this@ProfileInputFragment.dismiss()

                            }
                            .addOnFailureListener {
                                confirmET.error = when (it) {
                                    is FirebaseAuthInvalidCredentialsException ->
                                        getString(R.string.err_invalidPassword)
                                    else -> it.toString()
                                }
                            }
                    }
                }
            }

        }
    }

}