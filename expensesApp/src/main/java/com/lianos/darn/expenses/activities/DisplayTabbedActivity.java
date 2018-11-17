package com.lianos.darn.expenses.activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources.Theme;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;
import com.lianos.darn.expenses.R;
import com.lianos.darn.expenses.activities.PersonalInfoActivity.PersonalInfo;
import com.lianos.darn.expenses.activities.fragments.CalendarFragment;
import com.lianos.darn.expenses.activities.fragments.InfoFragment;
import com.lianos.darn.expenses.protocol.Protocol;
import com.lianos.darn.expenses.utilities.AlertUtils;
import com.lianos.darn.expenses.utilities.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import static com.lianos.darn.expenses.activities.ElementAmountActivity.NEW_EXPENSE_FILE;
import static com.lianos.darn.expenses.activities.ElementAmountActivity.NEW_SAVING_FILE;
import static com.lianos.darn.expenses.activities.PersonalInfoActivity.PERSONAL_INFO_KEY;

public class DisplayTabbedActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final Logger log = LoggerFactory.getLogger(DisplayTabbedActivity.class);

    public static final String EXPENSES_FILE = "expensesFile";

    public static final String SAVINGS_FILE = "savingsFile";

    private boolean secondBackPressed = false;

    private boolean isPaused = false;

    private Intent element;

    private PersonalInfo info;

    private RedrawableFragment fragment;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        log.debug("Entering display activity..");

        super.onCreate(savedInstanceState);

        // Link with the XML file.
        setContentView(R.layout.activity_display_tabbed);

        // Enable toolbar.
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Do not show name of activity on the top bar.
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        // Link with the XML file.
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        // Bind drawer with listener.
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Find passed personal info object.
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;

        if (info == null) info = (PersonalInfo) bundle.getSerializable(PERSONAL_INFO_KEY);
        if (info == null) return;

        log.debug("Found personal info: [{}].", info);

        // Setup spinner
        Spinner spinner = findViewById(R.id.spinner);
        spinner.setAdapter(new SpinnerAdapter(
                toolbar.getContext(),
                new String[]{
                        "Display",
                        "Calendar",
                        "Section 3",
                }));

        // Bind spinner with listener.
        spinner.setOnItemSelectedListener(new SpinnerListener(getSupportFragmentManager()));

        // ------- Buttons.

        // Bind add element button with listener.
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(
                view -> {

                    element = new Intent(DisplayTabbedActivity.this, ElementAmountActivity.class);
                    element.putExtra(PERSONAL_INFO_KEY, info);
                    DisplayTabbedActivity.this.startActivity(element);

                }
        );

        redraw();

    }

    private void redraw() {

        // ------- Drawer display fields.

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);

        TextView header = headerView.findViewById(R.id.drawer_header);
        header.setText(info.name);

        TextView headerSub = headerView.findViewById(R.id.drawer_subtitle);
        headerSub.setText(String.format("%s %s %s", getString(R.string.wage), info.wage, "Euros"));

        if (fragment != null) fragment.redraw(info);

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

                if (value instanceof Protocol.Expense) info.expenses.add(value);
                else if (value instanceof Protocol.Saving) info.savings.add(value);
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
            redraw();

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
        getMenuInflater().inflate(R.menu.menu_display_tabbed, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
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

    private static class SpinnerAdapter extends ArrayAdapter<String> implements ThemedSpinnerAdapter {

        private final ThemedSpinnerAdapter.Helper mDropDownHelper;

        SpinnerAdapter(Context context, String[] objects) {

            super(context, android.R.layout.simple_list_item_1, objects);
            mDropDownHelper = new ThemedSpinnerAdapter.Helper(context);

        }

        @Override
        public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {

            View view;

            if (convertView == null) {

                // Inflate the drop down using the helper's LayoutInflater
                LayoutInflater inflater = mDropDownHelper.getDropDownViewInflater();
                view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);

            } else view = convertView;

            TextView textView = view.findViewById(android.R.id.text1);
            textView.setText(getItem(position));

            return view;
        }

        @Override
        public Theme getDropDownViewTheme() { return mDropDownHelper.getDropDownViewTheme(); }

        @Override
        public void setDropDownViewTheme(Theme theme) { mDropDownHelper.setDropDownViewTheme(theme); }

    }

    class SpinnerListener implements OnItemSelectedListener {

        private final FragmentManager manager;

        SpinnerListener(FragmentManager manager) { this.manager = manager; }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            // When the given dropdown item is selected, show its contents in the
            // container view.
            FragmentTransaction transaction = manager.beginTransaction();

            switch (position) {

                case 0:

                    transaction.replace(R.id.container, (Fragment) (fragment = InfoFragment.newInstance(info))).commit();
                    break;

                case 1:

                    transaction.replace(R.id.container, (Fragment) (fragment = CalendarFragment.newInstance(info))).commit();
                    break;

            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {}


    }

    public interface RedrawableFragment { void redraw(PersonalInfo info); }

}
