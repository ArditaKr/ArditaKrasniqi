package com.example.arditakrasniqi.domain.usecase

import com.example.arditakrasniqi.data.model.UserModel
import com.example.arditakrasniqi.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class InsertUserUseCase  @Inject constructor(private val userRepository: UserRepository) {
    fun execute(userModel: UserModel): Unit = userRepository.insertUserData(userModel)
}