package com.redc4ke.taniechlanie.data.viewmodels

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.math.BigDecimal

class RequestViewModel : ViewModel() {

    private object Request {
        val shopName = MutableLiveData<String>()
        var alcoholName: String? = null
        var volume: Int? = null
        var voltage: BigDecimal? = null
        var price: BigDecimal? = null
        var imageUri: Uri? = null
    }


    fun setShop(name: String) {
        Request.shopName.value = name
    }

    fun getShop(): MutableLiveData<String> {
        return Request.shopName
    }

    fun fillRequest(name: String, vm: Int, volt: BigDecimal, pc: BigDecimal) {
        with(Request) {
            alcoholName = name
            volume = vm
            voltage = volt
            price = pc
        }
    }

    fun setImage(uri: Uri) {
        Request.imageUri = uri
    }
}