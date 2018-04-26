package com.heliam1.HowToBeFit.ui;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.heliam1.HowToBeFit.R;
import com.heliam1.HowToBeFit.models.ExerciseSet;
import com.heliam1.HowToBeFit.models.ExerciseSetAndListPreviousExerciseSet;
import com.heliam1.HowToBeFit.models.PreviousExerciseSet;

import java.util.ArrayList;
import java.util.List;

public class ExerciseSetAdapter extends RecyclerView.Adapter<ExerciseSetAdapter.ExerciseSetViewHolder> implements ExerciseSetTouchHelperAdapter {
    private Context mContext;
    private List<ExerciseSetAndListPreviousExerciseSet> mEexerciseSetsAndTheirPreviousSets;

    public ExerciseSetAdapter(Context context, List<ExerciseSetAndListPreviousExerciseSet> exerciseSetsAndTheirPreviousSets) {
        mContext = context;
        mEexerciseSetsAndTheirPreviousSets = exerciseSetsAndTheirPreviousSets;
    }

    @Override
    public ExerciseSetViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        //
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachImmediatelyToParent = false;

        View view = inflater.inflate(R.layout.item_exerciseset, viewGroup, shouldAttachImmediatelyToParent);
        ExerciseSetViewHolder viewHolder = new ExerciseSetViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ExerciseSetViewHolder holder, int position) {
        holder.bind(mEexerciseSetsAndTheirPreviousSets.get(position), position);
    }

    @Override
    public void onItemDismiss(int position) {
        mEexerciseSetsAndTheirPreviousSets.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        ExerciseSetAndListPreviousExerciseSet prev = mEexerciseSetsAndTheirPreviousSets.remove(fromPosition);
        mEexerciseSetsAndTheirPreviousSets.add(toPosition > fromPosition ? toPosition - 1 : toPosition, prev);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public int getItemCount() { return mEexerciseSetsAndTheirPreviousSets.size(); }

    class ExerciseSetViewHolder extends RecyclerView.ViewHolder implements ExerciseSetTouchHelperViewHolder {
        TextView setStartTimeNameNumber;
        RecyclerView previousSetsRecycleView;
        EditText currentSetWeight;
        EditText currentSetReps;
        TextView personalBestWeightReps;

        private Context mContext;

        public ExerciseSetViewHolder(View itemView) {
            super(itemView);
            setStartTimeNameNumber = itemView.findViewById(R.id.setStartTimeNameNumber);
            previousSetsRecycleView = itemView.findViewById(R.id.previousSetsRecyclerView);
            currentSetWeight = itemView.findViewById(R.id.currentSetWeight);
            currentSetReps = itemView.findViewById(R.id.currentSetReps);
            personalBestWeightReps = itemView.findViewById(R.id.personalBestWeightAndReps);
            mContext = itemView.getContext();
        }

        public void bind(ExerciseSetAndListPreviousExerciseSet exerciseSetAndList, int position) {
            // mEexerciseSetsAndTheirPreviousSets
            // and also need position though..
            int startTimeSeconds = calculateSetStart(position);
            String startTime = formatStartTime(startTimeSeconds);

            setStartTimeNameNumber.setText(startTime + " "
                    + exerciseSetAndList.getExerciseSet().getExerciseName() + "-"
                    + Integer.toString(exerciseSetAndList.getExerciseSet().getSetNumber()));

            LinearLayoutManager layoutManager
                    = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
            previousSetsRecycleView.setLayoutManager(layoutManager);

            // DEBUGGING NEXT 4 LINEs
            List<PreviousExerciseSet> previousExerciseSets = exerciseSetAndList.getPreviousExerciseSets();
            previousExerciseSets.add(new PreviousExerciseSet(100, 6, "Yest", 2));
            previousExerciseSets.add(new PreviousExerciseSet(100, 6, "Yest", 2));
            previousExerciseSets.add(new PreviousExerciseSet(100, 6, "Yest", 2));

            PreviousSetAdapter adapter = new PreviousSetAdapter(mContext, previousExerciseSets);
            previousSetsRecycleView.setAdapter(adapter);

            personalBestWeightReps.setText(
                    Integer.toString(exerciseSetAndList.getExerciseSet().getPbWeight()) + " "
                            + Integer.toString(exerciseSetAndList.getExerciseSet().getPbReps()));

            previousSetsRecycleView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                        // Disallow the touch request for parent scroll on touch of child view
                        view.getParent().requestDisallowInterceptTouchEvent(true);
                    }
                    return false;
                }
            });
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }

        private int calculateSetStart(int position) {
            int setStart = 0; // seconds

            int i = 0; //
            while (i < position) {
                ExerciseSetAndListPreviousExerciseSet exerciseSetAndListPreviousExerciseSet
                        = mEexerciseSetsAndTheirPreviousSets.get(position);
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
}
