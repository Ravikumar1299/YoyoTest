package com.yoyobeep.test.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player_table")
data class PlayerDataClass(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val email:String,
    val age: String,
    val height: Float,
    val weight: Int,
    val gender: String,
    val playerImage: ByteArray? = null
)

@Entity(tableName = "player_record_table")
data class PlayerRecordDataClass(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // Auto-generated primary key for records
    val playerId: Int,
    val name: String,// Foreign key linking to PlayerDataClass
    val level: Int = 0,
    val shuttles: Int = 0,
    val cumulativeDistance: Int = 0,
    val cumulativeTime: Float = 0f,
    val shuttleTime: Float = 0f,
    val speed: Float = 0f,
    val date: String, // Date in YYYY-MM-DD format
    val time: String
)


