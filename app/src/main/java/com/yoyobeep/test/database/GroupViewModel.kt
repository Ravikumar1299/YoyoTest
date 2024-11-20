package com.yoyobeep.test.database

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class GroupViewModel(application: Application) : AndroidViewModel(application) {

    private val groupDao: GroupDao = PlayerDatabase.getDatabase(application).groupDao()

    // MutableStateFlow to hold the list of groups
    private val _groups = MutableStateFlow<List<GroupDataClass>>(emptyList())
    val groups: StateFlow<List<GroupDataClass>> get() = _groups

    // MutableLiveData to hold the current group data
    private val _currentGroup = MutableStateFlow<GroupDataClass?>(null)
    val currentGroup: StateFlow<GroupDataClass?> = _currentGroup

    // Variables for the group fields
    var groupId by mutableStateOf(0)
    var groupName by mutableStateOf("")
    var groupIcon by mutableStateOf<Bitmap?>(null)
    var image by mutableStateOf<Bitmap?>(null)
    init {
        // Fetch all groups from the database and update _groups
        groupDao.getAllGroups().observeForever { groupList ->
            _groups.value = groupList
        }
    }


    // Function to fetch a group by ID
    fun fetchGroupById(id: Int) {
        viewModelScope.launch {
            val group = groupDao.getGroupById(id)
            _currentGroup.value = group
            setGroupData(group)
        }
    }

    // Function to save a group
    fun saveGroup() {
        viewModelScope.launch {
            val group = GroupDataClass(
                groupName = groupName,
                groupIcon = groupIcon?.let { convertBitmapToByteArray(it) }
            )
            groupDao.insertGroup(group)
            resetGroup()
        }
    }


    // Convert Bitmap to ByteArray (Similar to your PlayerViewModel)
    private fun convertBitmapToByteArray(bitmap: Bitmap): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

    // Convert ByteArray to Bitmap (Similar to your PlayerViewModel)
    fun getBitmapFromByteArray(byteArray: ByteArray?): Bitmap? {
        return byteArray?.let {
            val inputStream = ByteArrayInputStream(it)
            BitmapFactory.decodeStream(inputStream)
        }
    }

    // Function to reset group fields
    fun resetGroup() {
        groupId = 0
        groupName = ""
        groupIcon = null
    }

    // Function to set group data
    fun setGroupData(group: GroupDataClass?) {
        group?.let {
            groupId = it.id
            groupName = it.groupName
            groupIcon = getBitmapFromByteArray(it.groupIcon)
        }
    }

    fun updatePlayerImage(bitmap: Bitmap?) {
        image = bitmap
    }

//    // Function to update an existing group
//    fun updateGroup() {
//        viewModelScope.launch {
//            val updatedGroup = GroupDataClass(
//                id = groupId,
//                groupName = groupName,
//                groupIcon = groupIcon?.let { convertBitmapToByteArray(it) }
//            )
//            groupDao.updateGroup(updatedGroup)
//            resetGroup()
//        }
//    }
//
//    // Function to delete a group by ID
//    fun deleteGroup(id: Int) {
//        viewModelScope.launch {
//            groupDao.deleteGroup(id)
//        }
//    }
}
