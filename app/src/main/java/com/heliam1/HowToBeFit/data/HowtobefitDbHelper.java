package com.heliam1.HowToBeFit.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.heliam1.HowToBeFit.data.HowtobefitContract.WorkoutEntry;
import com.heliam1.HowToBeFit.data.HowtobefitContract.ExerciseSetEntry;
import com.heliam1.HowToBeFit.models.ExerciseSet;

public class HowtobefitDbHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = HowtobefitDbHelper.class.getSimpleName();

    // .db file name
    private static final String DATABASE_NAME = "heliam1.TimeLord.db";

    // Database version. If you change the database schema, increment the database version.
    private static final int DATABASE_VERSION = 1;

    // Constructs a new instance of {@link HowtobefitDbHelper}.
    // @param context of the app
    public HowtobefitDbHelper(Context context) {
        super(context, DATABASE_NAME, null,
                DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.v(LOG_TAG, "Database created");
        // Create a String that contains the SQL statement to create the tasks table
        String SQL_CREATE_WORKOUTS_TABLE = "CREATE TABLE " + WorkoutEntry.TABLE_NAME + " ("
                + WorkoutEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + WorkoutEntry.COLUMN_WORKOUT_NAME + " TEXT NOT NULL, "
                + WorkoutEntry.COLUMN_WORKOUT_IMAGE + " BLOB, "
                + WorkoutEntry.COLUMN_WORKOUT_LAST_DATE_COMPLETED + " INTEGER NOT NULL, " // ZonedDateTime
                + WorkoutEntry.COLUMN_WORKOUT_DURATION + " INTEGER NOT NULL DEFAULT 0);";

        String SQL_CREATE_EXERCISE_SETS_TABLE = "CREATE TABLE " + ExerciseSetEntry.TABLE_NAME + " ("
                + ExerciseSetEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ExerciseSetEntry._WORKOUT_ID + " INTEGER NOT NULL, "
                + ExerciseSetEntry.COLUMN_EXERCISE_NAME + " TEXT NOT NULL, "
                + ExerciseSetEntry.COLUMN_SET_NUMBER + " INTEGER NOT NULL, "
                + ExerciseSetEntry.COLUMN_SET_DURATION + " INTEGER NOT NULL DEFAULT 30, "
                + ExerciseSetEntry.COLUMN_SET_REST + " INTEGER NOT NULL DEFAULT 30, "
                + ExerciseSetEntry.COLUMN_SET_WEIGHT + " DOUBLE NOT NULL DEFAULT 0, "
                + ExerciseSetEntry.COLUMN_SET_REPS + " INTEGER NOT NULL DEFAULT 1, "
                + ExerciseSetEntry.COLUMN_SET_DATE_STRING + " TEXT NOT NULL, " // ZonedDateTime
                + ExerciseSetEntry.COLUMN_SET_DATE_LONG + " INTEGER NOT NULL, " // ZonedDateTime
                + ExerciseSetEntry.COLUMN_SET_ORDER + " INTEGER NOT NULL, "
                + ExerciseSetEntry.COLUMN_PB_WEIGHT + " DOUBLE NOT NULL DEFAULT 0, "
                + ExerciseSetEntry.COLUMN_PB_REPS + " INTEGER NOT NULL DEFAULT 0);";

        // Execute the SQL statements
        db.execSQL(SQL_CREATE_WORKOUTS_TABLE);
        db.execSQL(SQL_CREATE_EXERCISE_SETS_TABLE);

        insertDefaults(db);

        if (db.getVersion() < DATABASE_VERSION)
            onUpgrade(db, db.getVersion(), DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.v(LOG_TAG, "Upgrading database...");
        int upgradeTo = oldVersion + 1;

        // This while loop ensures we incrementally update the database to the current version.
        while (upgradeTo <= newVersion)
        {
            switch (upgradeTo)
            {
                case 2:
                    /* Chang the database
                    Log.d(LOG_TAG, "Adding steps table");
                    String SQL_CREATE_STEPS_TABLE = "CREATE TABLE " + StepEntry.TABLE_NAME + " ("
                            + StepEntry._TASK_ID + " INTEGER NOT NULL, "
                            + StepEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                            + StepEntry.COLUMN_STEP_STRING + " TEXT, "
                            + StepEntry.COLUMN_STEP_COMPLETED + " INTEGER NOT NULL DEFAULT 0, "
                            + StepEntry.COLUMN_STEP_ORDER + " INTEGER NOT NULL);";

                    // Execute the SQL statement
                    db.execSQL(SQL_CREATE_STEPS_TABLE);
                    */
                    break;
            }
            upgradeTo++;
        }
    }

    private void insertDefaults(SQLiteDatabase db) {
        Log.v(LOG_TAG, "Inserting defaults");
        ContentValues values = new ContentValues();

        /*
        values.put(WorkoutEntry._ID, 1);
        values.put(WorkoutEntry.COLUMN_WORKOUT_NAME, "SSF5x Chest");
        // values.put(WorkoutEntry.
        values.put(WorkoutEntry.COLUMN_WORKOUT_LAST_DATE_COMPLETED, "Yet to complete");
        values.put(WorkoutEntry.COLUMN_WORKOUT_DURATION, 75);

        long didItWork = db.insertOrThrow(WorkoutEntry.TABLE_NAME, null, values);
        Log.v(LOG_TAG, Long.toString(didItWork));

        values.clear();

        values.put(WorkoutEntry._ID, 2);
        values.put(WorkoutEntry.COLUMN_WORKOUT_NAME, "SSF5x Back");
        // values.put(WorkoutEntry.
        values.put(WorkoutEntry.COLUMN_WORKOUT_LAST_DATE_COMPLETED, "Yet to complete");
        values.put(WorkoutEntry.COLUMN_WORKOUT_DURATION, 44);

        didItWork = db.insertOrThrow(WorkoutEntry.TABLE_NAME, null, values);
        Log.v(LOG_TAG, Long.toString(didItWork));

        /*
        values.clear();

        values.put(ExerciseSetEntry._ID, 1);
        values.put(ExerciseSetEntry._WORKOUT_ID, 2);
        values.put(ExerciseSetEntry.COLUMN_EXERCISE_NAME, "Deadlift");
        values.put(ExerciseSetEntry.COLUMN_SET_NUMBER, "1");
        values.put(ExerciseSetEntry.COLUMN_SET_DURATION, 60000);
        values.put(ExerciseSetEntry.COLUMN_SET_REST, 180000);
        values.put(ExerciseSetEntry.COLUMN_SET_WEIGHT, 100);
        values.put(ExerciseSetEntry.COLUMN_SET_REPS, 6);
        values.put(ExerciseSetEntry.COLUMN_SET_DATE_STRING, "Today");
        values.put(ExerciseSetEntry.COLUMN_SET_DATE_LONG, 2);
        values.put(ExerciseSetEntry.COLUMN_SET_ORDER, 1);
        values.put(ExerciseSetEntry.COLUMN_PB_WEIGHT, 110);
        values.put(ExerciseSetEntry.COLUMN_PB_REPS, 6);

        didItWork = db.insert(ExerciseSetEntry.TABLE_NAME, null, values);
        Log.v(LOG_TAG, Long.toString(didItWork));

        values.clear();

        values.put(ExerciseSetEntry._ID, 2);
        values.put(ExerciseSetEntry._WORKOUT_ID, 2);
        values.put(ExerciseSetEntry.COLUMN_EXERCISE_NAME, "Deadlift");
        values.put(ExerciseSetEntry.COLUMN_SET_NUMBER, "2");
        values.put(ExerciseSetEntry.COLUMN_SET_DURATION, 60000);
        values.put(ExerciseSetEntry.COLUMN_SET_REST, 180000);
        values.put(ExerciseSetEntry.COLUMN_SET_WEIGHT, 100);
        values.put(ExerciseSetEntry.COLUMN_SET_REPS, 6);
        values.put(ExerciseSetEntry.COLUMN_SET_DATE_STRING, "Today");
        values.put(ExerciseSetEntry.COLUMN_SET_DATE_LONG, 2);
        values.put(ExerciseSetEntry.COLUMN_SET_ORDER, 2);
        values.put(ExerciseSetEntry.COLUMN_PB_WEIGHT, 110);
        values.put(ExerciseSetEntry.COLUMN_PB_REPS, 6);

        didItWork = db.insert(ExerciseSetEntry.TABLE_NAME, null, values);
        Log.v(LOG_TAG, Long.toString(didItWork));

        values.clear();

        values.put(ExerciseSetEntry._ID, 1);
        values.put(ExerciseSetEntry._WORKOUT_ID, 2);
        values.put(ExerciseSetEntry.COLUMN_EXERCISE_NAME, "Deadlift");
        values.put(ExerciseSetEntry.COLUMN_SET_NUMBER, "1");
        values.put(ExerciseSetEntry.COLUMN_SET_DURATION, 60000);
        values.put(ExerciseSetEntry.COLUMN_SET_REST, 180000);
        values.put(ExerciseSetEntry.COLUMN_SET_WEIGHT, 100);
        values.put(ExerciseSetEntry.COLUMN_SET_REPS, 6);
        values.put(ExerciseSetEntry.COLUMN_SET_DATE_STRING, "Today");
        values.put(ExerciseSetEntry.COLUMN_SET_DATE_LONG, 3);
        values.put(ExerciseSetEntry.COLUMN_SET_ORDER, 1);
        values.put(ExerciseSetEntry.COLUMN_PB_WEIGHT, 110);
        values.put(ExerciseSetEntry.COLUMN_PB_REPS, 6);

        didItWork = db.insert(ExerciseSetEntry.TABLE_NAME, null, values);
        Log.v(LOG_TAG, Long.toString(didItWork));

        values.clear();

        values.put(ExerciseSetEntry._ID, 2);
        values.put(ExerciseSetEntry._WORKOUT_ID, 2);
        values.put(ExerciseSetEntry.COLUMN_EXERCISE_NAME, "Deadlift");
        values.put(ExerciseSetEntry.COLUMN_SET_NUMBER, "2");
        values.put(ExerciseSetEntry.COLUMN_SET_DURATION, 60000);
        values.put(ExerciseSetEntry.COLUMN_SET_REST, 180000);
        values.put(ExerciseSetEntry.COLUMN_SET_WEIGHT, 100);
        values.put(ExerciseSetEntry.COLUMN_SET_REPS, 6);
        values.put(ExerciseSetEntry.COLUMN_SET_DATE_STRING, "Today");
        values.put(ExerciseSetEntry.COLUMN_SET_DATE_LONG, 3);
        values.put(ExerciseSetEntry.COLUMN_SET_ORDER, 2);
        values.put(ExerciseSetEntry.COLUMN_PB_WEIGHT, 110);
        values.put(ExerciseSetEntry.COLUMN_PB_REPS, 6);

        didItWork = db.insert(ExerciseSetEntry.TABLE_NAME, null, values);
        Log.v(LOG_TAG, Long.toString(didItWork));
        */

        /*
        values.clear();

        values.put(WorkoutEntry._ID, 3);
        values.put(WorkoutEntry.COLUMN_WORKOUT_NAME, "SSF5x Shoulders");
        // values.put(WorkoutEntry.
        values.put(WorkoutEntry.COLUMN_WORKOUT_LAST_DATE_COMPLETED, "Yet to complete");
        values.put(WorkoutEntry.COLUMN_WORKOUT_DURATION, 62);

        didItWork = db.insertOrThrow(WorkoutEntry.TABLE_NAME, null, values);
        Log.v(LOG_TAG, Long.toString(didItWork));

        values.clear();

        values.put(WorkoutEntry._ID, 4);
        values.put(WorkoutEntry.COLUMN_WORKOUT_NAME, "SSF5x Arms");
        // values.put(WorkoutEntry.
        values.put(WorkoutEntry.COLUMN_WORKOUT_LAST_DATE_COMPLETED, "Yet to complete");
        values.put(WorkoutEntry.COLUMN_WORKOUT_DURATION, 52);

        didItWork = db.insertOrThrow(WorkoutEntry.TABLE_NAME, null, values);
        Log.v(LOG_TAG, Long.toString(didItWork));

        values.clear();

        values.put(WorkoutEntry._ID, 5);
        values.put(WorkoutEntry.COLUMN_WORKOUT_NAME, "SSF5x Legs/Abs");
        // values.put(WorkoutEntry.
        values.put(WorkoutEntry.COLUMN_WORKOUT_LAST_DATE_COMPLETED, "Yet to complete");
        values.put(WorkoutEntry.COLUMN_WORKOUT_DURATION, 48);

        didItWork = db.insertOrThrow(WorkoutEntry.TABLE_NAME, null, values);
        Log.v(LOG_TAG, Long.toString(didItWork));
        */
    }
}
