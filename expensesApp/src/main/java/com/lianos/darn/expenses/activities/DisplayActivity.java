package com.lianos.darn.expenses.activities;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
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
import com.lianos.darn.expenses.R;
import com.lianos.darn.expenses.activities.PersonalInfoActivity.PersonalInfo;
import com.lianos.darn.expenses.protocol.Protocol;
import com.lianos.darn.expenses.protocol.Protocol.Expense;
import com.lianos.darn.expenses.protocol.Protocol.Saving;
import com.lianos.darn.expenses.utilities.AlertUtils;
import com.lianos.darn.expenses.utilities.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static com.lianos.darn.expenses.activities.ElementAmountActivity.NEW_EXPENSE_FILE;
import static com.lianos.darn.expenses.activities.ElementAmountActivity.NEW_SAVING_FILE;
import static com.lianos.darn.expenses.activities.PersonalInfoActivity.PERSONAL_INFO_KEY;
import static com.lianos.darn.expenses.utilities.AlertUtils.debugAlert;
import static com.lianos.darn.expenses.utilities.AlertUtils.editTextAlert;

public class DisplayActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final Logger log = LoggerFactory.getLogger(DisplayActivity.class);

    public static final String EXPENSES_FILE = "expensesFile";

    public static final String SAVINGS_FILE = "savingsFile";

    private boolean secondBackPressed = false;

    private Intent element;

    boolean isPaused = false;

    private PersonalInfo info;

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

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;

        if (info == null) info = (PersonalInfo) bundle.getSerializable(PERSONAL_INFO_KEY);
        if (info == null) return;

        log.debug("Found personal info: [{}]. Will set fields.", info);
        AtomicInteger totalMoney = new AtomicInteger();
        totalMoney.set(info.wage);

        info.expenses.forEach(e -> totalMoney.addAndGet(-e.getAmount()));
        info.savings.forEach(s -> totalMoney.addAndGet(-s.getAmount()));

        TextView greeting = findViewById(R.id.greeting);
        greeting.setText(String.format("%s %s", getString(R.string.greetings), info.name));

        TextView remaining = findViewById(R.id.remaining);
        final int money = totalMoney.get();
        remaining.setText(String.format("%s %s %s", getString(R.string.remaining), money, "Euros"));

        TextView expenses = findViewById(R.id.expenses);
        expenses.setSelected(true);
        expenses.setText(String.format("%s %s", getString(R.string.expenses), PersonalInfo.getList(info.expenses)));

        TextView savings = findViewById(R.id.savings);
        savings.setSelected(true);
        savings.setText(String.format("%s %s", getString(R.string.savings), PersonalInfo.getList(info.savings)));

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

        // ------- Buttons.

        // Bind add element button with listener.
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(
                view -> {

                    element = new Intent(DisplayActivity.this, ElementAmountActivity.class);
                    element.putExtra(PERSONAL_INFO_KEY, info);
                    DisplayActivity.this.startActivity(element);

                }
        );


    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onStop() {

        File expensesFile = new File(getFilesDir(), EXPENSES_FILE);
        File savingsFile = new File(getFilesDir(), SAVINGS_FILE);
        try {

            DatabaseUtil.dump(expensesFile, info.expenses);
            DatabaseUtil.dump(savingsFile, info.savings);

        } catch (IOException e) { log.error("Failed.", e); }

        super.onStop();

    }

    @Override
    protected void onPause() { super.onPause(); isPaused = true; }

    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onResume() {

        super.onResume();

        if (isPaused) {

            File cache = null;
            try {

                AtomicReference<Protocol.Amount> amount = new AtomicReference<>();

                cache = new File(getCacheDir(), NEW_EXPENSE_FILE);
                if (cache.exists()) {

                    DatabaseUtil.readDatabase(cache, Protocol.Expense.class, amount::set);


                } else {

                    cache = new File(getCacheDir(), NEW_SAVING_FILE);
                    if (cache.exists()) {

                        DatabaseUtil.readDatabase(cache, Protocol.Saving.class, amount::set);

                    }

                }

                Protocol.Amount value = amount.get();
                if (value == null) { isPaused = false; return; }

                if (value instanceof Expense) info.expenses.add(value);
                else if (value instanceof Saving) info.savings.add(value);
                else throw new IllegalArgumentException("Unexpected class: " + value.getClass());

            } catch (IOException e) {

                log.error("Failed.", e);
                AlertUtils.fileCreationFailure(this);
                return;

            } finally {

                if (cache != null && !cache.delete())
                    log.error("failed to delete cached file [{}]", cache.getPath());

            }

            log.debug("Resuming with info: [{}]", info);

            displayFields();
        }

        isPaused = false;

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

        if (id == R.id.action_settings) return true;

        return super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {

            case R.id.newExpense:

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

}
