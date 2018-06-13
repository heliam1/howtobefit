package com.heliam1.HowToBeFit.models;

import java.util.List;

public class StartTimeExerciseSetListPreviousExerciseSet {
    private Long startTime;
    private ExerciseSet exerciseSet;
    private List<PreviousExerciseSet> previousExerciseSets;

    public StartTimeExerciseSetListPreviousExerciseSet(long startTime, ExerciseSet exerciseSet,
                                                       List<PreviousExerciseSet> previousExerciseSets) {
        this.startTime = startTime;
        this.exerciseSet = exerciseSet;
        this.previousExerciseSets = previousExerciseSets;
    }

    public StartTimeExerciseSetListPreviousExerciseSet(ExerciseSet exerciseSet,
                                                       List<PreviousExerciseSet> previousExerciseSets) {
        this.exerciseSet = exerciseSet;
        this.previousExerciseSets = previousExerciseSets;
    }

    public  StartTimeExerciseSetListPreviousExerciseSet() {
        // empty
    };

    public long getStartTime() { return startTime; }

    public void setStartTime(long time) { this.startTime = time; }

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
