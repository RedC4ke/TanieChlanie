package com.redc4ke.taniechlanie.ui.profile.management

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
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
                    forwardBT.setOnClickListener {
                        step2(displaynameET.text.toString(), user)
                    }
                }
                1 -> {
                    headerTV.text = getString(R.string.profile_editEmail)
                    emailTIL.visibility = View.VISIBLE
                    emailET.setText(user.email)
                    forwardBT.setOnClickListener {
                        step2(emailET.text.toString(), user)
                    }
                }
                2 -> {
                    headerTV.text = getString(R.string.profile_editPassword)
                    passwordTIL.visibility = View.VISIBLE
                    passwordET.hint = getString(R.string.profile_hint_newpwd)
                    forwardBT.setOnClickListener {
                        step2(passwordET.text.toString(), user)
                    }
                }
                3 -> {
                    step2("", user)
                }
            }
        }
    }

    private fun step2(value: String, user: FirebaseUser) {
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
                                Toast.makeText(context, "Błąd: $it",
                                    Toast.LENGTH_LONG).show()
                            }
                    }
                }
            }

        }
    }

}