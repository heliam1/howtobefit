package com.heliam1.HowToBeFit.ui.ExerciseSets;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.heliam1.HowToBeFit.R;
import com.heliam1.HowToBeFit.di.HowToBeFitApplication;
import com.heliam1.HowToBeFit.models.ExerciseSet;
import com.heliam1.HowToBeFit.models.ExerciseSetAndListPreviousExerciseSet;
import com.heliam1.HowToBeFit.repositories.ExerciseSetRepository;
import com.heliam1.HowToBeFit.utils.NotificationUtils;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class ExerciseSetsActivity extends AppCompatActivity implements ExerciseSetsView {
    @Inject
    ExerciseSetRepository exerciseSetRepository;

    private ExerciseSetsPresenter mExerciseSetsPresenter;
    private long mWorkoutId;
    private long mWorkoutDate;

    private ActionBar mActionBar;

    private ConstraintLayout mAddEerciseSetContraintLayout;
    private EditText mEditAddExerciseName;
    private EditText mEditAddSetNumber;
    private EditText mEditAddSetDuration;
    private EditText mEditAddSetRest;
    private EditText mEditAddPbWeight;
    private EditText mEditAddPbReps;

    private RecyclerView mExerciseSetsRecyclerView;
    private ExerciseSetAdapter mExerciseSetAdapter;

    private Button mStartTimers;
    private EditText mTimeElapsed;
    private TextView mActualTimeElapsed;
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_sets);

        ((HowToBeFitApplication) getApplication()).getAppComponent().inject(this);

        mActionBar = getSupportActionBar();
        mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));
        mActionBar.setTitle(getIntent().getStringExtra("workoutName"));

        mAddEerciseSetContraintLayout = findViewById(R.id.constraint_layout_add_set);
        mAddEerciseSetContraintLayout.setVisibility(View.GONE);
        mEditAddExerciseName = findViewById(R.id.edit_text_add_set_exercise);
        mEditAddSetNumber = findViewById(R.id.edit_text_add_set_number);
        mEditAddSetDuration = findViewById(R.id.edit_text_add_set_duration);
        mEditAddSetRest = findViewById(R.id.edit_text_add_set_rest);
        mEditAddPbWeight = findViewById(R.id.edit_text_add_pb_weight);
        mEditAddPbReps = findViewById(R.id.edit_text_add_pb_reps);

        mExerciseSetsRecyclerView = findViewById(R.id.exerciseSetsRecyclerView);

        mTimeElapsed = findViewById(R.id.timeElapsed);
        mStartTimers = findViewById(R.id.text_view_label_actualTimerElapsed);
        mActualTimeElapsed = findViewById(R.id.actualTimeElapsed);

        mWorkoutId = getIntent().getLongExtra("workoutId", 0);
        mWorkoutDate = getIntent().getLongExtra("workoutDate", 0);
        if (mWorkoutId == 0) {
            Log.e("ExerciseSetsActivity", "no workout");
        }

        mExerciseSetsPresenter = new ExerciseSetsPresenter(this, exerciseSetRepository, AndroidSchedulers.mainThread());
        mExerciseSetsPresenter.loadExerciseSets(mWorkoutId, mWorkoutDate);

        mActualTimeElapsed.setText("00:00:00");
        mTimeElapsed.setText("00:00:00");

        mStartTimers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mExerciseSetsPresenter.startTimers();
                findViewById(R.id.constraint_layout_timers).requestFocus();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_exercisesets, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                showChooseSaveDialog();
                return true;

            case R.id.item_menu_add_set:
                if (item.getTitle().equals("Add A Set")) {
                    mAddEerciseSetContraintLayout.setVisibility(View.VISIBLE);
                    item.setTitle("✓");
                } else { // adding it to UI
                    mExerciseSetsPresenter.addExerciseSet(mWorkoutId,
                            mEditAddExerciseName.getText().toString(),
                            mEditAddSetNumber.getText().toString(),
                            mEditAddSetDuration.getText().toString(),
                            mEditAddSetRest.getText().toString(),
                            mEditAddPbWeight.getText().toString(),
                            mEditAddPbReps.getText().toString());
                    mAddEerciseSetContraintLayout.setVisibility(View.GONE);
                    item.setTitle("Add A Set");
                }
                return true;

            case R.id.item_menu_delete_workout:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Delete this workout?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mExerciseSetsPresenter.deleteWorkout(mWorkoutId);
                        finish();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (dialogInterface != null) {
                            dialogInterface.dismiss();
                        }
                    }
                });
                // Create and show the AlertDialog
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    private void showChooseSaveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Exit and?");
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mExerciseSetsPresenter.saveExerciseSets(mWorkoutId);
                finish();
            }
        });
        builder.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        showChooseSaveDialog();
    }

    @Override
    protected void onDestroy() {
        mExerciseSetsPresenter.unsubscribe();
        super.onDestroy();
    }

    @Override
    public void displayExerciseSets() {
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mExerciseSetsRecyclerView.setLayoutManager(layoutManager);

        mExerciseSetAdapter = new ExerciseSetAdapter(this, mExerciseSetsPresenter);
        mExerciseSetsRecyclerView.setAdapter(mExerciseSetAdapter);

        ItemTouchHelper.Callback callback =
                new ExerciseSetTouchHelperCallback(mExerciseSetAdapter, this);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mExerciseSetsRecyclerView);
    }

    @Override
    public void displayAddedSet(int position) {
        mExerciseSetAdapter.notifyItemInserted(position);
        // mExerciseSetAdapter.notifyDataSetChanged();
    }

    @Override
    public void displayNoExerciseSets() {

    }

    @Override
    public void displayErrorLoadingExerciseSets() {

    }

    @Override
    public void displayActualElapsedTime(String time) {
        mActualTimeElapsed.setText(time);
    }

    @Override
    public void displayElapsedTime(String time) {
        mTimeElapsed.setText(time);
    }

    @Override
    public String getTimeElapsed() {
        return mTimeElapsed.getText().toString();
    }

    @Override
    public boolean isTimeElapsedFocused() {
        return mTimeElapsed.isFocused();
    }

    @Override
    public void notifyStartNextSet(String action) {
        // if we wanted to use a service
        // Intent notifiyStartNext = new Intent(this, NextSetIntentService)
        // then use this next line of code inside that class^/service
        // NotificationsTasks.executeTask(this, action);
        NotificationUtils.clearAllNotifications(this);
        NotificationUtils.remindUserBecauseSetStart(this);
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
