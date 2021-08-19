package com.redc4ke.taniechlanie.ui.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.FirebaseListener
import com.redc4ke.taniechlanie.data.profile.ProfileRequestAdapter
import com.redc4ke.taniechlanie.data.viewmodels.RequestViewModel
import com.redc4ke.taniechlanie.databinding.FragmentProfileRequestBinding
import com.redc4ke.taniechlanie.ui.MainActivity
import com.redc4ke.taniechlanie.ui.base.BaseFragment

class ProfileRequestFragment : BaseFragment<FragmentProfileRequestBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentProfileRequestBinding
        get() = FragmentProfileRequestBinding::inflate

    override fun onAttach(context: Context) {
        super.onAttach(context)

        setTransitions(R.transition.slide_from_right, R.transition.slide_to_right)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val requestViewModel =
            ViewModelProvider(requireActivity() as MainActivity)[RequestViewModel::class.java]
        val adapter = ProfileRequestAdapter(requireContext())

        requestViewModel.fetch(FirebaseAuth.getInstance().uid.toString(), object: FirebaseListener {
            override fun onComplete(resultCode: Int) {
                if (resultCode == FirebaseListener.OTHER) {
                    Toast.makeText(requireContext(), R.string.toast_error, Toast.LENGTH_LONG).show()
                }
            }
        })
        requestViewModel.getRequestList().observe(viewLifecycleOwner, {
            adapter.update(it)
        })

        binding.profileRequestRV.adapter = adapter

        binding.profileRequestReturnBT.setOnClickListener {
            findNavController().navigate(R.id.profileMenu_dest)
        }
    }

}