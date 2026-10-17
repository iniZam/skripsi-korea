package com.example.gamebahasakorea.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.gamebahasakorea.data.local.dao.QuestionDao
import com.example.gamebahasakorea.data.local.dao.UserPerformanceDao
import com.example.gamebahasakorea.data.local.entity.QuestionEntity
import com.example.gamebahasakorea.data.local.entity.UserPerformanceEntity

/**
 * Main Database Class Room
 */
@Database(
    entities = [QuestionEntity::class, UserPerformanceEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun questionDao(): QuestionDao
    abstract fun userPerformanceDao(): UserPerformanceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "game_bahasa_korea_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
