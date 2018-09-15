package com.lianos.darn.expenses;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

public class SignUpActivity extends AppCompatActivity {

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

            Log.d("Debug message", "Clicked plus_button to sign up.");

            Intent personal_info_activity = new Intent(SignUpActivity.this, PersonalInfoActivity.class);
            SignUpActivity.this.startActivity(personal_info_activity);

        }

    }

}
