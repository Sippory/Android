package net.sippory.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import net.sippory.data.dao.BottleDao
import net.sippory.data.entity.BottleEntity

@Database(
    entities = [BottleEntity::class],
    version = 2,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bottleDao(): BottleDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance =
                    Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "sippory_database",
                    )
                        .fallbackToDestructiveMigration() // 개발 중이므로 간단하게 DB 재생성
                        .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
