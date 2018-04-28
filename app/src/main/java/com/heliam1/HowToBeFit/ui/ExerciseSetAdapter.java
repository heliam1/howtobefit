package com.heliam1.HowToBeFit.ui;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
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
import com.heliam1.HowToBeFit.presenters.ExerciseSetsPresenter;

import java.util.ArrayList;
import java.util.List;

public class ExerciseSetAdapter extends RecyclerView.Adapter<ExerciseSetAdapter.ExerciseSetViewHolder> implements ExerciseSetTouchHelperAdapter {
    private Context mContext;
    private ExerciseSetsPresenter mExerciseSetsPresenter;

    // both of these are inside the Presenter
    // private List<ExerciseSetAndListPreviousExerciseSet> mExerciseSetsAndTheirPreviousSets;
    // private List<String> mStartTimes = new ArrayList<>();


    public ExerciseSetAdapter(Context context, ExerciseSetsPresenter exerciseSetsPresenter) {
        mContext = context;
        mExerciseSetsPresenter = exerciseSetsPresenter;
        mExerciseSetsPresenter.setStartTimes();
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
        holder.bind(mExerciseSetsPresenter.getExerciseSetsAndListPreviousExerciseSets().get(position), position);
    }

    @Override
    public void onItemDismiss(int position) {
        mExerciseSetsPresenter.getExerciseSetsAndListPreviousExerciseSets().remove(position);
        mExerciseSetsPresenter.setStartTimes();
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        ExerciseSetAndListPreviousExerciseSet prev = mExerciseSetsPresenter.getExerciseSetsAndListPreviousExerciseSets().remove(fromPosition);
        mExerciseSetsPresenter.getExerciseSetsAndListPreviousExerciseSets().add(toPosition > fromPosition ? toPosition - 1 : toPosition, prev);
        mExerciseSetsPresenter.setStartTimes();
        notifyItemMoved(fromPosition, toPosition);
        // Observer Pattern - An obsservable/the subject maintains a list of its dependencies (observers/
        // subscribers) and notifies them automatically of any state changes usually by
        // calling one of their methods
        // Define a subject and observers, when the subject state changes all registered subscribers are notified
        // The responsibility of subscribers is to register and unregister themselves on a subject and update
        // their state
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() { return mExerciseSetsPresenter.getExerciseSetsAndListPreviousExerciseSets().size(); }

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

            String setStartTimeNameNumberString = mExerciseSetsPresenter.getStartTimes().get(position) + " "
                    + exerciseSetAndList.getExerciseSet().getExerciseName() + "-"
                    + Integer.toString(exerciseSetAndList.getExerciseSet().getSetNumber());

            setStartTimeNameNumber.setText(setStartTimeNameNumberString);

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

            personalBestWeightReps.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    previousSetsRecycleView.smoothScrollToPosition(adapter.getItemCount() - 1);
                    return true;
                }
            });
        }

        @Override
        public void onItemSelected() {
            // itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }
}
