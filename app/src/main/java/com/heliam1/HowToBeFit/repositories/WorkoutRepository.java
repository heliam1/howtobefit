package com.heliam1.HowToBeFit.repositories;

import com.heliam1.HowToBeFit.models.Workout;

import java.util.List;

import io.reactivex.Single;

public interface WorkoutRepository {
    public Single<List<Workout>> getWorkouts();

    public Single<Long> saveWorkout(Workout workout);

    public Single<Long> deleteWorkout(Workout workout);

    // Single<List<Task>> searchTasks(String title);
}
