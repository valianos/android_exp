package com.lianos.darn.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.Switch;
import com.lianos.darn.myapplication.utilities.BackClickListener;

public class ThirdActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Link with activity xml file.
        setContentView(R.layout.activity_third);

        // Bind switch with listener.
        Switch switchButton = findViewById(R.id.on_off);
        switchButton.setOnCheckedChangeListener(new CheckedListener());

        // Bind back button with listener.
        ImageButton back = findViewById(R.id.back_button_third);
        back.setOnClickListener(new BackClickListener(this));

    }

    private class CheckedListener implements OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            Log.d("Debug message", "Switch state: " + isChecked);

        }

    }

}
