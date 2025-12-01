package net.sippory.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID

class ImageFileManager(
    private val context: Context
) : ImageManager {
    private val imageDir: File by lazy {
        File(context.filesDir, IMAGE_DIR).apply {
            if (!exists()) mkdirs()
        }
    }

    /**
     * URI로부터 이미지를 앱 내부 저장소로 복사하고 파일 경로를 반환합니다.
     */
    override fun saveImage(uri: Uri): String? {
        try {
            // MIME 타입으로부터 파일 확장자 추출
            val extension = getMimeTypeExtension(uri) ?: "jpg"

            // 고유한 파일명 생성
            val fileName = "IMG_${UUID.randomUUID()}.$extension"
            val imageFile = File(imageDir, fileName)

            // URI에서 이미지 복사
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(imageFile).use { output ->
                    input.copyTo(output)
                }
            }

            // 파일의 절대 경로 반환
            return imageFile.absolutePath
        } catch (e: IOException) {
            Log.e(TAG, "Failed to save image", e)
            return null
        }
    }

    /**
     * 이미지 파일을 삭제합니다.
     */
    override fun deleteImage(filePath: String?): Boolean {
        if (filePath == null) return false

        return try {
            val file = File(filePath)
            if (file.exists()) {
                file.delete()
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete image", e)
            false
        }
    }

    /**
     * 사용하지 않는 이미지 파일들을 정리합니다.
     */
    override fun cleanupUnusedImages(usedPaths: List<String>) {
        try {
            if (!imageDir.exists()) return

            imageDir.listFiles()?.forEach { file ->
                if (!usedPaths.contains(file.absolutePath)) {
                    file.delete()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to cleanup unused images", e)
        }
    }

    /**
     * 파일 경로를 Uri로 변환합니다.
     */
    override fun getUriFromPath(filePath: String?): Uri? {
        if (filePath == null) return null
        return try {
            Uri.fromFile(File(filePath))
        } catch (e: Exception) {
            null
        }
    }

    /**
     * URI로부터 MIME 타입을 감지하고 적절한 파일 확장자를 반환합니다.
     */
    private fun getMimeTypeExtension(uri: Uri): String? {
        val mimeType = context.contentResolver.getType(uri)
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
    }

    companion object {
        private const val TAG = "ImageFileManager"
        private const val IMAGE_DIR = "bottle_images"
    }
}
