package com.example.data.network

import android.util.Log
import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

data class ApiLecture(
    @SerializedName("id") val id: Any?,
    @SerializedName("batch") val batch: String?,
    @SerializedName("subject") val subject: String?,
    @SerializedName("faculty") val faculty: String?,
    @SerializedName("classroom") val classroom: String?,
    @SerializedName("dayOfWeek") val dayOfWeek: Int?,
    @SerializedName("startTime") val startTime: String?,
    @SerializedName("endTime") val endTime: String?,
    @SerializedName("isChanged") val isChanged: Boolean?
)

data class CurrentLectureApiResponse(
    @SerializedName("current") val current: ApiLecture?,
    @SerializedName("next") val next: ApiLecture?
)

data class StudentApiResponse(
    @SerializedName("id") val id: Int?,
    @SerializedName("enrollmentNumber") val enrollmentNumber: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("branch") val branch: String?,
    @SerializedName("semester") val semester: Int?,
    @SerializedName("batch") val batch: String?
)

data class EnrollmentApiResponse(
    @SerializedName("success") val success: Boolean?,
    @SerializedName("student") val student: StudentApiResponse?,
    @SerializedName("error") val error: String?
)

interface LectureApiService {

    @GET("api/timetable/current")
    suspend fun getCurrentLecture(
        @Query("enrollment") enrollment: String
    ): Response<CurrentLectureApiResponse>

    @POST("api/enrollment")
    suspend fun enroll(
        @Body payload: Map<String, String>
    ): Response<EnrollmentApiResponse>

    @GET("api/room/{roomId}")
    suspend fun getRoom(
        @Path("roomId") roomId: String
    ): Response<Map<String, Any>>
}

object LectureApiClient {

    // Default to Android Emulator host loopback (10.0.2.2:3000)
    var baseUrl: String = "http://10.0.2.2:3000/"

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    val service: LectureApiService by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(LectureApiService::class.java)
    }

    suspend fun fetchCurrentLectureFromBackend(enrollment: String): CurrentLectureApiResponse? {
        return try {
            val response = service.getCurrentLecture(enrollment)
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.w("LectureApiClient", "Backend responded with code: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.w("LectureApiClient", "Could not reach backend (${e.message}), using local offline engine.")
            null
        }
    }
}
