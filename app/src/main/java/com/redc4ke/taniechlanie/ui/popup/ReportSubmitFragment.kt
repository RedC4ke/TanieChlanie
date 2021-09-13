package com.redc4ke.taniechlanie.ui.popup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.Toast
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.viewmodels.Report
import com.redc4ke.taniechlanie.data.viewmodels.Request
import com.redc4ke.taniechlanie.databinding.FragmentReportBinding
import com.redc4ke.taniechlanie.ui.base.BaseDialogFragment

class ReportSubmitFragment(private val reportType: Int, private val itemId: String) :
    BaseDialogFragment<FragmentReportBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentReportBinding
        get() = FragmentReportBinding::inflate
    private var selectedReason = 0

    override fun onStart() {
        super.onStart()

        dialog?.window?.setLayout(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            val reasonList: List<String>

            when (reportType) {
                Report.ReportType.BOOZE -> {
                    reasonList = resources.getStringArray(R.array.report_booze).toList()
                    reportHeaderTV.text = getString(R.string.report_booze_header)
                }
                Report.ReportType.REVIEW -> {
                    reasonList = resources.getStringArray(R.array.report_review).toList()
                    reportHeaderTV.text = getString(R.string.report_review_header)
                }
                else -> {
                    reasonList = listOf("n/a")
                }
            }

            reportSpinner.adapter = ArrayAdapter(requireContext(), R.layout.spinner1, reasonList)
            reportSpinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    selectedReason = position
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }

            reportCancelBT.setOnClickListener {
                dismiss()
            }

            reportSendBT.setOnClickListener {
                val user = FirebaseAuth.getInstance().currentUser
                if (user == null) {
                    Toast.makeText(requireContext(), R.string.toast_error, Toast.LENGTH_LONG).show()
                    dismiss()
                }

                val details = reportET.text.toString()

                val report = Report(
                    null,
                    reportType,
                    itemId,
                    selectedReason,
                    details,
                    user!!.uid,
                    Timestamp.now(),
                    Request.RequestState.PENDING,
                    null,
                    null
                )


            }
        }
    }


}