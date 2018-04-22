package com.heliam1.HowToBeFit.di;

import com.heliam1.HowToBeFit.ui.ExerciseSetsActivity;
import com.heliam1.HowToBeFit.ui.WorkoutsFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton @Component (modules = AppModule.class) // , DbModule.class}
public interface AppComponent {
    void inject(HowToBeFitApplication application);

    void inject(WorkoutsFragment workoutsFragment);

    void inject(ExerciseSetsActivity exerciseSetsActivity);
}

// these are the places you want to inject
// dagger will look in the modules for things to provide
// this is where you choose to inject, you have to write the places you want to inject into here
// and then write a line of code into them (see Hackathon Interface and MainActivity class)

