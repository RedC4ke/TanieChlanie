package com.redc4ke.taniechlanie.data

import java.io.Serializable
import java.math.BigDecimal

data class AlcoObject(
        var id: Int,
        var name: String,
        var price: BigDecimal,
        var volume: Int,
        var voltage: BigDecimal,
        var shop: ArrayList<Int>,
        var categories: ArrayList<Int>,
        var photo: String?,
        var description: String?
): Serializable