package com.example.gamebahasakorea.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.gamebahasakorea.data.local.entity.UserPerformanceEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO untuk Merekam dan Menganalisis Log Performa Pengguna
 */
@Dao
interface UserPerformanceDao {

    @Insert
    suspend fun insertPerformanceLog(log: UserPerformanceEntity)

    @Insert
    suspend fun insertPerformanceLogs(logs: List<UserPerformanceEntity>)

    @Query("SELECT * FROM user_performance_logs ORDER BY sessionTimestamp DESC")
    fun getAllLogs(): Flow<List<UserPerformanceEntity>>

    @Query("SELECT * FROM user_performance_logs ORDER BY sessionTimestamp DESC LIMIT :limit")
    suspend fun getRecentLogs(limit: Int): List<UserPerformanceEntity>

    @Query("SELECT AVG(score) FROM user_performance_logs")
    suspend fun getAverageScore(): Double?

    @Query("SELECT AVG(CASE WHEN isCorrect THEN 1.0 ELSE 0.0 END) FROM user_performance_logs")
    suspend fun getAverageAccuracy(): Double?

    @Query("SELECT AVG(responseTimeSeconds) FROM user_performance_logs")
    suspend fun getAverageResponseTime(): Double?

    @Query("DELETE FROM user_performance_logs")
    suspend fun clearAllLogs()
}
