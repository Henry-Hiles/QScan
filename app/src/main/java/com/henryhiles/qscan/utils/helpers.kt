package com.henryhiles.qscan.utils

import android.webkit.URLUtil

object Helpers {
    fun isURL(value: String): Boolean {
        return URLUtil.isValidUrl(value)
    }
}