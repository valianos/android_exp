package com.lianos.darn.expenses;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainActivity extends AppCompatActivity {

    private static final Logger log = LoggerFactory.getLogger(MainActivity.class);

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

        log.debug("Started Expenses application.");

    }

    public class LoginClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            log.debug("Clicked log-in.");

            Intent loginActivity = new Intent(MainActivity.this, LoginActivity.class);
            MainActivity.this.startActivity(loginActivity);

        }

    }

    public class SignUpClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            log.debug("Clicked sign-up.");

            Intent signUpActivity = new Intent(MainActivity.this, SignUpActivity.class);
            MainActivity.this.startActivity(signUpActivity);

        }

    }

}
