package com.redc4ke.taniechlanie.data.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.viewmodels.UserViewModel
import com.redc4ke.taniechlanie.databinding.RowProfileManageBinding
import com.redc4ke.taniechlanie.ui.profile.management.ProfileInputFragment
import com.redc4ke.taniechlanie.ui.profile.management.ProfileManageFragment

class ProfileManageAdapter(private val context: ProfileManageFragment, private val userViewModel: UserViewModel,
    private val viewLifecycleOwner: LifecycleOwner):
    RecyclerView.Adapter<ProfileManageViewHolder>() {

    private var menu: MutableList<String>
    private val provider = FirebaseAuth.getInstance()
        .currentUser?.getIdToken(false)?.result?.signInProvider
    private val isEmail = (provider == "password")

    init {
        val list = context.resources.getStringArray(R.array.profile_manageMenu)
        menu = mutableListOf()
        list.forEachIndexed { index, s ->
            if (!(index in 1..2 && !isEmail))
                menu.add(s)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileManageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RowProfileManageBinding.inflate(inflater, parent, false)

        return ProfileManageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProfileManageViewHolder, position: Int) {
        with (holder.vb) {
            rowPMNameTV.text = menu[position]

            if (isEmail)
                rowSetup(this, position)
            else
                when (position) {
                    0 -> rowSetup(this, position)
                    else -> rowSetup(this, position+2)
                }

        }
    }

    override fun getItemCount(): Int {

        return menu.size
    }

    private fun rowSetup(vb: RowProfileManageBinding, action: Int) {
        userViewModel.getUserUpdates().observe(viewLifecycleOwner, {
            val user = userViewModel.getUser().value
            vb.rowPMValueTV.text = when (action) {
                0 -> user?.displayName
                1 -> user?.email
                else -> ""
            }
            vb.PMClickableLL.setOnClickListener {
                ProfileInputFragment(userViewModel, action).show(
                    context.parentFragmentManager, "ProfileInputFragment"
                )
            }
        })
    }
}

class ProfileManageViewHolder(val vb: RowProfileManageBinding): RecyclerView.ViewHolder(vb.root)