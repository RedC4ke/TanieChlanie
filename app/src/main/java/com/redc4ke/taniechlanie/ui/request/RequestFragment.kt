package com.redc4ke.taniechlanie.ui.request

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.Category
import com.redc4ke.taniechlanie.data.imageFromBitmap
import com.redc4ke.taniechlanie.data.viewmodels.CategoryViewModel
import com.redc4ke.taniechlanie.data.viewmodels.RequestViewModel
import com.redc4ke.taniechlanie.data.viewmodels.ShopViewModel
import com.redc4ke.taniechlanie.databinding.FragmentRequestBinding
import com.redc4ke.taniechlanie.ui.MainActivity
import com.redc4ke.taniechlanie.ui.base.BaseFragment
import com.redc4ke.taniechlanie.ui.popup.RequestShopFragment

private const val PICK_IMAGE = 1

class RequestFragment : BaseFragment<FragmentRequestBinding>(), DialogInterface.OnDismissListener {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentRequestBinding
        get() = FragmentRequestBinding::inflate
    private lateinit var requestViewModel: RequestViewModel
    private var selectedMinorMap = mapOf<Int, Category>()

    override fun onStart() {
        super.onStart()

        val act = requireActivity() as MainActivity
        val shopViewModel =
            ViewModelProvider(act)[ShopViewModel::class.java]
        val categoryViewModel =
            ViewModelProvider(act)[CategoryViewModel::class.java]
        requestViewModel =
            ViewModelProvider(act)[RequestViewModel::class.java]

        var minorMap = mapOf<Int, Category>()

        with(binding) {
            requestNameET.addTextChangedListener {
                requestNameTIL.error =
                    if (!it.isNullOrEmpty()) {
                        when (true) {
                            it.length > 20 -> getString(R.string.err_tooLong, "20")
                            it.length < 2 -> getString(R.string.err_tooShort, "2")
                            else -> null
                        }
                    } else {
                        getString(R.string.err_emptyField)
                    }
            }
            requestVolumeET.addTextChangedListener {
                requestVolumeTIL.error =
                    if (!it.isNullOrEmpty()) {
                        when (true) {
                            it.length > 6 -> getString(R.string.err_tooLongShortened, "6")
                            else -> null
                        }
                    } else {
                        getString(R.string.err_emptyField)
                    }
            }
            requestVoltageET.addTextChangedListener {
                requestVoltageTIL.error =
                    if (!it.isNullOrEmpty()) {
                        when (true) {
                            it.length > 5 -> getString(R.string.err_tooLongShortened, "5")
                            it[0] == '.' -> getString(R.string.err_wrongInput)
                            it.last() == '.' -> getString(R.string.err_wrongInput)
                            it.toString().toFloat() == 0f -> getString(R.string.err_zeroValue)
                            it.toString().toFloat() > 100f -> getString(R.string.err_over100p)
                            else -> null
                        }
                    } else {
                        getString(R.string.err_emptyField)
                    }
            }
            requestPriceET.addTextChangedListener {
                requestPriceTIL.error =
                    if (!it.isNullOrEmpty()) {
                        when (true) {
                            it.length > 10 -> getString(R.string.err_tooLongShortened, "10")
                            it[0] == '.' -> getString(R.string.err_wrongInput)
                            it.last() == '.' -> getString(R.string.err_wrongInput)
                            it.toString().toFloat() == 0f -> getString(R.string.err_zeroValue)
                            else -> null
                        }
                    } else {
                        getString(R.string.err_emptyField)
                    }
            }

            requestShopBT.setOnClickListener {
                RequestShopFragment(shopViewModel, requestViewModel)
                    .show(parentFragmentManager, "requestShopFragment")
            }
            requestPhotoBT.setOnClickListener {
                addPhoto()
            }
            requestDeletephotoBT.setOnClickListener {
                requestViewModel.setImage(null)
                it.visibility = View.GONE
                requestPhotoBT.text = getString(R.string.image_req)
            }
            val cards = listOf(catAdd1CV, catAdd2CV, catAdd3CV, catAdd4CV)
            cards.forEach { card ->
                (card.getChildAt(0)).setOnClickListener { it ->
                    it.visibility = View.GONE
                    card.getChildAt(1).visibility = View.VISIBLE
                }
            }


            with(requestViewModel) {
                getShop().observe(viewLifecycleOwner, {
                    requestShopBT.text = "${getString(R.string.request_shopbt)} $it"
                })
                getPhotoName().observe(viewLifecycleOwner, {
                    requestPhotoBT.text = "${getString(R.string.photo)} $it"
                    requestDeletephotoBT.visibility = View.VISIBLE
                })
                getSelectedCategories().observe(viewLifecycleOwner, {
                    selectedMinorMap = it
                })
            }
            with(categoryViewModel) {
                getAllMajor().observe(viewLifecycleOwner, {
                    setMajorSpinner(it)
                })
                getAll().observe(viewLifecycleOwner, { it ->
                    minorMap = it.filter { !it.value.major }
                    setMinorSpinner(selectedMinorMap, minorMap, cards)
                })
            }

            requestCancelBT.setOnClickListener {
                findNavController().navigate(R.id.alcoList_dest)
            }
            requestSendBT.setOnClickListener {
                listOf(
                    requestNameTIL,
                    requestVoltageTIL,
                    requestVolumeTIL,
                    requestPriceTIL
                ).forEach {
                    if (it.error != null) {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.err_overallcheck),
                            Toast.LENGTH_LONG
                        ).show()
                        return@setOnClickListener
                    }
                }

                if (FirebaseAuth.getInstance().currentUser == null) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.err_notloggedin),
                        Toast.LENGTH_LONG
                    ).show()
                    return@setOnClickListener
                }

                val input = mutableListOf<String>()
                listOf(requestNameET, requestVolumeET, requestVoltageET, requestPriceET).forEach {
                    input.add(it.text.toString())
                }

                requestViewModel.fillRequest(
                    input[0],
                    input[1].toInt(),
                    input[2].toBigDecimal(),
                    input[3].toBigDecimal()
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data?.data != null) {
            val bitmap =
                if (Build.VERSION.SDK_INT < 28) {
                    MediaStore.Images.Media.getBitmap(
                        requireContext().contentResolver,
                        data.data
                    )
                } else {
                    val src = ImageDecoder.createSource(
                        requireContext().contentResolver,
                        data.data!!
                    )
                    ImageDecoder.decodeBitmap(src)
                }
            with(requestViewModel) {
                setImage(imageFromBitmap(requireContext(), bitmap, "requestUpload"))
                setPhotoName(data.data!!.lastPathSegment ?: "")
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface?) {
        addPhoto()
    }

    private fun addPhoto() {
        val intent = Intent().apply {
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
            action = Intent.ACTION_GET_CONTENT
        }
        startActivityForResult(
            Intent.createChooser(intent, getString(R.string.chose_photo)),
            PICK_IMAGE
        )
    }

    private fun setMajorSpinner(majorMap: Map<Int, Category>) {
        val catNames = majorMap.values.map { it.name }
        binding.requestTypeSPINNER.adapter =
            ArrayAdapter(requireContext(), R.layout.spinner1, catNames)
        binding.requestTypeSPINNER.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    requestViewModel.setMajorCat(majorMap.values.toList()[position])
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

            }
    }

    internal fun setMinorSpinner(
        selected: Map<Int, Category>,
        minor: Map<Int, Category>,
        cardList: List<CardView>
    ) {
        Log.d("templog", selected.toString())
        val catNames: List<String> =
            listOf(getString(R.string.delete)) + minor.values.map { it.name }
        val spinners = cardList.map { it.getChildAt(1) as Spinner }
        cardList.forEach {
            it.visibility = View.GONE
            it.getChildAt(0).visibility = View.VISIBLE
            it.getChildAt(1).visibility = View.GONE
        }

        for (i: Int in (0) until selected.size) {
            cardList[i].visibility = View.VISIBLE
            (cardList[i].getChildAt(0) as Button).callOnClick()
        }
        if (selected.size < 4) cardList[selected.size].visibility = View.VISIBLE

        spinners.forEach { spinner ->
            spinner.adapter =
                ArrayAdapter(requireContext(), R.layout.spinner1, catNames)
            val index = spinners.indexOf(spinner)

            if (selected.size > index) {
                spinner.visibility = View.VISIBLE
                spinner.setSelection(catNames.indexOf(selected[index]?.name))
            } else {
                spinner.setSelection(spinners.indexOf(spinner) + 1)
            }

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (spinner.visibility == View.VISIBLE) {
                        if (position == 0) {
                            requestViewModel
                                .deleteCategory(spinners.indexOf(spinner))
                            setMinorSpinner(selectedMinorMap, minor, cardList)
                        } else {
                            requestViewModel
                                .addCategory(
                                    spinners.indexOf(spinner),
                                    minor.values.filter { it.name == catNames[position] }[0]
                                )
                            if (index < 3) {
                                cardList[index + 1].visibility = View.VISIBLE
                            }
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

            }
        }

    }

}