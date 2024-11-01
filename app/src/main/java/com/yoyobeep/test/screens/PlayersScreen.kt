@file:OptIn(ExperimentalMaterialApi::class)

package com.yoyobeep.test.screens

import androidx.compose.foundation.lazy.items

import android.graphics.BitmapFactory
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.BottomSheetState
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.yoyobeep.test.R
import com.yoyobeep.test.database.PlayerViewModel
import com.yoyobeep.test.ui.theme.AppBackgroundColor
import com.yoyobeep.test.ui.theme.BottomNavBarColor
import com.yoyobeep.test.ui.theme.SplashColor
import com.yoyobeep.test.ui.theme.fontFamily
import kotlinx.coroutines.launch
import java.io.ByteArrayInputStream

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialApi::class)
@Composable
fun PlayersScreen(navController: NavHostController,navBackStackEntry: NavBackStackEntry)
{
    val from = navBackStackEntry.arguments?.getString("from") ?: "nav_players"
    AddPlayerBottomSheet(navController,from)

    }

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
private fun AddPlayerBottomSheet(navController: NavHostController,from:String) {
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 700.dp) // Limit the maximum height
                    .padding(top = 16.dp)
            ) {
                SheetContent(playerViewModel = viewModel(), bottomSheetScaffoldState)
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
            PlayerMainContent(navController,bottomSheetScaffoldState.bottomSheetState, bottomSheetScaffoldState,from)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PlayerMainContent(
    navController: NavHostController,
    bottomSheetState: BottomSheetState,
    bottomSheetScaffoldState: BottomSheetScaffoldState,
    from: String,
) {
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()
    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    val selectedItem = remember { mutableStateOf("home") }
    val content = remember { mutableStateOf("Home Screen") }

    val viewModel: PlayerViewModel = viewModel()
    val players by viewModel.players.collectAsState()

    // Filter players based on searchText
    val filteredPlayers = players.filter { player ->
        player.name.contains(searchText.text, ignoreCase = true)
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        sheetContent = { CustomBottomSheetContent(navController, sheetState) }
    ) {
        Scaffold(
            backgroundColor = AppBackgroundColor,

            bottomBar = {
                if(from.equals("nav_players")) {
                    BottomAppBar(

                        backgroundColor = BottomNavBarColor,
                        cutoutShape = RoundedCornerShape(50),
                        modifier = Modifier
                            .navigationBarsPadding(),
                        content = {
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
                                            tint = Color(0xFF7988A3)
                                        )
                                    },
                                    label = {
                                        Text(text = "Home", color = Color(0xFF7988A3))// Add the label here
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
                                            tint = Color(0xFFFFFFFF)
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = "Players",
                                            color = Color.White
                                        ) // Add the label here
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

//                                Box(modifier = Modifier.weight(1f))


                            }
                        }
                    )
                }
            },

            floatingActionButton = {
                if(from.equals("nav_players")) {
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
                        modifier = Modifier
                            .height(54.dp)
                            .width(54.dp)
                    ) {
                        Icon(
                            Icons.Filled.Add,
                            tint = Color.White,
                            contentDescription = "Add"
                        )

                    }
                }
            },
            isFloatingActionButtonDocked = true,
            content = { paddingValues ->

                if(!from.equals("nav_players"))
                {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        Header(navController, "Solo")
//                    Spacer(modifier = Modifier.height(50.dp))



                        Spacer(modifier = Modifier.height(16.dp))


                        Column( modifier = Modifier
                            .padding(start = 22.dp, end = 22.dp)) {
                            Row(
                                modifier = Modifier
                                    .height(56.dp)
                                    .background(Color(0xFFD4CBAC), shape = RoundedCornerShape(16.dp))
                                    .padding(12.dp, 0.dp, 20.dp, 0.dp)
                            ) {
                                TextField(
                                    value = searchText,
                                    onValueChange = { searchText = it },
                                    placeholder = { Text("Search") },
                                    colors = TextFieldDefaults.textFieldColors(
                                        backgroundColor = Color.Transparent,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        disabledIndicatorColor = Color.Transparent,
                                        textColor = Color.Black,
                                        placeholderColor = Color.Gray
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_search_icon),
                                    contentDescription = null,
                                    tint = Color(0xFFAAAAAA),
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                )
                            }
                        }


                        Spacer(modifier = Modifier.height(16.dp))

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(start = 26.dp, end = 26.dp)
                        ) {
                            items(filteredPlayers) { player ->
                                PlayerItem(
                                    playerId = player.id,
                                    playerImage = player.playerImage,
                                    playerName = player.name,
                                    navController = navController,
                                    viewModel = viewModel,
                                    from
                                )
                            }
                        }
                    }
                }

                else
                {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
//                        Header(navController, "Solo")
                    Spacer(modifier = Modifier.height(50.dp))

                        Row(
                            modifier = Modifier
                                .padding(24.dp)
                        ) {

                            Text(
                                text = "List of Players (${filteredPlayers.size})",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.W400,
                                fontFamily = fontFamily,
                                modifier = Modifier
                                    .weight(1f)
                                    .align(Alignment.CenterVertically),
                                color = Color(0xFF161616)
                            )
                            Box(
                                modifier = Modifier
                                    .height(24.dp)
                                    .background(
                                        Color(0xFFE1D5A9),
                                        shape = RoundedCornerShape(30.dp)
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = Color(0xFFA59763),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .padding(
                                        horizontal = 8.dp,
                                        vertical = 4.dp
                                    )
                                    .clickable {
                                        // Launch coroutine to expand bottom sheet
                                        scope.launch {
                                            bottomSheetScaffoldState.bottomSheetState.expand()
                                        }
                                    }
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "New Player",
                                        fontSize = 12.sp,
                                        fontFamily = fontFamily,
                                        fontWeight = FontWeight.W400,
                                        color = Color(0xff6A5B26)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_plus),
                                        contentDescription = "Add Player",
                                        tint = Color.Unspecified,
                                        modifier = Modifier.size(10.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))


                        Column( modifier = Modifier
                            .padding(start = 22.dp, end = 22.dp)) {
                            Row(
                                modifier = Modifier
                                    .height(56.dp)
                                    .background(Color(0xFFD4CBAC), shape = RoundedCornerShape(16.dp))
                                    .padding(12.dp, 0.dp, 20.dp, 0.dp)
                            ) {
                                TextField(
                                    value = searchText,
                                    onValueChange = { searchText = it },
                                    placeholder = { Text("Search") },
                                    colors = TextFieldDefaults.textFieldColors(
                                        backgroundColor = Color.Transparent,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        disabledIndicatorColor = Color.Transparent,
                                        textColor = Color.Black,
                                        placeholderColor = Color.Gray
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_search_icon),
                                    contentDescription = null,
                                    tint = Color(0xFFAAAAAA),
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                )
                            }
                        }


                        Spacer(modifier = Modifier.height(16.dp))

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(start = 26.dp, end = 26.dp)
                        ) {
                            items(filteredPlayers) { player ->
                                PlayerItem(
                                    playerId = player.id,
                                    playerImage = player.playerImage,
                                    playerName = player.name,
                                    navController = navController,
                                    viewModel = viewModel,
                                    from
                                )
                            }
                        }
                    }
                }
            }
        )
    }
}



@Composable
fun PlayerItem(
    playerId: Int,
    playerImage: ByteArray?,
    playerName: String,
    navController: NavController,
    viewModel: PlayerViewModel = viewModel(), // Ensure ViewModel is properly provided
    from: String
) {
    val coroutineScope = rememberCoroutineScope() // Create a coroutine scope for launching coroutines

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                coroutineScope.launch {
                    // Fetch player details and navigate

                    if(from.equals("nav_players"))
                    {
//                        navController.navigate("playerdetailscreen")
                        viewModel.getPlayerById(playerId)?.let { player ->
                            navController.navigate("playerdetailscreen/${player.id}/${player.name}") {
                                // Optionally clear back stack if needed
                                popUpTo("playerdetailscreen") { inclusive = true }
                            }
                        }
                    }
                    else{
                        viewModel.getPlayerById(playerId)?.let { player ->
                            navController.navigate("quickTest/Solo Test/${player.id}/${player.name}") {
                                // Optionally clear back stack if needed
                                popUpTo("playerListScreen") { inclusive = true }
                            }
                        }
                    }

                }
            }
            .padding(vertical = 8.dp)
    ) {
        val imageBitmap = playerImage?.let { byteArrayToImageBitmap(it) }

        // Display the image
        Image(
            bitmap = imageBitmap ?: ImageBitmap.imageResource(id = R.drawable.img_dummy_male), // Fallback image
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = playerName,
            fontSize = 16.sp,
            fontWeight = FontWeight.W400,
            fontFamily = fontFamily,
            modifier = Modifier.weight(1f)
        )

        Icon(
            painter = painterResource(id = R.drawable.ic_forward_arrow),
            contentDescription = null
        )
    }

    Divider(
        color = Color(0xFFD8CFAE), // Divider color
        thickness = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
    )
}



//@Composable
//fun PlayerItem(playerImage: ByteArray?,playerName: String,navController: NavController) {
//    Row(
//        verticalAlignment = Alignment.CenterVertically,
//        modifier = Modifier
//            .fillMaxWidth()
//            .clickable { navController.navigate("playerdetailscreen")}
//            .padding(vertical = 8.dp)
//    ) {
//
//
//        val imageBitmap = playerImage?.let { byteArrayToImageBitmap(it) }
//
//        // Display the image
//        Image(
//            bitmap = imageBitmap ?: ImageBitmap.imageResource(id = R.drawable.ic_profile_img), // Fallback image if ByteArray is null
//            contentDescription = "Profile Picture",
//            modifier = Modifier
//                .size(40.dp)
//                .clip(CircleShape),
//            contentScale = ContentScale.Crop
//        )
//
//        Spacer(modifier = Modifier.width(8.dp))
//        Text(
//            text = playerName,
//            fontSize = 16.sp,
//            fontWeight = FontWeight.W400,
//            fontFamily = fontFamily,
//            modifier = Modifier.weight(1f)
//        )
//
//        Icon(painter = painterResource(id = R.drawable.ic_forward_arrow), contentDescription = null)
//    }
//
//    Divider(
//        color = Color(0xFFD8CFAE), // Black color
//        thickness = 1.dp,
//        modifier = Modifier
//            .fillMaxWidth()
//    )
//}

fun byteArrayToImageBitmap(byteArray: ByteArray): ImageBitmap {
    val bitmap = BitmapFactory.decodeStream(ByteArrayInputStream(byteArray))
    return bitmap.asImageBitmap()
}




