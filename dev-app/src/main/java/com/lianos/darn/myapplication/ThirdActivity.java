package com.lianos.darn.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.Switch;
import com.lianos.darn.myapplication.utilities.BackClickListener;
import com.lianos.darn.myapplication.utilities.FileUtils;

import java.io.*;

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

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            int value = bundle.getInt("a");
            Log.d("Debug message", "Received a: " + value);

        }

    }

    private class CheckedListener implements OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            Log.d("Debug message", "Switch state: " + isChecked);

            if (isChecked) {

                String filename = "myFile";
                String fileContents = "Hello world!";
                FileOutputStream outputStream;

                // getDirFiles() gives the persistent memory to write
                // getCacheDir() gives memory that will be erased when closing the app.
                // check: 'https://developer.android.com/training/data-storage/files#java'

                // Check if file exists, create it otherwise (persistent).
                File file = new File(getFilesDir(), filename);
                if (!file.exists()) {

                    Log.d("Debug message", "Creating file: " + file.getPath());
                    try {

                        outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                        outputStream.write(fileContents.getBytes());
                        outputStream.close();

                    } catch (Exception e) { e.printStackTrace(); }

                }

            } else {

                // Dummy switch off, dump in logs the contents of file.
                try {

                    File parent = getFilesDir();
                    File file = new File(parent, "myFile");
                    String fileContents = FileUtils.getStringFromFile(file.getPath());
                    Log.d("Debug message", "File contents are : " + fileContents);

                } catch (Exception e) { e.printStackTrace(); }

            }

        }

    }

}
