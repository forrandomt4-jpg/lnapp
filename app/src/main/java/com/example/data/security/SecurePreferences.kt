package com.example.data.security

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * SecurePreferences handles session and student identity persistence using
 * Android's EncryptedSharedPreferences (backed by the Android Keystore).
 * Provides a graceful fallback to MODE_PRIVATE for headless/testing environments.
 */
class SecurePreferences(context: Context) {

    private val prefs: SharedPreferences

    init {
        prefs = try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            EncryptedSharedPreferences.create(
                context,
                "secure_lecturenow_prefs",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            Log.w("SecurePreferences", "EncryptedSharedPreferences fallback to private prefs: ${e.message}")
            context.getSharedPreferences("secure_lecturenow_prefs_fallback", Context.MODE_PRIVATE)
        }
    }

    fun saveStudentEnrollment(
        enrollmentNumber: String,
        studentId: String,
        studentName: String,
        batch: String,
        branch: String = "Computer Engineering",
        semester: Int = 3
    ) {
        prefs.edit()
            .putString(KEY_ENROLLMENT_NO, enrollmentNumber)
            .putString(KEY_STUDENT_ID, studentId)
            .putString(KEY_STUDENT_NAME, studentName)
            .putString(KEY_BATCH, batch)
            .putString(KEY_BRANCH, branch)
            .putInt(KEY_SEMESTER, semester)
            .apply()
    }

    fun getSavedEnrollmentNumber(): String? = prefs.getString(KEY_ENROLLMENT_NO, null)

    fun getSavedStudentId(): String? = prefs.getString(KEY_STUDENT_ID, null)

    fun getSavedStudentName(): String? = prefs.getString(KEY_STUDENT_NAME, null)

    fun getSavedBatch(): String? = prefs.getString(KEY_BATCH, null)

    fun getSavedBranch(): String = prefs.getString(KEY_BRANCH, "Computer Engineering") ?: "Computer Engineering"

    fun getSavedSemester(): Int = prefs.getInt(KEY_SEMESTER, 3)

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    fun hasEnrolledStudent(): Boolean = getSavedEnrollmentNumber() != null

    companion object {
        private const val KEY_ENROLLMENT_NO = "enrollment_number"
        private const val KEY_STUDENT_ID = "student_id"
        private const val KEY_STUDENT_NAME = "student_name"
        private const val KEY_BATCH = "batch"
        private const val KEY_BRANCH = "branch"
        private const val KEY_SEMESTER = "semester"
    }
}
