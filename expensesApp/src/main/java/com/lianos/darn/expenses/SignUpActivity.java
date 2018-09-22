package com.lianos.darn.expenses;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SignUpActivity extends AppCompatActivity {

    private static final Logger log = LoggerFactory.getLogger(SignUpActivity.class);

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

            log.debug("Clicked plus_button to sign up.");

            Intent personal_info_activity = new Intent(SignUpActivity.this, PersonalInfoActivity.class);
            SignUpActivity.this.startActivity(personal_info_activity);

        }

    }

}
