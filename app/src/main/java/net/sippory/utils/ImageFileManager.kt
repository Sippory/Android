package net.sippory.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID

object ImageFileManager {
    private const val IMAGE_DIR = "bottle_images"

    /**
     * URI로부터 이미지를 앱 내부 저장소로 복사하고 파일 경로를 반환합니다.
     */
    fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
        try {
            // 이미지 저장 디렉토리 생성
            val imageDir = File(context.filesDir, IMAGE_DIR)
            if (!imageDir.exists()) {
                imageDir.mkdirs()
            }

            // 고유한 파일명 생성
            val fileName = "IMG_${UUID.randomUUID()}.jpg"
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
            e.printStackTrace()
            return null
        }
    }

    /**
     * 이미지 파일을 삭제합니다.
     */
    fun deleteImage(context: Context, filePath: String?): Boolean {
        if (filePath == null) return false

        return try {
            val file = File(filePath)
            if (file.exists()) {
                file.delete()
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 사용하지 않는 이미지 파일들을 정리합니다.
     */
    fun cleanupUnusedImages(context: Context, usedPaths: List<String>) {
        try {
            val imageDir = File(context.filesDir, IMAGE_DIR)
            if (!imageDir.exists()) return

            imageDir.listFiles()?.forEach { file ->
                if (!usedPaths.contains(file.absolutePath)) {
                    file.delete()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 파일 경로를 Uri로 변환합니다.
     */
    fun getUriFromPath(filePath: String?): Uri? {
        if (filePath == null) return null
        return try {
            Uri.fromFile(File(filePath))
        } catch (e: Exception) {
            null
        }
    }
}
