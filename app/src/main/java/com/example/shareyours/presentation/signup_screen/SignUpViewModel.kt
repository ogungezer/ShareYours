package com.example.shareyours.presentation.signup_screen

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.shareyours.presentation.login_screen.LoginState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class SignUpViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpState())
    val uiState : StateFlow<SignUpState> = _uiState.asStateFlow()


    fun signUpNewUser(
        auth: FirebaseAuth,
        email: String,
        password: String,
        username : String,
        context: Context,
        navController: NavController
    ){
        if(email != "" && password != "" && username != ""){
            auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        val user = auth.currentUser
                        val profileUpdates = userProfileChangeRequest {
                            displayName = username
                        }
                        user?.let{ currentUser ->
                            currentUser.updateProfile(profileUpdates)
                                .addOnCompleteListener {task ->
                                    if(task.isSuccessful){
                                        Log.d("TAG", "username added!")
                                    }
                                }
                        }
                        Toast.makeText(context,"Başarıyla kayıt oldun!",Toast.LENGTH_SHORT).show()
                        Log.d("TAG", "You've signed up successfully!")
                        navController.navigate(route = "loginscreen"){
                            popUpTo("signupscreen"){
                                inclusive = true
                            }
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("exception",exception.toString())
                    Toast.makeText(context, "Kayıt Başarısız!", Toast.LENGTH_LONG).show()
                }
        }
    }

    fun togglePasswordVisibility(toggledVisibility : Boolean){
        _uiState.update { it.copy(isPasswordOpen = toggledVisibility ) }
    }

}