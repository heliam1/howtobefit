package com.heliam1.HowToBeFit.presenters;

import com.heliam1.HowToBeFit.models.ExerciseSet;
import com.heliam1.HowToBeFit.models.ExerciseSetAndListPreviousExerciseSet;
import com.heliam1.HowToBeFit.repositories.ExerciseSetRepository;
import com.heliam1.HowToBeFit.ui.ExerciseSetsView;

import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class ExerciseSetsPresenter {
    private ExerciseSetsView mView;
    private ExerciseSetRepository mExerciseSetsRepository;
    private final Scheduler mainScheduler;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    public ExerciseSetsPresenter(ExerciseSetsView view,
                                 ExerciseSetRepository exerciseSetsRepository,
                                 Scheduler mainScheduler) {
        mView = view;
        mExerciseSetsRepository = exerciseSetsRepository;
        this.mainScheduler = mainScheduler;
    }

    public void loadExerciseSets(long id) {
        compositeDisposable.add(mExerciseSetsRepository.getExerciseSetsByWorkoutIdandPreviousSets(id)
                .subscribeOn(Schedulers.io())
                .observeOn(mainScheduler)
                .subscribeWith(new DisposableSingleObserver<List<ExerciseSetAndListPreviousExerciseSet>>() {
                    @Override
                    public void onSuccess(List<ExerciseSetAndListPreviousExerciseSet> exerciseSetAndPreviousList) {
                        if (exerciseSetAndPreviousList.isEmpty()) {
                            mView.displayNoExerciseSets();
                        } else {
                            mView.displayExerciseSets(exerciseSetAndPreviousList);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.displayToast("Something wrong with db");
                        mView.displayErrorLoadingExerciseSets();
                    }
                })
        );


    }

    public void unsubscribe() {
        compositeDisposable.clear();
    }
}
