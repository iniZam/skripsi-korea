package com.example.gamebahasakorea.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gamebahasakorea.data.local.entity.QuestionEntity

/**
 * Data Access Object (DAO) untuk Pengelolaan Soal
 */
@Dao
interface QuestionDao {

    @Query("SELECT * FROM questions")
    suspend fun getAllQuestions(): List<QuestionEntity>

    @Query("SELECT * FROM questions WHERE id = :id")
    suspend fun getQuestionById(id: Int): QuestionEntity?

    @Query("SELECT * FROM questions WHERE category = :category")
    suspend fun getQuestionsByCategory(category: String): List<QuestionEntity>

    @Query("SELECT * FROM questions WHERE difficultyScore BETWEEN :minDifficulty AND :maxDifficulty")
    suspend fun getQuestionsByDifficultyRange(minDifficulty: Double, maxDifficulty: Double): List<QuestionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuestionEntity>)

    @Query("SELECT COUNT(*) FROM questions")
    suspend fun getQuestionCount(): Int
}
