package com.lianos.darn.expenses.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import com.lianos.darn.expenses.R;
import com.lianos.darn.expenses.protocol.Protocol;
import com.lianos.darn.expenses.utilities.AlertUtils;
import com.lianos.darn.expenses.utilities.BackClickListener;
import com.lianos.darn.expenses.utilities.DatabaseUtil;
import com.lianos.darn.expenses.utilities.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.lianos.darn.expenses.activities.DisplayActivity.EXPENSES_FILE;
import static com.lianos.darn.expenses.activities.DisplayActivity.SAVINGS_FILE;
import static com.lianos.darn.expenses.utilities.AlertUtils.checkFields;
import static com.lianos.darn.expenses.utilities.FileUtils.getOrCreate;
import static com.lianos.darn.expenses.utilities.FileUtils.getStringFromFile;

public class PersonalInfoActivity extends AppCompatActivity {

    public static final String PERSONAL_INFO_FILE = "personalInfo";

    public static final String PERSONAL_INFO_KEY="personalInfoKey";

    private static final Logger log = LoggerFactory.getLogger(PersonalInfoActivity.class);

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

        @SuppressWarnings("Duplicates")
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onClick(View v) {

            log.debug("Clicked done button to add personal info.");

            EditText textFullName = findViewById(R.id.full_name);
            EditText textIncome = findViewById(R.id.income);
            String fullName = textFullName.getText().toString();
            String income = textIncome.getText().toString();

            log.debug("Full name: [{}], income: [{}]", fullName, income);
            if (!checkFields(activity, fullName, income)) return;

            // Prepare personal info object.
            PersonalInfo info = new PersonalInfo(fullName, Integer.parseInt(income));

            String fileContents = fullName + "-" + income;

            File file = new File(getFilesDir(), PERSONAL_INFO_FILE);
            FileUtils.dumpToFile(getApplicationContext(), file, fileContents);

            try {

                String contents = getStringFromFile(file.getPath());
                log.debug("Personal info created: [{}]. Checking for additional file..", contents);

            } catch (Exception e) {

                log.error("Could not save personal info.", e);
                AlertUtils.fileCreationFailure(activity);

                // Sanity check.
                log.debug("Deleting personal info file: [{}]", file.delete());
                return;

            }

            File expenseDb;
            File savingDb;
            try {

                expenseDb = DatabaseUtil.createDatabase(getFilesDir(), EXPENSES_FILE);
                DatabaseUtil.readDatabase(expenseDb, Protocol.Expense.class, info.expenses::add);

                savingDb = DatabaseUtil.createDatabase(getFilesDir(), SAVINGS_FILE);
                DatabaseUtil.readDatabase(savingDb, Protocol.Saving.class, info.savings::add);

            } catch (IOException e) {

                log.error("Failed.", e);
                AlertUtils.fileCreationFailure(activity);
                return;

            }

            log.debug(info.toString());

            Intent display = new Intent(PersonalInfoActivity.this, DisplayActivity.class);
            display.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            display.putExtra(PERSONAL_INFO_KEY, info);
            PersonalInfoActivity.this.startActivity(display);

        }

    }


    final static class PersonalInfo implements Serializable, Protocol {

        public final String name;

        public final int wage;

        public final List<Amount> expenses = new ArrayList<>();

        public final List<Amount> savings = new ArrayList<>();

        PersonalInfo(String name, int wage) {

            this.name = name;
            this.wage = wage;

        }

        @Override
        public String toString() {

            StringBuilder exp = getList(expenses);

            StringBuilder sav = getList(savings);

            return "PersonalInfo: {\n" +
                    "\tname: " + name + "\n" +
                    "\twage: " + wage + "\n" +
                    "\texpenses:\n" + exp + "\n" +
                    "\tsavings:\n" + sav + "\n" +
                    "}";

        }

        public static StringBuilder getList(List<Amount> list) {

            StringBuilder exp = new StringBuilder();
            for (Amount i : list) {

                exp.append(i.getAmount()).append(" ")
                   .append(i.getCurrency()).append("-")
                   .append(new Date(i.getDate()))
                   .append(" ");
                if (list.indexOf(i) != list.size() - 1) exp.append("\n");

            }

            return exp;

        }

    }

}
