package com.yoyobeep.test.screens


import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column


import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.ImageLoader
import coil.compose.LocalImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult


import com.yoyobeep.test.R
import com.yoyobeep.test.database.PlayerViewModel
import com.yoyobeep.test.screens.sign_in.GoogleAuthUiClient
import com.yoyobeep.test.screens.sign_in.SignInViewModel
import com.yoyobeep.test.ui.theme.fontFamily
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


//fun handleSignIn(result: GetCredentialResponse) {
//
//    val request: GetCredentialRequest = Builder()
//        .addCredentialOption(googleIdOption)
//        .build()
//    // Handle the successfully returned credential.
//    val credential = result.credential
//
//    when (credential) {
//
//        // Passkey credential
//        is PublicKeyCredential -> {
//            // Share responseJson such as a GetCredentialResponse on your server to
//            // validate and authenticate
//            responseJson = credential.authenticationResponseJson
//        }
//
//        // Password credential
//        is PasswordCredential -> {
//            // Send ID and password to your server to validate and authenticate.
//            val username = credential.id
//            val password = credential.password
//        }
//
//        // GoogleIdToken credential
//        is CustomCredential -> {
//            if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
//                try {
//                    // Use googleIdTokenCredential and extract the ID to validate and
//                    // authenticate on your server.
//                    val googleIdTokenCredential = GoogleIdTokenCredential
//                        .createFrom(credential.data)
//                    // You can use the members of googleIdTokenCredential directly for UX
//                    // purposes, but don't use them to store or control access to user
//                    // data. For that you first need to validate the token:
//                    // pass googleIdTokenCredential.getIdToken() to the backend server.
//                    GoogleIdTokenVerifier verifier = ... // see validation instructions
//                    GoogleIdToken idToken = verifier.verify(idTokenString);
//                    // To get a stable account identifier (e.g. for storing user data),
//                    // use the subject ID:
//                    idToken.getPayload().getSubject()
//                } catch (e: GoogleIdTokenParsingException) {
//                    Log.e("", "Received an invalid google id token response", e)
//                }
//            } else {
//                // Catch any unrecognized custom credential type here.
//                Log.e("", "Unexpected type of credential")
//            }
//        }
//
//        else -> {
//            // Catch any unrecognized credential type here.
//            Log.e("", "Unexpected type of credential")
//        }
//    }
//}

@Composable
fun LoginScreen(googleAuthUiClient: GoogleAuthUiClient, navController: NavHostController) {
var context = LocalContext.current
    val imageLoader: ImageLoader = LocalImageLoader.current
    val scope = rememberCoroutineScope()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Surface at the bottom
        Surface(
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(178.dp)
                .align(Alignment.BottomCenter), // Aligns the surface to the bottom
            color = Color(0xFFEFE9D3)
        ) {


            val viewModel = viewModel<SignInViewModel>()
            val state by viewModel.state.collectAsStateWithLifecycle()
            val playerViewModel = viewModel<PlayerViewModel>() // Assuming you have PlayerViewModel set up
            var isEmailexist = false

            LaunchedEffect(key1 = Unit) {
                if(googleAuthUiClient.getSignedInUser() != null) {
                    navController.navigate("homescreen")
                }
            }

            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartIntentSenderForResult(),
                onResult = { result ->
                    if(result.resultCode == Activity.RESULT_OK) {
                        scope.launch {
                            // Sign-in with Google
                            val signInResult = googleAuthUiClient.signInWithIntent(
                                intent = result.data ?: return@launch
                            )

                            // Check if the email already exists in the database
                            val emailId = signInResult.data?.emailId?.trim() ?: return@launch
                            val isEmailexist = playerViewModel.getplayerByemail(emailId)

                            // If the email does not exist, proceed with loading the profile picture and saving the user
                            if (!isEmailexist) {
                                val profilePictureUrl = signInResult.data?.profilePictureUrl

                                // Load the profile image if the URL is available
                                val bitmap = profilePictureUrl?.let {
                                    loadBitmapFromUrl(it,context,imageLoader)
                                }

                                playerViewModel.name = signInResult.data.username.toString()
                                playerViewModel.email = signInResult.data.emailId
                                playerViewModel.image = bitmap
                                playerViewModel.savePlayer()

                                // Pass the sign-in result and the profile picture bitmap to the ViewModel

                            }
//                            viewModel.onSignInResult(signInResult, playerViewModel, bitmap)
                            viewModel.onSignInResult(signInResult)
                        }
                    }
                }
            )
            

            LaunchedEffect(key1 = state.isSignInSuccessful) {
                if(state.isSignInSuccessful) {
                    navController.navigate("homescreen")
                    viewModel.resetState()
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = {
                        scope.launch {
                            val signInIntentSender = googleAuthUiClient.signIn()
                            launcher.launch(
                                IntentSenderRequest.Builder(
                                    signInIntentSender ?: return@launch
                                ).build()
                            )
                        }

                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black),
                    shape = RoundedCornerShape(20),
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(50.dp)
                        .align(Alignment.Center)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_google_logo),
                        contentDescription = "Google Logo",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Sign Up with Google",
                        color = Color.White
                    )
                }
            }
        }

        // Column content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = 198.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Box(
                modifier = Modifier
            ) {
                Image(painter = painterResource(id = R.drawable.ic_login),
                    contentDescription = "App Logo",
                    contentScale = ContentScale.Fit)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Test your endurance with the Yo-Yo and Beep Test app—track VO2 max and fitness levels easily.",
                fontSize = 20.sp,
                fontFamily = fontFamily,
                fontWeight = FontWeight.W600,
                modifier = Modifier.padding(start = 10.dp, end = 10.dp),
                        textAlign = TextAlign.Center
            )



            Spacer(modifier = Modifier.height(16.dp))
        }
    }

}

suspend fun loadBitmapFromUrl(url: String, context: Context, imageLoader: ImageLoader): Bitmap? {
    return withContext(Dispatchers.IO) {
        try {
            val request = ImageRequest.Builder(context)
                .data(url)
                .build()

            val result = (imageLoader.execute(request) as SuccessResult).drawable
            (result as BitmapDrawable).bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}



