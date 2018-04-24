package com.heliam1.HowToBeFit.models;

import java.util.List;

public class ExerciseSetAndListPreviousExerciseSet {
    private ExerciseSet exerciseSet;
    private List<PreviousExerciseSet> previousExerciseSets;

    public ExerciseSetAndListPreviousExerciseSet(ExerciseSet exerciseSet, List<PreviousExerciseSet> previousExerciseSets) {
        this.exerciseSet = exerciseSet;
        this.previousExerciseSets = previousExerciseSets;
    }

    public ExerciseSet getExerciseSet() {
        return exerciseSet;
    }

    public void setExerciseSet(ExerciseSet exerciseSet) {
        this.exerciseSet = exerciseSet;
    }

    public List<PreviousExerciseSet> getPreviousExerciseSets() {
        return previousExerciseSets;
    }

    public void setPreviousExerciseSets(List<PreviousExerciseSet> previousExerciseSets) {
        this.previousExerciseSets = previousExerciseSets;
    }
}
