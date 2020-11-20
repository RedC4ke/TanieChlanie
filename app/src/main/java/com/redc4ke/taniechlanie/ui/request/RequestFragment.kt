package com.redc4ke.taniechlanie.ui.request

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.transition.TransitionInflater
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.ItemCategory
import kotlinx.android.synthetic.main.fragment_request.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.Serializable
import kotlin.collections.ArrayList

class RequestFragment : Fragment(), Serializable {

    var categoryList: MutableList<ItemCategory?> = mutableListOf()
    private lateinit var buttonList: ArrayList<MaterialButton>
    private lateinit var containerList: ArrayList<ViewGroup>
    private var catButtonPosition = 0
    private var pickImage: Int = 1
    object Transitions {
        var enter: Int = R.transition.slide_from_right
        var exit: Int = R.transition.slide_to_right
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(Transitions.enter)
        reenterTransition = inflater.inflateTransition(Transitions.enter)
        exitTransition = inflater.inflateTransition(Transitions.exit)
        returnTransition = inflater.inflateTransition(Transitions.exit)
        allowEnterTransitionOverlap = true
        allowReturnTransitionOverlap = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_request, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        image_req_BT.setOnClickListener {
            val getIntent = Intent(Intent.ACTION_GET_CONTENT)
            getIntent.type = "image/*"
            startActivityForResult(getIntent, pickImage)
        }

        buttonList = arrayListOf(
                cat_req_BT1, cat_req_BT2, cat_req_BT3, cat_req_BT4)
        containerList = arrayListOf(
                cat_FL1, cat_FL2, cat_FL3, cat_FL4)

        categorySetOnClickListener(0)
        refreshCategories()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == pickImage)
            getImage(data)

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun getImage(data: Intent?) {
        if (data == null) {
            Toast.makeText(context, "Błąd", Toast.LENGTH_LONG).show()
            return
        } else {
            val inputStream = context?.contentResolver?.openInputStream(data.data!!)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val path = data.data!!.path

            val f = File(requireContext().cacheDir, "upload.jpeg")
            f.createNewFile()

            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
            val bitmapData = bos.toByteArray()

            val fos = FileOutputStream(f)
            fos.write(bitmapData)
            fos.flush()
            fos.close()

            image_req_desc.text = path.toString()
        }
    }

    private fun openCategoryList() {
        val directions = RequestFragmentDirections.toCategory(this)
        findNavController().navigate(directions)
    }

    private fun categorySetOnClickListener(pos: Int) {
        val bt = buttonList[pos]
        bt.setTextColor(ContextCompat.getColor(requireContext(), R.color.red_heavy))
        bt.setIconTintResource(R.color.red_heavy)
        bt.setOnClickListener {
            openCategoryList()
            catButtonPosition = pos
        }
    }

    //Manages category fragment result
    fun onCategoryResult(cat: ItemCategory?, pos: Int = catButtonPosition) {
        //If category is null, delete from list or do nothing
        if (cat == null) {
            if (pos < categoryList.size && categoryList[pos] != null) {
                categoryList.removeAt(pos)
            }
        //If there is category, replace or add to category list on right index
        } else {
            if (pos < categoryList.size) {
                categoryList[pos] = cat
            } else {
                categoryList.add(cat)
            }
        }
    }

    private fun refreshCategories() {
        var i = 0
        categoryList.forEach {
            val parent = containerList[i]
            Log.d("huj",parent.toString())
            layoutInflater.inflate(R.layout.row_details_category_twin,
                    parent, true) as ViewGroup
            parent.findViewById<TextView>(R.id.details_category_twin_TV).text = it!!.name
            parent.findViewById<ImageView>(R.id.drawable_details_categoryTwin)
                    .setBackgroundResource(it.icon)
            categorySetOnClickListener(i)
            i++
        }
        if (i<4) categorySetOnClickListener(i++)
    }
}