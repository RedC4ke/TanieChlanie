package com.redc4ke.taniechlanie.data.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.redc4ke.taniechlanie.data.AlcoObject
import java.math.BigDecimal
import java.text.Normalizer
import java.util.*
import kotlin.collections.ArrayList

class FilterViewModel : ViewModel() {

    private val textFilter = MutableLiveData<String?>()
    private val volumeFilter = MutableLiveData<Pair<Int, Int>?>()
    private val voltageFilter = MutableLiveData<Pair<Float, Float>?>()
    private val categoryFilter = MutableLiveData<ArrayList<Int>?>()
    private val priceFilter = MutableLiveData<Pair<Float, Float>?>()
    private var originalList: List<AlcoObject> = listOf()
    private val filteredList = MutableLiveData<List<AlcoObject>>()

    init {
        textFilter.value = null
        volumeFilter.value = Pair(0, 9999)
        voltageFilter.value = Pair(0f, 100f)
        categoryFilter.value = arrayListOf()
        priceFilter.value = Pair(0f, 9999f)
    }

    fun setAlcoObjectList(alcoObjectList: List<AlcoObject>) {
        originalList = alcoObjectList
        update()
    }

    fun setTextFilter(text: String?) {
        textFilter.value = text
        update()
    }

    fun setVolumeFilter(volume: Pair<Int, Int>?) {
        volumeFilter.value = volume
        update()
    }

    fun setVoltageFilter(voltage: Pair<Float, Float>?) {
        voltageFilter.value = voltage
        update()
    }

    fun setCategoryFilter(categories: ArrayList<Int>?) {
        categoryFilter.value = categories
        update()
    }

    fun setPriceFilter(price: Pair<Float, Float>?) {
        priceFilter.value = price
        update()
    }

    fun getFilteredList(): LiveData<List<AlcoObject>> {
        return filteredList
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

    private fun update() {
        val temporalFilteredList: MutableList<AlcoObject> = originalList.toMutableList()

        originalList.forEach { alcoObject ->
            if (!textFilter.value.isNullOrBlank()) {
                val name = Normalizer.normalize(
                    alcoObject.name.lowercase(Locale.ROOT), Normalizer.Form.NFD
                )
                if (!name.contains(textFilter.value ?: return@forEach)) temporalFilteredList.remove(
                    alcoObject
                )
            }

            if (volumeFilter.value != null) {
                if (alcoObject.volume < volumeFilter.value?.first ?: return@forEach ||
                    alcoObject.volume > volumeFilter.value?.second ?: return@forEach
                ) {
                    temporalFilteredList.remove(alcoObject)
                }
            }

            if (voltageFilter.value != null) {
                if (alcoObject.voltage < voltageFilter.value?.first?.toBigDecimal() ?: return@forEach ||
                    alcoObject.voltage > voltageFilter.value?.second?.toBigDecimal() ?: return@forEach
                ) {
                    temporalFilteredList.remove(alcoObject)
                }
            }

            if (!categoryFilter.value.isNullOrEmpty()) {
                categoryFilter.value?.forEach { category ->
                    if (!alcoObject.categories.contains(category)) {
                        temporalFilteredList.remove(alcoObject)
                    }
                }
            }

            if (priceFilter.value != null) {
                val displayPrice: BigDecimal =
                    alcoObject.shopToPrice.entries.sortedBy { it.value }[0].value
                        ?: BigDecimal.ZERO

                if (displayPrice < priceFilter.value?.first?.toBigDecimal() ||
                    displayPrice > priceFilter.value?.second?.toBigDecimal()) {
                        temporalFilteredList.remove(alcoObject)
                }
            }
        }
        filteredList.value = temporalFilteredList
    }

}