package com.lianos.darn.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import com.lianos.darn.myapplication.utilities.BackClickListener;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Link with activity xml file.
        setContentView(R.layout.activity_main);

        // Bind button with listener.
        ImageButton button = findViewById(R.id.button);
        button.setOnClickListener(new ClickListener());

        // Bind exit button with listener.
        ImageButton exit = findViewById(R.id.exit_button);
        exit.setOnClickListener(new BackClickListener(this));

    }


    public class ClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            Log.d("Debug message", "Clicked little fella");

            Parcel in = Parcel.obtain();
            try {

                in.writeInt(8);
                in.writeString("My String");
                Log.d("Debug message", "Saved an int and a string to input data parcel. Data position: " + in.dataPosition());

                // Reset the data position once done with writing values.
                in.setDataPosition(0);

                MyData data = new MyData(in);
                Log.d("Debug message", "Reading from myData. Int: " + data.a + " String: " + data.str);

                Intent second = new Intent(MainActivity.this, SecondActivity.class);
                second.putExtra("p", data);
                MainActivity.this.startActivity(second);

            } finally { in.recycle(); }

        }

    }

    public static class MyData implements Parcelable {

        public int a;

        String str;

        MyData(Parcel in) {

            // Reconstruct from the Parcel. Should be at the same order with the
            // one used to write on the parcel.
            Log.d("Debug message", "ParcelData(Parcel in): time to put back parcel data");
            a = in.readInt();
            str = in.readString();

        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {

            // Deconstruct back to a parcel. Rewrite everything back on the
            // correct order.
            dest.writeInt(a);
            dest.writeString(str);

        }

        @Override
        public int describeContents() { return this.hashCode(); }

        public static final Creator<MyData> CREATOR = new Creator<MyData>() {

            @Override
            public MyData createFromParcel(Parcel in) { return new MyData(in); }

            @Override
            public MyData[] newArray(int size) { return new MyData[size]; }

        };

    }

}
