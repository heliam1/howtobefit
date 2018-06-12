package com.heliam1.HowToBeFit.ui.ExerciseSets;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.heliam1.HowToBeFit.R;
import com.heliam1.HowToBeFit.di.HowToBeFitApplication;
import com.heliam1.HowToBeFit.models.StartTimeExerciseSetListPreviousExerciseSet;
import com.heliam1.HowToBeFit.repositories.ExerciseSetRepository;
import com.heliam1.HowToBeFit.repositories.TimersRepository;
import com.heliam1.HowToBeFit.utils.NotificationUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class ExerciseSetsActivity extends AppCompatActivity implements ExerciseSetsView {
    @Inject
    ExerciseSetRepository exerciseSetRepository;

    @Inject
    TimersRepository timersRepository;

    private ExerciseSetsPresenter mExerciseSetsPresenter;
    private long mWorkoutId;
    private long mWorkoutDate;
    private StartTimeExerciseSetListPreviousExerciseSet mCurrentElement;

    private ActionBar mActionBar;

    private ConstraintLayout mAddEerciseSetContraintLayout;
    private EditText mEditAddExerciseName;
    private EditText mEditAddSetNumber;
    private EditText mEditAddSetOrder;
    private EditText mEditAddSetDurationMinutes;
    private EditText mEditAddSetDurationSeconds;
    private EditText mEditAddSetRestMinutes;
    private EditText mEditAddSetRestSeconds;
    private EditText mEditAddPbWeight;
    private EditText mEditAddPbReps;
    private EditText mEditWeight;
    private EditText mEditReps;
    private RecyclerView mRecyclerPreviousSets;
    private Button mButtonMinimiseEditor;
    private Button mButtonDeleteExerciseSet;
    private Button mButtonSaveExerciseSet;

    private LinearLayout mExerciseSetsLinearLayout;

    private Button mStartTimers;
    private EditText mTimeElapsed;
    private TextView mActualTimeElapsed;
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_sets);

        ((HowToBeFitApplication) getApplication()).getAppComponent().inject(this);

        // Set action bar
        mActionBar = getSupportActionBar();
        mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));
        mActionBar.setTitle(getIntent().getStringExtra("workoutName"));

        // Set the editor
        mAddEerciseSetContraintLayout = findViewById(R.id.constraint_layout_add_set);
        mAddEerciseSetContraintLayout.setVisibility(View.GONE);
        mEditAddExerciseName = findViewById(R.id.edit_text_add_set_exercise);
        mEditAddSetNumber = findViewById(R.id.edit_text_add_set_number);
        mEditAddSetOrder = findViewById(R.id.edit_text_set_order);
        mEditAddSetDurationMinutes = findViewById(R.id.edit_text_add_set_duration_minutes);
        mEditAddSetDurationSeconds = findViewById(R.id.edit_text_add_set_duration_seconds);
        mEditAddSetRestMinutes = findViewById(R.id.edit_text_add_set_rest_minutes);
        mEditAddSetRestSeconds = findViewById(R.id.edit_text_add_set_rest_seconds);
        mEditAddPbWeight = findViewById(R.id.edit_text_add_pb_weight);
        mEditAddPbReps = findViewById(R.id.edit_text_add_pb_reps);
        mEditWeight = findViewById(R.id.edit_text_weight);
        mEditReps = findViewById(R.id.edit_text_reps);
        mRecyclerPreviousSets = findViewById(R.id.recycler_editor_prev_sets);

        mButtonMinimiseEditor = findViewById(R.id.button_minimise);
        mButtonDeleteExerciseSet = findViewById(R.id.button_delete_exercise_set);
        mButtonSaveExerciseSet = findViewById(R.id.button_save_exercise_set);

        // Set the list View
        mExerciseSetsLinearLayout = findViewById(R.id.exerciseSetsRecyclerView);

        // Set the timers
        mTimeElapsed = findViewById(R.id.timeElapsed);
        mStartTimers = findViewById(R.id.text_view_label_actualTimerElapsed);
        mActualTimeElapsed = findViewById(R.id.actualTimeElapsed);
        mActualTimeElapsed.setText("00:00:00");
        mTimeElapsed.setText("00:00:00");

        // populate the recycler View
        mWorkoutId = getIntent().getLongExtra("workoutId", 0);
        mWorkoutDate = getIntent().getLongExtra("workoutDate", 0);
        if (mWorkoutId == 0) {
            Log.e("ExerciseSetsActivity", "no workout");
        }
        mExerciseSetsPresenter = new ExerciseSetsPresenter(this, timersRepository, exerciseSetRepository, AndroidSchedulers.mainThread());
        mExerciseSetsPresenter.loadExerciseSets(mWorkoutId, mWorkoutDate);

        /*
        mExerciseSetsRecyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                mCurrentElement = mExerciseSetsPresenter.getCurrentElement(position);
                setEditorFields(mCurrentElement);
                mAddEerciseSetContraintLayout.setVisibility(View.VISIBLE);
            }
        }); */

        mStartTimers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mExerciseSetsPresenter.startTimers();
                findViewById(R.id.constraint_layout_timers).requestFocus();
            }
        });

        mButtonMinimiseEditor.setOnClickListener(view ->
                mAddEerciseSetContraintLayout.setVisibility(View.GONE));

        mButtonDeleteExerciseSet.setOnClickListener(view -> {
            showDeleteExerciseSetDialog();
        });

        mButtonSaveExerciseSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mExerciseSetsPresenter.addExerciseSet(mCurrentElement,
                        mWorkoutId,
                        mEditAddExerciseName.getText().toString(),
                        mEditAddSetNumber.getText().toString(),
                        mEditAddSetOrder.getText().toString(),
                        mEditAddSetDurationMinutes.getText().toString(),
                        mEditAddSetDurationSeconds.getText().toString(),
                        mEditAddSetRestMinutes.getText().toString(),
                        mEditAddSetRestSeconds.getText().toString(),
                        mEditAddPbWeight.getText().toString(),
                        mEditAddPbReps.getText().toString(),
                        mEditWeight.getText().toString(),
                        mEditReps.getText().toString()
                );
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
                if (item.getTitle().equals("ADD A SET")) {
                    mCurrentElement = null;
                    setEditorFields(mCurrentElement);
                    mAddEerciseSetContraintLayout.setVisibility(View.VISIBLE);
                    item.setTitle("Close");
                    return true;
                } else {
                    mCurrentElement = null;
                    setEditorFields(mCurrentElement);
                    mAddEerciseSetContraintLayout.setVisibility(View.GONE);
                    item.setTitle("ADD A SET");
                }
                return true;

            case R.id.item_menu_delete_workout:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Delete this workout?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mExerciseSetsPresenter.deleteWorkout(mWorkoutId);
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

    @Override
    public void clearEditor() {
        mCurrentElement = null;
        setEditorFields(mCurrentElement);
        mAddEerciseSetContraintLayout.setVisibility(View.GONE);
    }

    private void setEditorFields(StartTimeExerciseSetListPreviousExerciseSet element) {
        if (element == null) {
            mEditAddExerciseName.setText("");
            mEditAddSetNumber.setText("");
            mEditAddSetOrder.setText("");
            mEditAddSetDurationMinutes.setText("");
            mEditAddSetDurationSeconds.setText("");
            mEditAddSetRestMinutes.setText("");
            mEditAddSetRestSeconds.setText("");
            mEditAddPbWeight.setText("");
            mEditAddPbReps.setText("");
            mEditWeight.setText("");
            mEditReps.setText("");
            mRecyclerPreviousSets.setAdapter(null);
        } else {
            mEditAddExerciseName.setText(element.getExerciseSet().getExerciseName());
            mEditAddSetNumber.setText(Integer.toString(element.getExerciseSet().getSetNumber()));
            mEditAddSetOrder.setText(Integer.toString(element.getExerciseSet().getSetOrder()));

            SimpleDateFormat minutes = new SimpleDateFormat("mm");
            SimpleDateFormat seconds = new SimpleDateFormat("ss");

            mEditAddSetDurationMinutes.setText(
                    minutes.format(new Date(element.getExerciseSet().getSetDuration())));
            mEditAddSetDurationSeconds.setText(
                    seconds.format(new Date(element.getExerciseSet().getSetDuration())));
            mEditAddSetRestMinutes.setText(
                    minutes.format(new Date(element.getExerciseSet().getSetRest())));
            mEditAddSetRestSeconds.setText(
                    seconds.format(new Date(element.getExerciseSet().getSetDuration())));
            mEditAddPbWeight.setText(Double.toString(element.getExerciseSet().getPbWeight()));
            mEditAddPbReps.setText(Integer.toString(element.getExerciseSet().getPbReps()));

            if (element.getExerciseSet().getSetWeight() == -1) {
                mEditWeight.setText("");
            } else {
                mEditWeight.setText(Double.toString(element.getExerciseSet().getSetWeight()));
            }

            if (element.getExerciseSet().getSetReps() == -1) {
                mEditReps.setText("");
            } else {
                mEditReps.setText(Integer.toString(element.getExerciseSet().getSetReps()));
            }

            LinearLayoutManager layoutManager
                    = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            mRecyclerPreviousSets.setLayoutManager(layoutManager);

            mRecyclerPreviousSets.setAdapter(new PreviousSetAdapter(this, element.getPreviousExerciseSets()));
        }
    }

    private void showChooseSaveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Exit and?");
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mExerciseSetsPresenter.saveExerciseSets(mWorkoutId);
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

    private void showDeleteExerciseSetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete exercise set?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mExerciseSetsPresenter.deleteExerciseSet(mCurrentElement);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // do nothing lol
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
    public void displayExerciseSets(List<StartTimeExerciseSetListPreviousExerciseSet> list) {
        mExerciseSetsLinearLayout.removeAllViews();
        for (int i = 0; i < list.size(); i++) {
            Log.v("ExerciseSetsAcitvity", "Making child for lin lay");
            View exerciseSetItem = LayoutInflater.from(this).inflate(R.layout.item_exerciseset, mExerciseSetsLinearLayout, false);
            TextView setStartTime = exerciseSetItem.findViewById(R.id.setStartTime);
            TextView setNameNumber = exerciseSetItem.findViewById(R.id.setNameNumber);
            EditText currentSetWeight = exerciseSetItem.findViewById(R.id.currentSetWeight);
            EditText currentSetReps = exerciseSetItem.findViewById(R.id.currentSetReps);

            StartTimeExerciseSetListPreviousExerciseSet startExsetListprev = list.get(i);
            setNameNumber.setText(startExsetListprev.getExerciseSet().getExerciseName() + "#"
                    + Integer.toString(startExsetListprev.getExerciseSet().getSetNumber()));
            // convert long start time to correct string
            Date timeDate = new Date(startExsetListprev.getStartTime());
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
            String incorrectString = sdf.format(timeDate);
            // incorrect string displays 10:XX:XX, should display 00:XX:XX
            String startTime = "0" + incorrectString.charAt(1)
                    + incorrectString.charAt(2)  + incorrectString.charAt(3)
                    + incorrectString.charAt(4)  + incorrectString.charAt(5)
                    + incorrectString.charAt(6)  + incorrectString.charAt(7);
            setStartTime.setText(startTime);

            if (list.get(i).getExerciseSet().getSetWeight() != -1)
                currentSetWeight.setText(Double.toString(list.get(i).getExerciseSet().getSetWeight()));
            else {
                currentSetWeight.setText("");
            }

            if (list.get(i).getExerciseSet().getSetReps() != -1)
                currentSetReps.setText(Double.toString(list.get(i).getExerciseSet().getSetReps()));
            else {
                currentSetReps.setText("");
            }

            mExerciseSetsLinearLayout.addView(exerciseSetItem);
        }
    }

    @Override
    public void displayAddedSet(int position) {
        // mExerciseSetAdapter.notifyItemInserted(position);
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

    @Override
    public void finishActivity() {
        finish();
    }
}
