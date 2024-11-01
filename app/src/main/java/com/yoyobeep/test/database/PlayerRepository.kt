package com.yoyobeep.test.database

import kotlinx.coroutines.flow.Flow

class PlayerRepository (private val playerRecordDao: PlayerRecordDao){

    suspend fun insertPlayerRecord(record: PlayerRecordDataClass): Long {
        return playerRecordDao.insertPlayerRecord(record)
    }

    fun getPlayerRecordsByPlayerId(playerId: Int): Flow<List<PlayerRecordDataClass>> {
        return playerRecordDao.getPlayerRecordsByPlayerId(playerId)
    }

    suspend fun getPlayerRecordById(recordId: Int): PlayerRecordDataClass? {
        return playerRecordDao.getPlayerRecordById(recordId)
    }
}