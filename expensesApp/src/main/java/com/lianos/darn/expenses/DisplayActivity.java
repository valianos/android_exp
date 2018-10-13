package com.lianos.darn.expenses;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.lianos.darn.expenses.PersonalInfoActivity.PersonalInfo;
import com.lianos.darn.expenses.utilities.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static com.lianos.darn.expenses.PersonalInfoActivity.PERSONAL_INFO_KEY;
import static com.lianos.darn.expenses.utilities.AlertUtils.debugAlert;
import static com.lianos.darn.expenses.utilities.AlertUtils.editTextAlert;

public class DisplayActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final Logger log = LoggerFactory.getLogger(DisplayActivity.class);

    public static final String EXPENSES_FILE = "expensesFile";

    public static final String SAVINGS_FILE = "savingsFile";

    private boolean secondBackPressed = false;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        log.debug("Entering display activity..");

        super.onCreate(savedInstanceState);

        // Link with the XML file.
        setContentView(R.layout.activity_display);

        // Enable toolbar.
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Link with the XML file.
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        // Bind drawer with listener.
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Add values to fields.
        displayFields();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("DefaultLocale")
    private void displayFields() {

        // ------- Main display fields.

        AtomicInteger totalMoney = new AtomicInteger();

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;

        PersonalInfo info = (PersonalInfo) bundle.getSerializable(PERSONAL_INFO_KEY);
        if (info == null) return;

        log.debug("Found personal info: [{}]", info);
        totalMoney.set(info.wage);

        info.expenses.forEach(e -> totalMoney.addAndGet(-e));

        // Bind remaining-money button with listener.
        FloatingActionButton fab = findViewById(R.id.fab);
        final int money = totalMoney.get();
        fab.setOnClickListener(
                view -> Snackbar.make(view, "Remaining: " + money, Snackbar.LENGTH_LONG)
                        .show()
        );

        TextView greeting = findViewById(R.id.greeting);
        greeting.setText(String.format("%s %s", getString(R.string.greetings), info.name));

        TextView remaining = findViewById(R.id.remaining);
        remaining.setText(String.format("%s %s %s", getString(R.string.remaining), money, "Euros"));

        TextView expenses = findViewById(R.id.expenses);
        expenses.setSelected(true);
        expenses.setText(String.format("%s %s", getString(R.string.expenses), PersonalInfo.getList(info.expenses)));

        Calendar instance = Calendar.getInstance();
        int monthDay = instance.get(Calendar.DAY_OF_MONTH);
        int totalDays = instance.getActualMaximum(Calendar.DAY_OF_MONTH);
        float perDayMoney = (float) money / (totalDays - monthDay);

        TextView perDay = findViewById(R.id.per_day);
        perDay.setText(String.format("%s %.3f %s", getString(R.string.per_day), perDayMoney, "Euros"));

        // ------- Drawer display fields.

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);

        TextView header = headerView.findViewById(R.id.drawer_header);
        header.setText(info.name);

        TextView headerSub = headerView.findViewById(R.id.drawer_subtitle);
        headerSub.setText(String.format("%s %s %s", getString(R.string.wage), info.wage, "Euros"));

    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START);
        else {

            if (secondBackPressed) {

                super.onBackPressed();
                finish();
                return;

            }

            this.secondBackPressed = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(() -> secondBackPressed = false, 2000);

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.display, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        debugAlert(this, "Settings");

        if (id == R.id.action_settings) return true;

        return super.onOptionsItemSelected(item);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {

            case R.id.newExpense:

                editTextAlert(this, "Please enter the new expense..", "Expense adder", new TextConsumer(this));

                break;

            case R.id.nav_gallery:

                break;

            case R.id.nav_slideshow:

                break;

            case R.id.nav_manage:

                break;

            case R.id.nav_share:

                break;

            case R.id.nav_send:

                break;

            default: throw new UnsupportedOperationException("Unexpected id: " + id);

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public final class TextConsumer implements Consumer<String> {

        private final Activity activity;

        TextConsumer(Activity activity) { this.activity = activity; }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void accept(String s) {

            log.debug("User input from display activity: [{}]", s);

            Intent intent = activity.getIntent();
            Bundle bundle = intent.getExtras();
            assert bundle != null;
            PersonalInfo info = (PersonalInfo) bundle.getSerializable(PERSONAL_INFO_KEY);
            assert info != null;
            info.expenses.add(Integer.parseInt(s));

            intent.putExtra(PERSONAL_INFO_KEY, info);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

            File file = new File(getFilesDir(), EXPENSES_FILE);
            FileUtils.dumpToFile(activity, file, s);

            finish();
            startActivity(getIntent());

        }

    }

}
