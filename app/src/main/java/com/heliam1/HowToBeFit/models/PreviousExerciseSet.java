package com.heliam1.HowToBeFit.models;

public class PreviousExerciseSet {
    private double setWeight;
    private int setReps;
    private String setDateString;
    private long setDateLong;

    public PreviousExerciseSet(double setWeight, int setReps, String setDateString, long setDateLong) {
        this.setWeight = setWeight;
        this.setReps = setReps;
        this.setDateString = setDateString;
        this.setDateLong = setDateLong;
    }

    public double getSetWeight() {
        return setWeight;
    }

    public void setSetWeight(double setWeight) {
        this.setWeight = setWeight;
    }

    public int getSetReps() {
        return setReps;
    }

    public void setSetReps(int setReps) {
        this.setReps = setReps;
    }

    public String getSetDateString() {
        return setDateString;
    }

    public void setSetDateString(String setDateString) {
        this.setDateString = setDateString;
    }

    public long getSetDateLong() {
        return setDateLong;
    }

    public void setSetDateLong(long setDateLong) {
        this.setDateLong = setDateLong;
    }
}
