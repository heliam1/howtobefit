package com.heliam1.HowToBeFit.dagger;

import android.app.Application;

import com.heliam1.HowToBeFit.repositories.DatabaseRepository;
import com.heliam1.HowToBeFit.repositories.ExerciseSetRepository;
import com.heliam1.HowToBeFit.repositories.WorkoutRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {
    private final Application application;

    AppModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    WorkoutRepository providesWorkoutRepository() {
        return new DatabaseRepository(/*readable database or something*/);
    }

    @Provides
    @Singleton
    ExerciseSetRepository providesExerciseSetRepository() {
        return new DatabaseRepository(/*readable database or something*/);
    }
}

// 1 what do you want to inject
// 2 where do you want to inject
// 3 where will you construct it
