package com.redc4ke.taniechlanie.ui.about

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.redc4ke.taniechlanie.BuildConfig
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.about.AboutRecyclerViewAdapter
import com.redc4ke.taniechlanie.databinding.FragmentAboutBinding
import com.redc4ke.taniechlanie.ui.base.BaseFragment
import com.redc4ke.taniechlanie.ui.MainActivity

class AboutFragment : BaseFragment<FragmentAboutBinding>() {

    private lateinit var mainActivity: MainActivity
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentAboutBinding
        get() = FragmentAboutBinding::inflate

    override fun onAttach(context: Context) {
        val actionBar = activity?.actionBar
        actionBar?.title = "O aplikacji"

        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainActivity = requireActivity() as MainActivity

        setTransitions(R.transition.slide_down_about, R.transition.slide_up_about)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = binding.aboutRV
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = AboutRecyclerViewAdapter(this)

        binding.aboutVersionTV.text = BuildConfig.VERSION_NAME

        cardShape(view.findViewById(R.id.about_CV))
    }
}

fun cardShape(cv: CardView) {
    val shapeAppearanceModel: ShapeAppearanceModel = ShapeAppearanceModel()
            .toBuilder()
            .setAllCorners(CornerFamily.ROUNDED, 0F)
            .setBottomLeftCorner(CornerFamily.ROUNDED, 40F)
            .setBottomRightCorner(CornerFamily.ROUNDED, 40F)
            .build()

    val shapeDrawable = MaterialShapeDrawable(shapeAppearanceModel)
    cv.background = shapeDrawable

}


