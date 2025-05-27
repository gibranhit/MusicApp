package com.gibran.core.common


import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException

fun Throwable.handleError(errorCustom: Int): Int = when (this) {
    is UnknownHostException,
    is SocketTimeoutException,
    is IOException -> R.string.error_no_internet
    is HttpException -> R.string.error_server
    is SSLHandshakeException -> R.string.error_ssl
    else -> errorCustom
}