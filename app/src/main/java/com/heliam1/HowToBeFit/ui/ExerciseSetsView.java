package com.heliam1.HowToBeFit.ui;

import com.heliam1.HowToBeFit.models.ExerciseSet;
import com.heliam1.HowToBeFit.models.ExerciseSetAndListPreviousExerciseSet;

import java.util.List;

public interface ExerciseSetsView {

    public void displayExerciseSets(List<ExerciseSetAndListPreviousExerciseSet> exerciseSets);

    public void displayNoExerciseSets();

    public void displayErrorLoadingExerciseSets();

    public void displayToast(String message);
}
