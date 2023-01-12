package com.example.arditakrasniqi.domain.repository

import com.example.arditakrasniqi.data.model.UserModel

interface UserRepository {
    fun insertUserData(userModel: UserModel)
    fun deleteUserDataFromDB(id: Int)
}