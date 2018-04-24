package com.heliam1.HowToBeFit.repositories;

import com.heliam1.HowToBeFit.models.ExerciseSet;
import com.heliam1.HowToBeFit.models.ExerciseSetAndListPreviousExerciseSet;
import com.heliam1.HowToBeFit.models.PreviousExerciseSet;

import java.util.List;

import io.reactivex.Single;

public interface ExerciseSetRepository {
    public Single<List<ExerciseSet>> getExerciseSetsByWorkoutId(long id);

    Single<List<ExerciseSetAndListPreviousExerciseSet>> getExerciseSetsByWorkoutIdandPreviousSets(long id);

    public Single<List<PreviousExerciseSet>> getPreviousSets(String name, int setNumber);

    public Single<Long> saveExerciseSet(ExerciseSet exerciseSet);

    public Single<Long> deleteExerciseSet(ExerciseSet exerciseSet);

    // Single<List<Task>> searchTasks(String title);
}
