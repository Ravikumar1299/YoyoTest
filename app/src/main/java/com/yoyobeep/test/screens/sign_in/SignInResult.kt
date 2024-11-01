package com.yoyobeep.test.screens.sign_in

data class SignInResult(
    val data: UserData?,
    val errorMessage: String?
)

data class UserData(
    val userId: String,
    val emailId:String?,
    val username: String?,
    val profilePictureUrl: String?
)
