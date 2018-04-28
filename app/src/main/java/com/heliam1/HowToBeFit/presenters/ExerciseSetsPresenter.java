package com.heliam1.HowToBeFit.presenters;

import android.util.Log;

import com.heliam1.HowToBeFit.models.ExerciseSet;
import com.heliam1.HowToBeFit.models.ExerciseSetAndListPreviousExerciseSet;
import com.heliam1.HowToBeFit.repositories.ExerciseSetRepository;
import com.heliam1.HowToBeFit.ui.ExerciseSetsView;

import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class ExerciseSetsPresenter {
    private ExerciseSetsView mView;
    private ExerciseSetRepository mExerciseSetsRepository;
    private final Scheduler mainScheduler;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    static boolean actualStarted = false;
    private List<ExerciseSetAndListPreviousExerciseSet> mExerciseSetsAndListPreviousExerciseSets = new ArrayList<>();
    private List<String> mStartTimes = new ArrayList<>();

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
                    public void onSuccess(List<ExerciseSetAndListPreviousExerciseSet> exerciseSetsAndPreviousList) {
                        mExerciseSetsAndListPreviousExerciseSets = exerciseSetsAndPreviousList;
                        if (exerciseSetsAndPreviousList.isEmpty()) {
                            mView.displayNoExerciseSets();
                        } else {
                            mView.displayExerciseSets(exerciseSetsAndPreviousList);
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

    public void setElapsedTime() {

    }

    public void startTimers() {
        // if (!actualStarted) {
            actualStarted = true;
            int POLL_INTERVAL = 1;

            long startTime = Calendar.getInstance().getTimeInMillis();

            // Every second
             Observable.interval(POLL_INTERVAL, TimeUnit.SECONDS)
                    .subscribeOn(Schedulers.newThread()) // TODO: use io? deffs not computation
                    .observeOn(mainScheduler)
                    .subscribe(new Observer<Long>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            compositeDisposable.add(d);
                        }

                        @Override
                        public void onNext(Long aLong) {
                            long currentTime = Calendar.getInstance().getTimeInMillis();

                            // Actual Time Elapsed

                            Date actualTimeElapsedDate = new Date(currentTime - startTime);
                            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
                            String incorrectString = sdf.format(actualTimeElapsedDate);
                            // incorrect string displays 10:XX:XX, should display 00:XX:XX
                            String timeString = "0" + incorrectString.charAt(1)
                                    + incorrectString.charAt(2)  + incorrectString.charAt(3)
                                    + incorrectString.charAt(4)  + incorrectString.charAt(5)
                                    + incorrectString.charAt(6)  + incorrectString.charAt(7);

                            // Display the time elapsed since starting
                            mView.displayActualElapsedTime(timeString);

                            if (!mView.isTimeElapsedFocused()) {

                                String timeElapsedString = mView.getTimeElapsed();

                                Date timeElapsed = actualTimeElapsedDate;
                                try {
                                    String timeString1 = "1" + timeElapsedString.charAt(1)
                                            + timeElapsedString.charAt(2) + timeElapsedString.charAt(3)
                                            + timeElapsedString.charAt(4) + timeElapsedString.charAt(5)
                                            + timeElapsedString.charAt(6) + timeElapsedString.charAt(7);
                                    timeElapsed = sdf.parse(timeString1);
                                } catch (Exception e) {
                                    Log.v("ExerciseSetsPresenter", "PARSE FAILED");
                                    mView.displayToast("Bad Parse");
                                }

                                Log.v("ExerciseSetsPresenter", "do we get here");
                                long timeElapsedLong = timeElapsed.getTime();
                                timeElapsedLong = timeElapsedLong + 1000;

                                String incorrectString2 = sdf.format(new Date(timeElapsedLong));
                                String timeString2 = "0" + incorrectString2.charAt(1)
                                        + incorrectString2.charAt(2) + incorrectString2.charAt(3)
                                        + incorrectString2.charAt(4) + incorrectString2.charAt(5)
                                        + incorrectString2.charAt(6) + incorrectString2.charAt(7);

                                Log.v("ExerciseSetsPresenter", timeString2);

                                mView.displayElapsedTime(timeString2);

                                // NOTIFICATION
                                // TODO: notification, and time until next notification

                                for (int i = 0; i < mStartTimes.size(); i++) {
                                    Log.v("ExerciseSetsPresenter", timeString2);
                                    Log.v("ExerciseSetsPresenter", mStartTimes.get(i));
                                    if (timeString2.equals("00:" + mStartTimes.get(i))) {
                                        Log.v("ExerciseSetsPresenter", "Sending notifcation");
                                        mView.displayToast("Sending notification");
                                    }
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        //}
    }

    public void setExerciseSetsAndListPreviousExerciseSets(List<ExerciseSetAndListPreviousExerciseSet>
                                                                   exerciseSetsAndListPreviousExerciseSets) {
        mExerciseSetsAndListPreviousExerciseSets = exerciseSetsAndListPreviousExerciseSets;
    }

    public List<ExerciseSetAndListPreviousExerciseSet> getExerciseSetsAndListPreviousExerciseSets() {
        return mExerciseSetsAndListPreviousExerciseSets;
    }

    public void setStartTimes() {
        for (int i = 0; i < mExerciseSetsAndListPreviousExerciseSets.size(); i++) {
            int setStart = calculateSetStart(i);
            mStartTimes.add(formatStartTime(setStart));
        }
    }

    public List<String> getStartTimes() {
        return mStartTimes;
    }

    public void unsubscribe() {
        compositeDisposable.clear();
    }


    private int calculateSetStart(int position) {
        int setStart = 0; // seconds

        int i = 0; //
        while (i < position) {
            ExerciseSetAndListPreviousExerciseSet exerciseSetAndListPreviousExerciseSet
                    = mExerciseSetsAndListPreviousExerciseSets.get(position);
            ExerciseSet exerciseSet = exerciseSetAndListPreviousExerciseSet.getExerciseSet();
            setStart = setStart + exerciseSet.getSetDuration() + exerciseSet.getSetRest();
            i++;
        }

        return setStart;
    }

    private String formatStartTime(int totalSeconds) {
        int minutes = (totalSeconds / 60);
        int seconds = totalSeconds % 60;

        String minutesString;
        if (minutes < 10) {
            minutesString = "0" + Integer.toString(minutes);
        } else {
            minutesString = Integer.toString(minutes);
        }

        String secondsString;
        if (seconds < 10) {
            secondsString = "0" + Integer.toString(seconds);
        } else {
            secondsString = Integer.toString(seconds);
        }

        return (minutesString + ":" + secondsString);
    }
}
