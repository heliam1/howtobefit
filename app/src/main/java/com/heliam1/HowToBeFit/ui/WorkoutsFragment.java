package com.heliam1.HowToBeFit.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.heliam1.HowToBeFit.R;
import com.heliam1.HowToBeFit.models.Workout;
import com.heliam1.HowToBeFit.presenters.WorkoutsPresenter;
import com.heliam1.HowToBeFit.repositories.WorkoutRepository;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class WorkoutsFragment extends Fragment implements WorkoutsView {
    @Inject
    WorkoutRepository workoutRepository;

    WorkoutsPresenter mWorkoutPresenter;

    GridView mWorkoutsGridview;

    List<Workout> workouts;

    public WorkoutsFragment() {};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_workouts, container,
                false);

        GridView mWorkoutsGridview = (GridView) rootView.findViewById(R.id.workouts_gridview);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        mWorkoutPresenter = new WorkoutsPresenter(this, workoutRepository, AndroidSchedulers.mainThread());
        mWorkoutPresenter.loadWorkouts();

    }

    @Override
    public void displayWorkouts(List<Workout> workouts) {
        this.workouts = workouts;

        WorkoutListAdapter workoutListAdapter = new WorkoutListAdapter(getContext(), this.workouts);
        mWorkoutsGridview.setAdapter(workoutListAdapter);
    }

    public void displayNoWorkouts() {

    }

    public void displayErrorLoadingWorkouts() {

    }

    public void displayErrorSavingWorkouts() {

    }

    public void displaySuccessSavingWorkout() {

    }

    public void displayErrorDeletingWorkout() {

    }

    public void displaySuccessDeletingWorkout() {

    }
}
