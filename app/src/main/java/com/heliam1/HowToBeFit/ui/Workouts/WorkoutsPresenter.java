package com.heliam1.HowToBeFit.ui.Workouts;

import com.heliam1.HowToBeFit.models.Workout;
import com.heliam1.HowToBeFit.repositories.WorkoutRepository;

import java.time.format.DateTimeFormatterBuilder;
import java.util.Calendar;
import java.util.Date;
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
                        mView.displayToast("Something wrong with db");
                        mView.displayErrorLoadingWorkouts();
                    }
                })
        );
    }

    public void saveWorkout(String workoutName) {
        // input val
        try {
            if (workoutName == null)
                throw new Exception("bad parse");
        } catch (Exception e) {
            mView.displayToast("Bad workout");
            return;
        }

        compositeDisposable.add(mWorkoutRepository.saveWorkout(
                new Workout(null, workoutName, 0, Calendar.getInstance().getTimeInMillis(), 30))
                .subscribeOn(Schedulers.io())
                .observeOn(mainScheduler)
                .subscribeWith(new DisposableSingleObserver<Long>() {
                    @Override
                    public void onSuccess(Long idOfSavedWorkout) {
                        if (idOfSavedWorkout == null) {
                            // TODO: this should be in on error only?
                            mView.displayErrorSavingWorkouts();
                        } else {
                            mView.displaySuccessSavingWorkout();
                        }
                        loadWorkouts();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.displayErrorSavingWorkouts();
                    }
                })
        );
    }

    /*
    public void deleteWorkout(Workout workout) {
        compositeDisposable.add(mWorkoutRepository.deleteWorkout(workout)
            .subscribeOn(Schedulers.io())
            .observeOn(mainScheduler)
            .subscribeWith(new DisposableSingleObserver<Long>() {
                @Override
                public void onSuccess(Long idOfDeletedWorkout) {
                    if (idOfDeletedWorkout == null) {
                        // TODO: this should be in on error only?
                        mView.displayErrorDeletingWorkout();
                    } else {
                        mView.displaySuccessDeletingWorkout();
                    }
                }

                @Override
                public void onError(Throwable e) {
                    mView.displayErrorDeletingWorkout();
                }
            })
        );
    }*/

    public void unsubscribe() {
        compositeDisposable.clear();
    }
}
