package com.heliam1.HowToBeFit.sync;

import android.content.Context;

public class NotificationsTasks {
    public static final String ACTION_OPEN_ACTIVITY = "open-activity";
    public static final String ACTION_ACKNOWLEDGE_NOTIFICATION = "acknowledge-notification";
    public static final String ACTION_DISMISS_NOTIFICATION = "dismiss-notification";

    public static void executeTask(Context context, String action) {
        if (ACTION_OPEN_ACTIVITY.equals(action)) {
            openActivity();
        } else if (ACTION_ACKNOWLEDGE_NOTIFICATION.equals(action)) {

        } else if (ACTION_DISMISS_NOTIFICATION.equals(action)) {

        }
    }

    private static void openActivity(/* Context context */) {

    }
}
