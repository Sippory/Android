package net.sippory.utils

import android.net.Uri

interface ImageManager {
    fun saveImage(uri: Uri): String?

    fun deleteImage(filePath: String?): Boolean

    fun cleanupUnusedImages(usedPaths: List<String>)

    fun getUriFromPath(filePath: String?): Uri?
}
