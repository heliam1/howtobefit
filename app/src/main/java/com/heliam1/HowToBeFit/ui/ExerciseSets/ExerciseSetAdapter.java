package com.heliam1.HowToBeFit.ui.ExerciseSets;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.heliam1.HowToBeFit.R;
import com.heliam1.HowToBeFit.models.StartTimeExerciseSetListPreviousExerciseSet;
import com.heliam1.HowToBeFit.models.PreviousExerciseSet;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableConverter;
import io.reactivex.subjects.PublishSubject;

public class ExerciseSetAdapter extends RecyclerView.Adapter<ExerciseSetAdapter.ExerciseSetViewHolder> implements ExerciseSetTouchHelperAdapter {

    private Context mContext;
    private ExerciseSetsPresenter mPresenter;

    public ExerciseSetAdapter(Context context, ExerciseSetsPresenter presenter) {
        mContext = context;
        mPresenter = presenter;
    }

    @Override
    public ExerciseSetViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.item_exerciseset, viewGroup, false);
        ExerciseSetViewHolder viewHolder = new ExerciseSetViewHolder(
                view, new CurrentSetWeightEditTextListener(), new CurrentSetRepsEditTextListener());

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ExerciseSetViewHolder holder, final int position) {
        final StartTimeExerciseSetListPreviousExerciseSet element = mPresenter.getCurrentElement(position);
        holder.bind(element, position);

        holder.weightEditTextListener.updatePosition(holder.getAdapterPosition());
        if (mPresenter.getCurrentElement(position).getExerciseSet().getSetWeight() != -1)
            holder.currentSetWeight.setText(Double.toString(mPresenter.getCurrentElement(position).getExerciseSet().getSetWeight()));
        else {
            holder.currentSetWeight.setText("");
        }

        holder.repsEditTextListener.updatePosition(holder.getAdapterPosition());
        if (mPresenter.getCurrentElement(position).getExerciseSet().getSetReps() != -1)
            holder.currentSetWeight.setText(Double.toString(mPresenter.getCurrentElement(position).getExerciseSet().getSetWeight()));
        else {
            holder.currentSetWeight.setText("");
        }
    }


    @Override
    public void onItemDismiss(int position) {
        mPresenter.deleteExerciseSet(mPresenter.getCurrentElement(position));
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }


    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        StartTimeExerciseSetListPreviousExerciseSet prev = mPresenter.getList().remove(fromPosition);
        mPresenter.getList().add(toPosition > fromPosition ? toPosition - 1 : toPosition, prev);
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
    public int getItemCount() { return mPresenter.getList().size(); }

    class ExerciseSetViewHolder extends RecyclerView.ViewHolder implements ExerciseSetTouchHelperViewHolder {
        TextView setStartTime;
        TextView setNameNumber;
        EditText currentSetWeight;
        CurrentSetWeightEditTextListener weightEditTextListener;
        CurrentSetRepsEditTextListener repsEditTextListener;
        EditText currentSetReps;

        private Context mContext;

        public ExerciseSetViewHolder(View itemView, CurrentSetWeightEditTextListener weightListener, CurrentSetRepsEditTextListener repsListener) {
            super(itemView);
            setStartTime = itemView.findViewById(R.id.setStartTime);
            setNameNumber = itemView.findViewById(R.id.setNameNumber);
            currentSetWeight = itemView.findViewById(R.id.currentSetWeight);
            currentSetReps = itemView.findViewById(R.id.currentSetReps);
            mContext = itemView.getContext();

            weightEditTextListener = weightListener;
            repsEditTextListener = repsListener;

            currentSetWeight.addTextChangedListener(weightEditTextListener);
            currentSetReps.addTextChangedListener(repsEditTextListener);
        }

        public void bind(StartTimeExerciseSetListPreviousExerciseSet startExsetListprev, int position) {
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

    private class CurrentSetWeightEditTextListener implements TextWatcher {
        private int position;

        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            // no op
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            if (charSequence.toString().matches("[0-9]+") || charSequence.toString().matches("-?\\d+(\\.\\d+)?")){
                mPresenter.getCurrentElement(position).getExerciseSet().setSetWeight(Double.parseDouble(charSequence.toString()));
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // no op
        }
    }

    private class CurrentSetRepsEditTextListener implements TextWatcher {
        private int position;

        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            // no op
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            if (charSequence.toString().matches("[0-9]+")){
                mPresenter.getCurrentElement(position).getExerciseSet().setSetWeight(Double.parseDouble(charSequence.toString()));
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // no op
        }
    }
}
