package com.redc4ke.taniechlanie.data

interface RequestListener {
    companion object {
        const val SUCCESS = 1
        const val NOT_LOGGED_IN = 2
        const val REPEATING_CATEGORIES = 3
        const val OTHER = 4
        const val NETWORK_ERR = 5
    }

    fun onComplete(resultCode: Int)
}