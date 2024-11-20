package com.yoyobeep.test.screens.Group

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.BottomSheetValue
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.yoyobeep.test.R
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.yoyobeep.test.database.GroupViewModel
import com.yoyobeep.test.screens.Header
import com.yoyobeep.test.screens.PlayerMainContent
import com.yoyobeep.test.screens.loadBitmapFromUri
import com.yoyobeep.test.ui.theme.AppBackgroundColor
import com.yoyobeep.test.ui.theme.fontFamily
import kotlinx.coroutines.launch


@Composable
fun PickGroupScreen(navController: NavHostController) {
    var testName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Header(navController, "Group Test")

        Spacer(modifier = Modifier.height(16.dp))

        // Text Field for Test Name
        OutlinedTextField(
            value = testName,
            onValueChange = { testName = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            label = { Text("Test Name") },
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Group Picker Section
        GroupPickerSection()

        Spacer(modifier = Modifier.weight(1f))

        // Next Button
        Button(
            onClick = {
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
fun GroupPickerSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFFF5DD), shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "Pick Group",
            fontWeight = FontWeight.W400,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Group Items (Example with dummy data)
        val groups = listOf(
            Group("New Group", R.drawable.ic_add_group),
            Group("Mornings...", R.drawable.ic_group_icon),
            Group("Bee Club", R.drawable.ic_group_icon),
            Group("Zaag guys", R.drawable.ic_group_icon),
            Group("Jin Club", R.drawable.ic_group_icon),
            Group("Unicorn g...", R.drawable.ic_group_icon)
        )

        // Display groups in a grid
        Column {
            for (i in groups.chunked(3)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    i.forEach { group ->
                        GroupItem(group)
                    }
                }
            }
        }
    }
}


@Composable
fun GroupItem(group:Group)
{
    Column(verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(Color.White) // Replace with your desired background color
                .padding(3.dp) // Optional: To add padding around the icon
        ) {
            Image(
                painter = painterResource(id = group.iconResId), // Replace with your image resource
                contentDescription = "Group Icon",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(30.dp)
                    .align(Alignment.Center)// Adjust the size of the icon within the circle
            )
        }
        androidx.compose.material.Text(
            text = group.name,
            fontSize = 10.sp,
            fontFamily = fontFamily,
            fontWeight = FontWeight.W400,
            overflow = TextOverflow.Ellipsis
        )
    }
}

data class Group(val name: String, val iconResId: Int)

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
private fun AddGroupBottomSheet(navController: NavHostController) {
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
                AddGroupSheetContent(groupViewModel = viewModel(), bottomSheetScaffoldState)
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
//            PlayerMainContent(navController,bottomSheetScaffoldState.bottomSheetState,
//                bottomSheetScaffoldState)
        }
    }



}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddGroupSheetContent(
    groupViewModel: GroupViewModel,
    bottomSheetScaffoldState: BottomSheetScaffoldState
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope() // Coroutine scope for managing coroutines


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
    ) {
        // Image Picker
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            GroupImagePickerWithPhotoPicker(groupViewModel)
        } else {
            GroupImagePickerWithIntent(groupViewModel)
        }



        // Save Player Button
        Button(
            onClick = {

            },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Blue),
            shape = RoundedCornerShape(20),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(50.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "Add Group",
                color = Color.White
            )
        }
    }
}


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun GroupImagePickerWithPhotoPicker(groupViewModel: GroupViewModel) {
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
                groupViewModel.updatePlayerImage(bitmap)  // Ensure ViewModel is updated
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
            val imageBitmap = groupViewModel.image ?: BitmapFactory.decodeResource(
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

@Composable
fun GroupImagePickerWithIntent(groupViewModel:GroupViewModel) {
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
                groupViewModel.updatePlayerImage(imageBitmap) // Update image in ViewModel
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


