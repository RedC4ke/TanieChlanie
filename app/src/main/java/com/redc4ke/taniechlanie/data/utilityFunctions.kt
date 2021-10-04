package com.redc4ke.taniechlanie.data

import android.content.Context
import android.widget.Toast

fun longToast(context: Context, string: String) {
    Toast.makeText(context, string, Toast.LENGTH_LONG).show()
}