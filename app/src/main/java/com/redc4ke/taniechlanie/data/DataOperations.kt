package com.redc4ke.taniechlanie.data

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.TextView
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.ui.MainActivity
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

fun valueString(alcoObject: AlcoObject, context: MainActivity): String {
    val volume = alcoObject.volume.toBigDecimal()
    val voltage = alcoObject.voltage.times(100.toBigDecimal())
    val price = alcoObject.shopToPrice.entries
        .minByOrNull {
            if (it.value == null) {
                BigDecimal.valueOf(9999)
            } else {
                it.value!!
            }
        }?.value ?: BigDecimal.valueOf(0)
    val rounded = context.getPreferences(MODE_PRIVATE)
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

    return context.getString(string, substring)
}

fun lowestPriceString(alcoObject: AlcoObject, context: Context): String {
    return context.getString(R.string.suff_price,
        String.format("%.2f", alcoObject.shopToPrice.entries
            .minByOrNull {
                if (it.value == null) {
                    BigDecimal.valueOf(9999)
                } else {
                    it.value!!
                }
            }?.value ?: BigDecimal.valueOf(0)
        )
    )
}

fun priceString(price: BigDecimal, context: Context): String {
    return context.getString(
        R.string.suff_price,
        String.format("%.2f", price)
    )
}

fun volumeString(alcoObject: AlcoObject, context: Context): String {
    return context.getString(
        R.string.suff_volume,
        alcoObject.volume.toString()
    )
}

fun voltageString(alcoObject: AlcoObject, context: Context): String {
    return context.getString(
        R.string.suff_voltage,
        (alcoObject.voltage.times(100.toBigDecimal()).round(MathContext(2)))
            .stripTrailingZeros().toPlainString()
    )
}

fun imageFromBitmap(context: Context, bitmap: Bitmap, filename: String): File {
    val f = File(context.cacheDir, "$filename.jpg")
    f.createNewFile()

    val bos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
    val bitmapData = bos.toByteArray()

    val fos = FileOutputStream(f)
    fos.write(bitmapData)
    fos.flush()
    fos.close()

    return f
}

fun imageFromIntent(context: Context, intent: Intent, filename: String): File {
    val inputStream = context.contentResolver?.openInputStream(intent.data!!)
    val bitmap = BitmapFactory.decodeStream(inputStream)

    return imageFromBitmap(context, bitmap, filename)
}

fun autoBreak(s: String): String {
    val count = s.count { it.isWhitespace() }
    var r = s

    if (count == 1) {
        r = r.replace("\\s".toRegex(), "\n")
    }

    return r
}

fun textWrap(context: Context, s: String, tv: TextView, maxLines: Int = 6) {
    tv.text = s

    if (tv.lineCount > maxLines) {
        val lastCharShown = tv.layout.getLineVisibleEnd(maxLines - 1)
        tv.maxLines = maxLines

        val showMore = "  ${context.getString(R.string.show_more)}"

    }
}
