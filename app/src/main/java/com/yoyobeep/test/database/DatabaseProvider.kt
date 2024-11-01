package com.yoyobeep.test.database

import android.content.Context
import androidx.room.Room

object DatabaseProvider {

    private var INSTANCE: PlayerDatabase? = null

    fun getDatabase(context: Context): PlayerDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                PlayerDatabase::class.java,
                "player_database"
            ).build()
            INSTANCE = instance
            instance
        }
    }
}
