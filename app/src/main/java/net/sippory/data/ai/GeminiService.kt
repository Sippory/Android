package net.sippory.data.ai

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.serialization.json.Json
import net.sippory.BuildConfig
import java.io.IOException

class GeminiService(private val context: Context) {
    private val json = Json { ignoreUnknownKeys = true }

    private val generativeModel =
        GenerativeModel(
            modelName = "gemini-2.5-flash",
            apiKey = BuildConfig.GEMINI_API_KEY,
        )

    suspend fun analyzeBottle(imageUri: Uri): Result<BottleInfo> {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "=== AI 분석 시작 ===")
            Log.d(TAG, "이미지 URI: $imageUri")
            Log.d(TAG, "사용 모델: gemini-2.5-flash")
            Log.d(TAG, "API 키 존재 여부: ${BuildConfig.GEMINI_API_KEY.isNotEmpty()}")
        }

        // API 키 유효성 검사
        if (BuildConfig.GEMINI_API_KEY.isEmpty()) {
            Log.e(TAG, "❌ Gemini API key가 설정되지 않았습니다")
            return Result.failure(Exception("Gemini API key가 설정되지 않았습니다"))
        }

        return try {
            if (BuildConfig.DEBUG) Log.d(TAG, "비트맵 로딩 시작...")
            val bitmap =
                loadBitmapFromUri(imageUri)
                    ?: return Result.failure<BottleInfo>(Exception("이미지를 불러올 수 없습니다")).also {
                        Log.e(TAG, "❌ 비트맵 로딩 실패")
                    }

            if (BuildConfig.DEBUG) Log.d(TAG, "✅ 비트맵 로딩 성공: ${bitmap.width}x${bitmap.height}")

            val prompt =
                """
                이 이미지에 있는 술병을 분석해주세요.

                다음 정보를 JSON 형식으로 반환해주세요:
                {
                  "name": "술의 정확한 이름",
                  "type": "Wine, Whiskey, Vodka, Rum, Gin, Sake, Soju, Beer, Liqueur 중 하나",
                  "abv": 알코올 도수 (숫자만, 알 수 없으면 null),
                  "country": "생산 국가",
                  "description": "간단한 설명 (50자 이내)",
                  "confidence": 0-100 사이의 신뢰도 점수
                }

                만약 이미지에 술병이 없거나 명확하지 않으면, confidence를 0으로 설정하고 name을 "인식할 수 없음"으로 해주세요.
                반드시 JSON 형식만 반환하고, 다른 텍스트는 포함하지 마세요.
                """.trimIndent()

            if (BuildConfig.DEBUG) Log.d(TAG, "Gemini API 호출 중...")
            val response =
                generativeModel.generateContent(
                    content {
                        image(bitmap)
                        text(prompt)
                    },
                )

            if (BuildConfig.DEBUG) Log.d(TAG, "✅ API 응답 받음")
            val responseText =
                response.text ?: return Result.failure<BottleInfo>(Exception("응답이 없습니다")).also {
                    Log.e(TAG, "❌ 응답 텍스트가 null입니다")
                }

            if (BuildConfig.DEBUG) Log.d(TAG, "원본 응답: $responseText")

            // JSON 파싱
            val cleanedJson =
                responseText
                    .trim()
                    .removePrefix("```json")
                    .removePrefix("```")
                    .removeSuffix("```")
                    .trim()

            if (BuildConfig.DEBUG) Log.d(TAG, "정리된 JSON: $cleanedJson")

            val bottleInfo = json.decodeFromString<BottleInfo>(cleanedJson)
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "✅ JSON 파싱 성공")
                Log.d(TAG, "인식된 술: ${bottleInfo.name}")
                Log.d(TAG, "종류: ${bottleInfo.type}")
                Log.d(TAG, "신뢰도: ${bottleInfo.confidence}%")
            }

            if (bottleInfo.confidence < MIN_CONFIDENCE_THRESHOLD) {
                if (BuildConfig.DEBUG) Log.w(TAG, "⚠️ 신뢰도가 너무 낮음: ${bottleInfo.confidence}%")
                Result.failure(Exception("술병을 명확하게 인식할 수 없습니다. 다른 각도에서 찍어보세요."))
            } else {
                if (BuildConfig.DEBUG) Log.d(TAG, "✅ AI 분석 성공!")
                Result.success(bottleInfo)
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ AI 분석 실패", e)
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "에러 타입: ${e.javaClass.simpleName}")
                Log.e(TAG, "에러 메시지: ${e.message}")
                Log.e(TAG, "스택 트레이스: ${e.stackTraceToString()}")
            }
            Result.failure(Exception("AI 분석 실패: ${e.message}", e))
        }
    }

    private fun loadBitmapFromUri(uri: Uri): Bitmap? {
        return try {
            if (BuildConfig.DEBUG) Log.d(TAG, "ContentResolver로 스트림 열기...")

            // 먼저 이미지 크기 확인
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream, null, options)
            }

            // inSampleSize 계산하여 메모리 최적화
            options.inSampleSize = calculateInSampleSize(options, MAX_IMAGE_WIDTH, MAX_IMAGE_HEIGHT)
            options.inJustDecodeBounds = false

            if (BuildConfig.DEBUG) {
                Log.d(TAG, "원본 이미지 크기: ${options.outWidth}x${options.outHeight}")
                Log.d(TAG, "다운샘플링 비율: ${options.inSampleSize}")
            }

            // 실제 비트맵 디코딩
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream, null, options)
            }
        } catch (e: IOException) {
            Log.e(TAG, "비트맵 로딩 실패", e)
            null
        }
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int,
    ): Int {
        val (height: Int, width: Int) = options.outHeight to options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            // 요청된 크기보다 크게 유지하면서 가장 큰 inSampleSize 값 계산
            while ((halfHeight / inSampleSize) >= reqHeight &&
                (halfWidth / inSampleSize) >= reqWidth
            ) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    companion object {
        private const val TAG = "GeminiService"
        private const val MIN_CONFIDENCE_THRESHOLD = 30
        private const val MAX_IMAGE_WIDTH = 1024
        private const val MAX_IMAGE_HEIGHT = 1024

        @Volatile
        private var INSTANCE: GeminiService? = null

        fun getInstance(context: Context): GeminiService {
            return INSTANCE ?: synchronized(this) {
                val instance = GeminiService(context.applicationContext)
                INSTANCE = instance
                Log.d(TAG, "GeminiService 인스턴스 생성됨")
                instance
            }
        }
    }
}
