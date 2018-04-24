package com.heliam1.HowToBeFit.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.heliam1.HowToBeFit.R;
import com.heliam1.HowToBeFit.di.HowToBeFitApplication;
import com.heliam1.HowToBeFit.models.ExerciseSet;
import com.heliam1.HowToBeFit.models.ExerciseSetAndListPreviousExerciseSet;
import com.heliam1.HowToBeFit.presenters.ExerciseSetsPresenter;
import com.heliam1.HowToBeFit.repositories.ExerciseSetRepository;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class ExerciseSetsActivity extends AppCompatActivity implements ExerciseSetsView {
    @Inject
    ExerciseSetRepository exerciseSetRepository;

    private ExerciseSetsPresenter mExerciseSetsPresenter;

    private ListView mExerciseSetsListView;
    private TextView mTimeElapsed;
    private TextView mActualTimeElapsed;
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_sets);

        ((HowToBeFitApplication) getApplication()).getAppComponent().inject(this);

        mExerciseSetsListView = findViewById(R.id.exerciseSetsListView);
        mTimeElapsed = findViewById(R.id.timeElapsed);
        mActualTimeElapsed = findViewById(R.id.actualTimeElapsed);

        long workoutId = getIntent().getLongExtra("workoutId", 0);
        if (workoutId == 0) {
            Log.e("ExerciseSetsActivity", "no workout");
        }

        mExerciseSetsPresenter = new ExerciseSetsPresenter(this, exerciseSetRepository, AndroidSchedulers.mainThread());
        mExerciseSetsPresenter.loadExerciseSets(workoutId);
    }

    @Override
    protected void onDestroy() {
        mExerciseSetsPresenter.unsubscribe();
        super.onDestroy();
    }

    @Override
    public void displayExerciseSets(List<ExerciseSetAndListPreviousExerciseSet> exerciseSetsAndTheirPreviousSets) {
        ExerciseSetListAdapter adapter = new ExerciseSetListAdapter(this, exerciseSetsAndTheirPreviousSets);
        mExerciseSetsListView.setAdapter(adapter);
    }

    @Override
    public void displayNoExerciseSets() {

    }

    @Override
    public void displayErrorLoadingExerciseSets() {

    }

    @Override
    public void displayToast(String message) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        mToast.show();
    }
}
