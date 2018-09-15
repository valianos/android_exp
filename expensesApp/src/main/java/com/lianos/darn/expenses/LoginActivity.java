package com.lianos.darn.expenses;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Link with the XML file.
        setContentView(R.layout.activity_login);

        // Bind button with listener.
        ImageButton button_login_done = findViewById(R.id.button_done);
        button_login_done.setOnClickListener(new LoginActivity.ClickListener1());

        // Bind button with listener.
        ImageButton button_back = findViewById(R.id.back_button);
        button_back.setOnClickListener(new BackClickListener());

    }
    public class ClickListener1 implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            Log.d("Debug message", "Clicked log-in");

        }

    }

    public class BackClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            Log.d("Debug message", "Clicked to go back");

            finish();

        }

    }

}
