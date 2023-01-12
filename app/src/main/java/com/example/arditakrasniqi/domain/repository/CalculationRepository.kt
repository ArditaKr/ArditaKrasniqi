package com.example.arditakrasniqi.domain.repository

interface CalculationRepository {
    fun calculateBMI(weight: Double, height: Double): Double
}