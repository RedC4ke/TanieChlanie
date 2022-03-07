package com.redc4ke.taniechlanie.ui.profile

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.target.Target
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseUser
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.RequestListener
import com.redc4ke.taniechlanie.data.imageFromIntent
import com.redc4ke.taniechlanie.data.viewmodels.ModpanelViewModel
import com.redc4ke.taniechlanie.data.viewmodels.UserViewModel
import com.redc4ke.taniechlanie.databinding.FragmentProfileBinding
import com.redc4ke.taniechlanie.ui.MainActivity
import com.redc4ke.taniechlanie.ui.base.BaseFragment
import com.redc4ke.taniechlanie.ui.popup.ConfirmationFragment
import java.io.File

class ProfileFragment : BaseFragment<FragmentProfileBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentProfileBinding
        get() = FragmentProfileBinding::inflate
    private lateinit var userViewModel: UserViewModel
    private val pickAvatar = 1
    var user: FirebaseUser? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        user = userViewModel.getUser().value
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //start fetching data for mod panel
        if (userViewModel.getPermissionLevel() > 0) {
            val modpanelViewModel =
                ViewModelProvider(requireActivity())[ModpanelViewModel::class.java]
            modpanelViewModel.fetch()
        }

        setFragmentResultListener("confirmation") { _, bundle ->
            val result = bundle.getBoolean("value")
            if (result) {
                Log.d("login", "triggered logout")
                AuthUI.getInstance()
                    .signOut(requireContext())
                    .addOnSuccessListener {
                        Toast.makeText(
                            requireContext(), getString(R.string.toast_logout), Toast.LENGTH_SHORT
                        )
                            .show()
                        requireActivity().onBackPressed()
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

        with(binding) {
            if (user != null) {
                profileEditAvatarBT.setOnClickListener {
                    val getIntent = Intent(Intent.ACTION_GET_CONTENT)
                    getIntent.type = "image/*"
                    startActivityForResult(getIntent, pickAvatar)
                }
                userViewModel.getStats().observe(viewLifecycleOwner) {
                    profileSubmitsTV.text = getString(
                        R.string.profile_submits,
                        it?.get("submits") ?: "n/a"
                    )
                    profileReviewsTV.text = getString(
                        R.string.profile_reviews,
                        it?.get("reviews") ?: "n/a"
                    )
                }
                userViewModel.getTitle().observe(viewLifecycleOwner) {
                    profileRankTV.text = it["name"] as String
                }
                userViewModel.getUserName().observe(viewLifecycleOwner) {
                    profileNameTV.text = it
                }
                userViewModel.getAvatarUrl().observe(viewLifecycleOwner) {
                    Glide
                        .with(requireContext())
                        .load(it)
                        .listener(object : com.bumptech.glide.request.RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.toast_error),
                                    Toast.LENGTH_LONG
                                ).show()
                                binding.profileLoadingFL.visibility = View.GONE

                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                binding.profileLoadingFL.visibility = View.GONE

                                return false
                            }

                        })
                        .into(profilePictureIV)
                }
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
            binding.profileLoadingFL.visibility = View.VISIBLE
            if (data != null) {
                userViewModel.setAvatar(
                    getImage(data),
                    object : RequestListener {
                        override fun onComplete(resultCode: Int) {
                            if (resultCode != RequestListener.SUCCESS) {
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.toast_error),
                                    Toast.LENGTH_LONG
                                ).show()
                                binding.profileLoadingFL.visibility = View.GONE
                            }
                        }
                    })
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