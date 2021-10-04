package com.redc4ke.taniechlanie.ui.profile.modpanel

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.RequestListener
import com.redc4ke.taniechlanie.data.viewmodels.*
import com.redc4ke.taniechlanie.databinding.FragmentReportDetailsBinding
import com.redc4ke.taniechlanie.ui.MainActivity
import com.redc4ke.taniechlanie.ui.base.BaseFragment
import com.redc4ke.taniechlanie.ui.popup.BoozeDataChangeFragment
import com.redc4ke.taniechlanie.ui.popup.CategoryAddRemoveFragment
import com.redc4ke.taniechlanie.ui.popup.SpinnerFragment
import java.text.DateFormat

class ReportDetailsFragment : BaseFragment<FragmentReportDetailsBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentReportDetailsBinding
        get() = FragmentReportDetailsBinding::inflate
    private lateinit var report: Report

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val reportNullable = arguments?.getSerializable("request") as? Report
        if (reportNullable != null) {
            report = reportNullable
        } else {
            findNavController().navigateUp()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val provider = ViewModelProvider(requireActivity() as MainActivity)
        val alcoObjectViewModel = provider[AlcoObjectViewModel::class.java]
        val reviewViewModel = provider[ReviewViewModel::class.java]
        val userViewModel = provider[UserViewModel::class.java]
        val modpanelViewModel = provider[ModpanelViewModel::class.java]

        childFragmentManager.setFragmentResultListener(
            "spinnerResult", viewLifecycleOwner
        ) { _, bundle ->
            val actionPosition = bundle.getInt("spinnerPosition")
            val listener = object : RequestListener {
                // todo kinda clunky logic ngl
                override fun onComplete(resultCode: Int) {
                    // if result is success
                    if (resultCode == RequestListener.SUCCESS) {
                        // if action is other than pass the report forward
                        if (actionPosition != 6) {
                            modpanelViewModel.completeReport(
                                report,
                                true,
                                object : RequestListener {
                                    override fun onComplete(resultCode: Int) {
                                        if (resultCode == RequestListener.SUCCESS) {
                                            Toast.makeText(
                                                requireContext(),
                                                getString(R.string.success),
                                                Toast.LENGTH_LONG
                                            ).show()

                                            findNavController().navigateUp()
                                        } else {
                                            Toast.makeText(
                                                requireContext(),
                                                getString(R.string.toast_error),
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                }
                            )
                        } else {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.success),
                                Toast.LENGTH_LONG
                            ).show()
                            findNavController().navigateUp()
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.toast_error),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
            if (report.reportType == Report.ReportType.BOOZE) {
                // string array 'report_booze_actions'
                when (actionPosition) {
                    0 -> alcoObjectViewModel.deleteBooze(report.itemId.toLong(), listener)
                    1 -> alcoObjectViewModel.deletePhoto(report.itemId.toLong(), listener)
                    2 -> BoozeDataChangeFragment(
                        report.itemId.toLong(),
                        listener
                    )
                        .show(childFragmentManager, "boozeDataChange")
                    3 -> CategoryAddRemoveFragment(
                        CategoryAddRemoveFragment.ADD,
                        report.itemId.toLong(),
                        listener
                    )
                        .show(childFragmentManager, "categoryAddRemove")
                    4 -> CategoryAddRemoveFragment(
                        CategoryAddRemoveFragment.REMOVE,
                        report.itemId.toLong(),
                        listener
                    )
                        .show(childFragmentManager, "categoryAddRemove")
                    5 -> CategoryAddRemoveFragment(
                        CategoryAddRemoveFragment.CHANGE_MAJOR,
                        report.itemId.toLong(),
                        listener
                    )
                        .show(childFragmentManager, "categoryAddRemove")
                    6 -> modpanelViewModel.sendReportForward(report, listener)
                    7 -> modpanelViewModel.blockReporting(report.author, listener)
                }
            } else {
                // string array 'report_review_actions'
                when (actionPosition) {
                    0 -> reviewViewModel.removeById(report.itemId, listener)
                    1 -> {
                        reviewViewModel.removeById(report.itemId, listener)
                        //TODO function to get review author and block them
                    }
                    2 -> modpanelViewModel.blockReporting(report.author, listener)
                }
            }

            //temp
            childFragmentManager.setFragmentResult(
                "spinnerParent",
                bundleOf("listenerResult" to RequestListener.SUCCESS)
            )
        }

        with(binding) {
            repDetailsTV.text = report.details

            val spinnerFragment: SpinnerFragment

            if (report.reportType == Report.ReportType.BOOZE) {
                val item = alcoObjectViewModel.get(report.itemId.toLong())

                repDetailsReasonTV.text =
                    resources.getStringArray(R.array.report_booze)[report.reason]
                repDetailsItemTV.text = item?.name
                Glide
                    .with(requireContext())
                    .load(item?.photo)
                    .into(repDetailsPhotoIV)

            } else {
                repDetailsPhotoIV.visibility = View.GONE
                repDetailsReviewINCL.root.visibility = View.VISIBLE

                repDetailsReasonTV.text =
                    resources.getStringArray(R.array.report_review)[report.reason]
                repDetailsItemTV.text = getString(R.string.review)

                reviewViewModel.getReview(report.itemId).observe(viewLifecycleOwner, {
                    if (it != null) {
                        userViewModel.getOtherUser(it.author).observe(viewLifecycleOwner, { user ->
                            repDetailsReviewINCL.usernameTV.text = user?.name
                            Glide
                                .with(requireContext())
                                .load(user?.avatar)
                                .into(repDetailsReviewINCL.avatarIV)
                        })
                        repDetailsReviewINCL.timestampTV.text =
                            DateFormat.getDateInstance().format(it.timestamp.toDate())
                        repDetailsReviewINCL.reviewRB.rating = it.rating.toFloat()
                        repDetailsReviewINCL.contentTV.text = it.content
                    }
                })
            }

            repDetailsAcceptBT.setOnClickListener {
                SpinnerFragment(
                    resources.getStringArray(R.array.report_booze_actions).toList()
                ).show(childFragmentManager, "spinnerFragment")
            }
        }
    }
}

fun stub(int: Int) {}