package com.lianos.darn.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import com.lianos.darn.myapplication.MainActivity.MyData;
import com.lianos.darn.myapplication.utilities.BackClickListener;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Link with activity xml file.
        setContentView(R.layout.activity_second);

        // Get variables.
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            MyData data = bundle.getParcelable("p");
            assert data != null;
            Log.d("Debug message", "Received parcelable. Int: " + data.a + " String: " + data.str);

        }

        // Bind button with listener.
        ImageButton button = findViewById(R.id.little_guy);
        button.setOnClickListener(new ClickListener());

        // Bind back button with listener.
        ImageButton back = findViewById(R.id.back_button_second);
        back.setOnClickListener(new BackClickListener(this));

    }

    public class ClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            Log.d("Debug message", "Going to third.");

            Intent third = new Intent(SecondActivity.this, ThirdActivity.class);
            third.putExtra("a", 1);
            third.putExtra("B", 2);
            startActivity(third);

        }

    }

}
