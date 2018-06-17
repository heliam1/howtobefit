package com.heliam1.HowToBeFit.ui.ExerciseSets;

import android.util.Log;

import com.heliam1.HowToBeFit.models.ExerciseSet;
import com.heliam1.HowToBeFit.models.StartTimeExerciseSetListPreviousExerciseSet;
import com.heliam1.HowToBeFit.models.PreviousExerciseSet;
import com.heliam1.HowToBeFit.repositories.ExerciseSetRepository;
import com.heliam1.HowToBeFit.repositories.TimersRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class ExerciseSetsPresenter {
    private ExerciseSetsView mView;
    private ExerciseSetRepository mExerciseSetsRepository;
    private TimersRepository mTimersRepository;
    private final Scheduler mainScheduler;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public ExerciseSetsPresenter(ExerciseSetsView view,
                                 TimersRepository timersRepository,
                                 ExerciseSetRepository exerciseSetsRepository,
                                 Scheduler mainScheduler) {
        mView = view;
        mTimersRepository = timersRepository;
        mExerciseSetsRepository = exerciseSetsRepository;
        this.mainScheduler = mainScheduler;

        if (timersRepository.isStarted()) {
            startTimers();
        }
    }

    public void loadExerciseSets(long id, long date) {
        compositeDisposable.add(mExerciseSetsRepository.getExerciseSetsByWorkoutIdandPreviousSets(id, date)
                .subscribeOn(Schedulers.io())
                .observeOn(mainScheduler)
                .subscribeWith(new DisposableSingleObserver<List<StartTimeExerciseSetListPreviousExerciseSet>>() {
                    @Override
                    public void onSuccess(List<StartTimeExerciseSetListPreviousExerciseSet> list) {
                        if (list.isEmpty())
                            // mView.displayNoExerciseSets();
                            mView.displayExerciseSets(mExerciseSetsRepository.getListStartExsetListprevset());
                        else {
                            mView.displayExerciseSets(mExerciseSetsRepository.getListStartExsetListprevset());
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

    public void addExerciseSet(StartTimeExerciseSetListPreviousExerciseSet currentElement,
                               long workoutId, String exerciseName, String setNumber,
                               String setOrder, String setDurationMinutes, String setDurationSeconds,
                               String setRestMinutes, String setRestSeconds, String pbWeight,
                               String pbReps) {
        // input val
        try {
            if (exerciseName == null || exerciseName.equals(""))
                throw new Exception("bad name");

            if (setOrder.equals(""))
                setOrder = Integer.toString(getList().size() + 1);
            else {
                if (Integer.parseInt(setNumber) < 1)
                    throw new Exception("bad set number");
                if (Integer.parseInt(setOrder) > mExerciseSetsRepository.getListStartExsetListprevset().size())
                    setOrder = Integer.toString(mExerciseSetsRepository.getListStartExsetListprevset().size() + 1);
            }

            if (!setDurationMinutes.equals("")) {
                if (Integer.parseInt(setDurationMinutes) < 0)
                    throw new Exception("bad duration minutes");
            } else {
                setDurationMinutes = "0";
            }

            if (!setDurationSeconds.equals("")) {
                if (Integer.parseInt(setDurationSeconds) < 0)
                    throw new Exception("bad duration seconds");
            } else {
                setDurationSeconds = "0";
            }

            if (!setRestMinutes.equals("")) {
                if (Integer.parseInt(setRestMinutes) < 0)
                    throw new Exception("bad rest minutes");
            } else {
                setRestMinutes = "0";
            }

            if (!setRestSeconds.equals("")) {
                if (Integer.parseInt(setRestSeconds) < 0)
                    throw new Exception("bad rest seconds");
            } else {
                setRestSeconds = "0";
            }

            if (!pbWeight.equals("")) {
                if (Double.parseDouble(pbWeight) < 0)
                    throw new Exception("bad pb weight");
            } else {
                pbWeight = "-1";
            }
            if (!pbReps.equals("")){
                if (Integer.parseInt(pbReps) < 0)
                    throw new Exception("bad pb reps");
            } else {
                pbReps = "-1";
            }

        } catch (Exception e) {
            mView.displayToast("An exercise requires a name and set number (#*)");
            return;
        }

        long durationSeconds = Long.parseLong(setDurationSeconds) + 60 * Long.parseLong(setDurationMinutes);
        long restSeconds     = Long.parseLong(setRestSeconds) + 60 * Long.parseLong(setRestMinutes);

        if (currentElement == null) {
            ExerciseSet exerciseSet = new ExerciseSet(workoutId, exerciseName,
                    Integer.parseInt(setNumber), durationSeconds,
                    restSeconds, Integer.parseInt(setOrder), Double.parseDouble(pbWeight),
                    Integer.parseInt(pbReps), -1, -1);
            List<PreviousExerciseSet> emptyPrevSetList = new ArrayList<>();
            mExerciseSetsRepository.addStartExsetListprevset(new StartTimeExerciseSetListPreviousExerciseSet(exerciseSet, emptyPrevSetList));
            mView.displayToast("Exercise set added");
        } else {
            StartTimeExerciseSetListPreviousExerciseSet newElement = currentElement;
            newElement.getExerciseSet().setExerciseName(exerciseName);
            newElement.getExerciseSet().setSetNumber(Integer.parseInt(setNumber));
            newElement.getExerciseSet().setPbWeight(Double.parseDouble(pbWeight));
            newElement.getExerciseSet().setPbReps(Integer.parseInt(pbReps));
            mExerciseSetsRepository.replaceStartExsetListprevset(currentElement, newElement);
            mView.displayToast("Exercise set saved");
        }

        mView.displayExerciseSets(mExerciseSetsRepository.getListStartExsetListprevset());
        Log.v("ExerciseSetsPresenter", mExerciseSetsRepository.getListStartExsetListprevset().toString());
    }

    public void swapExerciseSetUp(StartTimeExerciseSetListPreviousExerciseSet startExsetListprev) {
        // edge case, can't move it up if it's the top/first set
        if (startExsetListprev.getExerciseSet().getSetOrder() > 1) {
            /*try {
                startExsetListprev.getExerciseSet().setSetWeight(
                        Double.parseDouble(mView.getSetWeightString(startExsetListprev.getExerciseSet().getSetOrder() - 1)));
            } catch (Exception e) { }
            try {
                startExsetListprev.getExerciseSet().setSetReps(
                        Integer.parseInt(mView.getSetRepsString(startExsetListprev.getExerciseSet().getSetOrder() - 1)));
            } catch (Exception e) { } */
            saveExerciseSetsToRepository();
            mExerciseSetsRepository.swapExerciseSetUp(startExsetListprev);
            /*mView.displaySwappedSet(startExsetListprev,
                    mExerciseSetsRepository.getListStartExsetListprevset().get(startExsetListprev.getExerciseSet().getSetOrder() - 1 + 1)); */
            mView.displayExerciseSets(mExerciseSetsRepository.getListStartExsetListprevset());
        }
    }

    public void swapExerciseSetDown(StartTimeExerciseSetListPreviousExerciseSet startExsetListprev) {
        // edge case, can't move it down if it's the bottom set
        if (startExsetListprev.getExerciseSet().getSetOrder() <= getList().size()) {
            /*
            try {
                startExsetListprev.getExerciseSet().setSetWeight(
                        Double.parseDouble(mView.getSetWeightString(startExsetListprev.getExerciseSet().getSetOrder() - 1)));
            } catch (Exception e) { }
            try {
                startExsetListprev.getExerciseSet().setSetReps(
                        Integer.parseInt(mView.getSetRepsString(startExsetListprev.getExerciseSet().getSetOrder() - 1)));
            } catch (Exception e) { } */
            saveExerciseSetsToRepository();
            mExerciseSetsRepository.swapExerciseSetDown(startExsetListprev);
            /*mView.displaySwappedSet(
                    mExerciseSetsRepository.getListStartExsetListprevset().get(startExsetListprev.getExerciseSet().getSetOrder() - 1 - 1),
                    startExsetListprev);*/
            mView.displayExerciseSets(mExerciseSetsRepository.getListStartExsetListprevset());
        }
    }

    public void deleteExerciseSet(StartTimeExerciseSetListPreviousExerciseSet element) {
        if (element != null) {
            mExerciseSetsRepository.removeStartExsetListprevset(element);
            mView.displayToast("Exercise set deleted");
        } else {
            mView.displayToast("Cannot delete non existent exercise set");
        }
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

    public void saveExerciseSetsToRepository() {
        List<String> setWeights = mView.getSetWeightStrings();
        List<String> setReps = mView.getSetRepsStrings();

        for (int i = 0; i < setWeights.size(); i++) {
            try {
                mExerciseSetsRepository.getListStartExsetListprevset().get(i).getExerciseSet().setSetWeight(
                        Double.parseDouble(setWeights.get(i)));
            } catch (Exception e) {
                mExerciseSetsRepository.getListStartExsetListprevset().get(i).getExerciseSet().setSetWeight(-1);
            }
                // mView.displayToast("Incorrect weight/reps for " +
                //        mExerciseSetsRepository.getListStartExsetListprevset().get(i).getExerciseSet().getExerciseName());
            try {
                mExerciseSetsRepository.getListStartExsetListprevset().get(i).getExerciseSet().setSetReps(
                        Integer.parseInt(setReps.get(i)));
            } catch (Exception e) {
                mExerciseSetsRepository.getListStartExsetListprevset().get(i).getExerciseSet().setSetReps(-1);
            }
        }
    }

    // ALSO SAVES WORKOUT
    public void saveExerciseSets(long id) {
        long time = Calendar.getInstance().getTimeInMillis();

        try { saveExerciseSetsToRepository(); }
        catch (Exception e) { mView.displayToast("Error saving exercise sets"); return; }

        compositeDisposable.add(mExerciseSetsRepository.saveExerciseSets(time)
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

        compositeDisposable.add(mExerciseSetsRepository.updateWorkout(id, time)
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
        if (!mTimersRepository.isStarted()) {
            mTimersRepository.startTimers();
            // Every second
            int POLL_INTERVAL = 1;

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
                            // Actual Time Elapsed

                            Date timeDate = new Date(mTimersRepository.calculateActualTime());
                            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
                            String incorrectString = sdf.format(timeDate);
                            // incorrect string displays 10:XX:XX, should display 00:XX:XX
                            String timeString = "0" + incorrectString.charAt(1)
                                    + incorrectString.charAt(2)  + incorrectString.charAt(3)
                                    + incorrectString.charAt(4)  + incorrectString.charAt(5)
                                    + incorrectString.charAt(6)  + incorrectString.charAt(7);
                            // Display the time elapsed since starting
                            mView.displayActualElapsedTime(timeString);

                            // if the view is not focused
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

                                List<Long> startTimes = mExerciseSetsRepository.getStartTimes();

                                for (int i = 0; i < startTimes.size(); i++) {
                                    Log.v("ExerciseSetsPresenter notif", Long.toString(timeNotif) + Long.toString(startTimes.get(i)));
                                    if (timeNotif == startTimes.get(i)) {
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

    public void unsubscribe() {
        compositeDisposable.clear();
    }

    public StartTimeExerciseSetListPreviousExerciseSet getCurrentElement(int position) {
        return mExerciseSetsRepository.getListStartExsetListprevset().get(position);
    }

    public List<StartTimeExerciseSetListPreviousExerciseSet> getList() {
        return mExerciseSetsRepository.getListStartExsetListprevset();
    }
}
