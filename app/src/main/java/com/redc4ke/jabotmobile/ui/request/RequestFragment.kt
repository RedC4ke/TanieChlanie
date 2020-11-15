package com.redc4ke.jabotmobile.ui.request

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.transition.TransitionInflater
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.redc4ke.jabotmobile.R
import com.redc4ke.jabotmobile.data.ItemCategory
import kotlinx.android.synthetic.main.fragment_request.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import kotlin.collections.ArrayList

class RequestFragment : Fragment() {

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

        val categoryButtons: ArrayList<Button> = arrayListOf(
                cat_req_BT1, cat_req_BT2, cat_req_BT3, cat_req_BT4)

        categoryButtons.forEach {
            it.setOnClickListener {
                this.findNavController().navigate(R.id.to_category)
            }
        }

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

}