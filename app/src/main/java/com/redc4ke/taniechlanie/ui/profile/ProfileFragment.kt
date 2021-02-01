package com.redc4ke.taniechlanie.ui.profile

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.setFragmentResultListener
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseUser
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.databinding.FragmentProfileBinding
import com.redc4ke.taniechlanie.ui.base.BaseFragment
import com.redc4ke.taniechlanie.ui.MainActivity
import com.redc4ke.taniechlanie.ui.popup.ConfirmationFragment

class ProfileFragment : BaseFragment<FragmentProfileBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentProfileBinding
        get() = FragmentProfileBinding::inflate
    private lateinit var user: FirebaseUser

    override fun onAttach(context: Context) {
        super.onAttach(context)
        user = (requireActivity() as MainActivity).auth.currentUser!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFragmentResultListener("confirmation") { s, bundle ->
            val result = bundle.getBoolean("value")
            if (result) {
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
            profilePictureIV.setImageResource(R.drawable.avatar)
            profileNameTV.text = user.displayName
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.logout_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logoutBT) {
            ConfirmationFragment(1).show(parentFragmentManager, "confirmation")
        }

        return super.onOptionsItemSelected(item)
    }
}