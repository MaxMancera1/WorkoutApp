package com.example.workoutapp.model

import java.util.Date

data class Workout(
    val type: String,
    val duration: Int, // in minutes
    val calories: Int,
    val distance: Double, // in kilometers
    val date: Date
)
