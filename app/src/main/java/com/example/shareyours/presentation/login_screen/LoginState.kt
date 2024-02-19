package com.example.shareyours.presentation.login_screen

import com.google.firebase.auth.FirebaseUser

data class LoginState(
    val currentUser : FirebaseUser?,
    val isPasswordOpen : Boolean = false
)
