package com.heliam1.HowToBeFit.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.heliam1.HowToBeFit.R;
import com.heliam1.HowToBeFit.di.HowToBeFitApplication;
import com.heliam1.HowToBeFit.models.Workout;
import com.heliam1.HowToBeFit.presenters.WorkoutsPresenter;
import com.heliam1.HowToBeFit.repositories.WorkoutRepository;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class MainActivity extends AppCompatActivity implements WorkoutsView {
    private static final String LOG_TAG = "MainActivity";

    // Track whether to display a two-pane or single-pane UI
    // A single-pane display refers to phone screens, and two-pane to larger tablet screens
    private boolean mTwoPane;

    @Inject
    WorkoutRepository workoutRepository;

    private WorkoutsPresenter mWorkoutPresenter;

    private static final int VERTICAL_DIMENSION_MULTIPLIER = 8;
    private ActionBar mActionBar;
    private ConstraintLayout mAddWorkout;
    private Button mSaveWorkout;
    private LinearLayout mWorkoutsGridLayout;
    private LinearLayout mColumn1;
    private LinearLayout mColumn2;
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((HowToBeFitApplication) getApplication()).getAppComponent().inject(this);

        setContentView(R.layout.activity_main);

        mActionBar = getSupportActionBar();
        mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));

        mAddWorkout = findViewById(R.id.add_workout_constraint_layout);
        mSaveWorkout = findViewById(R.id.save_workout);
        mWorkoutsGridLayout = findViewById(R.id.workouts_gridview);
        mColumn1 = findViewById(R.id.column1);
        mColumn2 = findViewById(R.id.column2);

        mAddWorkout.setVisibility(View.GONE);

        mWorkoutPresenter = new WorkoutsPresenter(this, workoutRepository, AndroidSchedulers.mainThread());
        mWorkoutPresenter.loadWorkouts();

        mSaveWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // mWorkoutsPresenter.saveWorkout()
                mAddWorkout.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_custom_workout:
                displayCreateWorkout();
                return true;
            // TODO:
            /*
            case R.id.search_task:
                return true;
            */
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        mWorkoutPresenter.unsubscribe();
        super.onDestroy();
    }

    private void displayCreateWorkout() {
        mAddWorkout.setVisibility(View.VISIBLE);
    }

    @Override
    public void displayWorkouts(List<Workout> workouts) {

        // mWorkoutsGridview.setEmptyView(findViewById(R.id.empty_view));

        // gonna have to programatically add children
        boolean column1Turn = true;

        for(int i = 0; i < workouts.size(); i++) {
            View workoutView = LayoutInflater.from(this).inflate(R.layout.item_workout, mWorkoutsGridLayout, false);

            Workout workout = workouts.get(i);

            ImageView workoutImageView = workoutView.findViewById(R.id.image_view_image_workout);
            EditText workoutNameView = workoutView.findViewById(R.id.edit_text_name_workout);
            TextView workoutDateView = workoutView.findViewById(R.id.text_view_date_workout);

            workoutImageView.requestLayout();
            workoutImageView.getLayoutParams().height =
                    (int) (VERTICAL_DIMENSION_MULTIPLIER * workout.getDuration());
            workoutImageView.setMinimumHeight(VERTICAL_DIMENSION_MULTIPLIER * workout.getDuration());
            // workoutImageView.setImage( // TODO:
            workoutImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, ExerciseSetsActivity.class);

                    intent.putExtra("workoutId", workout.getId());
                    intent.putExtra("workoutName",workout.getName());

                    startActivity(intent);
                }
            });


            workoutNameView.setText(workout.getName());
            workoutDateView.setText(workout.getDate());

            if (column1Turn) {
                mColumn1.addView(workoutView);
                column1Turn = false;
            } else {
                mColumn2.addView(workoutView);
                column1Turn = true;
            }

            Log.v(LOG_TAG, "child added to gridlayout");
        }
    }

    @Override
    public void displayNoWorkouts() {
        mWorkoutsGridLayout.setVisibility(View.GONE);
    }

    @Override
    public void displayErrorLoadingWorkouts() {
        mWorkoutsGridLayout.setVisibility(View.GONE);
    }

    public void displayErrorSavingWorkouts() {

    }

    public void displaySuccessSavingWorkout() {

    }

    public void displayErrorDeletingWorkout() {

    }

    public void displaySuccessDeletingWorkout() {

    }

    @Override
    public void displayToast (String message){
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        mToast.show();
    }
}
