package com.djordjekrutil.fsqhc.feature.model.util

import com.djordjekrutil.fsqhc.feature.model.Place

fun Place.hasMinimalDetails(): Boolean {
    return name.isNotEmpty() && categories.isNotEmpty()
}

fun Place.getPriceDisplay(): String {
    return when (price) {
        1 -> "$"
        2 -> "$$"
        3 -> "$$$"
        4 -> "$$$$"
        else -> ""
    }
}

fun Place.getRatingDisplay(): String {
    return rating?.let { "%.1f".format(it) } ?: ""
}

fun Place.getOpenStatusDisplay(): String {
    return when (isOpen) {
        true -> "Open"
        false -> "Closed"
        null -> ""
    }
}