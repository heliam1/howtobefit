package com.heliam1.HowToBeFit.repositories;

import com.heliam1.HowToBeFit.models.ExerciseSet;

import java.util.List;

import io.reactivex.Single;

public interface ExerciseSetRepository {
    public Single<List<ExerciseSet>> getExerciseSetsByWorkoutId(long id);

    public Single<Long> saveExerciseSet(ExerciseSet exerciseSet);

    public Single<Long> deleteExerciseSet(ExerciseSet exerciseSet);

    // Single<List<Task>> searchTasks(String title);
}
