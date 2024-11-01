package com.yoyobeep.test

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.android.gms.auth.api.identity.Identity
import com.yoyobeep.test.screens.Group.PickGroupScreen
import com.yoyobeep.test.screens.HomeScreen
import com.yoyobeep.test.screens.LoginScreen
import com.yoyobeep.test.screens.PlayerDetailScreen
import com.yoyobeep.test.screens.PlayersScreen
import com.yoyobeep.test.screens.QuickTestScreen
import com.yoyobeep.test.screens.SplashScreen
import com.yoyobeep.test.screens.profile.ProfileScreen
import com.yoyobeep.test.screens.sign_in.GoogleAuthUiClient
import com.yoyobeep.test.ui.theme.SplashColor
import com.yoyobeep.test.ui.theme.YoYoTestTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        setContent {
            YoYoTestTheme {
                BasicsApp(googleAuthUiClient = googleAuthUiClient,
                    finishAffinity = { finishAffinity() })
            }
        }

    }


}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BasicsApp(finishAffinity: () -> Unit, googleAuthUiClient: GoogleAuthUiClient) {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    BackHandler(enabled = true) {
        if (navController.currentDestination?.route == "homescreen") {
            navController.navigate("splashscreen") {
                popUpTo("homescreen") { inclusive = true }
            }
        } else if (navController.currentDestination?.route == "splashscreen") {
            finishAffinity()
        } else {
            navController.popBackStack()
        }
    }

    Scaffold(
        content = { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding))

            NavHost(navController = navController, startDestination = "splashscreen") {
                composable("splashscreen") {
                    SplashScreen(
                        logo = painterResource(id = R.drawable.ic_splash),
                        backgroundColor = SplashColor,
                        navController = navController
                    )
                }
            composable("profile") {
                ProfileScreen(
                    userData = googleAuthUiClient.getSignedInUser(),
                    onSignOut = {
                        scope.launch {
                            googleAuthUiClient.signOut()
                            navController.navigate("loginscreen")
                        }
                    },
                    navController,
                )
            }
                composable("homescreen") {
                    HomeScreen(navController,
                        userData = googleAuthUiClient.getSignedInUser())
                }
                composable("loginscreen") {
                    LoginScreen( googleAuthUiClient,navController)
                }

                composable("pickgroup") {
                    PickGroupScreen(navController)
                }
                composable("playerscreen/{from}") {backStackEntry->
                    PlayersScreen(navController, navBackStackEntry = backStackEntry)
                }
                composable("playerdetailscreen/{playerId}/{playerName}",
                    arguments = listOf(
                        navArgument("playerId") { type = NavType.IntType },
                        navArgument("playerName") { type = NavType.StringType }
                    )) {backStackEntry ->
                    val playerId = backStackEntry.arguments?.getInt("playerId") ?: -1
                    val playerName = backStackEntry.arguments?.getString("playerName") ?: ""

                    PlayerDetailScreen(playerId = playerId, playerName = playerName,navController = navController, navBackStackEntry = backStackEntry)
                }
//                composable("quickTest/{heading}") { backStackEntry ->
//                    QuickTestScreen(navController = navController, navBackStackEntry = backStackEntry)
//                }

                composable(
                    route = "quickTest/{heading}/{playerId}/{playerName}",
                    arguments = listOf(
                        navArgument("playerId") { type = NavType.IntType },
                        navArgument("playerName") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val playerId = backStackEntry.arguments?.getInt("playerId") ?: -1
                    val playerName = backStackEntry.arguments?.getString("playerName") ?: ""

//                    // Fetch image and other details based on playerId if needed
                    QuickTestScreen(playerId = playerId, playerName = playerName,navController = navController, navBackStackEntry = backStackEntry)
                }
            }
        }
    )
}

