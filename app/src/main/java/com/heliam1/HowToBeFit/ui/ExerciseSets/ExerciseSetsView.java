package com.heliam1.HowToBeFit.ui.ExerciseSets;

import com.heliam1.HowToBeFit.models.StartTimeExerciseSetListPreviousExerciseSet;

import java.util.List;

public interface ExerciseSetsView {

    public void displayExerciseSets(List<StartTimeExerciseSetListPreviousExerciseSet> list);

    public void displayAddedSet(int position);

    public void displaySwappedSet(StartTimeExerciseSetListPreviousExerciseSet setTop, StartTimeExerciseSetListPreviousExerciseSet setBot);

    public void displayNoExerciseSets();

    public void displayErrorLoadingExerciseSets();

    public void displayActualElapsedTime(String time);

    public void displayElapsedTime(String time);

    public String getTimeElapsed();

    public boolean isTimeElapsedFocused();

    public void notifyStartNextSet(String action);

    public void displayToast(String message);

    public void minimiseExerciseSet(StartTimeExerciseSetListPreviousExerciseSet element, boolean deleted);

    public String getSetWeightString(int i);
    public String getSetRepsString(int i);

    public List<String> getSetWeightStrings();
    public List<String> getSetRepsStrings();

    public void finishActivity();
}
