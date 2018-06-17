package com.heliam1.HowToBeFit.models;

public class ExerciseSet {

    private Long id;
    private long workoutId;
    private String exerciseName;
    private int setNumber;
    private long setDuration;
    private long setRest;
    private double setWeight;
    private int setReps;
    private String setDateString;
    private long setDateLong;
    private int setOrder;
    private double pbWeight;
    private int pbReps;

    // reading exercise set from db or updating
    public ExerciseSet(Long id, long workoutId, String exerciseName, int setNumber, long setDuration,
                       long setRest, double setWeight, int setReps, String setDateString,
                       long setDateLong, int setOrder, double pbWeight, int pbReps) {
        this.id = id;
        this.workoutId = workoutId;
        this.exerciseName = exerciseName;
        this.setNumber = setNumber;
        this.setDuration = setDuration;
        this.setRest = setRest;
        this.setWeight = setWeight;
        this.setReps = setReps;
        this.setDateString = setDateString;
        this.setDateLong = setDateLong;
        this.setWeight = setWeight;
        this.setOrder = setOrder;
        this.pbWeight = pbWeight;
        this.pbReps = pbReps;
    }

    // inserting / adding a new exercise set
    public ExerciseSet(long workoutId, String exerciseName, int setNumber, long setDuration,
                       long setRest, int setOrder, double pbWeight, int pbReps) {
        setDuration = setDuration * 1000;
        setRest = setRest * 1000;

        this.id = null;
        this.workoutId = workoutId;
        this.exerciseName = exerciseName;
        this.setNumber = setNumber;
        this.setDuration = setDuration;
        this.setRest = setRest;
        this.setDateString = "never"; // This should never be visible
        this.setDateLong = 0L;
        this.setOrder = setOrder;
        this.pbWeight = pbWeight;
        this.pbReps = pbReps;
    }

    public ExerciseSet(long workoutId, String exerciseName, int setNumber, long setDuration,
                       long setRest, int setOrder, double pbWeight, int pbReps, double weight, int reps) {
        setDuration = setDuration * 1000;
        setRest = setRest * 1000;

        this.id = null;
        this.workoutId = workoutId;
        this.exerciseName = exerciseName;
        this.setNumber = setNumber;
        this.setDuration = setDuration;
        this.setRest = setRest;
        this.setDateString = "never"; // This should never be visible
        this.setDateLong = 0L;
        this.setOrder = setOrder;
        this.pbWeight = pbWeight;
        this.pbReps = pbReps;
        this.setWeight = weight;
        this.setReps = reps;
    }

    public boolean hasId() {
        return (this.id != null);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getWorkoutId() {
        return workoutId;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public int getSetNumber() {
        return setNumber;
    }

    public void setSetNumber(int setNumber) {
        this.setNumber = setNumber;
    }

    public long getSetDuration() {
        return setDuration;
    }

    public void setSetDuration(int setDuration) {
        this.setDuration = setDuration;
    }

    public long getSetRest() {
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

    public String getSetDateString() {
        return setDateString;
    }

    public void setSetDateString(String setDateString) {
        this.setDateString = setDateString;
    }

    public long getSetDateLong() {
        return setDateLong;
    }

    public void setSetDate(long setDateLong) {
        this.setDateLong = setDateLong;
    }

    public double getPbWeight() {
        return pbWeight;
    }

    public void setPbWeight(double pbWeight) {
        this.pbWeight = pbWeight;
    }

    public int getPbReps() {
        return pbReps;
    }

    public void setPbReps(int pbReps) {
        this.pbReps = pbReps;
    }

    public int getSetOrder() {
        return setOrder;
    }

    public void setSetOrder(int order) { this.setOrder = order; }
}
