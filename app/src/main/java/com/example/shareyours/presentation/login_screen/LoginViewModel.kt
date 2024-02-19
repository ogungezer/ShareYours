package com.example.shareyours.presentation.login_screen

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginState(currentUser = null))
    val uiState : StateFlow<LoginState> = _uiState.asStateFlow()

    fun signInUser(
        auth: FirebaseAuth,
        email: String,
        password: String,
        context: Context,
        navController: NavController
    ){
        if(email != "" && password != ""){
            auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        _uiState.update { it.copy(currentUser = auth.currentUser) }
                        val currentUser = uiState.value.currentUser?.displayName
                        Toast.makeText(context, "Hoşgeldin $currentUser", Toast.LENGTH_LONG).show()
                        navController.navigate(route = "homescreen"){
                            popUpTo("loginscreen"){
                                inclusive = true
                            }
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(context, "E-posta veya şifre yanlış!", Toast.LENGTH_LONG).show()
                }
        }
    }

    fun togglePasswordVisibility(toggledVisibility : Boolean){
        _uiState.update { it.copy(isPasswordOpen = toggledVisibility ) }
    }

}