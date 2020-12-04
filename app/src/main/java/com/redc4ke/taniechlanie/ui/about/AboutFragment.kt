package com.redc4ke.taniechlanie.ui.about

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.redc4ke.taniechlanie.BuildConfig
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.about.AboutRecyclerViewAdapter
import com.redc4ke.taniechlanie.ui.MainActivity
import com.redc4ke.taniechlanie.ui.setTransitions
import kotlinx.android.synthetic.main.fragment_about.*

class AboutFragment : Fragment() {

    private lateinit var mainActivity: MainActivity

    override fun onAttach(context: Context) {
        val actionBar = activity?.actionBar
        actionBar?.title = "O aplikacji"

        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainActivity = requireActivity() as MainActivity

        setTransitions(this, R.transition.slide_down_about, R.transition.slide_up_about)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_about, container, false)
        val recyclerView = root.findViewById<RecyclerView>(R.id.about_RV)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = AboutRecyclerViewAdapter(this)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        about_version_TV.text = BuildConfig.VERSION_NAME


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


