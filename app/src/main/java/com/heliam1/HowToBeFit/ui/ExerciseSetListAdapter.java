package com.heliam1.HowToBeFit.ui;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.heliam1.HowToBeFit.R;
import com.heliam1.HowToBeFit.di.HowToBeFitApplication;
import com.heliam1.HowToBeFit.models.ExerciseSet;
import com.heliam1.HowToBeFit.models.ExerciseSetAndListPreviousExerciseSet;
import com.heliam1.HowToBeFit.models.PreviousExerciseSet;
import com.heliam1.HowToBeFit.presenters.ExerciseSetsPresenter;
import com.heliam1.HowToBeFit.repositories.ExerciseSetRepository;

import java.util.List;

import javax.inject.Inject;

public class ExerciseSetListAdapter extends ArrayAdapter<ExerciseSetAndListPreviousExerciseSet> {

    public ExerciseSetListAdapter(Context context, List<ExerciseSetAndListPreviousExerciseSet> exerciseSetsAndTheirPreviousSets) {
        super(context, 0, exerciseSetsAndTheirPreviousSets);
    }

    @Override
    public View getView(int position, View listItemView, ViewGroup parent) {
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.item_exerciseset,
                    parent, false);
        }

        ExerciseSetAndListPreviousExerciseSet exerciseSetAndListPreviousExerciseSet
                = getItem(position);

        ExerciseSet exerciseSet = exerciseSetAndListPreviousExerciseSet.getExerciseSet();
        List<PreviousExerciseSet> previousExerciseSets
                = exerciseSetAndListPreviousExerciseSet.getPreviousExerciseSets();


        TextView setStartTimeNameNumber = listItemView.findViewById(R.id.setStartTimeNameNumber);
        RecyclerView previousSetsRecycleView = listItemView.findViewById(R.id.previousSetsRecyclerView);
        EditText currentSetWeight = listItemView.findViewById(R.id.currentSetWeight);
        EditText currentSetReps = listItemView.findViewById(R.id.currentSetReps);
        TextView personalBestWeightReps = listItemView.findViewById(R.id.personalBestWeightAndReps);

        int startTimeSeconds = calculateSetStart(position);
        String startTime = formatStartTime(startTimeSeconds);

        setStartTimeNameNumber.setText(startTime + " " + exerciseSet.getExerciseName() + "-"
                + Integer.toString(exerciseSet.getSetNumber()));

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false);
        previousSetsRecycleView.setLayoutManager(layoutManager);

        PreviousSetAdapter adapter = new PreviousSetAdapter(getContext(), previousExerciseSets);
        previousSetsRecycleView.setAdapter(adapter);

        personalBestWeightReps.setText(Integer.toString(exerciseSet.getPbWeight()) + " "
                + Integer.toString(exerciseSet.getPbReps()));

        return listItemView;
    }

    private int calculateSetStart(int position) {
        int setStart = 0; // seconds

        int i = 0; //
        while (i < position) {
            ExerciseSetAndListPreviousExerciseSet exerciseSetAndListPreviousExerciseSet
                    = getItem(position);
            ExerciseSet exerciseSet = exerciseSetAndListPreviousExerciseSet.getExerciseSet();
            setStart = setStart + exerciseSet.getSetDuration() + exerciseSet.getSetRest();
            i++;
        }

        return setStart;
    }

    private String formatStartTime(int totalSeconds) {
        int minutes = (totalSeconds % 60);
        int seconds = totalSeconds - 60 * (totalSeconds % 60);

        return (Integer.toString(minutes) + ":" + Integer.toString(seconds));
    }
}
