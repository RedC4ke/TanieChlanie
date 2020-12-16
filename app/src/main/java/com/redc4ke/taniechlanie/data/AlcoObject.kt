package com.redc4ke.taniechlanie.data

import java.io.Serializable

data class AlcoObject(
        var id: Int,
        var name: String,
        var minPrice: Float,
        var maxPrice: Float?,
        var promoPrice: Float?,
        var volume: Int,
        var voltage: Float,
        var shop: ArrayList<Int>,
        var categories: ArrayList<Int>,
        var photo: String?
): Serializable