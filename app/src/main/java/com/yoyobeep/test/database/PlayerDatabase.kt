package com.yoyobeep.test.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
//@Database(entities = [PlayerDataClass::class, PlayerRecordDataClass::class], version = 1)
//abstract class PlayerDatabase : RoomDatabase() {
//    abstract fun playerDao(): PlayerDao
//    abstract fun playerRecordDao(): PlayerRecordDao
//
//    companion object {
//        @Volatile
//        private var INSTANCE: PlayerDatabase? = null
//
//        fun getDatabase(context: Context): PlayerDatabase {
//            return INSTANCE ?: synchronized(this) {
//                val instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    PlayerDatabase::class.java,
//                    "player_database"
//                ).build()
//                INSTANCE = instance
//                instance
//            }
//        }
//    }
//}



@Database(entities = [PlayerDataClass::class, PlayerRecordDataClass::class, GroupDataClass::class], version = 2) // Update version to 2
abstract class PlayerDatabase : RoomDatabase() {
    abstract fun playerDao(): PlayerDao
    abstract fun playerRecordDao(): PlayerRecordDao
    abstract fun groupDao(): GroupDao // Add this new DAO

    companion object {
        @Volatile
        private var INSTANCE: PlayerDatabase? = null

        fun getDatabase(context: Context): PlayerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PlayerDatabase::class.java,
                    "player_database"
                )
                    .addMigrations(MIGRATION_1_2) // Add migration from version 1 to 2
                    .build()
                INSTANCE = instance
                instance
            }
        }

        // Migration from version 1 to version 2 (adding group_table)
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create the new group_table
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `group_table` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                        `groupName` TEXT NOT NULL, 
                        `groupIcon` BLOB
                    )
                    """.trimIndent()
                )
            }
        }
    }
}


