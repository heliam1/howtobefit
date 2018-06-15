package com.heliam1.HowToBeFit.ui.ExerciseSets;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.heliam1.HowToBeFit.R;
import com.heliam1.HowToBeFit.di.HowToBeFitApplication;
import com.heliam1.HowToBeFit.models.StartTimeExerciseSetListPreviousExerciseSet;
import com.heliam1.HowToBeFit.repositories.ExerciseSetRepository;
import com.heliam1.HowToBeFit.repositories.TimersRepository;
import com.heliam1.HowToBeFit.utils.NotificationUtils;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    // functions as private static int expandedFlag = -1;
    private StartTimeExerciseSetListPreviousExerciseSet mCurrentElement = null;
    private static boolean mNewElementExpanded = false;

    private ActionBar mActionBar;

    private LinearLayout mExerciseSetsLinearLayout;

    private Button mStartTimers;
    private EditText mTimeElapsed;
    private TextView mActualTimeElapsed;
    private Toast mToast;

    private FloatingActionButton mFabAddExerciseSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_sets);

        ((HowToBeFitApplication) getApplication()).getAppComponent().inject(this);

        // Set action bar
        mActionBar = getSupportActionBar();
        mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));
        mActionBar.setTitle(getIntent().getStringExtra("workoutName"));

        // Set the list View
        mExerciseSetsLinearLayout = findViewById(R.id.exerciseSetsRecyclerView);

        // Set the timers
        mTimeElapsed = findViewById(R.id.timeElapsed);
        mStartTimers = findViewById(R.id.text_view_label_actualTimerElapsed);
        mActualTimeElapsed = findViewById(R.id.actualTimeElapsed);
        mFabAddExerciseSet = findViewById(R.id.floatingActionButton);
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

        mStartTimers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mExerciseSetsPresenter.startTimers();
                findViewById(R.id.constraint_layout_timers).requestFocus();
            }
        });

        mFabAddExerciseSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expandExerciseSet(null);
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

    @Override
    public List<String> getSetWeightStrings() {
        ArrayList<String> setWeights = new ArrayList<>();

        for (int i = 0; i < mExerciseSetsLinearLayout.getChildCount(); i++) {
            setWeights.add(((EditText) ((ViewGroup) mExerciseSetsLinearLayout.getChildAt(i)).getChildAt(2)).getText().toString());
        }
        return setWeights;
    }

    @Override
    public List<String> getSetRepsStrings() {
        ArrayList<String> setReps = new ArrayList<>();

        for (int i = 0; i < mExerciseSetsLinearLayout.getChildCount(); i++) {
            setReps.add(((EditText) ((ViewGroup) mExerciseSetsLinearLayout.getChildAt(i)).getChildAt(3)).getText().toString());
        }
        return setReps;
    }

    @Override
    public String getSetWeightString(int i) {
        return ((EditText) ((ViewGroup) mExerciseSetsLinearLayout.getChildAt(i)).getChildAt(2)).getText().toString();
    }

    @Override
    public String getSetRepsString(int i) {
        return ((EditText) ((ViewGroup) mExerciseSetsLinearLayout.getChildAt(i)).getChildAt(3)).getText().toString();
    }

    private void showDeleteExerciseSetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete exercise set?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mExerciseSetsPresenter.deleteExerciseSet(mCurrentElement);
                minimiseExerciseSet(mCurrentElement, true);
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

    static int mCount = 0;

    @Override
    public void displayExerciseSets(List<StartTimeExerciseSetListPreviousExerciseSet> list) {
        mCount++;
        Log.v("EXERCISESETSACTIVITY", Integer.toString(mCount));
        mExerciseSetsLinearLayout.removeAllViews();
        for (int i = 0; i < list.size(); i++) {

            View exerciseSetView = LayoutInflater.from(this).inflate(R.layout.item_exerciseset, mExerciseSetsLinearLayout, false);
            TextView setStartTime = exerciseSetView.findViewById(R.id.setStartTime);
            TextView setNameNumber = exerciseSetView.findViewById(R.id.setNameNumber);
            EditText currentSetWeight = exerciseSetView.findViewById(R.id.currentSetWeight);
            EditText currentSetReps = exerciseSetView.findViewById(R.id.currentSetReps);
            Button expandButton = exerciseSetView.findViewById(R.id.button_expand);
            TextView upButton =exerciseSetView.findViewById(R.id.button_up);
            TextView downButton = exerciseSetView.findViewById(R.id.button_down);

            StartTimeExerciseSetListPreviousExerciseSet element = list.get(i);
            StartTimeExerciseSetListPreviousExerciseSet startExsetListprev = element;

            setNameNumber.setText(startExsetListprev.getExerciseSet().getExerciseName() + " "
                    + Integer.toString(startExsetListprev.getExerciseSet().getSetNumber()));

            convertStartLongtoString(startExsetListprev.getStartTime());
            setStartTime.setText(convertStartLongtoString(startExsetListprev.getStartTime()));

            if (element.getExerciseSet().getSetWeight() != -1) {
                Log.v("EXERCISESETSACTIVITY", Double.toString(element.getExerciseSet().getSetWeight()));
                currentSetWeight.setText(Double.toString(element.getExerciseSet().getSetWeight()));
                Log.v("EXERCISESETSACTIVITY", currentSetWeight.getText().toString());
            } else {
                currentSetWeight.setText("");
            }

            if (element.getExerciseSet().getSetReps() != -1)
                currentSetReps.setText(Integer.toString(element.getExerciseSet().getSetReps()));
            else {
                currentSetReps.setText("");
            }

            expandButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mExerciseSetsPresenter.saveExerciseSetsToRepository();
                    expandExerciseSet(startExsetListprev);
                }
            });

            upButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    minimiseExerciseSet(mCurrentElement, false);
                    mExerciseSetsPresenter.swapExerciseSetUp(startExsetListprev);
                }
            });

            downButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    minimiseExerciseSet(mCurrentElement, false);
                    mExerciseSetsPresenter.swapExerciseSetDown(startExsetListprev);
                }
            });

            mExerciseSetsLinearLayout.addView(exerciseSetView);
        }
    }

    @Override
    public void minimiseExerciseSet(StartTimeExerciseSetListPreviousExerciseSet element, boolean deleted) {
        if (element != null) {
            if (!deleted) {
                mExerciseSetsLinearLayout.removeViewAt(element.getExerciseSet().getSetOrder() - 1);
                View exerciseSetView = LayoutInflater.from(this).inflate(R.layout.item_exerciseset, mExerciseSetsLinearLayout, false);
                TextView setStartTime = exerciseSetView.findViewById(R.id.setStartTime);
                TextView setNameNumber = exerciseSetView.findViewById(R.id.setNameNumber);
                EditText currentSetWeight = exerciseSetView.findViewById(R.id.currentSetWeight);
                EditText currentSetReps = exerciseSetView.findViewById(R.id.currentSetReps);
                Button expandButton = exerciseSetView.findViewById(R.id.button_expand);
                TextView upButton =exerciseSetView.findViewById(R.id.button_up);
                TextView downButton = exerciseSetView.findViewById(R.id.button_down);

                StartTimeExerciseSetListPreviousExerciseSet startExsetListprev = element;
                setNameNumber.setText(startExsetListprev.getExerciseSet().getExerciseName() + "#"
                        + Integer.toString(startExsetListprev.getExerciseSet().getSetNumber()));

                convertStartLongtoString(startExsetListprev.getStartTime());
                setStartTime.setText(convertStartLongtoString(startExsetListprev.getStartTime()));

                if (element.getExerciseSet().getSetWeight() != -1)
                    currentSetWeight.setText(Double.toString(element.getExerciseSet().getSetWeight()));
                else {
                    currentSetWeight.setText("");
                }

                if (element.getExerciseSet().getSetReps() != -1)
                    currentSetReps.setText(Integer.toString(element.getExerciseSet().getSetReps()));
                else {
                    currentSetReps.setText("");
                }

                expandButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mExerciseSetsPresenter.saveExerciseSetsToRepository();
                        expandExerciseSet(startExsetListprev);
                    }
                });

                upButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        minimiseExerciseSet(mCurrentElement, false);
                        mExerciseSetsPresenter.swapExerciseSetUp(startExsetListprev);
                    }
                });

                downButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        minimiseExerciseSet(mCurrentElement, false);
                        mExerciseSetsPresenter.swapExerciseSetDown(startExsetListprev);
                    }
                });
                mExerciseSetsLinearLayout.addView(exerciseSetView, element.getExerciseSet().getSetOrder() - 1);
            } else {
                mExerciseSetsLinearLayout.removeViewAt(element.getExerciseSet().getSetOrder() - 1);
            }
        } else {
            if (mNewElementExpanded) {
                mExerciseSetsLinearLayout.removeViewAt(mExerciseSetsPresenter.getList().size());
                mNewElementExpanded = false;
            }
            // decided not to add an exercise set, just remove the view
        }
        mCurrentElement = null;
        mFabAddExerciseSet.setVisibility(View.VISIBLE);
    }

    private void expandExerciseSet(StartTimeExerciseSetListPreviousExerciseSet element) {
        // if there is an expanded view already open, close it
        minimiseExerciseSet(mCurrentElement, false);
        mCurrentElement = element;
        mFabAddExerciseSet.setVisibility(View.GONE);

        View expandedView = LayoutInflater.from(this).inflate(R.layout.expanded_exerciseset,
                mExerciseSetsLinearLayout, false);
        EditText nameEditText = expandedView.findViewById(R.id.edit_text_expanded_name);
        TextView startTimeTextView = expandedView.findViewById(R.id.text_view_expanded_start);
        EditText setNumberEditText = expandedView.findViewById(R.id.edit_text_expanded_set_order);
        EditText durationMinutesEditText = expandedView.findViewById(R.id.edit_text_expanded_duration_minutes);
        EditText durationSecondsEditText = expandedView.findViewById(R.id.edit_text_expanded_duration_seconds);
        EditText restMinutesEditText = expandedView.findViewById(R.id.edit_text_expanded_rest_minutes);
        EditText restSecondsEditText = expandedView.findViewById(R.id.edit_text_expanded_rest_seconds);
        EditText pbWeightEditText = expandedView.findViewById(R.id.edit_text_expanded_pb_weight);
        EditText pbRepsEditText = expandedView.findViewById(R.id.edit_text_expanded_pb_reps);
        RecyclerView prevSetsRecyclerView = expandedView.findViewById(R.id.recycler_expanded_prev_sets);
        Button minimiseButton = expandedView.findViewById(R.id.button_expanded_minimise);
        Button deleteButton = expandedView.findViewById(R.id.button_expanded_delete);
        Button saveButton = expandedView.findViewById(R.id.button_expanded_save);

        if (mCurrentElement == null) {
            nameEditText.setText("");
            startTimeTextView.setText("");
            setNumberEditText.setText("");
            durationMinutesEditText.setText("");
            durationSecondsEditText.setText("");
            restMinutesEditText.setText("");
            restSecondsEditText.setText("");
            pbWeightEditText.setText("");
            pbRepsEditText.setText("");
            deleteButton.setVisibility(View.INVISIBLE);
        } else {
            nameEditText.setText(element.getExerciseSet().getExerciseName());
            startTimeTextView.setText(convertStartLongtoString(element.getStartTime()));
            setNumberEditText.setText(Integer.toString(element.getExerciseSet().getSetNumber()));

            SimpleDateFormat sdfMinutes = new SimpleDateFormat("mm");
            SimpleDateFormat sdfSeconds = new SimpleDateFormat("ss");
            durationMinutesEditText.setText(sdfMinutes.format(new Date(element.getExerciseSet().getSetDuration())));
            durationSecondsEditText.setText(sdfSeconds.format(new Date(element.getExerciseSet().getSetDuration())));
            restMinutesEditText.setText(sdfMinutes.format(new Date(element.getExerciseSet().getSetRest())));
            restSecondsEditText.setText(sdfSeconds.format(new Date(element.getExerciseSet().getSetRest())));

            if (element.getExerciseSet().getPbWeight() != -1)
                pbWeightEditText.setText(Double.toString(element.getExerciseSet().getPbWeight()));
            else {
                pbWeightEditText.setText("");
            }
            if (element.getExerciseSet().getPbReps() != -1)
                pbRepsEditText.setText(Integer.toString(element.getExerciseSet().getPbReps()));
            else {
                pbRepsEditText.setText("");
            }

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDeleteExerciseSetDialog();
                }
            });

            LinearLayoutManager layoutManager
                    = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            prevSetsRecyclerView.setLayoutManager(layoutManager);
            prevSetsRecyclerView.setAdapter(new PreviousSetAdapter(this, element.getPreviousExerciseSets()));
        }

        minimiseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                minimiseExerciseSet(mCurrentElement, false);
            }});

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String setOrder;
                if (mCurrentElement != null) {
                    setOrder = Integer.toString(mCurrentElement.getExerciseSet().getSetOrder());
                } else {
                    setOrder = "";
                }

                mExerciseSetsPresenter.addExerciseSet(mCurrentElement,
                        mWorkoutId,
                        nameEditText.getText().toString(),
                        setNumberEditText.getText().toString(),
                        setOrder,
                        durationMinutesEditText.getText().toString(),
                        durationSecondsEditText.getText().toString(),
                        restMinutesEditText.getText().toString(),
                        restSecondsEditText.getText().toString(),
                        pbWeightEditText.getText().toString(),
                        pbRepsEditText.getText().toString()
                );

                // stops android dynmaically removed view unfocus bug
                mExerciseSetsLinearLayout.requestFocus();

                if (mCurrentElement == null)
                    minimiseExerciseSet(mExerciseSetsPresenter.getList().get(
                            mExerciseSetsPresenter.getList().size() - 1), false);
                else {
                    minimiseExerciseSet(mCurrentElement, false);
                }

                mNewElementExpanded = false;
            }});

        if (mCurrentElement == null) {
            mExerciseSetsLinearLayout.addView(expandedView);
            mNewElementExpanded = true;
        } else {
            mExerciseSetsLinearLayout.removeViewAt(mCurrentElement.getExerciseSet().getSetOrder() - 1);
            mExerciseSetsLinearLayout.addView(expandedView, mCurrentElement.getExerciseSet().getSetOrder() - 1);
        }

        // those methods that are affected
        // test (properly me in the gym), colors, release to Aaron Vish Alex (hey this might not work but it works on my phone perfectly), update jobs
        // maybe release TimeLord
    }

    private String convertStartLongtoString(long time) {
        // convert long start time to correct string
        Date timeDate = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
        String incorrectString = sdf.format(timeDate);
        // incorrect string displays 10:XX:XX, should display 00:XX:XX
        String startTime = "0" + incorrectString.charAt(1)
                + incorrectString.charAt(2)  + incorrectString.charAt(3)
                + incorrectString.charAt(4)  + incorrectString.charAt(5)
                + incorrectString.charAt(6)  + incorrectString.charAt(7);
        return startTime;
    }

    @Override
    public void displaySwappedSet(StartTimeExerciseSetListPreviousExerciseSet setTop, StartTimeExerciseSetListPreviousExerciseSet setBot) {
        // we record their edit texts to each set,
        // then just swap the values in them.
        // but input validation is gross, I am considering this a pre optimisation unless edit text text is being destroyed
        // in which case it should be saved reactively after user finishes typing it

        ViewGroup viewTop = (ViewGroup) mExerciseSetsLinearLayout.getChildAt(setTop.getExerciseSet().getSetOrder() - 1);
        ViewGroup viewBot = (ViewGroup) mExerciseSetsLinearLayout.getChildAt(setBot.getExerciseSet().getSetOrder() - 1);

        // swap name and set number
        ((TextView) viewTop.getChildAt(0)).setText(setTop.getExerciseSet().getExerciseName()
                + "#" + setTop.getExerciseSet().getSetNumber());
        ((TextView) viewBot.getChildAt(0)).setText(setBot.getExerciseSet().getExerciseName()
                + "#" + setBot.getExerciseSet().getSetNumber());
        // swap time
        ((TextView) viewTop.getChildAt(1)).setText(convertStartLongtoString(setTop.getStartTime()));
        ((TextView) viewBot.getChildAt(1)).setText(convertStartLongtoString(setBot.getStartTime()));

        // magic numbers are the position of the edit texts for the children
        // store the top edit text strings into temp for swapping
        String weightTemp  = ((EditText) viewTop.getChildAt(2)).getText().toString();
        String repsTemp = ((EditText) viewTop.getChildAt(3)).getText().toString();

        // swap bot into top
        ((EditText) viewTop.getChildAt(2)).setText(
                ((EditText) viewBot.getChildAt(2)).getText().toString());
        ((EditText) viewTop.getChildAt(3)).setText(
                ((EditText) viewBot.getChildAt(3)).getText().toString());

        // put temp into bot
        ((EditText) viewTop.getChildAt(2)).setText(weightTemp);
        ((EditText) viewTop.getChildAt(3)).setText(repsTemp);
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
