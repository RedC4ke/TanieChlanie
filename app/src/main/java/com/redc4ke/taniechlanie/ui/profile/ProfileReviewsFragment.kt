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
import com.google.firebase.auth.FirebaseUser
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.profile.ProfileReviewAdapter
import com.redc4ke.taniechlanie.data.viewmodels.AlcoObjectViewModel
import com.redc4ke.taniechlanie.data.viewmodels.ReviewViewModel
import com.redc4ke.taniechlanie.databinding.FragmentProfileReviewBinding
import com.redc4ke.taniechlanie.ui.base.BaseFragment

class ProfileReviewsFragment: BaseFragment<FragmentProfileReviewBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentProfileReviewBinding
        get() = FragmentProfileReviewBinding::inflate
    private lateinit var alcoObjectViewModel: AlcoObjectViewModel
    private lateinit var reviewViewModel: ReviewViewModel
    private lateinit var madapter: ProfileReviewAdapter
    private var user: FirebaseUser? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        setTransitions(R.transition.slide_from_right, R.transition.slide_to_right)
        user = (parentFragment?.parentFragment as ProfileFragment).user

        alcoObjectViewModel = ViewModelProvider(requireActivity())
            .get(AlcoObjectViewModel::class.java)
        reviewViewModel = ViewModelProvider(requireActivity())
            .get(ReviewViewModel::class.java)

        if (user != null) {
            reviewViewModel.downloadUser(user!!.uid)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        madapter = ProfileReviewAdapter(requireContext(), listOf())

        if (user != null) {
            reviewViewModel.downloadUser(user!!.uid)
                .addOnSuccessListener {
                    val list = reviewViewModel.getForUser(user!!.uid, alcoObjectViewModel)
                    madapter.addData(list)
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), getString(R.string.error,
                        it.toString()), Toast.LENGTH_LONG).show()
                }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.profileReviewsRV.run {
            this.layoutManager = LinearLayoutManager(requireContext())
            this.adapter = madapter
        }
    }

    override fun onStart() {
        super.onStart()

        with (binding) {
            profileReviewsReturnBT.setOnClickListener {
                findNavController().navigate(R.id.action_profileReviews_dest_to_profileMenu_dest)
            }
        }
    }
}