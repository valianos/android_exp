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
import com.lianos.darn.expenses.utilities.AlertUtils;
import com.lianos.darn.expenses.utilities.BackClickListener;
import com.lianos.darn.expenses.utilities.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static com.lianos.darn.expenses.utilities.AlertUtils.checkFields;
import static com.lianos.darn.expenses.utilities.FileUtils.getStringFromFile;

public class PersonalInfoActivity extends AppCompatActivity {

    public static final String PERSONAL_INFO_FILE = "personalInfo";

    private static final Logger log = LoggerFactory.getLogger(LoginActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //Link with the XML file.
        setContentView(R.layout.activity_personal_info);

        ImageButton back = findViewById(R.id.back_button);
        back.setOnClickListener(new BackClickListener(this));

        ImageButton next = findViewById(R.id.button_done);
        next.setOnClickListener(new ClickListener(this));

    }

    public class ClickListener implements View.OnClickListener{

        private final Activity activity;

        public ClickListener(Activity activity) { this.activity = activity; }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onClick(View v){

            log.debug("Clicked done button to add personal info.");

            EditText textFullName = findViewById(R.id.full_name);
            EditText textIncome = findViewById(R.id.income);
            String fullName = textFullName.getText().toString();
            String income = textIncome.getText().toString();

            log.debug("Full name: [{}], income: [{}]", fullName, income);
            if (!checkFields(activity, fullName, income)) return;

            String fileContents = fullName + "-" + income;

            File file = new File(getFilesDir(), PERSONAL_INFO_FILE);
            FileUtils.dumpToFile(getApplicationContext(), file, fileContents);

            try {

                String contents = getStringFromFile(file.getPath());
                log.debug("Personal info created: [{}]. Going to display activity..", contents);

                Intent display = new Intent(PersonalInfoActivity.this, DisplayActivity.class);
                PersonalInfoActivity.this.startActivity(display);

            } catch (Exception e) {

                log.error("Could not save personal info.", e);
                AlertUtils.fileCreationFailure(activity);

                // Sanity check.
                log.debug("Deleting file: [{}]", file.delete());

            }

        }

    }

}
