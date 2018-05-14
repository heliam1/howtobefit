package com.heliam1.HowToBeFit.repositories;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.heliam1.HowToBeFit.data.HowtobefitContract.WorkoutEntry;
import com.heliam1.HowToBeFit.data.HowtobefitContract.ExerciseSetEntry;
import com.heliam1.HowToBeFit.models.ExerciseSet;
import com.heliam1.HowToBeFit.models.ExerciseSetAndListPreviousExerciseSet;
import com.heliam1.HowToBeFit.models.PreviousExerciseSet;
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
    public Single<List<ExerciseSet>> getExerciseSetsByWorkoutId(long id) {
        return Single.fromCallable(() -> {
            try {
                return queryExerciseSetsByWorkoutId(id);
            } catch (Exception e) {
                throw new RuntimeException("Something wrong with db");
            }
        });
    }

    @Override
    public Single<List<PreviousExerciseSet>> getPreviousSets(String name, int setNumber) {
        return Single.fromCallable(() -> {
            try {
                return queryPreviousExerciseSets(name, setNumber);
            } catch (Exception e) {
                throw new RuntimeException("Something wrong with db");
            }
        });
    }

    @Override
    public Single<List<ExerciseSetAndListPreviousExerciseSet>>
    getExerciseSetsByWorkoutIdandPreviousSets(long id) {
        return Single.fromCallable(() -> {
            try {
                List<ExerciseSet> exerciseSets = queryExerciseSetsByWorkoutId(id);

                List<ExerciseSetAndListPreviousExerciseSet> list = new ArrayList<>();

                for (int i = 0; i < exerciseSets.size(); i++)
                {
                    ExerciseSet exerciseSet = exerciseSets.get(i);

                    List<PreviousExerciseSet> previousExerciseSets =
                            queryPreviousExerciseSets(exerciseSet.getExerciseName(),
                                    exerciseSet.getSetNumber());

                    list.add(new ExerciseSetAndListPreviousExerciseSet(exerciseSet,
                            previousExerciseSets));
                }

                return list;
            } catch (Exception e) {
                throw new RuntimeException("Something wrong with db");
            }
        });
    }

    private List<Workout> queryWorkouts() {
        String[] projection = {
                WorkoutEntry._ID,
                WorkoutEntry.COLUMN_WORKOUT_NAME,
                WorkoutEntry.COLUMN_WORKOUT_IMAGE,
                WorkoutEntry.COLUMN_WORKOUT_LAST_DATE_COMPLETED,
                WorkoutEntry.COLUMN_WORKOUT_DURATION};

        String sortOrder = WorkoutEntry._ID;

        Cursor cursor = contentResolver.query(WorkoutEntry.CONTENT_URI,
                projection, null, null, sortOrder);

        List<Workout> workouts = new ArrayList<Workout>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            workouts.add(new Workout(
                    cursor.getLong(cursor.getColumnIndex(WorkoutEntry._ID)),
                    cursor.getString(cursor.getColumnIndex(WorkoutEntry.COLUMN_WORKOUT_NAME)),
                    cursor.getInt(cursor.getColumnIndex(WorkoutEntry.COLUMN_WORKOUT_IMAGE)),
                    cursor.getString(cursor.getColumnIndex(
                            WorkoutEntry.COLUMN_WORKOUT_LAST_DATE_COMPLETED)),
                    cursor.getInt(cursor.getColumnIndex(WorkoutEntry.COLUMN_WORKOUT_DURATION))));
            cursor.moveToNext();
        }
        cursor.close();

        return workouts;
    }

    private List<ExerciseSet> queryExerciseSetsByWorkoutId(long workoutId) {
        String[] projection = {
                ExerciseSetEntry._ID,
                ExerciseSetEntry._WORKOUT_ID,
                ExerciseSetEntry.COLUMN_EXERCISE_NAME,
                ExerciseSetEntry.COLUMN_SET_NUMBER,
                ExerciseSetEntry.COLUMN_SET_DURATION,
                ExerciseSetEntry.COLUMN_SET_REST,
                ExerciseSetEntry.COLUMN_SET_WEIGHT,
                ExerciseSetEntry.COLUMN_SET_REPS,
                ExerciseSetEntry.COLUMN_SET_DATE_STRING,
                ExerciseSetEntry.COLUMN_SET_DATE_LONG,
                ExerciseSetEntry.COLUMN_SET_ORDER,
                ExerciseSetEntry.COLUMN_PB_WEIGHT,
                ExerciseSetEntry.COLUMN_PB_REPS};

        String selection = ExerciseSetEntry._WORKOUT_ID + "=?"
                + " AND " + ExerciseSetEntry.COLUMN_SET_ORDER + "!=?";

        String[] selectionArgs = {Long.toString(workoutId, -1)};

        // String sortOrder = ExerciseSetEntry.COLUMN_SET_DATE + " DESC";
        String sortOrder = ExerciseSetEntry.COLUMN_SET_ORDER + " ASC";

        Cursor cursor = contentResolver.query(ExerciseSetEntry.CONTENT_URI,
                projection, selection, selectionArgs, sortOrder);

        List<ExerciseSet> exerciseSets = new ArrayList<ExerciseSet>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            exerciseSets.add(new ExerciseSet(
                    cursor.getLong(cursor.getColumnIndex(ExerciseSetEntry._ID)),
                    cursor.getLong(cursor.getColumnIndex(ExerciseSetEntry._WORKOUT_ID)),
                    cursor.getString(cursor.getColumnIndex(ExerciseSetEntry.COLUMN_EXERCISE_NAME)),
                    cursor.getInt(cursor.getColumnIndex(ExerciseSetEntry.COLUMN_SET_NUMBER)),
                    cursor.getLong(cursor.getColumnIndex(ExerciseSetEntry.COLUMN_SET_DURATION)),
                    cursor.getLong(cursor.getColumnIndex(ExerciseSetEntry.COLUMN_SET_REST)),
                    cursor.getDouble(cursor.getColumnIndex(ExerciseSetEntry.COLUMN_SET_WEIGHT)),
                    cursor.getInt(cursor.getColumnIndex(ExerciseSetEntry.COLUMN_SET_REPS)),
                    cursor.getString(cursor.getColumnIndex(ExerciseSetEntry.COLUMN_SET_DATE_STRING)),
                    cursor.getLong(cursor.getColumnIndex(ExerciseSetEntry.COLUMN_SET_DATE_LONG)),
                    cursor.getInt(cursor.getColumnIndex(ExerciseSetEntry.COLUMN_SET_ORDER)),
                    cursor.getDouble(cursor.getColumnIndex(ExerciseSetEntry.COLUMN_PB_WEIGHT)),
                    cursor.getInt(cursor.getColumnIndex(ExerciseSetEntry.COLUMN_PB_REPS))));

            cursor.moveToNext();
        }
        cursor.close();

        return exerciseSets;
    }

    public List<PreviousExerciseSet> queryPreviousExerciseSets(String exerciseSetName,
                                                                         int exerciseSetSetNumber ) {
        String[] projection = {
                // ExerciseSetEntry.COLUMN_EXERCISE_NAME,
                // ExerciseSetEntry.COLUMN_SET_NUMBER,
                ExerciseSetEntry.COLUMN_SET_WEIGHT,
                ExerciseSetEntry.COLUMN_SET_REPS,
                ExerciseSetEntry.COLUMN_SET_DATE_STRING,
                ExerciseSetEntry.COLUMN_SET_DATE_LONG};

        String selection = ExerciseSetEntry.COLUMN_EXERCISE_NAME + "=?"
                + " AND " + ExerciseSetEntry.COLUMN_SET_NUMBER + "=?";

        String[] selectionArgs = {exerciseSetName, Integer.toString(exerciseSetSetNumber)};

        // Assumes recycler view will be from left to right
        String sortOrder = ExerciseSetEntry.COLUMN_SET_DATE_LONG + " ASC";

        Cursor cursor = contentResolver.query(ExerciseSetEntry.CONTENT_URI,
                projection, selection, selectionArgs, sortOrder);

        List<PreviousExerciseSet> previousExerciseSets = new ArrayList<PreviousExerciseSet>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            previousExerciseSets.add(new PreviousExerciseSet(
                    cursor.getDouble(cursor.getColumnIndex(ExerciseSetEntry.COLUMN_SET_WEIGHT)),
                    cursor.getInt(cursor.getColumnIndex(ExerciseSetEntry.COLUMN_SET_REPS)),
                    cursor.getString(cursor.getColumnIndex(ExerciseSetEntry.COLUMN_SET_DATE_STRING)),
                    cursor.getLong(cursor.getColumnIndex(ExerciseSetEntry.COLUMN_SET_DATE_LONG))));

            cursor.moveToNext();
        }
        cursor.close();

        return previousExerciseSets;
    }

    @Override
    public Single<Long> saveWorkout(Workout workout) {
        return Single.fromCallable(() -> {
            try {
                return upsertWorkout(workout);
            } catch (Exception e) {
                throw new RuntimeException("Something wrong with db");
            }
        });
    }

    @Override
    public Single<Long> saveExerciseSet(ExerciseSet exerciseSet) {
        return Single.fromCallable(() -> {
            try {
                return upsertExerciseSet(exerciseSet);
            } catch (Exception e) {
                throw new RuntimeException("Something wrong with db");
            }
        });
    }

    private Long upsertWorkout(Workout workout) {
        ContentValues values = new ContentValues();
        values.put(WorkoutEntry.COLUMN_WORKOUT_NAME, workout.getName());
        values.put(WorkoutEntry.COLUMN_WORKOUT_IMAGE, workout.getImage());
        values.put(WorkoutEntry.COLUMN_WORKOUT_LAST_DATE_COMPLETED, workout.getDate());
        values.put(WorkoutEntry.COLUMN_WORKOUT_DURATION, workout.getDuration());

        // uri or Long savedWorkoutId; deffs want long but convert from uri?
        Uri uri;

        if (workout.hasId()) {
            contentResolver.update(
                    ContentUris.withAppendedId(WorkoutEntry.CONTENT_URI, workout.getId()),
                    values, null, null);

            // TODO:
            uri = ContentUris.withAppendedId(WorkoutEntry.CONTENT_URI, workout.getId());
        } else {
            uri = contentResolver.insert(WorkoutEntry.CONTENT_URI, values);
        }
        return parseUriToId(uri);
    }

    private Long upsertExerciseSet(ExerciseSet exerciseSet) {
        ContentValues values = new ContentValues();
        values.put(ExerciseSetEntry._WORKOUT_ID, exerciseSet.getWorkoutId());
        values.put(ExerciseSetEntry.COLUMN_EXERCISE_NAME, exerciseSet.getExerciseName());
        values.put(ExerciseSetEntry.COLUMN_SET_NUMBER, exerciseSet.getSetNumber());
        values.put(ExerciseSetEntry.COLUMN_SET_DURATION, exerciseSet.getSetDuration());
        values.put(ExerciseSetEntry.COLUMN_SET_REST, exerciseSet.getSetRest());
        values.put(ExerciseSetEntry.COLUMN_SET_WEIGHT, exerciseSet.getSetWeight());
        values.put(ExerciseSetEntry.COLUMN_SET_REPS, exerciseSet.getSetReps());
        values.put(ExerciseSetEntry.COLUMN_SET_DATE_STRING, exerciseSet.getSetDateString());
        values.put(ExerciseSetEntry.COLUMN_SET_DATE_LONG, exerciseSet.getSetDateLong());
        values.put(ExerciseSetEntry.COLUMN_SET_ORDER, exerciseSet.getSetOrder());
        values.put(ExerciseSetEntry.COLUMN_PB_WEIGHT, exerciseSet.getPbWeight());
        values.put(ExerciseSetEntry.COLUMN_PB_REPS, exerciseSet.getPbReps());

        // uri or Long savedWorkoutId; deffs want long but convert from uri?
        Uri uri;

        if (exerciseSet.getId() != null) {
            contentResolver.update(
                    ContentUris.withAppendedId(ExerciseSetEntry.CONTENT_URI, exerciseSet.getId()),
                    values, null, null);

            // TODO:
            uri = ContentUris.withAppendedId(ExerciseSetEntry.CONTENT_URI, exerciseSet.getId());
        } else {
            uri = contentResolver.insert(ExerciseSetEntry.CONTENT_URI, values);
        }
        return parseUriToId(uri);
    }

    Long parseUriToId(Uri uri) {
        String idString = uri.toString();
        idString = idString.replaceFirst("1","");
        String idValueString = idString.replaceAll("[^0-9]", "");
        Log.v("Task:", "extracted long id: " + idValueString);
        return Long.parseLong(idValueString);
    }

    // if workout id == null, insert, else, update?
    // or just have separate methods?

    // need thigns for delete

    @Override
    public Single<Long> deleteWorkout(Workout workout) {
        return Single.fromCallable(() -> {
            try {
                Long id  = workout.getId();
                contentResolver.delete(
                        ContentUris.withAppendedId(WorkoutEntry.CONTENT_URI, workout.getId()),
                        null, null);
                return id;
            } catch (Exception e) {
                throw new RuntimeException("Something wrong with db");
            }
        });
    }

    @Override
    public Single<Long> deleteExerciseSet(ExerciseSet exerciseSet) {
        return Single.fromCallable(() -> {
            try {
                Long id  = exerciseSet.getId();
                contentResolver.delete(
                        ContentUris.withAppendedId(ExerciseSetEntry.CONTENT_URI, exerciseSet.getId()),
                        null, null);
                return id;
            } catch (Exception e) {
                throw new RuntimeException("Something wrong with db");
            }
        });
    }
}
