package com.heliam1.HowToBeFit.models;

public class Workout {
    private Long id;
    private String name;
    private int image;
    private long date;
    private long duration;

    public Workout(Long id, String name, int image, long date, long duration) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.date = date;
        this.duration = duration;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getImage() {
        return image;
    }

    public long getDate() {
        return date;
    }

    public long getDuration() {
        return duration;
    }
}
