package com.yoyobeep.test.screens.sign_in

import android.graphics.Bitmap
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yoyobeep.test.database.PlayerViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

import kotlinx.coroutines.flow.update

class SignInViewModel: ViewModel() {

    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    fun onSignInResult(
        result: SignInResult,
//        playerViewModel: PlayerViewModel,
//        loadBitmapFromUrl: Bitmap?
    ) {
//        playerViewModel.name = "My Self (${result.data?.username.toString()})"
//        playerViewModel.email = result.data?.emailId.toString()
//        playerViewModel.image = loadBitmapFromUrl
//        playerViewModel.savePlayer()
        _state.update { it.copy(
            isSignInSuccessful = result.data != null,
            signInError = result.errorMessage
        ) }
    }

    fun resetState() {
        _state.update { SignInState() }
    }
}