package com.heliam1.HowToBeFit.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.heliam1.HowToBeFit.data.HowtobefitContract.WorkoutEntry;
import com.heliam1.HowToBeFit.data.HowtobefitContract.ExerciseSetEntry;

public class HowtobefitProvider extends ContentProvider {
    public static final String LOG_TAG = HowtobefitProvider.class.getSimpleName();

    private static final int WORKOUTS = 100;        // URI matcher code for workouts table
    private static final int WORKOUT_ID = 101;      // URI matcher code for the content URI for a single task in the workouts table
    private static final int EXERCISE_SETS = 200;   // URI matcher code for the content URI for the exercisesets table
    private static final int EXERCISE_SET_ID = 201; // URI matcher code for the content URI for a single step in the exercise sets table

    /**
     * UriMatcher object to match a content URI to a corresponding code. The input passed into the
     * constructor represents the code to return for the root URI.It's common to use NO_MATCH as the
     * input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        sUriMatcher.addURI(HowtobefitContract.CONTENT_AUTHORITY,
                HowtobefitContract.PATH_WORKOUTS, WORKOUTS);

        sUriMatcher.addURI(HowtobefitContract.CONTENT_AUTHORITY,
                HowtobefitContract.PATH_WORKOUTS + "/#", WORKOUT_ID);

        sUriMatcher.addURI(HowtobefitContract.CONTENT_AUTHORITY,
                HowtobefitContract.PATH_EXERCISE_SET, EXERCISE_SETS);

        sUriMatcher.addURI(HowtobefitContract.CONTENT_AUTHORITY,
                HowtobefitContract.PATH_EXERCISE_SET + "/#", EXERCISE_SET_ID);
    }

    private HowtobefitDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new HowtobefitDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case WORKOUTS:
                cursor = database.query(WorkoutEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            case WORKOUT_ID:
                selection = WorkoutEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(WorkoutEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            case EXERCISE_SETS:
                cursor = database.query(ExerciseSetEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            case EXERCISE_SET_ID:
                selection = ExerciseSetEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(ExerciseSetEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        // Set notification URI on the Cursor,
        // so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the cursor
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case WORKOUTS:
                return insertWorkout(uri, contentValues);
            case EXERCISE_SETS:
                return insertExerciseSet(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertWorkout(Uri uri, ContentValues values) {
        values = sanitiseWorkout(values);
        // If values were not sanitary
        if (values == null) {
            return null;
        }

        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(WorkoutEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the task content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    private Uri insertExerciseSet(Uri uri, ContentValues values) {
        values = sanitiseExerciseSet(values);
        // If values were not sanitary
        if (values == null) {
            return null;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(ExerciseSetEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the task content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case WORKOUTS:
                return updateWorkout(uri, contentValues, selection, selectionArgs);
            case WORKOUT_ID:
                // For the TASK_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = WorkoutEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateWorkout(uri, contentValues, selection, selectionArgs);
            case EXERCISE_SETS:
                return updateExerciseSet(uri, contentValues, selection, selectionArgs);
            case EXERCISE_SET_ID:
                selection = ExerciseSetEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateExerciseSet(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateWorkout(Uri uri, ContentValues values, String selection,
                              String[] selectionArgs) {

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        values = sanitiseWorkout(values);
        // if values bad sanitised update failed
        if (values == null) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(WorkoutEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }

    private int updateExerciseSet(Uri uri, ContentValues values, String selection,
                              String[] selectionArgs) {

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        values = sanitiseExerciseSet(values);
        // if values bad sanitised update failed
        if (values == null) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(ExerciseSetEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }

    private ContentValues sanitiseWorkout(ContentValues values) {
        // Check sanity of input
        String name = values.getAsString(WorkoutEntry.COLUMN_WORKOUT_NAME);
        // TODO: Image
        String zonedDateTime = values.getAsString(WorkoutEntry.COLUMN_WORKOUT_LAST_DATE_COMPLETED);
        Integer durationSeconds = values.getAsInteger(WorkoutEntry.COLUMN_WORKOUT_DURATION);

        if (name == null || TextUtils.isEmpty(name)) {
            // TODO: Move this into view?
            // mToast.makeText(getContext(), "A workout requires a name", Toast.LENGTH_SHORT).show();
            return null;
        }

        if (zonedDateTime == null || TextUtils.isEmpty(zonedDateTime)) {
            // TODO: Move this into view?
            Log.e(LOG_TAG, "ERROR PASSING DATE TO TABLE");
            // mToast.makeText(getContext(), "ERROR PASSING DATE TO TABLE", Toast.LENGTH_SHORT).show();
            return null;
        }

        if (durationSeconds == null) {
            durationSeconds = 0;
            values.put(WorkoutEntry.COLUMN_WORKOUT_DURATION, durationSeconds);
        }

        return values;
    }

    private ContentValues sanitiseExerciseSet(ContentValues values) {
        Long workoutId = values.getAsLong(ExerciseSetEntry._WORKOUT_ID);
        String exerciseName = values.getAsString(ExerciseSetEntry.COLUMN_EXERCISE_NAME);
        Integer setNumber = values.getAsInteger(ExerciseSetEntry.COLUMN_SET_NUMBER);
        Integer setDuration = values.getAsInteger(ExerciseSetEntry.COLUMN_SET_DURATION);
        Integer setRest = values.getAsInteger(ExerciseSetEntry.COLUMN_SET_REST);
        Integer setWeight = values.getAsInteger(ExerciseSetEntry.COLUMN_SET_WEIGHT);
        Integer setReps = values.getAsInteger(ExerciseSetEntry.COLUMN_SET_REPS);
        String setDate = values.getAsString(ExerciseSetEntry.COLUMN_SET_DATE);
        Integer setOrder = values.getAsInteger(ExerciseSetEntry.COLUMN_SET_ORDER);
        Integer pbWeight = values.getAsInteger(ExerciseSetEntry.COLUMN_PB_WEIGHT);
        Integer pbReps = values.getAsInteger(ExerciseSetEntry.COLUMN_SET_REPS);

        // Sanitise
        if (workoutId == null) {
            // TODO: Move this into view?
            Log.e(LOG_TAG, "ERROR PASSING ID TO WORKOUT TABLE");
            // mToast.makeText(getContext(), "ERROR PASSING ID TO WORKOUT TABLE",
            //        Toast.LENGTH_SHORT).show();
            return null;
        }
        if (exerciseName == null) {
            // TODO: Move this into view?
            // mToast.makeText(getContext(), "A workout requires a name", Toast.LENGTH_SHORT).show();
            return null;
        }
        if (setNumber == null) {
            Log.e(LOG_TAG, "ERROR PASSING SET NUMBER TO WORKOUT TABLE");
            // mToast.makeText(getContext(), "ERROR PASSING SET NUMBER TO WORKOUT TABLE", Toast.LENGTH_SHORT).show();
            return null;
        }
        if (setDuration == null) {
            setDuration = 30; // seconds
            values.put(ExerciseSetEntry.COLUMN_SET_DURATION, setDuration);
        }
        if (setRest == null) {
            setRest = 30;
            values.put(ExerciseSetEntry.COLUMN_SET_REST, setRest);
        }
        if (setWeight == null) {
            setWeight = 0;
            values.put(ExerciseSetEntry.COLUMN_SET_WEIGHT, setWeight);
        }
        if (setReps == null) {
            setReps = 1;
            values.put(ExerciseSetEntry.COLUMN_SET_REPS, setReps);
        }
        if (setReps < 0) {
            Log.e(LOG_TAG, "ERROR PASSING SET REPS TO WORKOUT TABLE");
            // mToast.makeText(getContext(), "Set reps cannot be negative",
            //        Toast.LENGTH_SHORT).show();
            return null;
        }
        if (setDate == null) {
            // TODO: Move this into view?
            Log.e(LOG_TAG, "ERROR PASSING DATE TO TABLE");
            // mToast.makeText(getContext(), "ERROR PASSING DATE TO TABLE", Toast.LENGTH_SHORT).show();
            return null;
        }
        if (setOrder == null) {
            Log.e(LOG_TAG, "ERROR PASSING SET ORDER TO WORKOUT TABLE");
            // mToast.makeText(getContext(), "Set order cannot be negative",
            //         Toast.LENGTH_SHORT).show();
            return null;
        }
        if (pbWeight == null) {
            pbWeight = 0;
            values.put(ExerciseSetEntry.COLUMN_PB_WEIGHT, pbWeight);
        }
        if (pbReps == null) {
            pbReps = 0;
            values.put(ExerciseSetEntry.COLUMN_PB_REPS, pbReps);
        }

        return values;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case WORKOUTS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(WorkoutEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case WORKOUT_ID:
                // Delete a single row given by the ID in the URI
                selection = WorkoutEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(WorkoutEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case EXERCISE_SETS:
                rowsDeleted = database.delete(ExerciseSetEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case EXERCISE_SET_ID:
                // Delete a single row given by the ID in the URI
                selection = ExerciseSetEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(ExerciseSetEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case WORKOUTS:
                return WorkoutEntry.CONTENT_LIST_TYPE;
            case WORKOUT_ID:
                return WorkoutEntry.CONTENT_ITEM_TYPE;
            case EXERCISE_SETS:
                return ExerciseSetEntry.CONTENT_LIST_TYPE;
            case EXERCISE_SET_ID:
                return ExerciseSetEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }


}
