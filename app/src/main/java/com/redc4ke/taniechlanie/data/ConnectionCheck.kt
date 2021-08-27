package com.redc4ke.taniechlanie.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket

object ConnectionCheck {
    fun perform(requestListener: RequestListener) {
        CoroutineScope(IO).launch {
            kotlin.runCatching {
                val socket = Socket()
                socket.connect(InetSocketAddress("8.8.8.8", 53), 4000)
                socket.close()

                withContext(Main) {
                    requestListener.onComplete(RequestListener.SUCCESS)
                }
            }.onFailure {
                withContext(Main) {
                    requestListener.onComplete(RequestListener.NETWORK_ERR)
                }
            }
        }
    }
}