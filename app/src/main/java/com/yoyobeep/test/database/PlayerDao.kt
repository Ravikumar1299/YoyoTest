package com.yoyobeep.test.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayer(player: PlayerDataClass)

    @Query("SELECT * FROM player_table")
    fun getAllPlayers(): Flow<List<PlayerDataClass>>

    @Query("SELECT * FROM player_table WHERE id = :playerId")
    suspend fun getPlayerById(playerId: Int): PlayerDataClass?

    @Update
    suspend fun updatePlayer(player: PlayerDataClass)

    @Query("DELETE FROM player_table WHERE id = :playerId")
    suspend fun deletePlayer(playerId: Int)

    @Query("DELETE FROM player_record_table WHERE playerId = :playerId")
    suspend fun deleteRecordsByPlayerId(playerId: Int)

    @Query("SELECT * FROM player_table WHERE name LIKE '%' || :name || '%'")
    suspend fun getPlayersByName(name: String): List<PlayerDataClass>

    @Query("SELECT EXISTS(SELECT 1 FROM player_table WHERE email = :email LIMIT 1)")
    suspend fun getPlayerByEmail(email: String?): Boolean
}




