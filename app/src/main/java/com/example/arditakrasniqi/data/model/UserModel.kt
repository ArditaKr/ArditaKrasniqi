package com.example.arditakrasniqi.data.model

import androidx.annotation.Keep
import androidx.room.Entity

@Entity
@Keep
data class UserModel(
    val name: String,
    val weight: Int,
    val height: Int,
    val gender: String
)
