package com.yoyobeep.test.screens

import androidx.compose.foundation.lazy.items
import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.BottomSheetState
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.TextButton
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter

import com.yoyobeep.test.R
import com.yoyobeep.test.SortDialog
import com.yoyobeep.test.byteArrayToBitmap
import com.yoyobeep.test.database.PlayerDataClass
import com.yoyobeep.test.database.PlayerRecordDataClass
import com.yoyobeep.test.database.PlayerViewModel
import com.yoyobeep.test.ui.theme.AppBackgroundColor
import com.yoyobeep.test.ui.theme.SplashColor
import com.yoyobeep.test.ui.theme.fontFamily
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.Calendar


@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialApi::class)
@Composable
fun PlayerDetailScreen(playerId:Int,playerName:String,navController: NavHostController, navBackStackEntry: NavBackStackEntry) {
    val viewModel: PlayerViewModel = viewModel()
    var player by remember { mutableStateOf<PlayerDataClass?>(null) }
    LaunchedEffect(playerId) {
        player = viewModel.getPlayerById(playerId)
    }
        AddPlayerBottomSheet(navController,playerId,player)
    // Pass the splash color to the child composable

}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PlayerProfileScreen(
    navController: NavHostController,
    bottomSheetScaffoldState: BottomSheetScaffoldState,
    playerId: Int
) {
    val viewModel: PlayerViewModel = viewModel()

    val currentPlayer by viewModel.currentPlayer.collectAsState()

    // Fetch player data when the screen is launched
    LaunchedEffect(playerId) {
        viewModel.fetchPlayerById(playerId)
    }
    var player by remember { mutableStateOf<PlayerDataClass?>(null) }
    val playerRecords by viewModel.playerRecords.collectAsState()

    // Use LaunchedEffect to fetch player data
    LaunchedEffect(playerId) {
        player = viewModel.getPlayerById(playerId)
        viewModel.fetchPlayerRecordsByPlayerId(playerId)
    }

    // Observe playerRecords for update

    // Add a loading indicator to handle the case when player data is still being fetched
    if (player == null) {
        CircularProgressIndicator(modifier = Modifier.fillMaxSize())
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            HeaderWithImage(navController, player, bottomSheetScaffoldState, viewModel)

            Spacer(modifier = Modifier.height(16.dp))
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)) {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, shape = RoundedCornerShape(8.dp))
                        .padding(8.dp)
                        .height(IntrinsicSize.Min)
                ) {
                    PlayerStat("Height (Ft)", player!!.height.toString())
                    VerticalDivider(color = Color(0xffE0E0E0))
                    PlayerStat("Weight (Kg)", player!!.weight.toString())
                }
            }

            ResultsSection(playerRecords)
        }
    }
}



@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HeaderWithImage(
    navController: NavHostController,
    player: PlayerDataClass?,
    bottomSheetScaffoldState: BottomSheetScaffoldState,
    viewModel: PlayerViewModel
) {
var context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            color = SplashColor
        ) {
            Row(

                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp, 30.dp, 15.dp, 0.dp)

            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically

                ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White.copy(alpha = 0.27f), shape = CircleShape)
                    .clickable { // Use clickable modifier for back navigation
                        navController.popBackStack() // Call the passed-in callback
                },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_left),
                        contentDescription = null,
                        tint = Color.White
                    )
                }
                    var showDeleteConfirmation by remember { mutableStateOf(false) }
                    var showDialog by remember { mutableStateOf(false) }
                    var selectedOption by remember { mutableStateOf("") }
                    var popupPosition by remember { mutableStateOf<IntSize?>(null) }
                    val scope = rememberCoroutineScope()
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Your other UI components

                        // Box with three dots
                        TopEndMenuIcon { size ->
                            showDialog = true
                            popupPosition = size
                        }

                        if (showDialog && popupPosition != null) {
                            Popup(alignment = Alignment.BottomEnd,
                                properties = PopupProperties(
                                    focusable = true, // Ensure focus to detect outside clicks
                                    dismissOnClickOutside = true // Close when clicked outside
                                ),
                                onDismissRequest = {
                                    showDialog = false // Close the popup when clicking outside
                                }
                                ) {
                                Column(
                                    modifier = Modifier
                                        .background(Color.White, shape = RoundedCornerShape(12.dp))
                                        .padding(8.dp)
                                        .width(140.dp),
                                ) {
                                    Text(
                                        "Edit",
                                        modifier = Modifier
                                            .clickable {
                                                selectedOption = "Edit"
                                                showDialog = false
                                                // Remember a coroutine scope within AddPlayerBottomSheet
                                                scope.launch { // Launch a coroutine to call the suspend function
                                                    bottomSheetScaffoldState.bottomSheetState.expand()
                                                }

                                            }
                                            .padding(8.dp)
                                        .fillMaxWidth(),
                                        textAlign = TextAlign.Center
                                    )
                                    Divider( color = Color(0xFFE5E5E5))


                                    Text(
                                        "Delete",
                                        modifier = Modifier
                                            .clickable {
                                                showDeleteConfirmation = true
                                            }
                                            .padding(8.dp)
                                            .fillMaxWidth(),
                                        textAlign = TextAlign.Center
                                    )

                                    if (showDeleteConfirmation) {
                                        DeleteConfirmationDialog(
                                            onConfirm = {
                                                player?.id?.let { viewModel.deletePlayerAndRecords(it) }
                                                Toast.makeText(context, "Player deleted successfully", Toast.LENGTH_SHORT).show()
                                                navController.popBackStack()
//                                                player?.id?.let { deletePlayer(it) }
                                            },
                                            onDismiss = {
                                                showDeleteConfirmation = false
                                            }
                                        )
                                    }


                                }
                            }
                        }


                    }

            }

            }


        }

        val imageBitmap = player?.playerImage?.let { byteArrayToBitmap(it) }
        val imageImageBitmap = imageBitmap?.asImageBitmap()

        Image(
            bitmap = imageImageBitmap ?: ImageBitmap.imageResource(id = R.drawable.img_dummy_male), // Replace with your image resource
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(70.dp)
                .align(Alignment.BottomCenter)
                .offset(y = -50.dp) // Adjust the offset to cut the image into the blue background
                .clip(CircleShape)
                .border(
                    width = 3.dp, // Adjust border width as needed
                    color = Color.White,
                    shape = CircleShape
                ),
            contentScale = ContentScale.Crop
        )


        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .wrapContentWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                Text(
                    text = player!!.name,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.W400,
                    fontSize = 20.sp,
                    color = Color(0xff3D3D3D)
                )
                Text(
                    text = player.age,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.W400,
                    fontSize = 14.sp,
                    color = Color(0xff9E9E9E)
                )
            }
        }
    }
}



@Composable
fun TopEndMenuIcon(onClick: (IntSize) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.TopEnd
    ) {
        var size by remember { mutableStateOf(IntSize.Zero) }

        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color.White.copy(alpha = 0.27f), shape = CircleShape)
                .clickable {
                    onClick(size)
                }
                .onGloballyPositioned { layoutCoordinates ->
                    size = layoutCoordinates.size
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_dots_vertical), // Replace with your actual icon resource
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}

@Composable
fun PlayerStat(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = label,
            fontFamily = fontFamily,
            fontWeight = FontWeight.W400,
            fontSize = 12.sp,
            color = Color(0xff767676))

        Text(text = value,
            fontFamily = fontFamily,
            fontWeight = FontWeight.W400,
            fontSize = 16.sp,
            color = Color.Black)
    }
}

@Composable
fun ResultsSection(playerRecord: List<PlayerRecordDataClass>) {
    var isDialogOpen by remember { mutableStateOf(false) }


    var sortedRecords by remember { mutableStateOf(playerRecord.sortedBy { it.id }) }
    // Function to sort records
    fun sortRecords(ascending: Boolean) {
        sortedRecords = if (ascending) {
            playerRecord.sortedBy { it.id } // Assuming date is in the right format
        } else {
            playerRecord.sortedByDescending { it.id }
        }
    }
    LaunchedEffect(playerRecord) {
        // Default sort order is ascending
        sortedRecords = playerRecord.sortedBy { it.id }
    }
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.material.Text(
                text = "Results",
                fontSize = 24.sp,
                fontWeight = FontWeight.W400,
                fontFamily = fontFamily
            )
            TextButton(onClick = { isDialogOpen = true }) {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    androidx.compose.material.Text(
                        text = "Sort",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W400,
                        fontFamily = fontFamily,
                        color = Color(0xFF1C1C1C)
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.ic_sort), // Replace with your sort icon resource
                        contentDescription = "Sort",
                        tint = Color(0xFF1C1C1C),
                        modifier = Modifier
                            .size(20.dp)
                            .padding(start = 4.dp)
                            .clickable { isDialogOpen = true}// Optional: Adjust padding between text and icon
                    )

                    if (isDialogOpen) {
                        SortDialog(
                            onDismiss = { isDialogOpen = false },
                            onOldSortSelected = {
                                sortRecords(ascending = false)
                            },
                            onNewSortSelected = {
                                sortRecords(ascending = true)
                            }
                        )
                    }
                }
            }
        }


    LazyColumn {
      items(sortedRecords){playerRecord->
          ResultCard(
              playerRecord
          )

      }
    }


}


@Composable
fun ResultCard(
    item: PlayerRecordDataClass
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        backgroundColor = Color(0xFFFFFFFFF),
        modifier = Modifier.fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {

                    Row {
                        Box(
                            modifier = Modifier
                                .padding(
                                    horizontal = 8.dp,
                                    vertical = 4.dp
                                ) // Padding inside the box
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            androidx.compose.material.Text(
                                text = "${item.name}",
                                fontSize = 14.sp,
                                fontFamily = fontFamily,
                                fontWeight = FontWeight.W400,
                                color = Color(0xff3D3D3D)

                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .background(
                                    Color(0xFFD5D5FF),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(
                                    horizontal = 8.dp,
                                    vertical = 4.dp
                                ) // Padding inside the box
                        )
                        {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                androidx.compose.material.Text(
                                    text = "L${item.level}",
                                    fontSize = 10.sp,
                                    fontFamily = fontFamily,
                                    fontWeight = FontWeight.W400,
                                    color = Color(0xff2C2C7E)
                                )
                            }

                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .background(
                                    Color(0xFFD5E8FF),
                                    shape = RoundedCornerShape(8.dp)
                                ) // Background color with rounded corners
                                .padding(
                                    horizontal = 8.dp,
                                    vertical = 4.dp
                                ) // Padding inside the box
                        )
                        {
                            Row(verticalAlignment = Alignment.CenterVertically) {


                                androidx.compose.material.Text(
                                    text = "Shuttles ${item.shuttles}",
                                    fontSize = 10.sp,
                                    fontFamily = fontFamily,
                                    fontWeight = FontWeight.W400,
                                    color = Color(0xff171717)
                                )
                            }

                        }


                    }
                }
                Column(horizontalAlignment = Alignment.End) {

//                    Row {
//                        Box(
//                            modifier = Modifier
//                                .background(
//                                    Color(0xFFEAEAEA),
//                                    shape = RoundedCornerShape(8.dp)
//                                ) // Background color with rounded corners
//                                .padding(
//                                    horizontal = 8.dp,
//                                    vertical = 4.dp
//                                ) // Padding inside the box
//                        )
//                        {
//                            androidx.compose.material.Text(
//                                text = "${item.date}",
//                                fontSize = 10.sp,
//                                fontFamily = fontFamily,
//                                fontWeight = FontWeight.W400,
//                                color = Color(0xff4F4F4F)
//                            )
//                        }
//
//                        Spacer(modifier = Modifier.width(4.dp))
//                        Box(
//                            modifier = Modifier
//                                .background(
//                                    Color(0xFFEAEAEA),
//                                    shape = RoundedCornerShape(8.dp)
//                                ) // Background color with rounded corners
//                                .padding(
//                                    horizontal = 8.dp,
//                                    vertical = 4.dp
//                                ) // Padding inside the box
//                        )
//                        {
//                            androidx.compose.material.Text(
//                                text = "${item.date}",
//                                fontSize = 10.sp,
//                                fontFamily = fontFamily,
//                                fontWeight = FontWeight.W400,
//                                color = Color(0xff4F4F4F)
//                            )
//                        }
//                    }


                }

            }



                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                            .height(IntrinsicSize.Min)
                    ) {
                        val averageSpeed = item.cumulativeDistance / item.cumulativeTime
                        val timeFor20Meters = 20 / averageSpeed
                        val formattedTimeFor20Meters = String.format("%.2f", timeFor20Meters)

                        var totalspeed = averageSpeed * averageSpeed
                        var  fspeed = String.format("%.2f", totalspeed)
                        StatItem(label = "Distance\n(m)", value = item.cumulativeDistance.toString())
                        VerticalDivider(color = Color(0xffE0E0E0))
                        StatItem(label = "Time\n(Sec & Minutes)", value ="${item.cumulativeTime}")
                        VerticalDivider(color = Color(0xffE0E0E0))
                        StatItem(label = "Shuttle Time\n(Sec)", value = formattedTimeFor20Meters)
                        VerticalDivider(color = Color(0xffE0E0E0))
                        StatItem(label = "Speed\n(km/h)", value = fspeed)

                    }
                }


        }
    }


}



data class TestResult(
    val date: String,
    val time: String,
    val groupName: String,
    val groupNumber: String,
    val cumDist: String,
    val cumTime: String,
    val shuttleTime: String,
    val speed: String,
    val level: String,
    val shuttles: String
)


@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
 private fun AddPlayerBottomSheet(
    navController: NavHostController,
    playerId:Int,
    player: PlayerDataClass?
) {
    val context = LocalContext.current

    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberBottomSheetState(
            initialValue = BottomSheetValue.Collapsed
        )
    )

    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        sheetElevation = 8.dp,
        sheetShape = RoundedCornerShape(
            bottomStart = 0.dp,
            bottomEnd = 0.dp,
            topStart = 12.dp,
            topEnd = 12.dp
        ),
        sheetGesturesEnabled = true,
        sheetContent = {
            // Wrapping content with a Box to limit height and add padding
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 700.dp) // Limit the maximum height
                    .padding(top = 16.dp)
            ) {
                SheetContent(playerViewModel = viewModel(),bottomSheetScaffoldState,player)
            }
        },
        // Adjust the peek height for initial view
        sheetPeekHeight = 0.dp
    ) {
        val splashColor = AppBackgroundColor // Use the defined splash color

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = splashColor // Set the background color for the Surface
        ) {
            MainContent(navController,bottomSheetScaffoldState.bottomSheetState, bottomSheetScaffoldState,playerId)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SheetContent(
    playerViewModel: PlayerViewModel = viewModel(),
    bottomSheetScaffoldState: BottomSheetScaffoldState,
    player: PlayerDataClass? // Optional player object for editing
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope() // Coroutine scope for managing coroutines

    // Check if editing existing player
    val isEditing = player != null

    LaunchedEffect(bottomSheetScaffoldState.bottomSheetState.isCollapsed) {
        if (!bottomSheetScaffoldState.bottomSheetState.isCollapsed) {
            // Populate with existing data on edit or reset for new player creation
            if (isEditing) {
                playerViewModel.setPlayerData(player!!) // Populate with existing player data
            } else {
                playerViewModel.resetPlayer() // Reset for new player creation
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
    ) {
        // Image Picker
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ImagePickerWithPhotoPicker(playerViewModel)
        } else {
            ImagePickerWithIntent(playerViewModel)
        }

        // Name Input Field
        OutlinedTextField(
            value = playerViewModel.name,
            onValueChange = { playerViewModel.name = it },
            label = { Text("Name") },
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Age Input Field
        AgeInputField(playerViewModel)

        Spacer(modifier = Modifier.height(16.dp))

        // Height and Weight
        HeightWeightSlider(playerViewModel)

        // Gender Selection
        GenderSelection(playerViewModel)

        Spacer(modifier = Modifier.height(24.dp))

        // Save Player Button
        Button(
            onClick = {
                if (playerViewModel.name.isEmpty() || playerViewModel.image == null) {
                    // Show toast message if name or image is missing
                    Toast.makeText(context, "Please enter all required fields (image and name)", Toast.LENGTH_SHORT).show()
                } else {
                    if (isEditing) {
                        // Update existing player details
                        playerViewModel.updatePlayer(playerViewModel) // Use an update function in the ViewModel
                    } else {
                        // Save new player details
                        playerViewModel.savePlayer()
                    }
                    Toast.makeText(context, "Player Saved", Toast.LENGTH_SHORT).show()

                    // Hide the bottom sheet after saving
                    scope.launch {
                        bottomSheetScaffoldState.bottomSheetState.collapse()
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Blue),
            shape = RoundedCornerShape(20),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(50.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "Save Player",
                color = Color.White
            )
        }
    }
}




@OptIn(ExperimentalMaterialApi::class)


@Composable
fun SheetContent(
    playerViewModel: PlayerViewModel = viewModel(),
    bottomSheetScaffoldState: BottomSheetScaffoldState
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope() // Coroutine scope for managing coroutines


    LaunchedEffect(bottomSheetScaffoldState.bottomSheetState.isCollapsed) {
        if (!bottomSheetScaffoldState.bottomSheetState.isCollapsed) {
            playerViewModel.resetPlayer()
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
    ) {
        // Image Picker
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ImagePickerWithPhotoPicker(playerViewModel)
        } else {
            ImagePickerWithIntent(playerViewModel)
        }

        // Name Input Field
        OutlinedTextField(
            value = playerViewModel.name,
            onValueChange = { playerViewModel.name = it },
            label = { Text("Name") },
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Age Input Field
        AgeInputField(playerViewModel)

        Spacer(modifier = Modifier.height(16.dp))

        // Height and Weight
        HeightWeightSlider(playerViewModel)

        // Gender Selection
        GenderSelection(playerViewModel)

        Spacer(modifier = Modifier.height(24.dp))

        // Save Player Button
        Button(
            onClick = {
                if (playerViewModel.name.isEmpty() || playerViewModel.image == null) {
                    // Show toast message if name or image is missing
                    Toast.makeText(context, "Please enter all required fields (image and name)", Toast.LENGTH_SHORT).show()
                } else {
                    // Save Player and show success toast
                    playerViewModel.savePlayer()
                    Toast.makeText(context, "Player details Updated", Toast.LENGTH_SHORT).show()

                    // Hide the bottom sheet after saving
                    scope.launch {
                        bottomSheetScaffoldState.bottomSheetState.collapse()
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Blue),
            shape = RoundedCornerShape(20),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(50.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "Save Player",
                color = Color.White
            )
        }
    }
}


@Composable
fun ImagePickerWithIntent(viewModel: PlayerViewModel) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // Launch a coroutine to load the bitmap
            coroutineScope.launch {
                imageBitmap = loadBitmapFromUri(context,it)
                imageUri = it
                viewModel.updatePlayerImage(imageBitmap) // Update image in ViewModel
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.size(100.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = rememberAsyncImagePainter(imageUri ?: ""),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
            )

            IconButton(
                onClick = {
                    launcher.launch("image/*")
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(32.dp)
                    .background(Color.White, shape = CircleShape)
            ) {
                Icon(
                    painter = rememberAsyncImagePainter(model = R.drawable.ic_upload_img),
                    contentDescription = "Upload",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun ImagePickerWithPhotoPicker(viewModel: PlayerViewModel) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            coroutineScope.launch {
                val bitmap = loadBitmapFromUri(context,it)
                Log.d("ImagePicker", "New image picked: $bitmap")
                viewModel.updatePlayerImage(bitmap)  // Ensure ViewModel is updated
                imageUri = it
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        // Outer Box for image and icon
        Box(
            modifier = Modifier.size(100.dp), // Slightly larger to accommodate the icon
            contentAlignment = Alignment.Center
        ) {
            // Circular Image
            val imageBitmap = viewModel.image ?: BitmapFactory.decodeResource(
                LocalContext.current.resources, R.drawable.img_dummy_male
            )
            Image(
                bitmap = imageBitmap.asImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(90.dp) // Inner circle size
                    .clip(CircleShape)
                    .background(Color.Gray)
            )

            // Icon Button placed outside the circle
            IconButton(
                onClick = {
                    launcher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(32.dp)
                    .background(Color.White, shape = CircleShape)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_upload_img),
                    contentDescription = "Upload",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}


suspend fun loadBitmapFromUri(context: Context, uri: Uri): Bitmap? {
    return withContext(Dispatchers.IO) {
        val inputStream = context.contentResolver.openInputStream(uri)
        BitmapFactory.decodeStream(inputStream)
    }
}




private fun convertBitmapToByteArray(bitmap: Bitmap): ByteArray {
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
    return byteArrayOutputStream.toByteArray()
}


@Composable
fun GenderButton(
    emoji: String,
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = if (isSelected) Color(0xFFDDEEFF) else Color.White
        ),
        shape = RoundedCornerShape(24.dp),
        modifier = modifier
            .height(56.dp)
            .border(
                width = 2.dp,
                color = if (isSelected) Color(0xFF0066FF) else Color(0xFFDDEEFF),
                shape = RoundedCornerShape(24.dp)
            ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = emoji,
                style = androidx.compose.material.MaterialTheme.typography.body1
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                style = androidx.compose.material.MaterialTheme.typography.body1.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color(0xFF0066FF) else Color.Black
                )
            )
        }
    }
}


@Composable
fun GenderSelection(playerViewModel: PlayerViewModel) {
    Text(
        text = "Gender",
        modifier = Modifier.padding(start = 8.dp),
        fontSize = 10.sp,
        fontFamily = fontFamily,
        fontWeight = FontWeight.W400,
        color = Color(0xff989898)
    )

    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        GenderButton(
            emoji = "👦",
            text = "Male",
            isSelected = playerViewModel.gender == "Male",
            onClick = { playerViewModel.gender = "Male" },
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(16.dp))

        GenderButton(
            emoji = "👧",
            text = "Female",
            isSelected = playerViewModel.gender == "Female",
            onClick = { playerViewModel.gender = "Female" },
            modifier = Modifier.weight(1f)
        )
    }
}


@ExperimentalMaterialApi
@Composable
fun MainContent(
    navController: NavHostController,
    bottomSheetState: BottomSheetState,
    bottomSheetScaffoldState: BottomSheetScaffoldState,
    playerId: Int
) {

    val currentValue: BottomSheetValue = bottomSheetState.currentValue
    val offset = try {
        bottomSheetState.requireOffset()
    } catch (e: Exception) {
        Offset.Zero
    }

    val progress =
        bottomSheetState.progress
    PlayerProfileScreen(navController,bottomSheetScaffoldState,playerId)



}


@Composable
fun DeleteConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Are you sure, you want to delete this Player",
                fontSize = 18.sp, // Slightly adjusted for better fit
                fontFamily = fontFamily,
                fontWeight = FontWeight.W400,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp) // Add spacing under title
            )
        },
        buttons = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp), // Centralize buttons with padding
                horizontalArrangement = Arrangement.SpaceEvenly // Equal spacing between buttons
            ) {
                // Cancel Button
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFE0E0E0)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f) // Equal width for both buttons
                        .height(48.dp) // Height matching the design
                ) {
                    Text("Cancel",
                        fontSize = 16.sp,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.W500,
                        color = Color.Black)
                }

                Spacer(modifier = Modifier.width(16.dp)) // Space between buttons

                // Delete Button
                Button(
                    onClick = onConfirm,
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Text("Delete",
                        fontSize = 16.sp,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.W500,
                        color = Color.White)
                }
            }
        },
        properties = DialogProperties(),
        shape = RoundedCornerShape(12.dp), // Rounded corner for the dialog itself
        backgroundColor = Color.White
    )
}


@Composable
fun AgeInputField(playerViewModel: PlayerViewModel) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // DatePickerDialog to show the date picker
    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            val selectedDate = "$dayOfMonth/${month + 1}/$year"
            playerViewModel.age = selectedDate  // Update the ViewModel
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    // Restrict the DatePicker to not allow future dates
    datePickerDialog.datePicker.maxDate = System.currentTimeMillis()

    OutlinedTextField(
        value = playerViewModel.age,  // Use the ViewModel's age value
        onValueChange = {},  // Disable manual text input
        label = { Text("Select Date of Birth") },
        modifier = Modifier
            .fillMaxWidth()
            .height(62.dp)
            .clickable {
                // Show the DatePickerDialog on click
                datePickerDialog.show()
            },
        readOnly = true,  // Make it read-only to avoid showing the keyboard
        enabled = false   // Disable the field to prevent input
    )
}


@Composable
fun HeightWeightSlider(playerViewModel: PlayerViewModel) {
    Column(
        modifier = Modifier.padding(8.dp)
    ) {
        // Height Slider
        SliderWithLabel(
            label = "Height (ft)",
            value = playerViewModel.height.toFloat(),
            valueRange = 2.5f..8.5f,
            steps = 5,
            onValueChange = { newValue ->
                playerViewModel.height = newValue // Update height in ViewModel
            },
            displayValue = String.format("%.1f", playerViewModel.height.toFloat()),
            labels = listOf("2.5ft", "3.5ft", "4.5ft", "5.5ft", "6.5ft", "7.5ft", "8.5ft")
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Weight Slider
        SliderWithLabel(
            label = "Weight (Kg)",
            value = playerViewModel.weight.toFloat(),
            valueRange = 20f..200f,
            steps = 5,
            onValueChange = { newValue ->
                playerViewModel.weight = newValue.toInt() // Update weight in ViewModel
            },
            displayValue = String.format("%.0f", playerViewModel.weight.toFloat()),
            labels = listOf("20 kg", "50 kg", "80 kg", "110 kg", "140 kg", "170 kg", "200 kg")
        )
    }
}

@Composable
fun SliderWithLabel(
    label: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    onValueChange: (Float) -> Unit,
    displayValue: String,
    labels: List<String>
) {
    Column {
        // Label and Current Value
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label,
                fontSize = 12.sp,
                fontFamily = fontFamily,
                fontWeight = FontWeight.W400,
                color = Color(0xff989898))
            Text(
                text = displayValue,
                fontSize = 16.sp,
                fontFamily = fontFamily,
                fontWeight = FontWeight.W400,
                color = Color(0xff242424),
                textAlign = TextAlign.End,
            )
        }

        // Slider
        Slider(
            value = value,
            onValueChange = { newValue ->
                onValueChange(newValue) // Update the ViewModel state
            },
            valueRange = valueRange,
            colors = SliderDefaults.colors(
                thumbColor = SplashColor,
                activeTrackColor = SplashColor,
                inactiveTrackColor = Color(0xffECECEC)
            ),
            modifier = Modifier.fillMaxWidth()
        )

        // Labels below the slider
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            labels.forEach { label ->
                Text(
                    text = label,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f),
                    fontSize = 8.sp,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.W400,
                    color = Color(0xff989898)
                )
            }
        }
    }
}




