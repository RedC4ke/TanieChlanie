package com.redc4ke.taniechlanie.data

import android.content.Context.MODE_PRIVATE
import android.util.Log
import android.view.Display
import androidx.fragment.app.Fragment
import com.redc4ke.taniechlanie.R
import java.math.MathContext
import java.math.RoundingMode
import kotlin.math.round

fun valueString(alcoObject: AlcoObject, fragment: Fragment): String {
    val volume = alcoObject.volume.toBigDecimal()
    val voltage = alcoObject.voltage.times(100.toBigDecimal())
    val price = alcoObject.price
    val rounded = fragment.requireActivity().getPreferences(MODE_PRIVATE)
            .getBoolean("rounded_mR", false)

    val value =
        if (price != (0).toBigDecimal())
                ((voltage * volume) / price)
        else
            (0).toBigDecimal()

    val substring = if (rounded) {
        (value.setScale(0, RoundingMode.CEILING).div(100.toBigDecimal())).toString()
    } else {
        value.setScale(0, RoundingMode.CEILING).toInt().toString()
    }

    val string = if (rounded) R.string.suff_rvalue else R.string.suff_mrvalue

    return fragment.getString(string, substring)
}

fun priceString(alcoObject: AlcoObject, fragment: Fragment): String {
    return fragment.getString(R.string.suff_price,
            String.format("%.2f", alcoObject.price))
}

fun volumeString(alcoObject: AlcoObject, fragment: Fragment): String {
    return fragment.getString(R.string.suff_volume,
            alcoObject.volume.toString())
}

fun voltageString(alcoObject: AlcoObject, fragment: Fragment): String {
    return fragment.getString(R.string.suff_voltage,
            (alcoObject.voltage.times(100.toBigDecimal()).round(MathContext(2)))
                    .stripTrailingZeros().toPlainString())
}