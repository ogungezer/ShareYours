package com.example.shareyours.presentation.home_screen

import com.example.shareyours.Post

data class ListState(
    val postList: MutableList<Post> = mutableListOf(),
    val isListEmpty : Boolean = false,
    val isRefreshing : Boolean = false
)