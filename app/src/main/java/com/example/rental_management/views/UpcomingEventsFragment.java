package com.example.rental_management.views;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rental_management.R;
import com.example.rental_management.Utils;
import com.example.rental_management.adapters.UpcomingEventAdapter;
import com.example.rental_management.models.Event;
import com.example.rental_management.models.RecurringPattern;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UpcomingEventsFragment extends Fragment {

    private final String TAG = this.getClass().getSimpleName();

    private static final int EDIT_EVENT_ACTIVITY_REQUEST_CODE = 1;
    public static final int RESULT_OK = Activity.RESULT_OK;
    public static final int RESULT_CANCELED = Activity.RESULT_CANCELED;

    private ImageButton changePeriodImageButton;
    public TextView periodTextView;
    private RecyclerView eventsRecyclerView;

    private ActivityResultLauncher<Intent> editEventLauncher;

    //public String period;
    private String todayDate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upcoming_events, container, false);

//        period = Utils.CURRENT_FILTER;
        editEventLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == UpcomingEventsFragment.RESULT_OK) {
                        // Handle the result of the activity if necessary
                        // You can refresh the event list here if needed
                        setUpRecyclerView();
                        Toast.makeText(getActivity(), "Event edited!", Toast.LENGTH_SHORT).show();
                    }
                });

        defineViews(view);
        initViews();
        defineListeners();

        return view;
    }


    private void defineViews(View view) {
        changePeriodImageButton = (ImageButton) view.findViewById(R.id.UpcomingEventsFragment_ImageButton_Period);
        periodTextView = (TextView) view.findViewById(R.id.UpcomingEventsFragment_TextView_Period);
        eventsRecyclerView = (RecyclerView) view.findViewById(R.id.UpcomingEventsFragment_RecyclerView_Events);
    }

    private void initViews() {
        periodTextView.setText(Utils.CURRENT_FILTER);
        setUpRecyclerView();
    }

    private void defineListeners() {
        changePeriodImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // inflate menu
                PopupMenu popup = new PopupMenu(getActivity(), view);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.popup_period, popup.getMenu());
                popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
                popup.show();

                setUpRecyclerView();
            }

            class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.PopupPeriod_Item_Today:
                            Utils.CURRENT_FILTER = Utils.TODAY;
                            periodTextView.setText(Utils.CURRENT_FILTER);
                            break;
                        case R.id.PopupPeriod_Item_Next7Days:
                            Utils.CURRENT_FILTER = Utils.NEXT_7_DAYS;
                            periodTextView.setText(Utils.CURRENT_FILTER);
                            break;
                        case R.id.PopupPeriod_Item_Next30Days:
                            Utils.CURRENT_FILTER = Utils.NEXT_30_DAYS;
                            periodTextView.setText(Utils.CURRENT_FILTER);
                            break;
                    }
                    setUpRecyclerView();
                    return true;
                }
            }


        });
    }


    public void setUpRecyclerView() {
        eventsRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setMeasurementCacheEnabled(false);
        eventsRecyclerView.setLayoutManager(layoutManager);
        UpcomingEventAdapter upcomingEventAdapter = new UpcomingEventAdapter(getActivity(), collectEvents(Calendar.getInstance().getTime()), this, editEventLauncher);
        eventsRecyclerView.setAdapter(upcomingEventAdapter);
    }


    private List<Event> collectEvents(Date today) {
        List<Event> events = null;
        try {
            switch (Utils.CURRENT_FILTER) {
                case Utils.TODAY:
                    events = collectTodayEvents(today);
                    break;
                case Utils.NEXT_7_DAYS:
                    events = collectNext7DaysEvents(today);
                    break;
                case Utils.NEXT_30_DAYS:
                    events = collectNext30DaysEvents(today);
                    break;
            }
        } catch (ParseException e) {
            Log.e(TAG, "An error has occurred while parsing the date string");
        }

        return events;
    }

    private List<Event> collectTodayEvents(Date today) {
        List<Event> eventList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);

        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        // Add recurring events
        List<RecurringPattern> recurringPatterns = readRecurringPatterns();
        Event event = new Event();
        return eventList;
    }

    private List<Event> collectNext7DaysEvents(Date today) throws ParseException {
        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.setTime(today);

        Calendar toCalendar = (Calendar) fromCalendar.clone();
        toCalendar.add(Calendar.DAY_OF_MONTH, 8);

        Date fromDate = fromCalendar.getTime();
        Date toDate = toCalendar.getTime();

        List<Event> eventList = new ArrayList<>();
        // Add recurring events
        Event event = new Event();
        return eventList;
    }

    private List<Event> collectNext30DaysEvents(Date today) throws ParseException {
        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.setTime(today);

        Calendar toCalendar = (Calendar) fromCalendar.clone();
        toCalendar.add(Calendar.DAY_OF_MONTH, 31);

        Date fromDate = fromCalendar.getTime();
        Date toDate = toCalendar.getTime();

        List<Event> eventList = new ArrayList<>();
        return eventList;
    }

    private List<Event> collectAllEvents(Date today) {
        List<Event> allEvents = new ArrayList<>();
        return  allEvents;
    }

    private List<RecurringPattern> readRecurringPatterns() {
        List<RecurringPattern> recurringPatterns = new ArrayList<>();
        return recurringPatterns;
    }

    private boolean isContains(List<Event> events, int eventId) {
        for (Event event : events) {
            if (event.getId() == eventId) {
                return true;
            }
        }
        return false;
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == getActivity().RESULT_OK) {
//            setUpRecyclerView();
//            Toast.makeText(getActivity(), "Event edited!", Toast.LENGTH_SHORT).show();
//        }
//    }
}
