package com.lianos.darn.expenses.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import com.lianos.darn.expenses.R;
import com.lianos.darn.expenses.activities.PersonalInfoActivity.PersonalInfo;
import com.lianos.darn.expenses.utilities.BackClickListener;
import com.lianos.darn.expenses.utilities.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

import static com.lianos.darn.expenses.activities.DisplayActivity.EXPENSES_FILE;
import static com.lianos.darn.expenses.activities.DisplayActivity.SAVINGS_FILE;
import static com.lianos.darn.expenses.activities.PersonalInfoActivity.PERSONAL_INFO_FILE;
import static com.lianos.darn.expenses.activities.PersonalInfoActivity.PERSONAL_INFO_KEY;
import static com.lianos.darn.expenses.activities.SignUpActivity.SIGNUP_CREDENTIALS_FILENAME;
import static com.lianos.darn.expenses.utilities.AlertUtils.checkFields;
import static com.lianos.darn.expenses.utilities.FileUtils.getOrCreate;
import static com.lianos.darn.expenses.utilities.FileUtils.getStringFromFile;

public class LoginActivity extends AppCompatActivity {

    private static final Logger log = LoggerFactory.getLogger(LoginActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Link with the XML file.
        setContentView(R.layout.activity_login);

        // Bind login button with listener.
        ImageButton buttonLoginDone = findViewById(R.id.button_done);
        buttonLoginDone.setOnClickListener(new ClickListener(this));

        // Bind back button with listener.
        ImageButton buttonBack = findViewById(R.id.back_button);
        buttonBack.setOnClickListener(new BackClickListener(this));

    }

    // TODO: This goes to activity display.
    public class ClickListener implements View.OnClickListener {

        private final Activity activity;

        public ClickListener(Activity activity) { this.activity = activity; }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onClick(View v) {

            log.debug("Initiating login..");

            File parent = getFilesDir();
            File file = new File(parent, SIGNUP_CREDENTIALS_FILENAME);
            String fileContents = null;
            try { fileContents = FileUtils.getStringFromFile(file.getPath()); }
            catch (Exception e) { log.error("Failed to read from file.", e); }

            if (fileContents == null) {

                log.error("File does not exist");
                finish();
                return;

            }

            // Here we get the variables from the text.
            EditText textUsername = findViewById(R.id.text_username);
            String username = textUsername.getText().toString();

            // Here we get the variables from the text.
            EditText textPassword = findViewById(R.id.text_password);
            String password = textPassword.getText().toString();

            if (!checkFields(activity, username, password)) return;

            String[] split = fileContents.split("-");
            if (!split[0].equals(username) || !split[1].equals(password)) {

                log.debug("Login was unsuccessful. Provided: [{}-{}] whereas saved: [{}]",
                        username, password, fileContents);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                alertDialogBuilder.setMessage(R.string.wrong_credentials);
                alertDialogBuilder.setPositiveButton(R.string.try_again,
                        (dialog, which) -> log.debug("Clicked to try again"));
                alertDialogBuilder.setNegativeButton(R.string.go_to_main,
                        (dialog, which) -> {

                            log.debug("Clicked go to main.");

                            Intent mainActivity = new Intent(LoginActivity.this, MainActivity.class);
                            LoginActivity.this.startActivity(mainActivity);

                        });

                AlertDialog dialog = alertDialogBuilder.create();
                dialog.show();

            } else {

                log.debug("Login was successful. User - password: [{}]", fileContents);

                // First check that the file that contains the personal info
                // doesn't exist.
                File personalInfo = new File(parent, PERSONAL_INFO_FILE);
                if (personalInfo.exists()) {

                    log.debug("Personal info file exists. Going to display activity..");
                    String personalContents;

                    try {personalContents = getStringFromFile(personalInfo.getPath()); }
                    catch (Exception e) {

                        log.error("Failed to read from personal info file.");
                        return;

                    }

                    // Prepare personal info object.
                    String[] parts = personalContents.split("-");
                    PersonalInfo info = new PersonalInfo(parts[0], Integer.parseInt(parts[1]));

                    // Check if expenses file exists, create it otherwise (persistent).
                    File expensesFile = new File(getFilesDir(), EXPENSES_FILE);
                    List<String> expensesContents = getOrCreate(getApplicationContext(), expensesFile.getPath());
                    if (expensesContents == null) return;

                    log.debug("Expenses file found [{}]. Contents: [{}].", expensesFile.exists(), expensesContents);
                    if (!expensesContents.isEmpty()) {

                        for (String expense : expensesContents)
                            if (!expense.isEmpty())
                                info.expenses.add(Integer.parseInt(expense));

                    }

                    // Check if savings file exists, create it otherwise (persistent).
                    File savingsFile = new File(getFilesDir(), SAVINGS_FILE);
                    List<String> savingsContents = getOrCreate(getApplicationContext(), savingsFile.getPath());
                    if (savingsContents == null) return;

                    log.debug("Savings file found [{}]. Contents: [{}].", savingsFile.exists(), savingsContents);
                    if (!savingsContents.isEmpty()) {

                        for (String saving : savingsContents)
                            if (!saving.isEmpty())
                                info.savings.add(Integer.parseInt(saving));

                    }

                    log.debug(info.toString());

                    Intent display = new Intent(LoginActivity.this, DisplayActivity.class);
                    display.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    display.putExtra(PERSONAL_INFO_KEY, info);
                    LoginActivity.this.startActivity(display);

                } else {

                    log.debug("Personal info file does not exist. Going to personal info activity..");

                    Intent personalInfoActivity = new Intent(LoginActivity.this, PersonalInfoActivity.class);
                    LoginActivity.this.startActivity(personalInfoActivity);

                }

            }

        }

    }

}
