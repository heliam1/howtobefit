package com.heliam1.HowToBeFit.models;

public class Workout {
    private Long id;
    private String name;
    private int image;
    private String date;
    private int duration;

    public Workout(long id, String name, int image, String date, int duration) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.date = date;
        this.duration = duration;
    }

    public boolean hasId() {
        return (this.id != null);
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

    public String getDate() {
        return date;
    }

    public int getDuration() {
        return duration;
    }
}
