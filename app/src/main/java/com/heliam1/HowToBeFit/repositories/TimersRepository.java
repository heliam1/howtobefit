package com.heliam1.HowToBeFit.repositories;

import android.util.Log;

import com.heliam1.HowToBeFit.models.ExerciseSet;
import com.heliam1.HowToBeFit.ui.ExerciseSets.ExerciseSetsView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class TimersRepository {
    private static boolean started = false;
    private static long originalTime;
    private static long elapsedTime = 0L;

    public TimersRepository() {}

    public boolean isStarted() {
        return started;
    }

    public void startTimers() {
        if (started != true) {
            originalTime = Calendar.getInstance().getTimeInMillis();
            started = true;
        }
    }

    public long calculateActualTime() {
        return Calendar.getInstance().getTimeInMillis() - originalTime;
    }
}
