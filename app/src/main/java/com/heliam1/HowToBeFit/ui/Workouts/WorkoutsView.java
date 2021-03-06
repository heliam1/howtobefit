package com.heliam1.HowToBeFit.ui.Workouts;

import com.heliam1.HowToBeFit.models.Workout;

import java.util.List;

public interface WorkoutsView {
    void displayWorkouts(List<Workout> workoutList);

    void displayNoWorkouts();

    void displayErrorLoadingWorkouts();

    void displayErrorSavingWorkouts();

    void displaySuccessSavingWorkout();

    void displayErrorDeletingWorkout();

    void displaySuccessDeletingWorkout();

    void displayToast(String message);
}
