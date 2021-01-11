package com.redc4ke.taniechlanie.ui.request

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.AlcoObject
import com.redc4ke.taniechlanie.data.Category
import com.redc4ke.taniechlanie.ui.BaseFragment
import com.redc4ke.taniechlanie.ui.MainActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_request.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.Serializable
import kotlin.collections.ArrayList

class RequestFragment : BaseFragment(), Serializable {

    var categoryList: MutableList<Category> = mutableListOf()
    private lateinit var buttonList: ArrayList<MaterialButton>
    private lateinit var containerList: ArrayList<ViewGroup>
    private lateinit var mainActivity: MainActivity
    private var catButtonPosition = 0
    private var pickImage: Int = 1
    private var hasImage = false

    override fun onAttach(context: Context) {
        mainActivity = requireActivity() as MainActivity

        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTransitions(R.transition.slide_from_right, R.transition.slide_to_right)
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

        req_upload_BT.setOnClickListener {
            if (proceedCheck()) {
                setTransitions(
                    R.transition.slide_from_left,
                    R.transition.slide_to_left)
                openShopList()
            }
        }
        addTextChangedListeners()
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

            val f = File(requireActivity().cacheDir, "upload.jpg")
            f.createNewFile()

            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
            val bitmapData = bos.toByteArray()

            val fos = FileOutputStream(f)
            fos.write(bitmapData)
            fos.flush()
            fos.close()

            image_req_desc.text = path.toString()


            hasImage = true
        }
    }

    private fun openShopList() {
        val directions = RequestFragmentDirections.toShopFragment(gatherObjectInfo(), hasImage)
        findNavController().navigate(directions)
    }

    private fun openCategoryList() {
        val directions = RequestFragmentDirections.toCategory(this)
        findNavController().navigate(directions)
    }

    private fun gatherObjectInfo(): AlcoObject {
        val categoryIdList = arrayListOf<Int>()
        categoryList.forEach {
            categoryIdList.add(it.id)
        }
        return AlcoObject(
                id = 0,
                name = name_ET.text.toString(),
                minPrice = price1_ET.text.toString().replace(",",".").toBigDecimal(),
                maxPrice = null,
                promoPrice = null,
                volume = volume_ET.text.toString().toInt(),
                voltage = voltage_ET.text.toString().replace(",",".")
                        .toBigDecimal().divide(100.toBigDecimal()),
                shop = arrayListOf(),
                categories = categoryIdList,
                photo = null
        )
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
    fun onCategoryResult(cat: Category?, pos: Int = catButtonPosition) {
        //If category is null, delete from list or do nothing
        if (cat == null) {
            if (pos < categoryList.size) {
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

    //Redraws the category list
    private fun refreshCategories() {
        var i = 0
        categoryList.forEach {
            val parent = containerList[i]
            layoutInflater.inflate(R.layout.row_details_category_twin,
                    parent, true) as ViewGroup
            parent.findViewById<TextView>(R.id.details_category_twin_TV).text = it.name
            val icon = parent.findViewById<ImageView>(R.id.drawable_details_categoryTwin)
            if (it.image != null) Picasso.get().load(it.image).into(icon)
            categorySetOnClickListener(i)
            i++
        }
        if (i<4) categorySetOnClickListener(i++)
    }

    //Checks if every required value is present
    private fun proceedCheck(): Boolean {
        val required: ArrayList<TextInputLayout> = arrayListOf(
                name_ETL, volume_ETL, voltage_ETL, price1_ETL)
        var check = true

        required.forEach {
            if (it.editText!!.text.toString().trim().isEmpty()) {
                it.isErrorEnabled
                it.error = "Podaj wartość!"
                it.errorIconDrawable = getDrawable(
                        requireContext(), R.drawable.ic_baseline_error_outline_24)
                check = false
            }
        }
        if (check == false) return check


        if (voltage_ET.text.toString().toFloat() > 100) {
            voltage_ETL.apply {
                isErrorEnabled
                error = "Zła wartość!"
                errorIconDrawable = getDrawable(
                        requireContext(), R.drawable.ic_baseline_error_outline_24)
            }
            check = false
        }

        if (categoryList.size == 0) {
            Toast.makeText(
                    requireContext(),
                    "Podaj przynajmniej jedną kategorię!",
                    Toast.LENGTH_LONG).show()
            check = false
        }

        return true
    }

    private fun addTextChangedListeners() {
        val layouts = arrayListOf(name_ETL, volume_ETL, voltage_ETL, price1_ETL)
        layouts.forEach {
            it.editText!!.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    it.error = null
                    if (s.toString().trim().isEmpty()) {
                        it.error = "Podaj wartość!"
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                }

            })
        }
    }
}