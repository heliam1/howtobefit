package com.heliam1.HowToBeFit.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.heliam1.HowToBeFit.R;

public class MainActivity extends AppCompatActivity {
    // Track whether to display a two-pane or single-pane UI
    // A single-pane display refers to phone screens, and two-pane to larger tablet screens
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }



}
