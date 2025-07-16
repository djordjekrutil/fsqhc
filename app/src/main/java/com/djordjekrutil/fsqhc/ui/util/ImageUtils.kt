package com.djordjekrutil.fsqhc.ui.util

import com.djordjekrutil.fsqhc.feature.model.Place

object ImageUtils {

    fun optimizePhotoUrl(originalUrl: String?, targetSize: String = "100x100"): String? {
        return originalUrl?.let { url ->
            if (url.contains("fastly.4sqi.net") && url.contains("/img/general/")) {
                try {
                    val parts = url.split("/")
                    val generalIndex = parts.indexOfFirst { it == "general" }
                    if (generalIndex >= 0 && generalIndex < parts.size - 1) {
                        val prefix = parts.take(generalIndex + 1).joinToString("/")
                        val suffix = parts.drop(generalIndex + 2).joinToString("/")
                        "$prefix/$targetSize/$suffix"
                    } else {
                        url
                    }
                } catch (_: Exception) {
                    url
                }
            } else {
                url
            }
        }
    }

    object Sizes {
        const val THUMBNAIL = "80x80"
        const val HERO = "800x400"
    }
}

fun Place.getOptimizedThumbnailUrl(): String? {
    return ImageUtils.optimizePhotoUrl(photoUrl, ImageUtils.Sizes.THUMBNAIL)
}

fun Place.getOptimizedHeroUrl(): String? {
    val heroUrl = details?.photos?.firstOrNull() ?: photoUrl
    return ImageUtils.optimizePhotoUrl(heroUrl, ImageUtils.Sizes.HERO)
}