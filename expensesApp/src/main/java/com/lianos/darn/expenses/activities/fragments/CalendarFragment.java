package com.lianos.darn.expenses.activities.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import com.lianos.darn.expenses.R;
import com.lianos.darn.expenses.activities.DisplayTabbedActivity.RedrawableFragment;
import com.lianos.darn.expenses.activities.PersonalInfoActivity.PersonalInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CalendarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalendarFragment extends Fragment implements RedrawableFragment {

    private static final Logger log = LoggerFactory.getLogger(InfoFragment.class);

    // The fragment initialization parameter, for personal info object.
    private static final String PERSONAL_INFO_PARAM = "personal_info_param";

    private PersonalInfo info;

    // Required empty public constructor
    public CalendarFragment() {}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param info The personal info to use.
     * @return A new instance of fragment CalendarFragment.
     */
    public static CalendarFragment newInstance(PersonalInfo info) {

        CalendarFragment fragment = new CalendarFragment();
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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        CalendarView calendar = view.findViewById(R.id.calendarView);


        return view;

    }

    @Override
    public void redraw(PersonalInfo info) {
        log.debug("Redrawing..");
    }
}
