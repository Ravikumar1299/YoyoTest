package com.yoyobeep.test.screens

import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.BottomAppBar
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.yoyobeep.test.ui.theme.SplashColor

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.yoyobeep.test.R
import com.yoyobeep.test.SortDialog
import com.yoyobeep.test.byteArrayToBitmap
import com.yoyobeep.test.database.PlayerDataClass
import com.yoyobeep.test.database.PlayerRecordDataClass
import com.yoyobeep.test.database.PlayerViewModel
import com.yoyobeep.test.getCurrentDateTime
import com.yoyobeep.test.screens.sign_in.UserData
import com.yoyobeep.test.ui.theme.AppBackgroundColor
import com.yoyobeep.test.ui.theme.BottomNavBarColor
import com.yoyobeep.test.ui.theme.fontFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(navController: NavHostController, userData: UserData?) {
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()
    val selectedItem = remember { mutableStateOf("home") }
    val content = remember { mutableStateOf("Home Screen") }
    val viewModel: PlayerViewModel = viewModel()
    val allPlayerRecords by viewModel.allPlayerRecords.collectAsState()
    val activity = (LocalContext.current as? Activity)
    var showExitDialog by remember { mutableStateOf(false) }

    // Handle back press
    BackHandler {
        showExitDialog = true
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = {
                androidx.compose.material3.Text(
                    text = "Are you sure you want to exit the app?",
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
                        .padding(
                            horizontal = 24.dp,
                            vertical = 12.dp
                        ), // Centralize buttons with padding
                    horizontalArrangement = Arrangement.SpaceEvenly // Equal spacing between buttons
                ) {
                    // Cancel Button
                    Button(
                        onClick = {
                            activity?.finish() // Exit the app
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFE0E0E0)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f) // Equal width for both buttons
                            .height(48.dp) // Height matching the design
                    ) {
                        androidx.compose.material3.Text(
                            "Ok",
                            fontSize = 16.sp,
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.W500,
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp)) // Space between buttons

                    // Delete Button
                    Button(
                        onClick = { showExitDialog = false },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        androidx.compose.material3.Text(
                            "Cancel",
                            fontSize = 16.sp,
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.W500,
                            color = Color.White
                        )
                    }
                }
            },
            properties = DialogProperties(),
            shape = RoundedCornerShape(12.dp), // Rounded corner for the dialog itself
            backgroundColor = Color.White
        )
    }

//
//    // Show Exit Dialog if the user presses back
//    if (showExitDialog) {
//        AlertDialog(
//            onDismissRequest = { showExitDialog = false },
//            title = { Text(text = "Exit App") },
//            text = { Text("Are you sure you want to exit the app?") },
//            confirmButton = {
//                Button(onClick = {
//                    activity?.finish() // Exit the app
//                }) {
//                    Text("OK")
//                }
//            },
//            dismissButton = {
//                Button(onClick = { showExitDialog = false }) {
//                    Text("Cancel")
//                }
//            }
//        )
//    }


    // Initialize sortedRecords
    var sortedRecords by remember { mutableStateOf(allPlayerRecords.sortedBy { it.id }) }

    // Function to sort records
    fun sortRecords(ascending: Boolean) {
        sortedRecords = if (ascending) {
            allPlayerRecords.sortedBy { it.id }
        } else {
            allPlayerRecords.sortedByDescending { it.id }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.fetchAllPlayerRecords()
    }

    // Update sortedRecords when allPlayerRecords changes
    LaunchedEffect(allPlayerRecords) {
        sortedRecords = allPlayerRecords.sortedBy { it.date }
    }

    var isDialogOpen by remember { mutableStateOf(false) }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        sheetContent = {
            CustomBottomSheetContent(navController, sheetState)
        }
    ) {
        Scaffold(
            bottomBar = {
                BottomAppBar(
                    backgroundColor = BottomNavBarColor,
                    cutoutShape = RoundedCornerShape(50),
                    modifier = Modifier.navigationBarsPadding()
                ) {
                    BottomNavigation(
                        modifier = Modifier.height(85.dp),
                        backgroundColor = BottomNavBarColor
                    ) {
                        BottomNavigationItem(
                            selected = selectedItem.value == "home",
                            onClick = {
                                content.value = "Home Screen"
                                selectedItem.value = "home"
                                navController.navigate("homescreen")
                            },
                            icon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_nav_home),
                                    contentDescription = "home",
                                    tint = Color.White
                                )
                            },
                            label = {
                                Text(text = "Home", color = Color.White)
                            },
                            alwaysShowLabel = true
                        )
                        BottomNavigationItem(
                            selected = selectedItem.value == "player",
                            onClick = {
                                navController.navigate("playerscreen/nav_players")
                            },
                            icon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_nav_face),
                                    contentDescription = "face",
                                    tint = Color(0xFF7988A3)
                                )
                            },
                            label = {
                                Text(text = "Players", color = Color(0xFF7988A3))
                            },
                            alwaysShowLabel = true
                        )

                        BottomNavigationItem(
                            selected = selectedItem.value == "player",
                            onClick = {
                            },
                            icon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_nav_users),
                                    contentDescription = "face",
                                    tint = Color(0xFF7988A3)
                                )
                            },
                            label = {
                                Text(text = "Groups", color = Color(0xFF7988A3))
                            },
                            alwaysShowLabel = true
                        )

                        Box(modifier = Modifier.weight(1f))

                    }
                }
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        scope.launch {
                            if (sheetState.isVisible) {
                                sheetState.hide()
                            } else {
                                sheetState.show()
                            }
                        }
                    },
                    shape = RoundedCornerShape(50),
                    backgroundColor = SplashColor,
                    modifier = Modifier.size(54.dp)
                ) {
                    Icon(Icons.Filled.Add, tint = Color.White, contentDescription = "Add")
                }
            },
            isFloatingActionButtonDocked = true,
            content = { paddingValues ->
                Column(modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)) {
                    // Header Section
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
                            Text(
                                text = "Hello, ${userData?.username ?: "Hello"}",
                                fontFamily = fontFamily,
                                fontWeight = FontWeight.W400,
                                fontSize = 24.sp,
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            val profilePicture = userData?.profilePictureUrl ?: R.drawable.img_dummy_male

                            AsyncImage(
                                model = profilePicture,
                                contentDescription = "Profile picture",
                                modifier = Modifier
                                    .size(40.dp)
                                    .clickable { navController.navigate("profile") }
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
        Text(text = "Groups",
            fontSize = 24.sp,
            fontFamily = fontFamily,
            fontWeight = FontWeight.W400)

                    // Results Section
                    Surface(
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                        modifier = Modifier.fillMaxSize(),
                        color = Color(0xFFEFE9D3)) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxSize()) {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                item {
                                    LazyRow {
//                                        items(getGroupList()) { item ->
//                                            GroupItem(color = item.color, groupIcon = item.groupIcon, groupName = item.groupName)
//                                        }
                                    }
                                }

                                item {
                                    Box(modifier = Modifier.fillMaxWidth()) {
                                        Divider(
                                            color = Color(0xFF1C1C1C),
                                            thickness = 3.dp,
                                            modifier = Modifier
                                                .width(53.dp)
                                                .align(Alignment.Center)
                                        )
                                    }
                                }
                                item {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Results",
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.W400,
                                            fontFamily = fontFamily
                                        )
                                        TextButton(onClick = { isDialogOpen = true }) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = "Sort",
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.W400,
                                                    fontFamily = fontFamily,
                                                    color = Color(0xFF1C1C1C)
                                                )
                                                Icon(
                                                    painter = painterResource(id = R.drawable.ic_sort),
                                                    contentDescription = "Sort",
                                                    tint = Color(0xFF1C1C1C),
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                                item {
                                    TextSwitchTest()
                                }

                                if (sortedRecords.isEmpty()) {
                                    item {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize() // Occupy the entire available space
                                                .padding(16.dp),
                                                contentAlignment = Alignment.Center
                                        ) {


                                            Column {

                                                Image(
                                                    painter = painterResource(id = R.drawable.img_no_record),
                                                    contentDescription = "No record",
                                                    modifier = Modifier
                                                        .width(236.dp)
                                                        .height(236.dp)
                                                )

                                                Spacer(modifier = Modifier.height(20.dp))


                                                Text(
                                                    text = "You Don’t have Any \n records yet",
                                                    fontSize = 20.sp,
                                                    fontWeight = FontWeight.W500,
                                                    fontFamily = fontFamily,
                                                    textAlign = TextAlign.Center,
                                                    modifier = Modifier.
                                                        align(Alignment.CenterHorizontally),
                                                    color = Color(0xFF000000) // Adjust color as needed
                                                )
                                            }

                                        }
                                    }
                                } else {
                                    items(sortedRecords) { item: PlayerRecordDataClass ->
                                        ResultCard(item)
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                }

                            }
                        }
                    }
                }
            }
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



//@OptIn(ExperimentalMaterialApi::class)
//@Composable
//fun HomeScreen(navController: NavHostController, userData: UserData?) {
//    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
//    val scope = rememberCoroutineScope()
//    val selectedItem = remember { mutableStateOf("home") }
//    val content = remember { mutableStateOf("Home Screen") }
//    val viewModel: PlayerViewModel = viewModel()
//    val allPlayerRecords by viewModel.allPlayerRecords.collectAsState()
//
//
//    // Initialize sortedRecords with default sorting order (ascending)
//    var sortedRecords by remember { mutableStateOf(allPlayerRecords.sortedBy { it.id }) }
//    var sortIconPosition by remember { mutableStateOf(Offset.Zero) }
//    // Function to sort records
//    fun sortRecords(ascending: Boolean) {
//        sortedRecords = if (ascending) {
//            allPlayerRecords.sortedBy { it.id } // Assuming date is in the right format
//        } else {
//            allPlayerRecords.sortedByDescending { it.id }
//        }
//    }
//
//    LaunchedEffect(Unit) {
//        viewModel.fetchAllPlayerRecords() // Fetch all records on start
//    }
//
//    // Update sortedRecords when allPlayerRecords changes
//    LaunchedEffect(allPlayerRecords) {
//        // Default sort order is ascending
//        sortedRecords = allPlayerRecords.sortedBy { it.date }
//    }
//
//    var isDialogOpen by remember { mutableStateOf(false) }
//
//    ModalBottomSheetLayout(
//        sheetState = sheetState,
//        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
//        sheetContent = { CustomBottomSheetContent(navController, sheetState) }
//    ) {
//        Scaffold(
//            bottomBar = {
//
//                BottomAppBar(
//
//                    backgroundColor = BottomNavBarColor,
//                    cutoutShape = RoundedCornerShape(50),
//                    modifier = Modifier
//                        .navigationBarsPadding(),
//                    content = {
//                        BottomNavigation(
//                            modifier = Modifier.height(85.dp),
//                            backgroundColor = BottomNavBarColor
//                        ) {
//                            BottomNavigationItem(
//                                selected = selectedItem.value == "home",
//                                onClick = {
//                                    content.value = "Home Screen"
//                                    selectedItem.value = "home"
//                                    navController.navigate("homescreen")
//                                },
//                                icon = {
//                                    Icon(
//                                        painter = painterResource(id = R.drawable.ic_nav_home),
//                                        contentDescription = "home",
//                                        tint = Color(0xFFFFFFFF)
//                                    )
//                                },
//                                label = {
//                                    Text(text = "Home", color = Color.White) // Add the label here
//                                },
//                                alwaysShowLabel = true
//                            )
//                            BottomNavigationItem(
//                                selected = selectedItem.value == "player",
//                                onClick = {
//                                    navController.navigate("playerscreen/nav_players")
//                                },
//                                icon = {
//                                    Icon(
//                                        painter = painterResource(id = R.drawable.ic_nav_face),
//                                        contentDescription = "face",
//                                        tint = Color(0xFFFFFFFF)
//                                    )
//                                },
//                                label = {
//                                    Text(
//                                        text = "Players",
//                                        color = Color.White
//                                    ) // Add the label here
//                                },
//                                alwaysShowLabel = true
//                            )
////                            BottomNavigationItem(
////                                selected = selectedItem.value == "users",
////                                onClick = {
////                                    content.value = "Users Screen"
////                                    selectedItem.value = "users"
////                                },
////                                icon = {
////                                    Icon(
////                                        painter = painterResource(id = R.drawable.ic_nav_users),
////                                        contentDescription = "home",
////                                        tint = Color(0xFFFFFFFF)
////                                    )
////                                },
////                                alwaysShowLabel = false
////                            )
//                            BottomNavigationItem(
//                                enabled = false,
//                                selected = false,
//                                onClick = {}, // Empty onClick handler
//                                icon = {
//                                    Icon(
//                                        painter = painterResource(id = R.drawable.ic_nav_face),
//                                        contentDescription = "home"
//                                    )
//                                },
//
//                                )
//
//
//                        }
//                    }
//                )
//
//            },
//            floatingActionButton = {
//                FloatingActionButton(
//                    onClick = {
//                        scope.launch {
//                            if (sheetState.isVisible) {
//                                sheetState.hide()
//                            } else {
//                                sheetState.show()
//                            }
//                        }
//                    },
//                    shape = RoundedCornerShape(50),
//                    backgroundColor = SplashColor,
//                    modifier = Modifier
//                        .height(54.dp)
//                        .width(54.dp)
//                ) {
//                    Icon(
//                        Icons.Filled.Add,
//                        tint = Color.White,
//                        contentDescription = "Add"
//                    )
//                }
//            },
//            isFloatingActionButtonDocked = true,
//            content = { paddingValues ->
//                Column(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(paddingValues)
//                ) {
//
//                    Surface(
//                        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(120.dp),
//                        color = SplashColor
//                    ) {
//                        Row(
//                            verticalAlignment = Alignment.CenterVertically,
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(15.dp, 30.dp, 15.dp, 0.dp)
//                        ) {
//                            Column {
//                                if (userData?.username != null) {
//                                    Text(
//                                        text = "Hello, " + userData.username,
//                                        fontFamily = fontFamily,
//                                        fontWeight = FontWeight.W400,
//                                        fontSize = 24.sp, color = Color.White
//                                    )
//                                    Spacer(modifier = Modifier.height(16.dp))
//                                } else {
//                                    Text(
//                                        text = "Hello",
//                                        fontFamily = fontFamily,
//                                        fontWeight = FontWeight.W400,
//                                        fontSize = 24.sp, color = Color.White
//                                    )
//                                }
//                            }
//                            Spacer(modifier = Modifier.weight(1f))
//                            if (userData?.profilePictureUrl != null) {
//                                AsyncImage(
//                                    model = userData.profilePictureUrl,
//                                    contentDescription = "Profile picture",
//                                    modifier = Modifier
//                                        .size(40.dp)
//                                        .clickable { navController.navigate("profile") }
//                                        .clip(CircleShape),
//                                    contentScale = ContentScale.Crop
//                                )
//                            } else {
//                                Image(
//                                    painter = painterResource(id = R.drawable.ic_profile_img),
//                                    contentDescription = "Profile Picture",
//                                    modifier = Modifier
//                                        .size(40.dp)
//                                        .clickable { navController.navigate("profile") }
//                                        .clip(CircleShape),
//                                    contentScale = ContentScale.Crop
//                                )
//                            }
//                            Spacer(modifier = Modifier.height(16.dp))
//                        }
//                    }
//
//                    Surface(
//                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
//                        modifier = Modifier.fillMaxSize(),
//                        color = Color(0xFFEFE9D3)
//                    ) {
//                        Column(modifier = Modifier.padding(16.dp)) {
//                            LazyColumn(
//                                modifier = Modifier.fillMaxSize(),
//                                content = {
//                                    item {
//                                        Box(modifier = Modifier.fillMaxWidth()) {
//                                            Divider(
//                                                color = Color(0xFF1C1C1C),
//                                                thickness = 3.dp,
//                                                modifier = Modifier
//                                                    .width(53.dp)
//                                                    .align(Alignment.Center)
//                                            )
//                                        }
//                                    }
//                                    // Results and Sort header
//                                    item {
//                                        Spacer(modifier = Modifier.height(8.dp))
//                                        Row(
//                                            modifier = Modifier.fillMaxWidth(),
//                                            horizontalArrangement = Arrangement.SpaceBetween,
//                                            verticalAlignment = Alignment.CenterVertically
//                                        ) {
//                                            Text(
//                                                text = "Results",
//                                                fontSize = 24.sp,
//                                                fontWeight = FontWeight.W400,
//                                                fontFamily = fontFamily
//                                            )
//                                            TextButton(onClick = { isDialogOpen = true }) {
//                                                Row(
//                                                    verticalAlignment = Alignment.CenterVertically
//                                                ) {
//                                                    Text(
//                                                        text = "Sort",
//                                                        fontSize = 14.sp,
//                                                        fontWeight = FontWeight.W400,
//                                                        fontFamily = fontFamily,
//                                                        color = Color(0xFF1C1C1C)
//                                                    )
//                                                    Icon(
//                                                        painter = painterResource(id = R.drawable.ic_sort),
//                                                        contentDescription = "Sort",
//                                                        tint = Color(0xFF1C1C1C),
//                                                        modifier = Modifier
//                                                            .size(20.dp)
//                                                            .padding(start = 4.dp)
//                                                            .clickable { isDialogOpen = true}
//                                                                )
//                                                }
//                                            }
//                                        }
//                                    }
//                                    // Display sorted records
//                                    items(sortedRecords) { item: PlayerRecordDataClass ->
//                                        ResultCard(item)
//                                        Spacer(modifier = Modifier.height(8.dp))
//                                    }
//                                }
//                            )
//                        }
//                    }
//                }
//            }
//        )
//        if (isDialogOpen) {
//            SortDialog(
//                onDismiss = { isDialogOpen = false },
//                onOldSortSelected = {
//                    sortRecords(ascending = false)
//                },
//                onNewSortSelected = {
//                    sortRecords(ascending = true)
//                }
//            )
//        }
//    }
//}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CustomBottomSheetContent(navController: NavHostController, sheetState: ModalBottomSheetState) {
    val fontFamily = FontFamily.Default // Replace with your custom fontFamily if needed
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp) // Increase the height of the BottomSheet 300.dp
            .padding(start = 16.dp, end = 16.dp, top = 5.dp, bottom = 5.dp)
            .navigationBarsPadding(), // Ensure content stays above the navigation bar
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top line
                Box(
            modifier = Modifier
                .width(40.dp)
                .height(2.dp)
                .background(Color.Gray)
                .padding(bottom = 16.dp)
        )

//        Spacer(modifier = Modifier.height(26.dp))
        Spacer(modifier = Modifier.height(16.dp))

        // Group Test
        Text(text = "Group Test",
            fontSize = 20.sp,
            fontFamily = fontFamily,
            fontWeight = FontWeight.W400,
        modifier = Modifier
            .clickable {
                navController.navigate("pickgroup")
                scope.launch {
                    sheetState.hide()
                }
            }
        )
        Divider(modifier = Modifier.padding(vertical = 16.dp))

        // Solo Test
        Text(
            text = "Solo Test",
            fontSize = 20.sp,
            fontFamily = fontFamily,
            fontWeight = FontWeight.W400,
            modifier = Modifier
                .clickable {
                    navController.navigate("playerscreen/from_solo")
                    scope.launch {
                        sheetState.hide()
                    }
                }
        )
        Divider(modifier = Modifier.padding(vertical = 16.dp))

        // Quick Test (No Record)
        Text(
            text = "Quick Test (No Record)",
            fontSize = 20.sp,
            fontFamily = fontFamily,
            fontWeight = FontWeight.W400,
            modifier = Modifier
                .clickable {
                    navController.navigate("quickTest/Quick Test/-1/a")
                    scope.launch {
                        sheetState.hide()

                    }
                }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun QuickTestScreen(
    playerId: Int,
    playerName: String,
    navController: NavHostController,
    navBackStackEntry: NavBackStackEntry
) {
    val heading = navBackStackEntry.arguments?.getString("heading") ?: "Quick Test"

    // State variables
    var timeLeft by remember { mutableStateOf(14_039L) }
    var initialTime by remember { mutableStateOf(14_039L) }
    var isTimerRunning by remember { mutableStateOf(false) }
    var shuttleCount by remember { mutableStateOf(0) }
    var cumulativeDistance by remember { mutableStateOf(0) }
    var roundedSpeed by remember { mutableStateOf(10.00f) }
    var speed by remember { mutableStateOf(10.0f) }
    var level by remember { mutableStateOf(1) }
    var levelToSave by remember { mutableStateOf(1) }
    var buttonText by remember { mutableStateOf("Start") }
    var isResting by remember { mutableStateOf(false) }
    var restTimeLeft by remember { mutableStateOf(10_000L) }
    var isCompleted by remember { mutableStateOf(false) }
    var cumulativeTime by remember { mutableStateOf(0f) }

    val mediaPlayer = remember { MediaPlayer() }

    var context = LocalContext.current

    val cumulativeDistanceMap = remember {
        mapOf(
            1 to 40, 2 to 80, 3 to 120, 4 to 200, 5 to 240,
            6 to 320, 7 to 440, 8 to 560, 9 to 680, 10 to 800,
            11 to 960, 12 to 1080, 13 to 1240, 14 to 1440, 15 to 1680,
            16 to 1960, 17 to 2280, 18 to 2520, 19 to 2680, 20 to 2880,
            21 to 3120, 22 to 3320, 23 to 3640, 24 to 2880, 25 to 3120
        )
    }
    val (date, time) = getCurrentDateTime()
    val viewModel: PlayerViewModel = viewModel()

    // Save player record function
    fun saveRecord() {

        if(cumulativeDistance >= 20)
        {
            viewModel.viewModelScope.launch {
                try {
                    viewModel.savePlayerRecord(
                        playerId = playerId,
                        name = playerName,
                        level = levelToSave,
                        shuttles = (cumulativeDistance / 20),
                        cumulativeDistance = cumulativeDistance,
                        cumulativeTime = cumulativeTime,
                        shuttleTime = (20 / (speed / 3.6f)).toFloat(),
                        speed = roundedSpeed,
                        date = date,
                        time=time
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    }

    // Timer logic
    LaunchedEffect(isTimerRunning) {
        if (isTimerRunning) {
            val speedMap = mapOf(
                1 to 10.0f, 2 to 12.0f, 3 to 13.0f, 4 to 13.0f, 5 to 13.5f,
                6 to 13.5f, 7 to 13.5f, 8 to 14.0f, 9 to 14.0f, 10 to 14.0f,
                11 to 14.0f, 12 to 14.5f, 13 to 14.5f, 14 to 14.5f, 15 to 14.5f,
                16 to 14.5f, 17 to 14.5f, 18 to 14.5f, 19 to 14.5f, 20 to 15.0f,
                21 to 15.0f, 22 to 15.0f, 23 to 15.0f, 24 to 15.0f, 25 to 15.0f
            )

            val shuttlesMap = mapOf(
                1 to 2, 2 to 2, 3 to 2, 4 to 4, 5 to 2,
                6 to 4, 7 to 6, 8 to 2, 9 to 4, 10 to 6,
                11 to 8, 12 to 2, 13 to 4, 14 to 6, 15 to 8,
                16 to 10, 17 to 12, 18 to 14, 19 to 16, 20 to 2,
                21 to 4, 22 to 6, 23 to 8, 24 to 10, 25 to 12
            )

            while (level <= 25 && isTimerRunning) {
                val shuttleSpeed = speedMap[level] ?: 10.0f
                val shuttleTime = (20 / (shuttleSpeed / 3.6f)) * 1000L

                val shuttlesInStage = shuttlesMap[level] ?: 1
                val totalTime = shuttleTime * shuttlesInStage
                cumulativeTime += totalTime / 1000
                cumulativeTime = String.format("%.2f", cumulativeTime).toFloat()
                val cumulativeDistanceLevel = cumulativeDistanceMap[level] ?: 0

                val distance = (shuttlesInStage * 20f)
                val timeInSeconds = (shuttleTime * shuttlesInStage) / 1000f

                speed = ((distance / timeInSeconds) * 3.6f)
                roundedSpeed = String.format("%.1f", speed.toFloat()).toFloat()

                timeLeft = totalTime.toLong()
                initialTime = timeLeft

                for (shuttle in 1..shuttlesInStage) {
                    shuttleCount = shuttle - 1
                    while (timeLeft > 0 && isTimerRunning) {
                        delay(100L)
                        timeLeft -= 100L

                        if (timeLeft <= totalTime - shuttle * shuttleTime) {
                            shuttleCount = shuttle
                            cumulativeDistance += 20
                            break
                        }
                    }
                }

                println("Cumulative Distance: $cumulativeDistance meters")

//                if (level < 25) {
//                    isResting = true
//                    restTimeLeft = 10_000L
//
//                    while (restTimeLeft > 0 && isResting) {
//                        // Play beep sound every 1 second (1000 ms)
//                        if (restTimeLeft % 1000L == 0L) {
//                            mediaPlayer.reset()
//                            val assetFileDescriptor = context.resources.openRawResourceFd(R.raw.beep_short)
//                            mediaPlayer.setDataSource(assetFileDescriptor.fileDescriptor, assetFileDescriptor.startOffset, assetFileDescriptor.length)
//                            mediaPlayer.prepare()
//                            mediaPlayer.start()
//                        }
//
//                        // Delay for 100 milliseconds
//                        delay(100L)
//
//                        // Decrease rest time by 100 milliseconds
//                        restTimeLeft -= 100L
//                    }
//
//// Release media player after timer finishes
//                    mediaPlayer.release()
//                    isResting = false
//                    level += 1
//                    shuttleCount = 0
//                }



                if (level < 25) {
                    isResting = true
                    restTimeLeft = 10_000L

                    while (restTimeLeft > 0 && isResting) {
                        // Play beep sound every 1 second (1000 ms)
                        if (restTimeLeft % 1000L == 0L) {
                            // Create a new instance of MediaPlayer each time
                            val mediaPlayer = MediaPlayer()

                            try {
                                val assetFileDescriptor = context.resources.openRawResourceFd(R.raw.beep_short)
                                mediaPlayer.setDataSource(assetFileDescriptor.fileDescriptor, assetFileDescriptor.startOffset, assetFileDescriptor.length)
                                mediaPlayer.prepare()
                                mediaPlayer.start()

                                // Ensure MediaPlayer is released after playing the sound
                                mediaPlayer.setOnCompletionListener {
                                    it.release()
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                mediaPlayer.release() // Release in case of an exception
                            }
                        }

                        // Delay for 100 milliseconds
                        delay(100L)

                        // Decrease rest time by 100 milliseconds
                        restTimeLeft -= 100L
                    }

                    // After the rest time ends
                    isResting = false
                    level += 1
                    shuttleCount = 0
                }else {
                    isTimerRunning = false
                    buttonText = "Completed"
                }
            }
        }
    }

    // Main Column Layout
    Column(
        modifier = Modifier
            .navigationBarsPadding()
            .fillMaxSize()
            .background(AppBackgroundColor),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Header
        Header(navController, heading)

        // Player Information
        if (heading == "Solo Test") {
            PlayerInfo(playerId, playerName)
        }

        // Timer and Stats
        if (!isCompleted) {
            TimerDisplay(mediaPlayer,context,timeLeft, initialTime, isResting, restTimeLeft)
            StatsCard(level, shuttleCount, cumulativeDistance, roundedSpeed)
            Text(
                text = "Cumulative Distance: $cumulativeDistance m",
                fontFamily = fontFamily,
                fontWeight = FontWeight.W400,
                fontSize = 20.sp,
                color = Color.Black
            )
        } else {
            if (heading != "Solo Test") {
                Text(
                    text = "Random Test",
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.W400,
                    fontSize = 20.sp,
                    color = Color.Black
                )
            }
            LevelAndShuttleButtons(cumulativeDistance,cumulativeDistanceMap) {
                levelToSave = it
            }
            StatsCardDetails(cumulativeDistance, cumulativeTime)
        }

        // Action Button
        ActionButton(buttonText, isTimerRunning, isCompleted) {
            if (isTimerRunning) {
                isTimerRunning = false
                isCompleted = true
                buttonText = "Play Again"
                if (heading == "Solo Test") {
                    saveRecord()
                    mediaPlayer.reset()
                    val assetFileDescriptor = context.resources.openRawResourceFd(R.raw.great_job)
                    mediaPlayer.setDataSource(assetFileDescriptor.fileDescriptor, assetFileDescriptor.startOffset, assetFileDescriptor.length)
//                    mediaPlayer.setDataSource(context, R.raw.great_job) // Assuming you have this file in your raw resources
                    mediaPlayer.prepare()
                    mediaPlayer.start()
                }
            } else if (isCompleted) {
                timeLeft = 14_039L
                shuttleCount = 0
                level = 1
                cumulativeDistance = 0
                isCompleted = false
                isTimerRunning = true
                buttonText = "End & View Results"
            } else {
                isTimerRunning = true
                buttonText = "End & View Results"
                mediaPlayer.reset()
                val startAssetFileDescriptor = context.resources.openRawResourceFd(R.raw.start_running) // replace with your actual audio resource
                mediaPlayer.setDataSource(startAssetFileDescriptor.fileDescriptor, startAssetFileDescriptor.startOffset, startAssetFileDescriptor.length)
                mediaPlayer.prepare()
                mediaPlayer.start()
            }
        }


        DisposableEffect(Unit) {
            onDispose {
                mediaPlayer.release()
            }
        }
    }
}

@Composable
fun Header(navController: NavHostController, heading: String) {
    Surface(
        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        color = SplashColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp, 30.dp, 15.dp, 0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White.copy(alpha = 0.27f), shape = CircleShape)
                    .clickable {
                        navController.popBackStack()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_left),
                    contentDescription = null,
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = heading,
                fontFamily = fontFamily,
                fontWeight = FontWeight.W400,
                fontSize = 20.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun PlayerInfo(playerId: Int, playerName: String) {
    val viewModel: PlayerViewModel = viewModel()
    var player by remember { mutableStateOf<PlayerDataClass?>(null) }

    LaunchedEffect(playerId) {
        player = viewModel.getPlayerById(playerId)
    }

    val imageBitmap = player?.playerImage?.let { byteArrayToBitmap(it) }
    val imageImageBitmap = imageBitmap?.asImageBitmap()

    Image(
        bitmap = imageImageBitmap ?: ImageBitmap.imageResource(id = R.drawable.img_dummy_male),
        contentDescription = "Profile Picture",
        modifier = Modifier
            .size(70.dp)
            .clip(CircleShape)
            .border(
                width = 3.dp,
                color = Color.White,
                shape = CircleShape
            ),
        contentScale = ContentScale.Crop
    )
    Text(
        text = playerName,
        fontFamily = fontFamily,
        fontWeight = FontWeight.W400,
        fontSize = 20.sp,
        color = Color(0xff3D3D3D)
    )
}

@Composable
fun TimerDisplay(
    mediaPlayer: MediaPlayer,
    context: Context,
    timeLeft: Long,
    initialTime: Long,
    isResting: Boolean,
    restTimeLeft: Long
) {
    val minutes = (timeLeft / 1000) / 60
    val seconds = (timeLeft / 1000) % 60
    val milliseconds = (timeLeft % 1000) / 100
    Text(
        text = String.format("%02d:%02d:%01d", minutes, seconds, milliseconds),
        fontFamily = fontFamily,
        fontWeight = FontWeight.W400,
        fontSize = 44.sp
    )

    if (isResting) {
        val restSeconds = (restTimeLeft / 1000) % 60
        Text(
            text = String.format("Rest: %d", restSeconds),
            fontFamily = fontFamily,
            fontWeight = FontWeight.W400,
            fontSize = 24.sp,
            color = Color.Red
        )
    }

    LinearProgressIndicator(
        progress = 1 - timeLeft.toFloat() / initialTime,
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(color = Color(0xffC1C1C1)),
        backgroundColor = Color(0xffC1C1C1),
        color = Color(0xff0054F7)
    )
}


@Composable
fun StatsCard(level: Int, shuttleCount: Int, cumulativeDistance: Int, roundedSpeed: Float) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = 8.dp,
        backgroundColor = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            StatItem(label = "Level", value = level.toString())
            VerticalDivider(color = Color(0xffE0E0E0))
            StatItem(label = "Shuttle", value = shuttleCount.toString())
            VerticalDivider(color = Color(0xffE0E0E0))
            StatItem(label = "Distance", value = "${shuttleCount * 20} m")
            VerticalDivider(color = Color(0xffE0E0E0))
            StatItem(label = "Speed(km/hr)", value = roundedSpeed.toString())
        }
    }
}

@Composable
fun LevelAndShuttleButtons(cumulativeDistance: Int,
                           cumulativeDistanceMap: Map<Int, Int>,
                           onLevelClick: (Int) -> Unit) {
    Row(
        modifier = Modifier.padding(top = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = {
                val currentLevel = cumulativeDistanceMap.entries.firstOrNull {
                    it.value >= cumulativeDistance
                }?.key ?: 1
                onLevelClick(currentLevel)
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFE3D7FF)),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .defaultMinSize(minWidth = 64.dp, minHeight = 40.dp)
        ) {
            Text(
                text = "L ${cumulativeDistanceMap.entries.firstOrNull { it.value >= cumulativeDistance }?.key ?: 1}",
                color = Color(0xFF5A4FCF),
                fontSize = 16.sp
            )
        }
        Button(
            onClick = { /* Handle shuttles click */ },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFE3D7FF)),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .defaultMinSize(minWidth = 64.dp, minHeight = 40.dp)
        ) {
            Text(
                text = "Shuttles ${(cumulativeDistance / 20).toInt()}",
                color = Color(0xFF5A4FCF),
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun StatsCardDetails(cumulativeDistance: Int, cumulativeTime: Float) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = 8.dp,
        backgroundColor = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
            ) {
                val averageSpeed = cumulativeDistance / cumulativeTime
                val timeFor20Meters = 20 / averageSpeed
                val formattedTimeFor20Meters = String.format("%.2f", timeFor20Meters)

                StatItem(label = "Cum Dist\n(m)", value = cumulativeDistance.toString())
                VerticalDivider(color = Color(0xffE0E0E0))
                StatItem(label = "Cum Time\n(Sec & Minutes)", value = String.format("%.2f", cumulativeTime))
                VerticalDivider(color = Color(0xffE0E0E0))
                StatItem(label = "Shuttle Time\n(Sec)", value = formattedTimeFor20Meters)
                VerticalDivider(color = Color(0xffE0E0E0))
                StatItem(label = "Speed\n(km/h)", value = String.format("%.2f", averageSpeed))
            }
        }
    }
}

@Composable
fun ActionButton(
    buttonText: String,
    isTimerRunning: Boolean,
    isCompleted: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = when (buttonText) {
                "End & View Results" -> Color.Red
                else -> Color.Blue
            }
        ),
        shape = RoundedCornerShape(20),
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .height(50.dp)

    ) {
        Text(
            text = buttonText,
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.W500,
            fontSize = 16.sp,
            color = Color.White
        )
    }
}








@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontFamily = fontFamily,
            fontWeight = FontWeight.W400,
            fontSize = 12.sp,
            color = Color.Black,
            textAlign = TextAlign.Center
            )
        Text(text = value, fontFamily = fontFamily,
            fontWeight = FontWeight.W500,
            fontSize = 20.sp,
            color = Color.Black,
            textAlign = TextAlign.Center)
    }
}





@Composable
private fun TextSwitchTest(): Int {
    val items = remember {
        listOf("Group", "Solo")
    }

    var selectedIndex by remember {
        mutableStateOf(1)
    }

    TextSwitch(
        selectedIndex = selectedIndex,
        items = items,
        onSelectionChange = {
            selectedIndex = it
        }
    )
return selectedIndex
}

@Immutable
private class TextSwitchColors(
    val backgroundColor: Color = Color(0xffE6DEBF),
    val contentColor: Color = Color(0xffF7F3E4),
    val textColor: Color = Color.Gray,
    val selectedTextColor: Color = Color.Black
)
@Composable
private fun TextSwitch(
    modifier: Modifier = Modifier,
    selectedIndex: Int,
    items: List<String>,
    colors: TextSwitchColors = TextSwitchColors(),
    animationSpec: AnimationSpec<Dp> = tween(durationMillis = 250, easing = FastOutSlowInEasing),
    onSelectionChange: (Int) -> Unit
) {

    BoxWithConstraints(
        modifier
            .padding(8.dp)
            .height(44.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(colors.backgroundColor)
            .padding(5.dp)
    ) {
        if (items.isNotEmpty()) {

            val maxWidth = this.maxWidth
            val tabWidth = maxWidth / items.size

            val indicatorOffset by animateDpAsState(
                targetValue = tabWidth * selectedIndex,
                animationSpec = animationSpec,
                label = "indicator offset"
            )

            // This is for shadow layer matching white background
            Box(
                modifier = Modifier
                    .offset(x = indicatorOffset)
                    .shadow(4.dp, RoundedCornerShape(8.dp))
                    .width(tabWidth)
                    .fillMaxHeight()
            )

            Row(modifier = Modifier
                .fillMaxWidth()

                .drawWithContent {

                    // This is for setting black tex while drawing on white background
                    val padding = 8.dp.toPx()
                    drawRoundRect(
                        topLeft = Offset(x = indicatorOffset.toPx() + padding, padding),
                        size = Size(size.width / 2 - padding * 2, size.height - padding * 2),
                        color = colors.selectedTextColor,
                        cornerRadius = CornerRadius(x = 8.dp.toPx(), y = 8.dp.toPx()),
                    )

                    drawWithLayer {
                        drawContent()

                        // This is white top rounded rectangle
                        drawRoundRect(
                            topLeft = Offset(x = indicatorOffset.toPx(), 0f),
                            size = Size(size.width / 2, size.height),
                            color = colors.contentColor,
                            cornerRadius = CornerRadius(x = 8.dp.toPx(), y = 8.dp.toPx()),
                            blendMode = BlendMode.SrcOut
                        )
                    }

                }
            ) {
                items.forEachIndexed { index, text ->
                    Box(
                        modifier = Modifier
                            .width(tabWidth)
                            .fillMaxHeight()
                            .clickable(
                                interactionSource = remember {
                                    MutableInteractionSource()
                                },
                                indication = null,
                                onClick = {
                                    onSelectionChange(index)

                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = text,
                            fontSize = 14.sp,
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.W400,
                            color = Color(0xff261F00)
                        )
                    }
                }
            }
        }
    }
}

private fun ContentDrawScope.drawWithLayer(block: ContentDrawScope.() -> Unit) {
    with(drawContext.canvas.nativeCanvas) {
        val checkPoint = saveLayer(null, null)
        block()
        restoreToCount(checkPoint)
    }
}


@Composable
fun GroupItem(color: Color,groupIcon:Int,groupName: String)
{
    Column(verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(color) // Replace with your desired background color
                .padding(3.dp) // Optional: To add padding around the icon
        ) {
            Image(
                painter = painterResource(id = groupIcon), // Replace with your image resource
                contentDescription = "Group Icon",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(30.dp)
                    .align(Alignment.Center)// Adjust the size of the icon within the circle
            )
        }
        Text(text = groupName,
            fontSize = 10.sp,
            fontFamily = fontFamily,
            fontWeight = FontWeight.W400,
            overflow = TextOverflow.Ellipsis)
    }
}

@Composable
fun ResultItem(testName: String, date: String, time: String, groupName: String, count: Int) {
    Card(
        shape = RoundedCornerShape(8.dp),
        backgroundColor = Color(0xFFFFFFFFF),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = testName,
                    fontSize = 16.sp,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.W400)
                Spacer(modifier = Modifier.height(12.dp))
                Row{
                    Box(
                        modifier = Modifier
                            .background(
                                Color(0xFFEAEAEA),
                                shape = RoundedCornerShape(8.dp)
                            ) // Background color with rounded corners
                            .padding(horizontal = 8.dp, vertical = 4.dp) // Padding inside the box
                    )
                    {
                        Text(text = "$date",
                            fontSize = 10.sp,
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.W400,
                            color = Color(0xff4F4F4F))
                    }

                    Spacer(modifier = Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .background(
                                Color(0xFFEAEAEA),
                                shape = RoundedCornerShape(8.dp)
                            ) // Background color with rounded corners
                            .padding(horizontal = 8.dp, vertical = 4.dp) // Padding inside the box
                    )
                    {
                        Text(text = "$time",
                            fontSize = 10.sp,
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.W400,
                            color = Color(0xff4F4F4F))
                    }
                }



            }
            Column(horizontalAlignment = Alignment.End) {

                Icon(
                    painter = painterResource(id = R.drawable.ic_forward_arrow), // Replace with your sort icon resource
                    contentDescription = "Sort",
                    modifier = Modifier
                        .size(20.dp)
                        .padding(4.dp) // Optional: Adjust padding between text and icon
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row {
                    Box(
                        modifier = Modifier
                            .background(
                                Color(0xFFD5D5FF),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp) // Padding inside the box
                    )
                    {
                        Row( verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_group_icon),
                                contentDescription = "Sort",
                                tint = Color.Unspecified,
                                modifier = Modifier
                                    .size(10.dp)
                            )
                            Text(
                                text = groupName,
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
                            .padding(horizontal = 8.dp, vertical = 4.dp) // Padding inside the box
                    )
                    {
                        Row( verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_group_icon), // Replace with your sort icon resource
                                contentDescription = "Sort",
                                tint = Color.Unspecified,
                                modifier = Modifier
                                    .size(10.dp)
                            )

                            Text(text = "$count",
                                fontSize = 10.sp,
                                fontFamily = fontFamily,
                                fontWeight = FontWeight.W400,
                                color = Color(0xff171717))
                        }

                    }


                }
            }
        }


    }
}






