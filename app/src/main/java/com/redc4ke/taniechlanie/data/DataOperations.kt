package com.redc4ke.taniechlanie.data

import android.content.Context.MODE_PRIVATE
import android.view.Display
import androidx.fragment.app.Fragment
import com.redc4ke.taniechlanie.R
import kotlin.math.round

fun valueString(alcoObject: AlcoObject, fragment: Fragment): String {
    val volume = alcoObject.volume
    val voltage = alcoObject.voltage
    val price = alcoObject.minPrice
    val rounded = fragment.requireActivity().getPreferences(MODE_PRIVATE)
            .getBoolean("rounded_mR", false)

    val value = ((voltage * volume) / price)


    val substring = if (rounded) {
        (round(value) / 100).toString()
    } else {
        round(value).toInt().toString()
    }

    val string = if (rounded) R.string.suff_rvalue else R.string.suff_mrvalue

    return fragment.getString(string, substring)
}

fun priceString(alcoObject: AlcoObject, fragment: Fragment): String {
    return fragment.getString(R.string.suff_price,
            String.format("%.2f", alcoObject.minPrice))
}

fun volumeString(alcoObject: AlcoObject, fragment: Fragment): String {
    return fragment.getString(R.string.suff_volume,
            alcoObject.volume.toString())
}

fun voltageString(alcoObject: AlcoObject, fragment: Fragment): String {
    return fragment.getString(R.string.suff_voltage,
            alcoObject.voltage.toBigDecimal().stripTrailingZeros().toPlainString())
}