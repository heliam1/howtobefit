package com.heliam1.HowToBeFit.models;

public class ExerciseSet {

    private long id;
    private long workoutId;
    private String workoutName;
    private int setNumber;
    private int setDuration;
    private int setRest;
    private double setWeight;
    private int setReps;
    private String setDate;
    private int setOrder;
    private int pbWeight;
    private int pbReps;

    public ExerciseSet(long id, long workoutId, String workoutName, int setNumber, int setDuration,
                       int setRest, double setWeight, int setReps, String setDate, int setOrder,
                       int pbWeight, int pbReps) {
        this.id = id;
        this.workoutId = workoutId;
        this.workoutName = workoutName;
        this.setNumber = setNumber;
        this.setDuration = setDuration;
        this.setRest = setRest;
        this.setWeight = setWeight;
        this.setReps = setReps;
        this.setDate = setDate;
        this.setWeight = setWeight;
        this.setOrder = setOrder;
        this.pbWeight = pbWeight;
        this.pbReps = pbReps;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getWorkoutName() {
        return workoutName;
    }

    public void setWorkoutName(String workoutName) {
        this.workoutName = workoutName;
    }

    public int getSetNumber() {
        return setNumber;
    }

    public void setSetNumber(int setNumber) {
        this.setNumber = setNumber;
    }

    public int getSetDuration() {
        return setDuration;
    }

    public void setSetDuration(int setDuration) {
        this.setDuration = setDuration;
    }

    public int getSetRest() {
        return setRest;
    }

    public void setSetRest(int setRest) {
        this.setRest = setRest;
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

    public String getSetDate() {
        return setDate;
    }

    public void setSetDate(String setDate) {
        this.setDate = setDate;
    }

    public int getPbWeight() {
        return pbWeight;
    }

    public void setPbWeight(int pbWeight) {
        this.pbWeight = pbWeight;
    }

    public int getPbReps() {
        return pbReps;
    }

    public void setPbReps(int pbReps) {
        this.pbReps = pbReps;
    }
}
