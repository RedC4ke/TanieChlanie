package com.redc4ke.taniechlanie.data

import java.io.Serializable
import java.math.BigDecimal

data class AlcoObject(
        var id: Long,
        var name: String,
        var shopToPrice: Map<Int, BigDecimal?>,
        var volume: Int,
        var voltage: BigDecimal,
        var categories: List<Int>,
        var photo: String?,
        var description: String?
) : Serializable