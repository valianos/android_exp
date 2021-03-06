package com.lianos.darn.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Link with activity xml file.
        setContentView(R.layout.activity_main);

        ImageButton button = findViewById(R.id.button);

        // Bind button with listener.
        button.setOnClickListener(new ClickListener());

    }


    public class ClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            Log.d("Debug message", "Clicked little fella");

            Intent second = new Intent(MainActivity.this, SecondActivity.class);
            MainActivity.this.startActivity(second);

            finish();

        }

    }

}
