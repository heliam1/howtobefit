package com.heliam1.HowToBeFit.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.heliam1.HowToBeFit.R;
import com.heliam1.HowToBeFit.models.ExerciseSet;
import com.heliam1.HowToBeFit.models.PreviousExerciseSet;


import java.util.List;

// TODO: THis needs to be a recycler view
public class PreviousSetAdapter extends RecyclerView.Adapter<PreviousSetAdapter.PreviousSetViewHolder> {

    private Context mContext;
    private List<PreviousExerciseSet> mPreviousExerciseSets;

    public PreviousSetAdapter(Context context, List<PreviousExerciseSet> previousExerciseSets) {
        mContext = context;
        mPreviousExerciseSets = previousExerciseSets;
    }

    class PreviousSetViewHolder extends RecyclerView.ViewHolder {
        TextView previousSetWeightAndReps;

        private Context mContext;

        public PreviousSetViewHolder(View itemView) {
            super(itemView);
            previousSetWeightAndReps = (TextView) itemView.findViewById(R.id.previousExerciseSetDateWeightRep);
            mContext = itemView.getContext();
        }

        public void bind(PreviousExerciseSet previousExerciseSet) {
            previousSetWeightAndReps.setText("26-04" + "\n" + previousExerciseSet.getSetWeight() + " "
                    + previousExerciseSet.getSetReps());
        }
    }

    @Override
    public PreviousSetViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        //
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachImmediatelyToParent = false;

        View view = inflater.inflate(R.layout.item_previousset, viewGroup, shouldAttachImmediatelyToParent);
        PreviousSetViewHolder viewHolder = new PreviousSetViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PreviousSetViewHolder holder, int position) {
        holder.bind(mPreviousExerciseSets.get(position));
    }

    @Override
    public int getItemCount() { return mPreviousExerciseSets.size(); }
}
