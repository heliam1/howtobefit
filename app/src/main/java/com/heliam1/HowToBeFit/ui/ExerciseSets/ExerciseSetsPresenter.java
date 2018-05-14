package com.heliam1.HowToBeFit.ui.ExerciseSets;

import android.util.Log;

import com.heliam1.HowToBeFit.models.ExerciseSet;
import com.heliam1.HowToBeFit.models.ExerciseSetAndListPreviousExerciseSet;
import com.heliam1.HowToBeFit.models.PreviousExerciseSet;
import com.heliam1.HowToBeFit.repositories.ExerciseSetRepository;
import com.heliam1.HowToBeFit.utils.NotificationUtils;

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
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class ExerciseSetsPresenter {
    private ExerciseSetsView mView;
    private ExerciseSetRepository mExerciseSetsRepository;
    private final Scheduler mainScheduler;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    long time;
    static boolean actualStarted = false;
    private List<ExerciseSetAndListPreviousExerciseSet> mExerciseSetsAndListPreviousExerciseSets = new ArrayList<>();
    private List<Long> mStartTimes = new ArrayList<Long>();

    public ExerciseSetsPresenter(ExerciseSetsView view,
                                 ExerciseSetRepository exerciseSetsRepository,
                                 Scheduler mainScheduler) {
        mView = view;
        mExerciseSetsRepository = exerciseSetsRepository;
        this.mainScheduler = mainScheduler;
    }

    public void setStartTimes() {
        mStartTimes.clear();
        for (int i = 0; i < mExerciseSetsAndListPreviousExerciseSets.size(); i++) {
            long setStart = calculateSetStart(i);
            mStartTimes.add(setStart);
        }
    }

    public void loadExerciseSets(long id, long date) {
        compositeDisposable.add(mExerciseSetsRepository.getExerciseSetsByWorkoutIdandPreviousSets(id, date)
                .subscribeOn(Schedulers.io())
                .observeOn(mainScheduler)
                .subscribeWith(new DisposableSingleObserver<List<ExerciseSetAndListPreviousExerciseSet>>() {
                    @Override
                    public void onSuccess(List<ExerciseSetAndListPreviousExerciseSet> exerciseSetsAndPreviousList) {
                        mExerciseSetsAndListPreviousExerciseSets = exerciseSetsAndPreviousList;
                        //if (exerciseSetsAndPreviousList.isEmpty()) {
                            //Log.v("ExerciseSetsPresenter", "empty list");
                            //mView.displayNoExerciseSets();
                        // } else {
                            setStartTimes();
                            mView.displayExerciseSets();
                        //}
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.displayToast("Something wrong with db");
                        mView.displayErrorLoadingExerciseSets();
                    }
                })
        );
    }

    public void addExerciseSet(long workoutId, String exerciseName, String setNumber,
                               String setDuration, String setRest, String pbWeight, String pbReps) {
        // input val
        try {
            if (exerciseName == null)
                throw new Exception("bad parse");
            if (Integer.parseInt(setNumber) < 1)
                throw new Exception("bad parse");
            if (Integer.parseInt(setDuration) < 1)
                throw new Exception("bad parse");
            if (Integer.parseInt(setRest) < 1)
                throw new Exception("bad parse");
            if (Double.parseDouble(pbWeight) < 0)
                throw new Exception("bad parse");
            if (Integer.parseInt(pbReps) < 0)
                throw new Exception("bad parse");
        } catch (Exception e) {
            mView.displayToast("Bad exercise-set");
            return;
        }

        int setOrder = mExerciseSetsAndListPreviousExerciseSets.size() + 1;
        ExerciseSet exerciseSet = new ExerciseSet(workoutId, exerciseName,
                Integer.parseInt(setNumber), Long.parseLong(setDuration),
                Long.parseLong(setRest), setOrder, Double.parseDouble(pbWeight),
                Integer.parseInt(pbReps));
        List<PreviousExerciseSet> emptyPrevSetList = new ArrayList<>();

        mExerciseSetsAndListPreviousExerciseSets.add(
                new ExerciseSetAndListPreviousExerciseSet(exerciseSet, emptyPrevSetList));

        setStartTimes();
        // mView.displayAddedSet(mExerciseSetsAndListPreviousExerciseSets.size() - 1);
        mView.displayAddedSet(mExerciseSetsAndListPreviousExerciseSets.size() - 1);
    }

    public void deleteWorkout(long id) {
        compositeDisposable.add(mExerciseSetsRepository.deleteWorkout(id)
                .subscribeOn(Schedulers.io())
                .observeOn(mainScheduler)
                .subscribeWith(new DisposableSingleObserver<Long>() {
                    @Override
                    public void onSuccess(Long id) {
                        mView.displayToast("Workout deleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.displayToast("Unable to delete workout");
                    }
                })
        );
    }

    // ALSO SAVES WORKOUT
    public void saveExerciseSets(long id) {
        long time = Calendar.getInstance().getTimeInMillis();

        List<ExerciseSet> exerciseSets = new ArrayList<>();
        for (int i = 0; i < mExerciseSetsAndListPreviousExerciseSets.size(); i++) {
            ExerciseSet exerciseSet = mExerciseSetsAndListPreviousExerciseSets.get(i).getExerciseSet();
            exerciseSet.setId(null);
            exerciseSet.setSetDate(time);
            exerciseSet.setSetOrder(i + 1);
            exerciseSets.add(exerciseSet);
        }

        compositeDisposable.add(mExerciseSetsRepository.saveExerciseSets(exerciseSets)
                .subscribeOn(Schedulers.io())
                .observeOn(mainScheduler)
                .subscribeWith(new DisposableSingleObserver<Long>() {
                    @Override
                    public void onSuccess(Long id) {
                        mView.displayToast("Exercise-Sets saved");
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.displayToast("Unable to save exercise sets");
                    }
                })
        );

        compositeDisposable.add(mExerciseSetsRepository.updateWorkout(id, time, calculateWorkoutDuration())
                .subscribeOn(Schedulers.io())
                .observeOn(mainScheduler)
                .subscribeWith(new DisposableSingleObserver<Long>() {
                    @Override
                    public void onSuccess(Long id) {
                        mView.displayToast("Workout saved");
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.displayToast("Unable to save workout");
                    }
                })
        );
    }

    public void startTimers() {
        if (!actualStarted) {
            actualStarted = true;
            int POLL_INTERVAL = 1;

            time = 0L;

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
                            time = time + 1000;

                            // Actual Time Elapsed

                            Date timeDate = new Date(time);
                            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
                            String incorrectString = sdf.format(timeDate);
                            // incorrect string displays 10:XX:XX, should display 00:XX:XX
                            String timeString = "0" + incorrectString.charAt(1)
                                    + incorrectString.charAt(2)  + incorrectString.charAt(3)
                                    + incorrectString.charAt(4)  + incorrectString.charAt(5)
                                    + incorrectString.charAt(6)  + incorrectString.charAt(7);
                            // Display the time elapsed since starting
                            mView.displayActualElapsedTime(timeString);

                            if (!mView.isTimeElapsedFocused()) {

                                String timeNotifString = mView.getTimeElapsed();
                                Date timeNotifDate = new Date();
                                try {
                                    String timeString1 = "1" +  timeNotifString.charAt(1)
                                            + timeNotifString.charAt(2) + timeNotifString.charAt(3)
                                            + timeNotifString.charAt(4) + timeNotifString.charAt(5)
                                            + timeNotifString.charAt(6) + timeNotifString.charAt(7);
                                    timeNotifDate = sdf.parse(timeString1);
                                } catch (Exception e) {
                                    Log.v("ExerciseSetsPresenter", "PARSE FAILED");
                                    mView.displayToast("Bad Parse");
                                }

                                Log.v("ExerciseSetsPresenter", "do we get here");
                                long timeNotif = timeNotifDate.getTime();
                                timeNotif = timeNotif + 1000;

                                String incorrectString2 = sdf.format(new Date(timeNotif));
                                String timeString2 = "0" + incorrectString2.charAt(1)
                                        + incorrectString2.charAt(2) + incorrectString2.charAt(3)
                                        + incorrectString2.charAt(4) + incorrectString2.charAt(5)
                                        + incorrectString2.charAt(6) + incorrectString2.charAt(7);
                                Log.v("ExerciseSetsPresenter string to display", timeString2);
                                mView.displayElapsedTime(timeString2);

                                // NOTIFICATION
                                // TODO: notification, and time until next notification

                                for (int i = 0; i < mStartTimes.size(); i++) {
                                    Log.v("ExerciseSetsPresenter notif", Long.toString(timeNotif) + Long.toString(mStartTimes.get(i)));
                                    if (timeNotif == mStartTimes.get(i)) {
                                        Log.v("ExerciseSetsPresenter", "Sending notification");
                                        mView.displayToast("Sending notification");
                                        // call to view -> activity to start service
                                        // since no service, just call to notif utils directly from view
                                        // instead of sync/SetReminderServiceIntent and sync/NotificationTasks then notif utils
                                        mView.notifyStartNextSet("no action");
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
        }
    }

    public void setExerciseSetsAndListPreviousExerciseSets(List<ExerciseSetAndListPreviousExerciseSet>
                                                                   exerciseSetsAndListPreviousExerciseSets) {
        mExerciseSetsAndListPreviousExerciseSets = exerciseSetsAndListPreviousExerciseSets;
    }

    public List<ExerciseSetAndListPreviousExerciseSet> getExerciseSetsAndListPreviousExerciseSets() {
        return mExerciseSetsAndListPreviousExerciseSets;
    }

    public List<Long> getStartTimes() {
        return mStartTimes;
    }

    public void unsubscribe() {
        compositeDisposable.clear();
    }


    private long calculateSetStart(int position) {
        long setStart = 0; // milliseconds

        if (position == 0)
            return setStart;

        for (int i = 0; i < position; i++) {
            ExerciseSet exerciseSet = mExerciseSetsAndListPreviousExerciseSets.get(i).getExerciseSet();
            setStart = setStart + exerciseSet.getSetDuration() + exerciseSet.getSetRest();
        }

        return setStart;
    }

    private long calculateWorkoutDuration() {
        long setEnd = 0;

        int i = 0;
        while (i < mExerciseSetsAndListPreviousExerciseSets.size()) {
            ExerciseSet exerciseSet = mExerciseSetsAndListPreviousExerciseSets.get(i).getExerciseSet();
            setEnd = exerciseSet.getSetDuration() + exerciseSet.getSetRest();
            i++;
        }

        if (setEnd < 1800000)
            setEnd = 1800000;
        return setEnd;
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
