package com.redc4ke.taniechlanie.data.profile

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.priceString
import com.redc4ke.taniechlanie.data.viewmodels.NewBoozeRequest
import com.redc4ke.taniechlanie.data.viewmodels.Request
import com.redc4ke.taniechlanie.data.voltageString
import com.redc4ke.taniechlanie.data.volumeString
import com.redc4ke.taniechlanie.databinding.RowProfileRequestBinding
import java.math.BigDecimal
import java.text.DateFormat

class ProfileRequestAdapter(private val context: Context) :
    RecyclerView.Adapter<ProfileRequestViewHolder>() {

    var requestList: List<Pair<Int, NewBoozeRequest>> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileRequestViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RowProfileRequestBinding.inflate(inflater)

        return ProfileRequestViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProfileRequestViewHolder, position: Int) {
        val request = requestList[position].second
        with(holder.vb) {
            profileRqNameTV.text = request.name

            if (request.created != null) {
                profileRqDateTV.text = DateFormat.getDateInstance().format(request.created!!)
            }
            if (request.reviewed != null) {
                profileRqVerDateTV.text = DateFormat.getDateInstance().format(request.reviewed!!)
            }

            profileRqShopTV.text = request.shopName
            profileRqPriceTV.text =
                priceString(request.price?.toBigDecimal() ?: BigDecimal.ZERO, context)
            profileRqVolumeTV.text = volumeString(request.volume?.toInt() ?: 0, context)
            profileRqVoltageTV.text = voltageString(request.voltage ?: BigDecimal.ZERO, context)

            profileRqStateTV.text = when (request.state) {
                Request.RequestState.APPROVED -> context.getString(R.string.approved).also {
                    profileRqStateTV.setTextColor(Color.GREEN)
                }
                Request.RequestState.DECLINED -> context.getString(R.string.declined).also {
                    profileRqStateTV.setTextColor(Color.RED)
                    profileRqReasonTV.visibility = View.VISIBLE
                    profileRqReasonTV2.visibility = View.VISIBLE
                    profileRqReasonTV.text = request.reason
                }
                else -> context.getString(R.string.state_pending).also {
                    profileRqVerDateTV.visibility = View.GONE
                    profileRqVerDateTV2.visibility = View.GONE
                }
            }

            Glide.with(context).load(request.photo).into(profileRqIV)

            //A hack to avoid weird and unfound problem (params were reset to wrap content on startup)
            root.layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        }

    }

    override fun getItemCount(): Int {
        return requestList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun update(list: List<Pair<Int, NewBoozeRequest>>) {
        requestList = list
        notifyDataSetChanged()
    }


}

class ProfileRequestViewHolder(val vb: RowProfileRequestBinding) : RecyclerView.ViewHolder(vb.root)