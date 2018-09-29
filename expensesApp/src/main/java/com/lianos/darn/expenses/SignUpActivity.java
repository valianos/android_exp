package com.lianos.darn.expenses;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;

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
        buttonAdd.setOnClickListener(new ClickListener());

    }

    public class ClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            log.debug("Clicked plus button to sign up.");

            // Here we get the variables from the text.
            EditText textUsername = findViewById(R.id.textNewUsername);
            EditText textPassword = findViewById(R.id.textNewPassword);
            String username = textUsername.getText().toString();
            String password = textPassword.getText().toString();

            log.debug("Username: [{}], password: [{}]", username, password);

            // Here we create the file, and save the variables in it.
            // It is comfy to add a 'delimiter' (i.e. '-', '@' etc) for splitting afterwards.
            String fileContents = username + "-" + password;

            // Check if file exists, create it otherwise (persistent).
            FileOutputStream outputStream;
            File file = new File(getFilesDir(), SIGNUP_CREDENTIALS_FILENAME);

            log.debug("Creating new file: [{}]", file.getPath());
            try {

                outputStream = openFileOutput(SIGNUP_CREDENTIALS_FILENAME, Context.MODE_PRIVATE);
                outputStream.write(fileContents.getBytes());
                outputStream.close();

            } catch (Exception e) { log.error("Error handling file.", e); }

            Intent personalInfoActivity = new Intent(SignUpActivity.this, PersonalInfoActivity.class);
            SignUpActivity.this.startActivity(personalInfoActivity);

        }

    }

}
