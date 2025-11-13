package com.example.workoutapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
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
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var addWorkoutButton: MaterialButton
    private lateinit var totalWorkouts: TextView
    private lateinit var totalCalories: TextView
    private lateinit var avgDuration: TextView

    private val workouts = mutableListOf<Workout>()
    private lateinit var adapter: WorkoutAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        addWorkoutButton = findViewById(R.id.addWorkoutButton)
        totalWorkouts = findViewById(R.id.totalWorkouts)
        totalCalories = findViewById(R.id.totalCalories)
        avgDuration = findViewById(R.id.avgDuration)

        setupRecyclerView()
        setupAddWorkoutButton()
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

    private fun setupAddWorkoutButton() {
        addWorkoutButton.setOnClickListener {
            showAddWorkoutDialog()
        }
    }

    private fun updateStats() {
        val workoutCount = workouts.size
        val totalCaloriesValue = workouts.sumOf { it.calories }
        val avgDurationValue = if (workouts.isNotEmpty()) workouts.map { it.duration }.average() else 0.0

        totalWorkouts.text = workoutCount.toString()
        totalCalories.text = getString(R.string.total_calories_format, totalCaloriesValue)
        avgDuration.text = getString(R.string.avg_duration_format, avgDurationValue.toInt())
    }

    private fun showAddWorkoutDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_workout, null)
        val spinner: Spinner = dialogView.findViewById(R.id.workoutTypeSpinner)
        val durationEditText: EditText = dialogView.findViewById(R.id.durationEditText)
        val caloriesEditText: EditText = dialogView.findViewById(R.id.caloriesEditText)
        val distanceEditText: EditText = dialogView.findViewById(R.id.distanceEditText)
        val datePicker: DatePicker = dialogView.findViewById(R.id.datePicker)

        // Set hints from string resources
        durationEditText.hint = getString(R.string.dialog_duration_hint)
        caloriesEditText.hint = getString(R.string.dialog_calories_hint)
        distanceEditText.hint = getString(R.string.dialog_distance_hint)

        val workoutTypes = resources.getStringArray(R.array.workout_types)
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, workoutTypes)
        spinner.adapter = spinnerAdapter

        val dialog = AlertDialog.Builder(this)
            .setTitle(R.string.dialog_add_workout_title)
            .setView(dialogView)
            .setPositiveButton(R.string.dialog_add_button, null)
            .setNegativeButton(R.string.dialog_cancel_button, null)
            .create()

        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {
                val type = spinner.selectedItem.toString()
                val duration = durationEditText.text.toString().toIntOrNull()
                val calories = caloriesEditText.text.toString().toIntOrNull()
                val distance = distanceEditText.text.toString().toDoubleOrNull()

                if (duration == null || calories == null || distance == null) {
                    Toast.makeText(this, R.string.fill_fields_prompt, Toast.LENGTH_SHORT).show()
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
        val formattedDate = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(workout.date)

        val workoutDetails = """
            ${getString(R.string.share_message_type, workout.type)}
            ${getString(R.string.share_message_duration, workout.duration)}
            ${getString(R.string.share_message_calories, workout.calories)}
            ${getString(R.string.share_message_distance, workout.distance)}
            ${getString(R.string.share_message_date, formattedDate)}
        """.trimIndent()

        val shareMessage = "${getString(R.string.share_message_title)}\n\n$workoutDetails"

        AlertDialog.Builder(this)
            .setTitle(workout.type)
            .setMessage(workoutDetails)
            .setPositiveButton(R.string.share_dialog_close, null)
            .setNeutralButton(R.string.share_dialog_title) { _, _ ->
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, shareMessage)
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
            }
            .show()
    }
}