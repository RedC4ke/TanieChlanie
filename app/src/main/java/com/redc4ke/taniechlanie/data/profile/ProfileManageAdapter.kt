package com.redc4ke.taniechlanie.data.profile

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.viewmodels.UserViewModel
import com.redc4ke.taniechlanie.databinding.RowProfileManageBinding
import com.redc4ke.taniechlanie.ui.MainActivity
import com.redc4ke.taniechlanie.ui.profile.management.DisplayNameFragment
import com.redc4ke.taniechlanie.ui.profile.management.ProfileManageFragment

class ProfileManageAdapter(private val context: ProfileManageFragment, private val userViewModel: UserViewModel,
    private val viewLifecycleOwner: LifecycleOwner):
    RecyclerView.Adapter<ProfileManageViewHolder>() {

    val menu: Array<String> = context.resources.getStringArray(R.array.profile_manageMenu)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileManageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RowProfileManageBinding.inflate(inflater, parent, false)

        return ProfileManageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProfileManageViewHolder, position: Int) {
        with (holder.vb) {
            rowPMNameTV.text = menu[position]
            when (position) {
                0 -> {
                    userViewModel.getUserUpdates().observe(viewLifecycleOwner, {
                        val user = userViewModel.getUser().value
                        rowPMValueTV.text = user?.displayName
                        PMClickableLL.setOnClickListener {
                            DisplayNameFragment(user, userViewModel).show(
                                context.parentFragmentManager, "DisplayNameFragment")
                        }
                    })
                }
                1 -> {
                    userViewModel.getUser().observe(viewLifecycleOwner, {
                        rowPMValueTV.text = it?.email
                    })
                }
                2 -> {

                }
            }
        }
    }

    override fun getItemCount(): Int {
        return menu.size
    }
}

class ProfileManageViewHolder(val vb: RowProfileManageBinding): RecyclerView.ViewHolder(vb.root)