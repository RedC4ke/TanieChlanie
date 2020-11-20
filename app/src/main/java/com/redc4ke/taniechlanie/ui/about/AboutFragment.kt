package com.redc4ke.taniechlanie.ui.about

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.transition.TransitionInflater
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.redc4ke.taniechlanie.R

class AboutFragment : Fragment() {

    override fun onAttach(context: Context) {
        val actionBar = activity?.actionBar
        actionBar?.title = "O aplikacji"

        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide_down_about)
        exitTransition = inflater.inflateTransition(R.transition.slide_up_about)
        returnTransition = inflater.inflateTransition(R.transition.slide_up_about)
        allowEnterTransitionOverlap = false
        allowReturnTransitionOverlap = false

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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


