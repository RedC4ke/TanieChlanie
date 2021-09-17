package com.redc4ke.taniechlanie.ui.profile.modpanel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.viewmodels.AlcoObjectViewModel
import com.redc4ke.taniechlanie.data.viewmodels.Report
import com.redc4ke.taniechlanie.databinding.FragmentReportDetailsBinding
import com.redc4ke.taniechlanie.ui.MainActivity
import com.redc4ke.taniechlanie.ui.base.BaseFragment

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

        val alcoObjectViewModel =
            ViewModelProvider(requireActivity() as MainActivity)[AlcoObjectViewModel::class.java]

        with(binding) {
            if (report.reportType == Report.ReportType.BOOZE) {
                val item = alcoObjectViewModel.get(report.itemId.toLong())

                repDetailsReasonTV.text =
                    resources.getStringArray(R.array.report_booze)[report.reason]
                repDetailsTV.text = report.details
                repDetailsItemTV.text = item?.name
                Glide
                    .with(requireContext())
                    .load(item?.photo)
                    .into(repDetailsPhotoIV)
            } else {
                val item =
            }
        }
    }
}