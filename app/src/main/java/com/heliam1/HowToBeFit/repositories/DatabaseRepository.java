package com.heliam1.HowToBeFit.repositories;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;

import com.heliam1.HowToBeFit.data.HowtobefitContract;
import com.heliam1.HowToBeFit.models.ExerciseSet;
import com.heliam1.HowToBeFit.models.Workout;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;

public class DatabaseRepository implements WorkoutRepository, ExerciseSetRepository {
    private final ContentResolver contentResolver;

    public DatabaseRepository(Context context) {
        this.contentResolver = context.getContentResolver();
    }

    @Override
    public Single<List<Workout>> getWorkouts() {
        return Single.fromCallable(() -> {
            try {
                return queryWorkouts();
            } catch (Exception e) {
                throw new RuntimeException("Something wrong with db");
            }
        });
    }

    @Override
    public Single<List<ExerciseSet>> getExerciseSetsById(long id) {
        return Single.fromCallable(() -> {
            try {
                return queryExerciseSetsByWorkoutId(id);
            } catch (Exception e) {
                throw new RuntimeException("Something wrong with db");
            }
        });
    }

    public List<Workout> queryWorkouts() {
        String[] projection = {
                HowtobefitContract.WorkoutEntry._ID,
                HowtobefitContract.WorkoutEntry.COLUMN_WORKOUT_NAME,
                HowtobefitContract.WorkoutEntry.COLUMN_WORKOUT_IMAGE,
                HowtobefitContract.WorkoutEntry.COLUMN_WORKOUT_LAST_DATE_COMPLETED,
                HowtobefitContract.WorkoutEntry.COLUMN_WORKOUT_DURATION};

        String sortOrder = HowtobefitContract.WorkoutEntry._ID;

        Cursor cursor = contentResolver.query(HowtobefitContract.ExerciseSetEntry.CONTENT_URI,
                projection, null, null, sortOrder);

        List<Workout> workouts = new ArrayList<Workout>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            workouts.add(new Workout(
                    cursor.getLong(cursor.getColumnIndex(
                            HowtobefitContract.WorkoutEntry._ID)),
                    cursor.getString(cursor.getColumnIndex(
                            HowtobefitContract.WorkoutEntry.COLUMN_WORKOUT_NAME)),
                    cursor.getInt(cursor.getColumnIndex(
                            HowtobefitContract.WorkoutEntry.COLUMN_WORKOUT_IMAGE)),
                    cursor.getString(cursor.getColumnIndex(
                            HowtobefitContract.WorkoutEntry.COLUMN_WORKOUT_LAST_DATE_COMPLETED)),
                    cursor.getInt(cursor.getColumnIndex(
                            HowtobefitContract.WorkoutEntry.COLUMN_WORKOUT_DURATION))));
            cursor.moveToNext();
        }
        cursor.close();

        return workouts;
    }

    public List<ExerciseSet> queryExerciseSetsByWorkoutId(long workoutId) {
        String[] projection = {
                HowtobefitContract.ExerciseSetEntry._ID,
                HowtobefitContract.ExerciseSetEntry._WORKOUT_ID,
                HowtobefitContract.ExerciseSetEntry.COLUMN_EXERCISE_NAME,
                HowtobefitContract.ExerciseSetEntry.COLUMN_SET_NUMBER,
                HowtobefitContract.ExerciseSetEntry.COLUMN_SET_DURATION,
                HowtobefitContract.ExerciseSetEntry.COLUMN_SET_REST,
                HowtobefitContract.ExerciseSetEntry.COLUMN_SET_WEIGHT,
                HowtobefitContract.ExerciseSetEntry.COLUMN_SET_REPS,
                HowtobefitContract.ExerciseSetEntry.COLUMN_SET_DATE,
                HowtobefitContract.ExerciseSetEntry.COLUMN_SET_ORDER,
                HowtobefitContract.ExerciseSetEntry.COLUMN_PB_WEIGHT,
                HowtobefitContract.ExerciseSetEntry.COLUMN_PB_REPS};

        String selection = HowtobefitContract.ExerciseSetEntry._WORKOUT_ID + "=?";

        String[] selectionArgs = {Long.toString(workoutId)};

        // String sortOrder = ExerciseSetEntry.COLUMN_SET_DATE + " DESC";
        String sortOrder = HowtobefitContract.ExerciseSetEntry.COLUMN_SET_ORDER + " ASC";

        Cursor cursor = contentResolver.query(HowtobefitContract.ExerciseSetEntry.CONTENT_URI,
                projection, selection, selectionArgs, sortOrder);

        List<ExerciseSet> exerciseSets = new ArrayList<ExerciseSet>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            exerciseSets.add(new ExerciseSet(
                    cursor.getLong(cursor.getColumnIndex(
                            HowtobefitContract.ExerciseSetEntry._ID)),
                    cursor.getLong(cursor.getColumnIndex(
                            HowtobefitContract.ExerciseSetEntry._WORKOUT_ID)),
                    cursor.getString(cursor.getColumnIndex(
                            HowtobefitContract.ExerciseSetEntry.COLUMN_EXERCISE_NAME)),
                    cursor.getInt(cursor.getColumnIndex(
                            HowtobefitContract.ExerciseSetEntry.COLUMN_SET_NUMBER)),
                    cursor.getInt(cursor.getColumnIndex(
                            HowtobefitContract.ExerciseSetEntry.COLUMN_SET_DURATION)),
                    cursor.getInt(cursor.getColumnIndex(
                            HowtobefitContract.ExerciseSetEntry.COLUMN_SET_REST)),
                    cursor.getDouble(cursor.getColumnIndex(
                            HowtobefitContract.ExerciseSetEntry.COLUMN_SET_WEIGHT)),
                    cursor.getInt(cursor.getColumnIndex(
                            HowtobefitContract.ExerciseSetEntry.COLUMN_SET_REPS)),
                    cursor.getString(cursor.getColumnIndex(
                            HowtobefitContract.ExerciseSetEntry.COLUMN_SET_DATE)),
                    cursor.getInt(cursor.getColumnIndex(
                            HowtobefitContract.ExerciseSetEntry.COLUMN_SET_ORDER)),
                    cursor.getInt(cursor.getColumnIndex(
                            HowtobefitContract.ExerciseSetEntry.COLUMN_PB_WEIGHT)),
                    cursor.getInt(cursor.getColumnIndex(
                            HowtobefitContract.ExerciseSetEntry.COLUMN_PB_REPS))));

            cursor.moveToNext();
        }
        cursor.close();

        return exerciseSets;
    }
}
