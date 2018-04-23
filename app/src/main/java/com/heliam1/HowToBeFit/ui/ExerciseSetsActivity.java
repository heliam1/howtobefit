package com.heliam1.HowToBeFit.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.heliam1.HowToBeFit.R;
import com.heliam1.HowToBeFit.di.HowToBeFitApplication;
import com.heliam1.HowToBeFit.presenters.ExerciseSetsPresenter;
import com.heliam1.HowToBeFit.repositories.ExerciseSetRepository;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class ExerciseSetsActivity extends AppCompatActivity implements ExerciseSetsView {
    @Inject
    ExerciseSetRepository exerciseSetRepository;

    private ExerciseSetsPresenter mExerciseSetsPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        ((HowToBeFitApplication) getApplication()).getAppComponent().inject(this);

        // mWorkoutsGridLayout = findViewById(R.id.workouts_gridview);
        // mColumn1 = findViewById(R.id.column1);
        // mColumn2 = findViewById(R.id.column2);

        mExerciseSetsPresenter= new ExerciseSetsPresenter(this, exerciseSetRepository, AndroidSchedulers.mainThread());
        mExerciseSetsPresenter.loadExerciseSets();
    }
}
