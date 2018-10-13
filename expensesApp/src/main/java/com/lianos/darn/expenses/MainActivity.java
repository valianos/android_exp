package com.lianos.darn.expenses;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static com.lianos.darn.expenses.SignUpActivity.SIGNUP_CREDENTIALS_FILENAME;

public class MainActivity extends AppCompatActivity {

    private static final Logger log = LoggerFactory.getLogger(MainActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setTitle(R.string.app_name);

        // Link with the XML file.
        setContentView(R.layout.activity_main);

        // Bind button with listener.
        Button loginButton = findViewById(R.id.button_login);
        loginButton.setOnClickListener(new LoginClickListener(this));

        // Bind button with listener.
        Button signUpButton = findViewById(R.id.button_signUp);
        signUpButton.setOnClickListener(new SignUpClickListener(this));

        log.debug("Started Expenses application.");

    }

    public class LoginClickListener implements View.OnClickListener {

        private final Activity activity;

        public LoginClickListener(Activity activity) { this.activity = activity; }

        @Override
        public void onClick(View v) {

            log.debug("Clicked login.");

            // First check that the file that contains the credentials
            // does exist. Show an alert if not.
            File parent = getFilesDir();
            File file = new File(parent, SIGNUP_CREDENTIALS_FILENAME);
            if (!file.exists()) {

                log.debug("File does not exist.");

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                alertDialogBuilder.setMessage(R.string.login_alert);
                alertDialogBuilder.setPositiveButton(R.string.login_positive_alert, (dialog, which) -> {

                            log.debug("Clicked to go to sign up. Following ..");

                            Intent signUpActivity = new Intent(MainActivity.this, SignUpActivity.class);
                            MainActivity.this.startActivity(signUpActivity);

                        });

                alertDialogBuilder.setNegativeButton(R.string.cancel, (dialog, which) -> log.debug("Clicked cancel."));

                AlertDialog dialog = alertDialogBuilder.create();
                dialog.show();

            } else {

                log.debug("File exists. Going to login.");

                Intent loginActivity = new Intent(MainActivity.this, LoginActivity.class);
                MainActivity.this.startActivity(loginActivity);

            }

        }

    }

    public class SignUpClickListener implements View.OnClickListener {

        private final Activity activity;

        public SignUpClickListener(Activity activity) { this.activity = activity; }

        @Override
        public void onClick(View v) {

            log.debug("Clicked sign-up.");

            // First check that the file that contains the credentials
            // doesn't exist. Show an alert if it exist.
            File parent = getFilesDir();
            File file = new File(parent, SIGNUP_CREDENTIALS_FILENAME);
            if (file.exists()) {

                log.debug("File exists..");

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                alertDialogBuilder.setMessage(R.string.sign_up_alert);
                alertDialogBuilder.setPositiveButton(R.string.sign_up_positive_alert, (dialog, which) -> {

                            log.debug("Clicked to go to login. Following ..");

                            Intent loginActivity = new Intent(MainActivity.this, LoginActivity.class);
                            MainActivity.this.startActivity(loginActivity);

                        });

                alertDialogBuilder.setNegativeButton(R.string.cancel, (dialog, which) -> log.debug("Clicked cancel."));

                alertDialogBuilder.setNeutralButton(R.string.force_sign_up, (dialog, which) -> {

                    log.debug("Clicked to force sign-up.");

                    Intent signUp = new Intent(MainActivity.this, SignUpActivity.class);
                    MainActivity.this.startActivity(signUp);

                });

                AlertDialog dialog = alertDialogBuilder.create();
                dialog.show();

            } else {

                log.debug("File does not exist. Going to sign up.");

                Intent signUpActivity = new Intent(MainActivity.this, SignUpActivity.class);
                MainActivity.this.startActivity(signUpActivity);

            }

        }

    }

}
