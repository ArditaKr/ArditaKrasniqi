package com.example.arditakrasniqi.domain.usecase

import com.example.arditakrasniqi.domain.repository.UserRepository
import javax.inject.Inject

class DeleteUserUseCase @Inject constructor(private val userRepository: UserRepository) {
    fun execute(id: Int): Unit = userRepository.deleteUserDataFromDB(id)
}