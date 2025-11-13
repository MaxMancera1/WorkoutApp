package com.example.workoutapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.workoutapp.R
import com.example.workoutapp.model.Workout
import java.text.SimpleDateFormat
import java.util.Locale

class WorkoutAdapter(private val workouts: MutableList<Workout>) :
    RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder>() {

    var onItemClick: ((Workout) -> Unit)? = null
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_workout, parent, false)
        return WorkoutViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        val workout = workouts[position]
        holder.bind(workout)
    }

    override fun getItemCount(): Int = workouts.size

    inner class WorkoutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val workoutType: TextView = itemView.findViewById(R.id.workoutType)
        private val workoutDate: TextView = itemView.findViewById(R.id.workoutDate)
        private val workoutDuration: TextView = itemView.findViewById(R.id.workoutDuration)
        private val workoutCalories: TextView = itemView.findViewById(R.id.workoutCalories)
        private val workoutDistance: TextView = itemView.findViewById(R.id.workoutDistance)

        fun bind(workout: Workout) {
            workoutType.text = workout.type
            workoutDate.text = dateFormat.format(workout.date)
            workoutDuration.text = "Duration: ${workout.duration} min"
            workoutCalories.text = "Calories: ${workout.calories}"
            workoutDistance.text = "Distance: ${workout.distance} km"
            itemView.setOnClickListener {
                onItemClick?.invoke(workout)
            }
        }
    }
}
