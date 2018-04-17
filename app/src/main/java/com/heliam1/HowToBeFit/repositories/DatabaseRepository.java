package com.heliam1.HowToBeFit.repositories;

import android.database.Cursor;

import com.heliam1.HowToBeFit.models.Workout;

import java.util.List;

import io.reactivex.Single;

public class DatabaseRepository implements WorkoutRepository, ExerciseSetRepository {

    /*
    @Override
    public Single<List<Workout>> getWorkouts() {
        return Single.fromCallable(() -> {
            try {
                return
            } catch (Exception e) {
                throw new RuntimeException("Something wrong with db");
            }
        });
    } */
}
