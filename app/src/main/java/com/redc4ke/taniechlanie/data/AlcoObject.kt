package com.redc4ke.taniechlanie.data

import java.io.Serializable
import java.math.BigDecimal

data class AlcoObject(
        var id: Int,
        var name: String,
        var shopToPrice: Map<Int, BigDecimal?>,
        var volume: Int,
        var voltage: BigDecimal,
        var categories: ArrayList<Int>,
        var photo: String?,
        var description: String?
) : Serializable