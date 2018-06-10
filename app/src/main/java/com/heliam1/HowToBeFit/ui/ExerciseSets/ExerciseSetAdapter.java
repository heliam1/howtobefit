package com.heliam1.HowToBeFit.ui.ExerciseSets;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableConverter;
import io.reactivex.subjects.PublishSubject;

public class ExerciseSetAdapter extends BaseAdapter {
        /* RecyclerView.Adapter<ExerciseSetAdapter.ExerciseSetViewHolder> /*implements ExerciseSetTouchHelperAdapter {*/
    private Context mContext;
    private List<StartTimeExerciseSetListPreviousExerciseSet> mList;

    private final PublishSubject<StartTimeExerciseSetListPreviousExerciseSet> onClickSubject = PublishSubject.create();

    public ExerciseSetAdapter(Context context, List<StartTimeExerciseSetListPreviousExerciseSet> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public StartTimeExerciseSetListPreviousExerciseSet getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return  mList.get(position).getStartTime();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // inflate the layout for each list row
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).
                    inflate(R.layout.item_exerciseset, parent, false);
        }

        // get current item to be displayed
        StartTimeExerciseSetListPreviousExerciseSet startExsetListprev = getItem(position);

        TextView setStartTime =convertView.findViewById(R.id.setStartTime);
        TextView setNameNumber = convertView.findViewById(R.id.setNameNumber);
        TextView currentSetWeight = convertView.findViewById(R.id.currentSetWeight);
        TextView currentSetReps = convertView.findViewById(R.id.currentSetReps);

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

        currentSetWeight.setText(Double.toString(startExsetListprev.getExerciseSet().getSetWeight()));
        currentSetReps.setText(Integer.toString(startExsetListprev.getExerciseSet().getSetReps()));

        return convertView;
    }

    @Override
    public int getCount() {
        return  mList.size();
    }

    /*
    @Override
    public ExerciseSetViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();

        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachImmediatelyToParent = false;

        View view = inflater.inflate(R.layout.item_exerciseset, viewGroup, shouldAttachImmediatelyToParent);
        ExerciseSetViewHolder viewHolder = new ExerciseSetViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ExerciseSetViewHolder holder, int position) {
        final StartTimeExerciseSetListPreviousExerciseSet element = mList.get(position);
        holder.bind(element, position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSubject.onNext(element);
            }
        });
    }
    */

    /*
    public Observable<StartTimeExerciseSetListPreviousExerciseSet> getPositionClicks() {
        return (Observable<StartTimeExerciseSetListPreviousExerciseSet>) onClickSubject;
    } */

    /*
    @Override
    public void onItemDismiss(int position) {
        mExerciseSetsPresenter.getExerciseSetsAndListPreviousExerciseSets().remove(position);
        mExerciseSetsPresenter.setStartTimes();
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        StartTimeExerciseSetListPreviousExerciseSet prev = mExerciseSetsPresenter.getExerciseSetsAndListPreviousExerciseSets().remove(fromPosition);
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
    } */

    /*
    @Override
    public int getItemCount() { return mList.size(); }

    class ExerciseSetViewHolder extends RecyclerView.ViewHolder implements ExerciseSetTouchHelperViewHolder {
        TextView setStartTime;
        TextView setNameNumber;
        RecyclerView previousSetsRecycleView;
        TextView currentSetWeight;
        TextView currentSetReps;

        private Context mContext;

        public ExerciseSetViewHolder(View itemView) {
            super(itemView);
            setStartTime = itemView.findViewById(R.id.setStartTime);
            setNameNumber = itemView.findViewById(R.id.setNameNumber);
            previousSetsRecycleView = itemView.findViewById(R.id.previousSetsRecyclerView);
            currentSetWeight = itemView.findViewById(R.id.currentSetWeight);
            currentSetReps = itemView.findViewById(R.id.currentSetReps);
            mContext = itemView.getContext();
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

            LinearLayoutManager layoutManager
                    = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
            previousSetsRecycleView.setLayoutManager(layoutManager);

            List<PreviousExerciseSet> previousExerciseSets = startExsetListprev.getPreviousExerciseSets();

            PreviousSetAdapter adapter = new PreviousSetAdapter(mContext, previousExerciseSets);
            previousSetsRecycleView.setAdapter(adapter);

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

            /*
            currentSetWeight.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return false;
                }
            });

            currentSetWeight.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return false;
                }
            }); */
        /*
            if (previousExerciseSets.size() > 0)
                previousSetsRecycleView.smoothScrollToPosition(adapter.getItemCount() - 1);
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
    */
}
