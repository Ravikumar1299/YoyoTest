package com.yoyobeep.test.database

import androidx.room.Entity
import androidx.room.PrimaryKey



@Entity(tableName = "group_table")
data class GroupDataClass(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val groupName: String,
    val groupIcon: ByteArray? = null
)