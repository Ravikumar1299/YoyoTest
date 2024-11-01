package com.yoyobeep.test.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.yoyobeep.test.screens.sign_in.UserData
import com.yoyobeep.test.ui.theme.AppBackgroundColor
import androidx.compose.material.Surface
import com.yoyobeep.test.ui.theme.SplashColor
import androidx.compose.material.Icon
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.yoyobeep.test.ui.theme.fontFamily
import com.yoyobeep.test.R
import com.yoyobeep.test.database.PlayerDataClass
import com.yoyobeep.test.database.PlayerViewModel

@Composable
fun ProfileScreen(
    userData: UserData?,
    onSignOut: () -> Unit,
    navController: NavHostController
) {
    val viewModel: PlayerViewModel = viewModel()
    var player by remember { mutableStateOf<PlayerDataClass?>(null) }
    val playersinfo by viewModel.playersByName.collectAsState()

    Column(
        modifier = Modifier
            .navigationBarsPadding()
            .fillMaxSize()
            .background(AppBackgroundColor),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
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
                    text = "Profile",
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.W400,
                    fontSize = 20.sp,
                    color = Color.White
                )

                Spacer(modifier = Modifier.weight(1f))

            }
        }

        // Profile Information (Image and Name) at the top
        Spacer(modifier = Modifier.height(24.dp)) // Optional spacing

        if (userData?.profilePictureUrl != null) {
            AsyncImage(
                model = userData.profilePictureUrl,
                contentDescription = "Profile picture",
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (userData?.username != null) {
            Text(
                text = userData.username,
                textAlign = TextAlign.Center,
                fontSize = 36.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Spacer to push the Sign Out button to the bottom
        Spacer(modifier = Modifier.weight(1f))

        // Sign-out Button at the bottom
        Button(
            onClick = {
                onSignOut()
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Blue),
            shape = RoundedCornerShape(20),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(50.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Logout",color = Color.White)
        }
    }
}
