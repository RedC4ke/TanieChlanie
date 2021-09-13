package com.redc4ke.taniechlanie.ui.menu.details

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.transition.MaterialContainerTransform
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.*
import com.redc4ke.taniechlanie.data.viewmodels.Report
import com.redc4ke.taniechlanie.data.viewmodels.UserViewModel
import com.redc4ke.taniechlanie.databinding.FragmentDetailsBinding
import com.redc4ke.taniechlanie.ui.base.BaseFragment
import com.redc4ke.taniechlanie.ui.MainActivity
import com.redc4ke.taniechlanie.ui.popup.ReportSubmitFragment

class DetailsFragment : BaseFragment<FragmentDetailsBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentDetailsBinding
        get() = FragmentDetailsBinding::inflate
    private lateinit var mainActivity: MainActivity
    lateinit var alcoObject: AlcoObject
    lateinit var userViewModel: UserViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)

        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainActivity = requireActivity() as MainActivity
        alcoObject = arguments?.getSerializable("alcoObject") as AlcoObject

        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.alcoListNH
            duration = 500
        }

        sharedElementReturnTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.alcoListNH
            duration = 200
        }

        (requireActivity() as MainActivity).supportActionBar!!.hide()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = super.onCreateView(inflater, container, savedInstanceState)
        binding.nameDetails.text = alcoObject.name

        // Inflate the layout for this fragment
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val list = userViewModel.getFavourites().value.orEmpty()
        binding.favouriteCHB.isChecked = list.contains(alcoObject.id.toLong())

        binding.favouriteCHB.setOnCheckedChangeListener { _, isChecked ->
            when {
                (userViewModel.getUser().value == null) -> {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.err_notloggedin),
                        Toast.LENGTH_LONG
                    ).show()
                    binding.favouriteCHB.isChecked = false
                    (requireActivity() as MainActivity).login()
                }
                (isChecked) -> userViewModel.addFavourite(requireContext(), alcoObject)
                else -> userViewModel.removeFavourite(requireContext(), alcoObject)
            }
        }

        if (alcoObject.photo != null) {
            binding.imageDetails.adjustViewBounds = true
            Glide.with(requireContext()).load(alcoObject.photo).into(binding.imageDetails)
        }

        binding.detailsReportBT.setOnClickListener {
            val reportDialog =
                ReportSubmitFragment(Report.ReportType.BOOZE, alcoObject.id.toString())
            reportDialog.show(parentFragmentManager, "reportSubmitFragment")
        }

        binding.detailsBackBT.setOnClickListener {
            findNavController().navigate(R.id.details_dest_pop)
        }
    }
}