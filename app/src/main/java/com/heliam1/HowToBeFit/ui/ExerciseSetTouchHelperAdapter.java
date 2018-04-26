package com.heliam1.HowToBeFit.ui;

import android.support.v7.widget.RecyclerView;

public interface ExerciseSetTouchHelperAdapter {
    void onItemMove(int fromPosition, int toPosition);
    void onItemDismiss(int position);
}
