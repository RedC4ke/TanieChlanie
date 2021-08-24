package com.redc4ke.taniechlanie.data.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.viewmodels.UserViewModel
import com.redc4ke.taniechlanie.databinding.RowProfileBinding
import com.redc4ke.taniechlanie.ui.MainActivity
import com.redc4ke.taniechlanie.ui.profile.ProfileMenuFragment

class ProfileMenuAdapter(private val context: ProfileMenuFragment) :
    RecyclerView.Adapter<ProfileMenuViewHolder>() {

    private val userViewModel =
        ViewModelProvider(context.activity as MainActivity)[UserViewModel::class.java]
    private val menu: MutableList<String> =
        context.resources.getStringArray(R.array.profile_menu).toMutableList().also {
            if (userViewModel.isModerator()) {
                it.add(context.getString(R.string.profile_modpanel))
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileMenuViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RowProfileBinding.inflate(inflater, parent, false)

        return ProfileMenuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProfileMenuViewHolder, position: Int) {
        when (position) {
            0 -> {
                holder.vb.root.setOnClickListener {
                    context.findNavController()
                        .navigate(R.id.to_profileManage_dest)
                }
            }

            1 -> {
                holder.vb.root.setOnClickListener {
                    context.findNavController()
                        .navigate(R.id.action_profileMenu_dest_to_profileReviews_dest)
                }
            }

            2 -> {
                holder.vb.root.setOnClickListener {
                    context.findNavController()
                        .navigate(R.id.profileRequests_dest)
                }
            }

            else -> {
                holder.vb.root.setOnClickListener {
                    context.requireParentFragment().requireParentFragment().findNavController()
                        .navigate(R.id.action_profile_dest_to_modpanel_dest)
                }
            }
        }

        holder.vb.profileRowNameTV.text = menu[position]
    }

    override fun getItemCount(): Int {
        return menu.size
    }
}

class ProfileMenuViewHolder(val vb: RowProfileBinding) : RecyclerView.ViewHolder(vb.root)