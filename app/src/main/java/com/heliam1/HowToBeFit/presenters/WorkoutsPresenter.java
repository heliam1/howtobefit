package com.heliam1.HowToBeFit.presenters;

import com.heliam1.HowToBeFit.models.Workout;
import com.heliam1.HowToBeFit.repositories.WorkoutRepository;
import com.heliam1.HowToBeFit.ui.WorkoutsView;

import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class WorkoutsPresenter {
    private WorkoutsView mView;
    private WorkoutRepository mWorkoutRepository;
    private final Scheduler mainScheduler;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public WorkoutsPresenter(WorkoutsView view, WorkoutRepository workoutsRepository,
                             Scheduler mainScheduler) {
        mView = view;
        mWorkoutRepository = workoutsRepository;
        this.mainScheduler = mainScheduler;
    }

    public void loadWorkouts() {
        compositeDisposable.add(mWorkoutRepository.getWorkouts()
                .subscribeOn(Schedulers.io())
                .observeOn(mainScheduler)
                .subscribeWith(new DisposableSingleObserver<List<Workout>>() {
                    @Override
                    public void onSuccess(List<Workout> workoutList) {
                        if (workoutList.isEmpty()) {
                            mView.displayNoWorkouts();
                        } else {
                            mView.displayWorkouts(workoutList);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.displayError();
                    }
                })
        );
    }

    public void unsubscribe() {
        compositeDisposable.clear();
    }
}
