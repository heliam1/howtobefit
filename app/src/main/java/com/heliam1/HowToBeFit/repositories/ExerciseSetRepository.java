package com.heliam1.HowToBeFit.repositories;

import com.heliam1.HowToBeFit.models.ExerciseSet;
import com.heliam1.HowToBeFit.models.ExerciseSetAndListPreviousExerciseSet;
import com.heliam1.HowToBeFit.models.PreviousExerciseSet;

import java.util.List;

import io.reactivex.Single;

public interface ExerciseSetRepository {
    // public Single<List<ExerciseSet>> getExerciseSetsByWorkoutId(long id);

    Single<List<ExerciseSetAndListPreviousExerciseSet>> getExerciseSetsByWorkoutIdandPreviousSets(long id, long date);

    public Single<List<PreviousExerciseSet>> getPreviousSets(String name, int setNumber);

    public Single<Long> saveExerciseSets(List<ExerciseSet> exerciseSets);

    public Single<Long> deleteExerciseSet(ExerciseSet exerciseSet);

    public Single<Long> deleteWorkout(long id);

    public Single<Long> updateWorkout(long id, long time, long duration);


    // Single<List<Task>> searchTasks(String title);
}
