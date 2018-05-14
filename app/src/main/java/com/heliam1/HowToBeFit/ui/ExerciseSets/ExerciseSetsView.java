package com.heliam1.HowToBeFit.ui.ExerciseSets;

import com.heliam1.HowToBeFit.models.ExerciseSet;
import com.heliam1.HowToBeFit.models.ExerciseSetAndListPreviousExerciseSet;

import java.util.List;

public interface ExerciseSetsView {

    public void displayExerciseSets();

    public void displayAddedSet(int position);

    public void displayNoExerciseSets();

    public void displayErrorLoadingExerciseSets();

    public void displayActualElapsedTime(String time);

    public void displayElapsedTime(String time);

    public String getTimeElapsed();

    public boolean isTimeElapsedFocused();

    public void notifyStartNextSet(String action);

    public void displayToast(String message);
}
