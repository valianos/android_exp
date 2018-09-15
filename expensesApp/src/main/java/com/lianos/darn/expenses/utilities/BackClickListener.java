package com.lianos.darn.expenses.utilities;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

/**
 * @author Vasilis Lianos
 */
public class BackClickListener implements View.OnClickListener {

    private final AppCompatActivity activity;

    public BackClickListener(AppCompatActivity activity) { this.activity = activity; }

    @Override
    public void onClick(View v) {

        Log.d("Debug message", "Going back..");
        activity.finish();

    }

}