package com.lianos.darn.expenses;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Link with the XML file.
        setContentView(R.layout.activity_main);

        // Bind button with listener.
        Button loginButton = findViewById(R.id.button_login);
        loginButton.setOnClickListener(new LoginClickListener());

        // Bind button with listener.
        Button signUpButton = findViewById(R.id.button_signUp);
        signUpButton.setOnClickListener(new SignUpClickListener());

    }

    public class LoginClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            Log.d("Debug message", "Clicked log-in");

            Intent loginActivity = new Intent(MainActivity.this, LoginActivity.class);
            MainActivity.this.startActivity(loginActivity);

        }

    }

    public class SignUpClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            Log.d("Debug message", "Clicked log-in");

            Intent signUpActivity = new Intent(MainActivity.this, SignUpActivity.class);
            MainActivity.this.startActivity(signUpActivity);

        }

    }

}
