package com.yoyobeep.test.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface GroupDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: GroupDataClass)

    @Query("SELECT * FROM group_table")
    fun getAllGroups(): LiveData<List<GroupDataClass>>

    @Query("SELECT * FROM group_table WHERE id = :id")
    suspend fun getGroupById(id: Int): GroupDataClass?
}
