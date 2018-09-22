package com.lianos.darn.expenses;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import com.lianos.darn.expenses.utilities.BackClickListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginActivity extends AppCompatActivity {

    private static final Logger log = LoggerFactory.getLogger(LoginActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Link with the XML file.
        setContentView(R.layout.activity_login);

        // Bind login button with listener.
        ImageButton buttonLoginDone = findViewById(R.id.button_done);
        buttonLoginDone.setOnClickListener(new ClickListener());

        // Bind back button with listener.
        ImageButton buttonBack = findViewById(R.id.back_button);
        buttonBack.setOnClickListener(new BackClickListener(this));

    }

    public class ClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            log.debug("Clicked to finish login.");

        }

    }

}
