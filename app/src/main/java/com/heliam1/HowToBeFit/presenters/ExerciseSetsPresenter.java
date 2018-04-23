package com.heliam1.HowToBeFit.presenters;

import com.heliam1.HowToBeFit.models.ExerciseSet;
import com.heliam1.HowToBeFit.repositories.ExerciseSetRepository;
import com.heliam1.HowToBeFit.ui.ExerciseSetsView;

import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;

public class ExerciseSetsPresenter {
    private ExerciseSetsView mView;
    private ExerciseSetRepository mExerciseSetsRepository;
    private final Scheduler mainScheduler;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public ExerciseSetsPresenter(ExerciseSetsView view, ExerciseSetRepository exerciseSetsRepository,
                                 Scheduler mainScheduler) {
        mView = view;
        mExerciseSetsRepository = exerciseSetsRepository;
        this.mainScheduler = mainScheduler;
    }

    public void loadExerciseSets() {

    }
}
