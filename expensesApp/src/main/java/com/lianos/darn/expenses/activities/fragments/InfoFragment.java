package com.lianos.darn.expenses.activities.fragments;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.lianos.darn.expenses.R;
import com.lianos.darn.expenses.activities.DisplayTabbedActivity.RedrawableFragment;
import com.lianos.darn.expenses.activities.PersonalInfoActivity.PersonalInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InfoFragment extends Fragment implements RedrawableFragment {

    private static final Logger log = LoggerFactory.getLogger(InfoFragment.class);

    // The fragment initialization parameter, for personal info object.
    private static final String PERSONAL_INFO_PARAM = "personal_info_param";

    private PersonalInfo info;

    // Required empty public constructor
    public InfoFragment() {}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param info The personal info to use.
     * @return A new instance of fragment InfoFragment.
     */
    public static InfoFragment newInstance(PersonalInfo info) {

        InfoFragment fragment = new InfoFragment();
        Bundle args = new Bundle();
        args.putSerializable(PERSONAL_INFO_PARAM, info);
        fragment.setArguments(args);
        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (getArguments() != null) info = (PersonalInfo) getArguments().getSerializable(PERSONAL_INFO_PARAM);


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_info, container, false);

        // Add values to fields.
        displayFields(view);

        return view;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void redraw(PersonalInfo info) {

        log.debug("Redrawing..");
        this.info = info;
        displayFields(Objects.requireNonNull(getView()));

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("DefaultLocale")
    private void displayFields(View view) {

        // ------- Main display fields.

        log.debug("Found personal info: [{}]. Will set fields.", info);
        AtomicInteger totalMoney = new AtomicInteger();
        totalMoney.set(info.wage);

        info.expenses.forEach(e -> totalMoney.addAndGet(-e.getAmount()));
        info.savings.forEach(s -> totalMoney.addAndGet(-s.getAmount()));

        TextView greeting = view.findViewById(R.id.greeting);
        greeting.setText(String.format("%s %s", getString(R.string.greetings), info.name));

        TextView remaining = view.findViewById(R.id.remaining);
        final int money = totalMoney.get();
        remaining.setText(String.format("%s %s %s", getString(R.string.remaining), money, "Euros"));

        TextView expenses = view.findViewById(R.id.expenses);
        expenses.setSelected(true);
        expenses.setText(String.format("%s %s", getString(R.string.expenses), PersonalInfo.getList(info.expenses)));

        TextView savings = view.findViewById(R.id.savings);
        savings.setSelected(true);
        savings.setText(String.format("%s %s", getString(R.string.savings), PersonalInfo.getList(info.savings)));

        Calendar instance = Calendar.getInstance();
        int monthDay = instance.get(Calendar.DAY_OF_MONTH);
        int totalDays = instance.getActualMaximum(Calendar.DAY_OF_MONTH);
        float perDayMoney = (float) money / (totalDays - monthDay);

        TextView perDay = view.findViewById(R.id.per_day);
        perDay.setText(String.format("%s %.3f %s", getString(R.string.per_day), perDayMoney, "Euros"));

    }

}
