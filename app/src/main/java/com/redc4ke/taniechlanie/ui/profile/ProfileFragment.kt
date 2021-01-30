package com.redc4ke.taniechlanie.ui.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseUser
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.databinding.FragmentProfileBinding
import com.redc4ke.taniechlanie.ui.BaseFragment
import com.redc4ke.taniechlanie.ui.MainActivity
import com.squareup.picasso.Picasso

class ProfileFragment : BaseFragment<FragmentProfileBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentProfileBinding
        get() = FragmentProfileBinding::inflate
    private lateinit var user: FirebaseUser

    override fun onAttach(context: Context) {
        super.onAttach(context)
        user = (requireActivity() as MainActivity).auth.currentUser!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with (binding) {
            profilePictureIV.setImageResource(R.drawable.avatar)
            profileNameTV.text = user.displayName
        }
    }
}