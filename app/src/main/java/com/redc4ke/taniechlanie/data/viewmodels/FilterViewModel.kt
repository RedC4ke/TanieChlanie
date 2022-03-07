package com.redc4ke.taniechlanie.data.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.redc4ke.taniechlanie.data.AlcoObject
import com.redc4ke.taniechlanie.data.Category
import com.redc4ke.taniechlanie.data.valueDecimal
import java.math.BigDecimal
import java.text.Normalizer
import java.util.*
import kotlin.collections.ArrayList

class FilterViewModel : ViewModel() {

    private val textFilter = MutableLiveData<String?>()
    private val volumeFilter = MutableLiveData<Pair<Int, Int>?>()
    private val voltageFilter = MutableLiveData<Pair<Float, Float>?>()
    private val typeFilter = MutableLiveData<List<Category>?>()
    private val categoryFilter = MutableLiveData<List<Category>?>()
    private val priceFilter = MutableLiveData<Pair<Float, Float>?>()
    private val valueFilter = MutableLiveData<Pair<Float, Float>?>()
    private var originalList: List<AlcoObject> = listOf()
    private val filteredList = MutableLiveData<List<AlcoObject>>()
    private val favoriteList = MutableLiveData<List<Long>>()
    private var favOnlyFilter = false

    private var maxPrice = MutableLiveData<BigDecimal>()
    private var maxVolume = MutableLiveData<Int>()
    private var maxValue = MutableLiveData<BigDecimal>()

    private var firstUpdate = true
    private val isActive = MutableLiveData<Boolean>()

    var allTypesSelected = false
    var allCategoriesSelected = false

    init {
        textFilter.value = ""
        volumeFilter.value = Pair(0, 1)
        voltageFilter.value = Pair(0f, 100f)
        categoryFilter.value = listOf()
        typeFilter.value = listOf()
        priceFilter.value = Pair(0f, 1f)
        valueFilter.value = Pair(0f, 1f)

        maxPrice.value = BigDecimal.ONE
        maxVolume.value = 1
        maxValue.value = BigDecimal.ONE

        isActive.value = false
    }

    fun clear() {
        textFilter.value = ""
        volumeFilter.value = Pair(0, maxVolume.value ?: 1)
        voltageFilter.value = Pair(0f, 100f)
        categoryFilter.value = listOf()
        typeFilter.value = listOf()
        priceFilter.value = Pair(0f, maxPrice.value?.toFloat() ?: 1f)
        valueFilter.value = Pair(0f, maxValue.value?.toFloat() ?: 1f)
        favOnlyFilter = false
    }

    fun setAlcoObjectList(alcoObjectList: List<AlcoObject>) {
        if (alcoObjectList.size != originalList.size) {
            originalList = alcoObjectList

            alcoObjectList.forEach { alcoObject ->
                if (alcoObject.volume > maxVolume.value ?: 0) maxVolume.value = alcoObject.volume
                val price = alcoObject.shopToPrice.values.sortedBy { it }[0] ?: BigDecimal.ZERO
                if (price > maxPrice.value) maxPrice.value = price
                val value = valueDecimal(alcoObject)
                if (value > maxValue.value) maxValue.value = value
            }

            if (firstUpdate) {
                volumeFilter.value = Pair(0, maxVolume.value?.toInt() ?: 1)
                valueFilter.value = Pair(0f, maxValue.value?.toFloat() ?: 1f)
                priceFilter.value = Pair(0f, maxPrice.value?.toFloat() ?: 1f)

                firstUpdate = false
            }

            update()
        }
    }

    fun setTextFilter(text: String?) {
        textFilter.value = text
        update()
    }

    fun setVolumeFilter(volume: Pair<Int, Int>?) {
        volumeFilter.value = volume
    }

    fun setVoltageFilter(voltage: Pair<Float, Float>?) {
        voltageFilter.value = voltage
    }

    fun setCategoryFilter(categories: List<Category>?) {
        categoryFilter.value = categories
    }

    fun setTypeFilter(types: List<Category>?) {
        typeFilter.value = types
    }

    fun setPriceFilter(price: Pair<Float, Float>?) {
        priceFilter.value = price
    }

    fun setValueFilter(value: Pair<Float, Float>?) {
        valueFilter.value = value
    }

    fun setFavFilter(b: Boolean) {
        favOnlyFilter = b
    }

    fun setFavList(list: List<Long>) {
        favoriteList.value = list
    }

    fun getPriceFilter(): LiveData<Pair<Float, Float>?> {
        return priceFilter
    }

    fun getVolumeFilter(): LiveData<Pair<Int, Int>?> {
        return volumeFilter
    }

    fun getVoltageFilter(): LiveData<Pair<Float, Float>?> {
        return voltageFilter
    }

    fun getValueFilter(): LiveData<Pair<Float, Float>?> {
        return valueFilter
    }

    fun getCategoryFilter(): LiveData<List<Category>?> {
        return categoryFilter
    }

    fun getTypeFilter(): LiveData<List<Category>?> {
        return typeFilter
    }

    fun getFilteredList(): LiveData<List<AlcoObject>> {
        return filteredList
    }

    fun getMaxPrice(): LiveData<BigDecimal> {
        return maxPrice
    }

    fun getMaxVolume(): LiveData<Int> {
        return maxVolume
    }

    fun getMaxValue(): LiveData<BigDecimal> {
        return maxValue
    }

    fun getFavFilter(): Boolean {
        return favOnlyFilter
    }

    fun isActive(): LiveData<Boolean> {
        return isActive
    }

    fun update() {
        val temporalFilteredList: MutableList<AlcoObject> = originalList.toMutableList()

        originalList.forEach { alcoObject ->
            if (favOnlyFilter) {
                if (favoriteList.value?.contains(alcoObject.id) == false) {
                    temporalFilteredList.remove(alcoObject)
                }
            }

            if (!textFilter.value.isNullOrBlank()) {
                val name = Normalizer.normalize(
                    alcoObject.name.lowercase(Locale.ROOT), Normalizer.Form.NFD
                )
                if (!name.contains(textFilter.value ?: return@forEach)) {
                    temporalFilteredList.remove(alcoObject)
                    return@forEach
                }
            }

            if (volumeFilter.value != null) {
                if (alcoObject.volume < volumeFilter.value?.first ?: return@forEach ||
                    alcoObject.volume > volumeFilter.value?.second ?: return@forEach
                ) {
                    temporalFilteredList.remove(alcoObject)
                    return@forEach
                }
            }

            if (voltageFilter.value != null) {
                if (alcoObject.voltage < voltageFilter.value?.first?.toBigDecimal() ?: return@forEach ||
                    alcoObject.voltage > voltageFilter.value?.second?.toBigDecimal() ?: return@forEach
                ) {
                    temporalFilteredList.remove(alcoObject)
                    return@forEach
                }
            }

            if (!categoryFilter.value.isNullOrEmpty()) {
                if (!alcoObject.categories.any { category ->
                        categoryFilter.value?.map { it.id }?.contains(category) == true
                    }) {

                    temporalFilteredList.remove(alcoObject)
                    return@forEach
                }
            }

            if (!typeFilter.value.isNullOrEmpty()) {
                if (!alcoObject.categories.any { category ->
                        typeFilter.value?.map { it.id }?.contains(category) == true
                    }) {

                    temporalFilteredList.remove(alcoObject)
                    return@forEach
                }
            }

            if (priceFilter.value != null) {
                val displayPrice: BigDecimal =
                    alcoObject.shopToPrice.entries.sortedBy { it.value }[0].value
                        ?: BigDecimal.ZERO

                if (displayPrice < priceFilter.value?.first?.toBigDecimal() ||
                    displayPrice > priceFilter.value?.second?.toBigDecimal()
                ) {
                    temporalFilteredList.remove(alcoObject)
                    return@forEach
                }
            }

            if (valueFilter.value != null) {
                val value = valueDecimal(alcoObject)

                if (value < valueFilter.value?.first?.toBigDecimal() ||
                    value > valueFilter.value?.second?.toBigDecimal()
                ) {
                    temporalFilteredList.remove(alcoObject)
                    return@forEach
                }
            }
        }
        filteredList.value = temporalFilteredList.sortedBy { it.name }
        isActive.value = filteredList.value?.size != originalList.size
    }

}