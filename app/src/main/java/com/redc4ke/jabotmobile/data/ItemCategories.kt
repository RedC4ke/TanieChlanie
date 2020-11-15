package com.redc4ke.jabotmobile.data

import com.redc4ke.jabotmobile.R
import java.io.Serializable

object ItemCategories {
    val list = arrayListOf(
            ItemCategory(1, "Wino czerwone", R.drawable.red_wine),
            ItemCategory(2, "Wino białe", R.drawable.white_wine),
            ItemCategory(3, "Whisky", R.drawable.whisky),
            ItemCategory(4, "Niska jakość", R.drawable.low_quality),
            ItemCategory(5, "Napój winny", R.drawable.wine_alike),
            ItemCategory(6, "Piwo", R.drawable.beer),
            ItemCategory(7, "Szampan", R.drawable.champagne),
            ItemCategory(8, "Rum", R.drawable.rum),
            ItemCategory(9, "Wódka smakowa", R.drawable.flavoured_vodka),
            ItemCategory(10, "Napój ziołowy", R.drawable.herbal),
            ItemCategory(11, "Koncernowe", R.drawable.koncernowe),
            ItemCategory(12, "Kraft", R.drawable.kraft)
    )

}

data class ItemCategory(val id: Int, val name: String, val icon: Int): Serializable
