package com.redc4ke.taniechlanie.ui.profile

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseUser
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.imageFromIntent
import com.redc4ke.taniechlanie.data.profile.ProfileMenuAdapter
import com.redc4ke.taniechlanie.data.setImage
import com.redc4ke.taniechlanie.data.viewmodels.UserViewModel
import com.redc4ke.taniechlanie.databinding.FragmentProfileBinding
import com.redc4ke.taniechlanie.ui.MainActivity
import com.redc4ke.taniechlanie.ui.base.BaseFragment
import com.redc4ke.taniechlanie.ui.popup.ConfirmationFragment
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class ProfileFragment : BaseFragment<FragmentProfileBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentProfileBinding
        get() = FragmentProfileBinding::inflate
    private lateinit var userViewModel: UserViewModel
    private val pickAvatar = 1
    private var user: FirebaseUser? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        user = userViewModel.getUser().value
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFragmentResultListener("confirmation") {_, bundle ->
            val result = bundle.getBoolean("value")
            if (result) {
                Log.d("login", "triggered logout")
                AuthUI.getInstance()
                    .signOut(requireContext())
                    .addOnSuccessListener {
                        requireActivity().onBackPressed()
                        Toast.makeText(
                            requireContext(), getString(R.string.toast_logout), Toast.LENGTH_SHORT)
                            .show()
                    }
            }
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

            with (binding) {
                if (user != null) {
                    userViewModel.getUserUpdates().observe(viewLifecycleOwner, {
                        val name = user!!.displayName
                        val photo = user!!.photoUrl
                        profileNameTV.text = name
                        setImage(requireContext(), "avatar", profilePictureIV, photo)
                    })
                    profileEditAvatarBT.setOnClickListener {
                        val getIntent = Intent(Intent.ACTION_GET_CONTENT)
                        getIntent.type = "image/*"
                        startActivityForResult(getIntent, pickAvatar)
                    }
                    userViewModel.getStats().observe(viewLifecycleOwner, {
                        profileSubmitsTV.text = getString(R.string.profile_submits, it["submits"])
                        profileReviewsTV.text = getString(R.string.profile_reviews, it["reviews"])
                    })
                    userViewModel.getTitle().observe(viewLifecycleOwner, {
                        profileRankTV.text = it["name"] as String
                    })
                } else {
                    profilePictureIV.setImageResource(R.drawable.ic_baseline_account_circle_24)
                    profileNameTV.text = getString(R.string.guest)
                }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.logout_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == pickAvatar) {
            if (data != null) {
                userViewModel.setAvatar(requireContext(), getImage(data))
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logoutBT) {
            ConfirmationFragment(1).show(parentFragmentManager, "confirmation")
        }

        return super.onOptionsItemSelected(item)
    }

    private fun getImage(data: Intent): File {
        return imageFromIntent(requireContext(), data, "avatar")
    }
}