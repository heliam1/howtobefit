package com.heliam1.HowToBeFit.repositories;

import com.heliam1.HowToBeFit.models.ExerciseSet;

import java.util.List;

import io.reactivex.Single;

public interface ExerciseSetRepository {
    public Single<List<ExerciseSet>> getExerciseSetsById(long id);

    // Single<List<Task>> searchTasks(String title);
}
