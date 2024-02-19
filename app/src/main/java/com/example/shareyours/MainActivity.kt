package com.example.shareyours

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.shareyours.presentation.create_post_screen.CreatePostScreen
import com.example.shareyours.presentation.home_screen.HomeScreen
import com.example.shareyours.presentation.login_screen.LoginScreen
import com.example.shareyours.presentation.signup_screen.SignUpScreen
import com.example.shareyours.ui.theme.ShareYoursTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        auth = Firebase.auth

        var startDestination = "signupscreen"
        val currentUser = auth.currentUser
        if(currentUser != null) {
            startDestination = "homescreen"
        }

        super.onCreate(savedInstanceState)
        setContent {
            ShareYoursTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    StartApp(
                        navController = navController,
                        auth = auth,
                        startDestination = startDestination
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartApp(navController: NavHostController, startDestination: String, auth: FirebaseAuth){
    NavHost(navController = navController, startDestination = startDestination){
        composable(route = "signupscreen"){
            SignUpScreen(auth = auth, navController = navController)
        }
        composable(route = "loginscreen"){
            LoginScreen(auth = auth, navController = navController)
        }
        composable(route = "homescreen"){
            Scaffold(
                topBar = { MyTopAppBar(auth = auth, navController = navController, title = "HomeScreen", showDropDownIcon = true) }
            ) { paddingValue ->
                HomeScreen(padding = paddingValue)
            }
        }
        composable(route = "createpostscreen"){
            Scaffold(
                topBar = { MyTopAppBar(auth = auth, navController = navController, title = "Create a Post", showNavIcon = true) }
            ) { paddingValues ->
                CreatePostScreen(auth = auth, padding = paddingValues, navController = navController)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(auth: FirebaseAuth?, navController: NavHostController, title : String?, showNavIcon : Boolean = false, showDropDownIcon : Boolean = false) {
    var dropDownShow by remember { mutableStateOf(false) }
    TopAppBar(
        title = { Text(text = title ?: "", color = MaterialTheme.colorScheme.onBackground) },
        actions = {
            if (showDropDownIcon) {
                IconButton(
                    onClick = {
                        dropDownShow = !dropDownShow
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                DropdownMenu(
                    expanded = dropDownShow,
                    onDismissRequest = { dropDownShow = false },
                    offset = DpOffset(x = 16.dp, y = 2.dp),
                    modifier = Modifier.background(color = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "Gönderi oluştur",
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        },
                        onClick = {
                            dropDownShow = false
                            navController.navigate("createpostscreen")
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Create,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    )
                    Divider(
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        thickness = 0.5.dp
                    )
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "Çıkış yap",
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        },
                        onClick = {
                            dropDownShow = false
                            auth?.signOut()
                            navController.navigate("loginscreen") {
                                popUpTo(route = "homescreen") {
                                    inclusive = true
                                }
                            }
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    )
                }
            }
        },
        navigationIcon = {
            if (showNavIcon) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "navigate back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    )
}

