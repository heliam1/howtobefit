package com.heliam1.HowToBeFit.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.heliam1.HowToBeFit.R;
import com.heliam1.HowToBeFit.models.Workout;

import java.util.List;

public class WorkoutListAdapter extends BaseAdapter {

    private Context mContext;
    private List<Workout> mWorkouts;
    private static final int VERTICAL_DIMENSION_MULTIPLIER = 8;

    public WorkoutListAdapter(Context context, List<Workout> workouts) {
        // super(context, 0, workouts);
        mContext = context;
        mWorkouts = workouts;

    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        View workoutView = convertView;

        if (workoutView == null) {
            workoutView = LayoutInflater.from(mContext).inflate(R.layout.item_workout, parent, false);
        }

        Workout workout = getItem(position);

        ImageView workoutImageView = workoutView.findViewById(R.id.image_view_image_workout);
        EditText workoutNameView = workoutView.findViewById(R.id.edit_text_name_workout);
        TextView workoutDateView = workoutView.findViewById(R.id.text_view_date_workout);

        workoutImageView.requestLayout();
        workoutImageView.getLayoutParams().height =
                (int) (VERTICAL_DIMENSION_MULTIPLIER * workout.getDuration());

        workoutImageView.setMinimumHeight(VERTICAL_DIMENSION_MULTIPLIER * workout.getDuration());

        // workoutImageView.setImage( // TODO:
        workoutNameView.setText(workout.getName());
        workoutDateView.setText(workout.getDate());

        return workoutView;
    }

    @Override
    public int getCount() { return mWorkouts.size(); }

    @Override
    public Workout getItem(int i) {
        return mWorkouts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }
}
