package com.lianos.darn.expenses.activities;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.lianos.darn.expenses.R;
import com.lianos.darn.expenses.protocol.Protocol;
import com.lianos.darn.expenses.protocol.Protocol.Expense;
import com.lianos.darn.expenses.protocol.Protocol.Saving;
import com.lianos.darn.expenses.utilities.AlertUtils;
import com.lianos.darn.expenses.utilities.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;

import static com.lianos.darn.expenses.utilities.AlertUtils.checkFields;

public class ElementAmountActivity extends AppCompatActivity {

    private static final Logger log = LoggerFactory.getLogger(ElementAmountActivity.class);

    public static final String NEW_EXPENSE_FILE = "newExpenseFile";

    public static final String NEW_SAVING_FILE = "newSavingFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        log.debug("Entering element activity..");

        setContentView(R.layout.activity_element_amount);

        // Bind done button with listener.
        ImageButton buttonLoginDone = findViewById(R.id.save_amount);
        buttonLoginDone.setOnClickListener(new ClickListener(this));

    }

    public class ClickListener implements View.OnClickListener {

        private final Activity activity;

        public ClickListener(Activity activity) { this.activity = activity; }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onClick(View v) {

            EditText amountField = findViewById(R.id.amount);
            EditText dateField = findViewById(R.id.date_picker);
            RadioGroup radios = findViewById(R.id.amountRadioGroup);
            RadioButton radioField = findViewById(radios.getCheckedRadioButtonId());

            String amount = amountField.getText().toString();
            String date = dateField.getText().toString();
            String radio = radioField.getText().toString();

            // If date is left blank, take current time.
            if (date.isEmpty()) date = String.valueOf(new Date(System.currentTimeMillis()));
            if (!checkFields(activity, amount, date, radio)) return;

            Protocol.Amount newAmount;
            File cache;
            if (radioField.getText().toString().equals(getString(R.string.radioButtonExpense))) {

                newAmount = new Expense(Integer.parseInt(amount), Date.parse(date), null, null);
                cache = new File(getCacheDir(), NEW_EXPENSE_FILE);

            } else {

                newAmount = new Saving(Integer.parseInt(amount), Date.parse(date), null, null);
                cache = new File(getCacheDir(), NEW_SAVING_FILE);

            }

            try { DatabaseUtil.dump(cache, Collections.singletonList(newAmount)); }
            catch (IOException e) {

                log.error("Failed.", e);
                AlertUtils.fileCreationFailure(activity);
                return;

            }

            finish();

        }

    }

}
