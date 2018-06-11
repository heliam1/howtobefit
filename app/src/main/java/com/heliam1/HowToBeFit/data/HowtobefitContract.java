package com.heliam1.HowToBeFit.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import com.heliam1.HowToBeFit.BuildConfig;

public final class HowtobefitContract {
    // Prevent accidental instantiation by developers
    private HowtobefitContract() {}

    // Allows both release and debug to be installed
    public static final String CONTENT_AUTHORITY = BuildConfig.APPLICATION_ID;
    // com.heliam1.howtobefit or com.heliam1.howtobefit.debug

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Paths to each database
    public static final String PATH_WORKOUTS = "workouts";
    public static final String PATH_EXERCISE_SET = "exercise_set";

    // Inner class for workout table
    public static final class WorkoutEntry implements BaseColumns {
        // Content uri to access workout data via the provider
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_WORKOUTS);

        // MIME type of the {@link #CONTENT_URI} for a list of Workouts.
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WORKOUTS;

        // MIME type of the {@link #CONTENT_URI} for a single workout.
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WORKOUTS;

        public final static String TABLE_NAME = "workouts";

        // Columns
        public final static String _ID = BaseColumns._ID;                       // INTEGER
        public final static String COLUMN_WORKOUT_NAME = "name";                // TEXT
        public final static String COLUMN_WORKOUT_IMAGE = "image";              // BLOB
        public final static String COLUMN_WORKOUT_LAST_DATE_COMPLETED = "date"; // LONG
        public final static String COLUMN_WORKOUT_DURATION = "duration";        // INTEGER
    }

    // Inner class for exercise_set table
    public static final class ExerciseSetEntry implements BaseColumns {
        // Content uri to access exercise_set data via the provider
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_EXERCISE_SET);

        // MIME type of the {@link #CONTENT_URI} for a list of exercise_sets.
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EXERCISE_SET;

        // MIME type of the {@link #CONTENT_URI} for a single exercise_set.
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EXERCISE_SET;

        public final static String TABLE_NAME = "exercise_sets";

        // Columns
        public final static String _ID = BaseColumns._ID;                       // INTEGER
        public final static String _WORKOUT_ID = "id_workout";                  // INTEGER
        public final static String COLUMN_EXERCISE_NAME = "exercise_name";      // TEXT
        public final static String COLUMN_SET_NUMBER = "set_number";            // INTEGER
        public final static String COLUMN_SET_DURATION = "set_duration";        // INTEGER
        public final static String COLUMN_SET_REST = "set_rest";                // INTEGER
        public final static String COLUMN_SET_WEIGHT = "set_weight";            // INTEGER
        public final static String COLUMN_SET_REPS = "set_reps";                // INTEGER
        public final static String COLUMN_SET_DATE_STRING = "set_date_string";  // TEXT
        public final static String COLUMN_SET_DATE_LONG = "set_date_long";      // INTEGER
        public final static String COLUMN_SET_ORDER = "set_order";              // INTEGER
        public final static String COLUMN_PB_WEIGHT = "pb_weight";              // INTEGER
        public final static String COLUMN_PB_REPS = "pb_reps";                  // INTEGER
    }
}
