package com.redc4ke.taniechlanie.data.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.redc4ke.taniechlanie.data.Category
import java.io.File
import java.math.BigDecimal

class RequestViewModel : ViewModel() {

    private object Request {
        val shopName = MutableLiveData<String>()
        var alcoholName: String? = null
        var volume: Int? = null
        var voltage: BigDecimal? = null
        var price: BigDecimal? = null
        var image: File? = null
        var majorCategory: Category? = null
        var categories = MutableLiveData<Map<Int, Category>>()
    }
    private val photoName = MutableLiveData<String>()

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

    fun setImage(file: File?) {
        Request.image = file
    }

    fun setPhotoName(name: String) {
        photoName.value = name
    }

    fun getPhotoName(): MutableLiveData<String> {
        return photoName
    }

    fun setMajorCat(category: Category) {
        Request.majorCategory = category
    }

    fun addCategory(index: Int, category: Category) {
        val catList = Request.categories.value as MutableMap<Int, Category>
        catList[index] = category
        Request.categories.value = catList
    }

    fun getSelectedCategories() : LiveData<Map<Int, Category>> {
        return Request.categories
    }


}