package com.lianos.darn.expenses;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static com.lianos.darn.expenses.utilities.AlertUtils.checkFields;
import static com.lianos.darn.expenses.utilities.FileUtils.dumpToFile;

public class SignUpActivity extends AppCompatActivity {

    private static final Logger log = LoggerFactory.getLogger(SignUpActivity.class);

    public static final String SIGNUP_CREDENTIALS_FILENAME = "loginCredentialsFilename";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Link with the XML file.
        setContentView(R.layout.activity_signup);

        // Bind button with listener.
        ImageButton buttonAdd = findViewById(R.id.add_button);
        buttonAdd.setOnClickListener(new ClickListener(this));

    }

    public class ClickListener implements View.OnClickListener {

        private final Activity activity;

        public ClickListener(Activity activity) { this.activity = activity; }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onClick(View v) {

            log.debug("Clicked plus button to sign up.");

            // Here we get the variables from the text.
            EditText textUsername = findViewById(R.id.textNewUsername);
            EditText textPassword = findViewById(R.id.textNewPassword);
            String username = textUsername.getText().toString();
            String password = textPassword.getText().toString();

            log.debug("Username: [{}], password: [{}]", username, password);

            if (!checkFields(activity, username, password)) return;

            // Here we create the file, and save the variables in it.
            // It is comfy to add a 'delimiter' (i.e. '-', '@' etc) for splitting afterwards.
            String fileContents = username + "-" + password;

            // Check if file exists, create it otherwise (persistent).
            File file = new File(getFilesDir(), SIGNUP_CREDENTIALS_FILENAME);
            if (!dumpToFile(getApplicationContext(), file, fileContents)) return;

            // TODO: Check that personalInfoFile does not exist. If it exists, delete it first and proceed.
            Intent personalInfoActivity = new Intent(SignUpActivity.this, PersonalInfoActivity.class);
            SignUpActivity.this.startActivity(personalInfoActivity);

        }

    }

}
