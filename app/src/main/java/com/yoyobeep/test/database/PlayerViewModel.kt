package com.yoyobeep.test.database

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream


class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    private val playerDao: PlayerDao = PlayerDatabase.getDatabase(application).playerDao()
    private val playerRecordDao: PlayerRecordDao = PlayerDatabase.getDatabase(application).playerRecordDao()

    private val _allPlayerRecords = MutableStateFlow<List<PlayerRecordDataClass>>(emptyList())
    val allPlayerRecords: StateFlow<List<PlayerRecordDataClass>> = _allPlayerRecords.asStateFlow()

    private val _playerData = MutableLiveData<PlayerDataClass>()
    val playerData: LiveData<PlayerDataClass> get() = _playerData

    private val _currentPlayer = MutableStateFlow<PlayerDataClass?>(null)
    val currentPlayer: StateFlow<PlayerDataClass?> = _currentPlayer
    // Mutable variables for each player field
    var id by mutableStateOf(-1)
    var name by mutableStateOf("")
    var email by mutableStateOf("")
    var age by mutableStateOf("")
    var height by mutableStateOf(2.5f)
    var weight by mutableStateOf(20)
    var gender by mutableStateOf("Male")
    var image by mutableStateOf<Bitmap?>(null)

    // MutableStateFlow to hold the list of players
    private val _players = MutableStateFlow<List<PlayerDataClass>>(emptyList())
    val players: StateFlow<List<PlayerDataClass>> get() = _players

    private val _playersByName = MutableStateFlow<List<PlayerDataClass>>(emptyList())
    val playersByName: StateFlow<List<PlayerDataClass>> = _playersByName

    private val _playerRecords = MutableStateFlow<List<PlayerRecordDataClass>>(emptyList())
    val playerRecords: StateFlow<List<PlayerRecordDataClass>> get() = _playerRecords


    init {
        // Fetch players from the database and update _players
        viewModelScope.launch {
            playerDao.getAllPlayers().collect { playerList ->
                _players.value = playerList
            }
        }
    }



    // Function to fetch players by name
    fun fetchPlayersByName(name: String) {
        viewModelScope.launch {
            val players = playerDao.getPlayersByName(name) // Query players by name
            _playersByName.value = players // Update the state
        }
    }

    // Insert a player into the database
    fun savePlayer() {

        viewModelScope.launch {

            val player = PlayerDataClass(
                name = name,
                email = email,
                age = age,
                height = height,
                weight = weight,
                gender = gender,
                playerImage = image?.let { convertBitmapToByteArray(it) }
            )
            playerDao.insertPlayer(player)
        }
    }

    fun updatePlayerImage(bitmap: Bitmap?) {
        image = bitmap
    }

//    private fun convertBitmapToByteArray(bitmap: Bitmap): ByteArray {
//        val byteArrayOutputStream = ByteArrayOutputStream()
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
//        return byteArrayOutputStream.toByteArray()
//    }

    private fun convertBitmapToByteArray(bitmap: Bitmap): ByteArray {
        var compressedBitmap = bitmap
        var quality = 85 // Start at 85% compression quality
        val byteArrayOutputStream = ByteArrayOutputStream()
        var imageByteArray: ByteArray

        do {
            // Clear the previous output stream
            byteArrayOutputStream.reset()

            // Compress the bitmap to JPEG format
            compressedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)
            imageByteArray = byteArrayOutputStream.toByteArray()

            // Check if the image size is under the required limit
            val imageSizeKB = imageByteArray.size / 1024

            // If the size is greater than the desired max size, reduce quality or resize
            if (imageSizeKB > 200) {
                quality -= 10 // Reduce quality by 10%
                if (quality < 50) {
                    // If reducing quality isn't enough, resize the bitmap
                    val newWidth = (compressedBitmap.width * 0.8).toInt()
                    val newHeight = (compressedBitmap.height * 0.8).toInt()
                    compressedBitmap = Bitmap.createScaledBitmap(compressedBitmap, newWidth, newHeight, true)
                }
            }

        } while (imageSizeKB > 200 && quality > 50)

        return imageByteArray
    }


    fun getBitmapFromByteArray(byteArray: ByteArray?): Bitmap? {
        return byteArray?.let {
            val inputStream = ByteArrayInputStream(it)
            BitmapFactory.decodeStream(inputStream)
        }
    }

    // New method to fetch a player by ID
    suspend fun getPlayerById(playerId: Int): PlayerDataClass? {
        return playerDao.getPlayerById(playerId)
    }


    // Save a player record
    fun savePlayerRecord(
        playerId: Int,
        name:String,
        level: Int,
        shuttles: Int,
        cumulativeDistance: Int,
        cumulativeTime: Float,
        shuttleTime: Float,
        speed: Float,
        date: String,
        time: String
    ) {
        viewModelScope.launch {
            val record = PlayerRecordDataClass(
                playerId = playerId,
                name = name,
                level = level,
                shuttles = shuttles,
                cumulativeDistance = cumulativeDistance,
                cumulativeTime = cumulativeTime,
                shuttleTime = shuttleTime,
                speed = speed,
                date = date, // Date in YYYY-MM-DD format
                time = time
            )
            playerRecordDao.insertPlayerRecord(record)
            // Refresh the player records
            _playerRecords.value = playerRecordDao.getPlayerRecordsByPlayerId(playerId).first()
        }
    }

    // Get records by player ID
    fun getPlayerRecordsByPlayerId(playerId: Int): Flow<List<PlayerRecordDataClass>> {
        return playerRecordDao.getPlayerRecordsByPlayerId(playerId)
    }

    suspend fun getplayerByemail(email: String?): Boolean {
        return playerDao.getPlayerByEmail(email)
    }

    // Get a specific player record by ID

    fun fetchPlayerRecordsByPlayerId(playerId: Int) {
        viewModelScope.launch {
            _playerRecords.value = playerRecordDao.getPlayerRecordsByPlayerId(playerId).first()
        }
    }



    fun fetchAllPlayerRecords() {
        viewModelScope.launch {
            playerRecordDao.getAllPlayerRecords().collect { records ->
                _allPlayerRecords.value = records
            }
        }
    }


    // Fetch a specific player record by ID
    suspend fun getPlayerRecordById(recordId: Int): PlayerRecordDataClass? {
        return playerRecordDao.getPlayerRecordById(recordId)
    }


    fun resetPlayer() {
        name = ""
        image = null
        age = ""
        height = 0f
        weight = 0
        gender = "Male"
        image = null
    // Reset height as needed
        // Reset other fields...
    }

    // Method to set existing player data
    fun setPlayerData(player: PlayerDataClass) {
        id = player.id
        name = player.name
        age = player.age
        height = player.height
        weight = player.weight
        gender = player.gender
        image = getBitmapFromByteArray(player.playerImage)
    }

    fun updatePlayer(player: PlayerViewModel) {
        viewModelScope.launch {
            // Update player data based on the provided player object and its ID
            playerDao.updatePlayer(
                PlayerDataClass(
                    id = player.id,
                    email  = player.email,
                    name = player.name,
                    age = player.age,
                    height = player.height,
                    weight = player.weight,
                    gender = player.gender, // Assuming gender is included in player
                    playerImage = convertBitmapToByteArray(player.image!!)
                )

            )
            fetchPlayerById(player.id)
        }
    }
    fun fetchPlayerById(playerId: Int) {
        viewModelScope.launch {
            val player = playerDao.getPlayerById(playerId)
            _currentPlayer.value = player
        }
    }

    fun deletePlayerAndRecords(playerId: Int) {
        viewModelScope.launch {
            playerDao.deletePlayer(playerId) // Delete the player
            playerDao.deleteRecordsByPlayerId(playerId) // Delete associated records
        }
    }



//    fun setPlayerData(player: PlayerDataClass) {
//        // Populate the ViewModel state with the player's existing data
//        _currentPlayer.value = player
//    }
//
//    // Reset player data
//    fun resetPlayer() {
//        _currentPlayer.value = null
//    }


}




