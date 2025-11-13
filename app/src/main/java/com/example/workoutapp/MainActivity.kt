package com.example.workoutapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.workoutapp.adapter.WorkoutAdapter
import com.example.workoutapp.model.Workout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Calendar
import java.util.Date

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fab: FloatingActionButton
    private lateinit var totalWorkouts: TextView
    private lateinit var totalCalories: TextView
    private lateinit var avgDuration: TextView

    private val workouts = mutableListOf<Workout>()
    private lateinit var adapter: WorkoutAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        fab = findViewById(R.id.fab)
        totalWorkouts = findViewById(R.id.totalWorkouts)
        totalCalories = findViewById(R.id.totalCalories)
        avgDuration = findViewById(R.id.avgDuration)

        setupRecyclerView()
        setupFab()
        updateStats()
    }

    private fun setupRecyclerView() {
        adapter = WorkoutAdapter(workouts)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter.onItemClick = {
            showWorkoutDetails(it)
        }
    }

    private fun setupFab() {
        fab.setOnClickListener {
            showAddWorkoutDialog()
        }
    }

    private fun updateStats() {
        val workoutCount = workouts.size
        val totalCaloriesValue = workouts.sumOf { it.calories }
        val avgDurationValue = if (workouts.isNotEmpty()) workouts.map { it.duration }.average() else 0.0

        totalWorkouts.text = "Workouts: $workoutCount"
        totalCalories.text = "Calories: $totalCaloriesValue"
        avgDuration.text = "Avg Duration: ${String.format("%.2f", avgDurationValue)} min"
    }

    private fun showAddWorkoutDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_workout, null)
        val spinner: Spinner = dialogView.findViewById(R.id.workoutTypeSpinner)
        val durationEditText: EditText = dialogView.findViewById(R.id.durationEditText)
        val caloriesEditText: EditText = dialogView.findViewById(R.id.caloriesEditText)
        val distanceEditText: EditText = dialogView.findViewById(R.id.distanceEditText)
        val datePicker: DatePicker = dialogView.findViewById(R.id.datePicker)

        val workoutTypes = arrayOf("Running", "Cycling", "Swimming", "Yoga", "Weight Training")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, workoutTypes)
        spinner.adapter = spinnerAdapter

        val dialog = AlertDialog.Builder(this)
            .setTitle("Add Workout")
            .setView(dialogView)
            .setPositiveButton("Add", null)
            .setNegativeButton("Cancel", null)
            .create()

        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {
                val type = spinner.selectedItem.toString()
                val duration = durationEditText.text.toString().toIntOrNull()
                val calories = caloriesEditText.text.toString().toIntOrNull()
                val distance = distanceEditText.text.toString().toDoubleOrNull()

                if (duration == null || calories == null || distance == null) {
                    Toast.makeText(this, "Please fill all fields correctly", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val calendar = Calendar.getInstance()
                calendar.set(datePicker.year, datePicker.month, datePicker.dayOfMonth)
                val date = calendar.time

                val workout = Workout(type, duration, calories, distance, date)
                workouts.add(workout)
                adapter.notifyItemInserted(workouts.size - 1)
                updateStats()
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun showWorkoutDetails(workout: Workout) {
        val workoutDetails = "Check out my workout!\n" +
                "Type: ${workout.type}\n" +
                "Duration: ${workout.duration} min\n" +
                "Calories: ${workout.calories}\n" +
                "Distance: ${workout.distance} km\n" +
                "Date: ${android.text.format.DateFormat.getDateFormat(this).format(workout.date)}"

        AlertDialog.Builder(this)
            .setTitle(workout.type)
            .setMessage(workoutDetails)
            .setPositiveButton("Close", null)
            .setNeutralButton("Share") { _, _ ->
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, workoutDetails)
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
            }
            .show()
    }
}