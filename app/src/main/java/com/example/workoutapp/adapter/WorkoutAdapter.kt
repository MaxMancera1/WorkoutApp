package com.example.workoutapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.workoutapp.R
import com.example.workoutapp.model.Workout
import java.text.SimpleDateFormat
import java.util.Locale

class WorkoutAdapter(private val workouts: MutableList<Workout>) :
    RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder>() {

    var onItemClick: ((Workout) -> Unit)? = null
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

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
        private val workoutIcon: ImageView = itemView.findViewById(R.id.workoutIcon)
        private val workoutType: TextView = itemView.findViewById(R.id.workoutType)
        private val workoutDate: TextView = itemView.findViewById(R.id.workoutDate)
        private val workoutDuration: TextView = itemView.findViewById(R.id.workoutDuration)
        private val workoutCalories: TextView = itemView.findViewById(R.id.workoutCalories)
        private val workoutDistance: TextView = itemView.findViewById(R.id.workoutDistance)

        fun bind(workout: Workout) {
            workoutType.text = workout.type
            workoutDate.text = dateFormat.format(workout.date)
            workoutDuration.text = "${workout.duration} min"
            workoutCalories.text = "${workout.calories} kcal"
            workoutDistance.text = "${workout.distance} km"

            val iconRes = when (workout.type) {
                "Running" -> R.drawable.ic_running
                "Cycling" -> R.drawable.ic_cycling
                "Swimming" -> R.drawable.ic_swimming
                "Yoga" -> R.drawable.ic_yoga
                "Weight Training" -> R.drawable.ic_weight_training
                else -> R.drawable.ic_running // A default icon
            }
            workoutIcon.setImageResource(iconRes)

            itemView.setOnClickListener {
                onItemClick?.invoke(workout)
            }
        }
    }
}
