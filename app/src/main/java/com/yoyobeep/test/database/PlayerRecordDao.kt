package com.yoyobeep.test.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayerRecord(record: PlayerRecordDataClass): Long

    @Query("SELECT * FROM player_record_table WHERE playerId = :playerId")
    fun getPlayerRecordsByPlayerId(playerId: Int): Flow<List<PlayerRecordDataClass>>

    @Query("SELECT * FROM player_record_table WHERE id = :recordId")
    suspend fun getPlayerRecordById(recordId: Int): PlayerRecordDataClass?

    @Query("SELECT * FROM player_record_table")
    fun getAllPlayerRecords(): Flow<List<PlayerRecordDataClass>>
}
