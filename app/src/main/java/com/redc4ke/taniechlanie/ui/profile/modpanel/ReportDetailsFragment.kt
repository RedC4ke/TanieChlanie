package com.redc4ke.taniechlanie.ui.profile.modpanel

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.RequestListener
import com.redc4ke.taniechlanie.data.viewmodels.*
import com.redc4ke.taniechlanie.databinding.FragmentReportDetailsBinding
import com.redc4ke.taniechlanie.ui.MainActivity
import com.redc4ke.taniechlanie.ui.base.BaseFragment
import com.redc4ke.taniechlanie.ui.popup.SpinnerFragment
import java.text.DateFormat

class ReportDetailsFragment : BaseFragment<FragmentReportDetailsBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentReportDetailsBinding
        get() = FragmentReportDetailsBinding::inflate
    private lateinit var report: Report

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        report = arguments?.getSerializable("request") as Report
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
                override fun onComplete(resultCode: Int) {
                    if (resultCode == RequestListener.SUCCESS) {

                    } else {

                    }
                }
            }
            if (report.reportType == Report.ReportType.BOOZE) {
                // string array 'report_booze_actions'
                when (actionPosition) {
                    0 -> modpanelViewModel.deleteBooze(report.itemId.toLong(), listener)
                    1 -> modpanelViewModel.deletePhoto(report.itemId.toLong(), listener)
                    2 -> {}
                    3 -> {}
                    4 -> {}
                    5 -> {}
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