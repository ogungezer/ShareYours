package com.example.shareyours.presentation.login_screen

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.shareyours.R
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(auth: FirebaseAuth, navController: NavController) {
    val viewModel : LoginViewModel = viewModel()
    val state = viewModel.uiState.collectAsState()
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 34.dp)
            .padding(horizontal = 20.dp)
    ) {
        val context = LocalContext.current
        var email by rememberSaveable { mutableStateOf("") }
        var password by rememberSaveable { mutableStateOf("") }
        Text(text = "GİRİŞ YAP", color = MaterialTheme.colorScheme.onBackground, fontSize = 32.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(24.dp))
        TextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(text = "Email")
            }
        )
        Spacer(modifier = Modifier.height(24.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(text = "Password")
            },
            trailingIcon = {
                IconButton(onClick = {viewModel.togglePasswordVisibility(!state.value.isPasswordOpen)}){
                    Icon(painter = if(state.value.isPasswordOpen) painterResource(id = R.drawable.visibility_24px) else painterResource(id = R.drawable.visibility_off_24px), contentDescription = null)
                }
            },
            visualTransformation = if(!state.value.isPasswordOpen) PasswordVisualTransformation() else VisualTransformation.None
        )
        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                viewModel.signInUser(auth, email, password,context,navController)
            },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(0.dp))
        ) {
            Text(text = "Giris Yap", fontSize = 14.sp)
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Hesabın yoksa")
            Spacer(modifier = Modifier.width(2.dp))
            TextButton(
                onClick = {
                    navController.navigate("signupscreen"){
                        popUpTo("loginscreen"){
                            inclusive = true
                        }
                    }
                }
            ) {
                Text(text = "Kayıt Ol")
            }
        }

    }
}

