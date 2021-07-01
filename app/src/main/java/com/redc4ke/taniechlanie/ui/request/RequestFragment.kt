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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
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

    override fun onStart() {
        super.onStart()

        val act = requireActivity() as MainActivity
        val shopViewModel =
            ViewModelProvider(act)[ShopViewModel::class.java]
        val categoryViewModel =
            ViewModelProvider(act)[CategoryViewModel::class.java]
        requestViewModel =
            ViewModelProvider(act)[RequestViewModel::class.java]

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


            with(requestViewModel) {
                getShop().observe(viewLifecycleOwner, {
                    requestShopBT.text = "${getString(R.string.request_shopbt)} $it"
                })
                getPhotoName().observe(viewLifecycleOwner, {
                    requestPhotoBT.text = "${getString(R.string.photo)} $it"
                    requestDeletephotoBT.visibility = View.VISIBLE
                })
                getSelectedCategories().observe(viewLifecycleOwner, {
                    var i = 1
                    it.forEach { (_, _) ->
                        if (i < cards.size) {
                            cards[i].visibility = View.VISIBLE
                        }
                        i++
                    }
                })
            }
            with(categoryViewModel) {
                getAllMajor().observe(viewLifecycleOwner, {
                    setMajorSpinner(it)
                })
                getAll().observe(viewLifecycleOwner, { it ->
                    val minorCatList = it.filter { !it.value.major }
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
        binding.requestTypeSPINNER.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
    
    private fun setMinorSpinner(minorMap: Map<Int, Category>, spinnerList: List<Spinner>) {
        val catNames = minorMap.values.map { it.name }
        var i = 0
        spinnerList.forEach {
            it.adapter = ArrayAdapter(requireContext(), R.layout.spinner1, catNames)
            it.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    requestViewModel.addCategory(i, minorMap.values.toList()[position])
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

            }
            i++
        }
    }

}