package com.example.rental_management.views;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rental_management.R;
import com.example.rental_management.Utils;
import com.example.rental_management.adapters.EventAdapter;
import com.example.rental_management.adapters.GridAdapter;
import com.example.rental_management.models.Event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarFragment extends Fragment {

    private final String TAG = this.getClass().getSimpleName();

    private static final int ADD_NEW_EVENT_ACTIVITY_REQUEST_CODE = 0;
    private static final int EDIT_EVENT_ACTIVITY_REQUEST_CODE = 1;
    public static final int RESULT_OK = Activity.RESULT_OK;
    public static final int RESULT_CANCELED = Activity.RESULT_CANCELED;

    public static final Calendar calendar = Calendar.getInstance(Locale.ENGLISH);

    private List<Date> dates = new ArrayList<>();
    private List<Event> events = new ArrayList<>();

    private ImageButton previousMonthImageButton, nextMonthImageButton;
    private TextView currentDateTextView;
    private GridView datesGridView;

    // AlertDialog components
    public RecyclerView savedEventsRecyclerView;
    private Button addNewEventButton;
    private TextView noEventTextView;

    private AlertDialog alertDialog;

    // ActivityResultLauncher for editing event
    private ActivityResultLauncher<Intent> editEventLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        editEventLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == CalendarFragment.RESULT_OK) {
                        // Handle the result of the activity if necessary
                        // You can refresh the event list here if needed
                        setUpCalendar();
                        Toast.makeText(getActivity(), "Event edited!", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                    }
                });

        defineViews(view);
        defineListeners();
        setUpCalendar();

        return view;
    }

    private void defineViews(View view) {
        nextMonthImageButton = view.findViewById(R.id.CalenderFragment_Button_Next);
        previousMonthImageButton = view.findViewById(R.id.CalenderFragment_Button_Prev);
        currentDateTextView = view.findViewById(R.id.CalenderFragment_TextView_CurrentDate);
        datesGridView = view.findViewById(R.id.CalenderFragment_GridView_Dates);
    }

    private void defineListeners() {
        nextMonthImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH, 1);
                setUpCalendar();
            }
        });

        previousMonthImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH, -1);
                setUpCalendar();
            }
        });

        datesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Avoid clicking on non-activate dates
                Date viewDate = dates.get(position);
                Calendar viewCalendar = calendar.getInstance();
                viewCalendar.setTime(viewDate);
                if (viewCalendar.get(Calendar.YEAR) != calendar.get(Calendar.YEAR) || viewCalendar.get(Calendar.MONTH) != calendar.get(Calendar.MONTH)) {
                    return;
                }

                // Show events alert dialog
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setCancelable(true);
                View dialogView = LayoutInflater.from(getActivity().getBaseContext()).inflate(R.layout.layout_alert_dialog, parent, false);
                builder.setView(dialogView);
                alertDialog = builder.create();
                alertDialog.show();
                alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        setUpCalendar();
                    }
                });

                savedEventsRecyclerView = (RecyclerView) dialogView.findViewById(R.id.AlertDialog_RecyclerView_ListEvents);
                addNewEventButton = (Button) dialogView.findViewById(R.id.AlertDialog_Button_AddEvent);
                noEventTextView = (TextView) dialogView.findViewById(R.id.AlertDialog_TextView_NoEvent);


                final String date = Utils.eventDateFormat.format(dates.get(position));
                final List<Event> eventsByDate = collectEventsByDate(dates.get(position));

                if (eventsByDate.isEmpty()) {
                    savedEventsRecyclerView.setVisibility(View.INVISIBLE);
                    noEventTextView.setVisibility(View.VISIBLE);
                    addNewEventButton.setText("CREATE EVENT");
                } else {
                    savedEventsRecyclerView.setVisibility(View.VISIBLE);
                    noEventTextView.setVisibility(View.GONE);
                    savedEventsRecyclerView.setHasFixedSize(true);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
                    savedEventsRecyclerView.setLayoutManager(layoutManager);
                    EventAdapter eventAdapter = new EventAdapter(getActivity(), eventsByDate, alertDialog, CalendarFragment.this, editEventLauncher);
                    savedEventsRecyclerView.setAdapter(eventAdapter);
                    eventAdapter.notifyDataSetChanged();
                    addNewEventButton.setText("ADD EVENT");
                }


                addNewEventButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), NewEventActivity.class);
                        intent.putExtra("date", date);
                        startActivityForResult(intent, ADD_NEW_EVENT_ACTIVITY_REQUEST_CODE);
                        alertDialog.dismiss();
                    }
                });
            }
        });

    }

    public void setUpCalendar() {
        String dateString = Utils.dateFormat.format(calendar.getTime());
        currentDateTextView.setText(dateString);

        dates.clear();

        Calendar monthCalendar = (Calendar) calendar.clone();
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1); // start from Monday

        int firstDayOfMonth = monthCalendar.get(Calendar.DAY_OF_WEEK) - 2;
        monthCalendar.add(Calendar.DAY_OF_MONTH, -firstDayOfMonth);

//        collectEventsByMonth(Utils.yearFormat.format(calendar.getTime()), Utils.monthFormat.format(calendar.getTime()));

        while (dates.size() < Utils.MAX_CALENDAR_DAYS) {
            dates.add(monthCalendar.getTime());
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }


        GridAdapter gridAdapter = new GridAdapter(getContext(), dates, calendar, events);
        datesGridView.setAdapter(gridAdapter);

    }

    private void collectEventsByMonth(String year, String month) {
        events.clear();
    }

    private List<Event> collectEventsByDate(Date date) {
        List<Event> eventList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
//
//        int month = calendar.get(Calendar.MONTH);
//        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
//        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return eventList;
    }

    private boolean isContains(List<Event> events, int eventId) {
        for (Event event : events) {
            if (event.getId() == eventId) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_NEW_EVENT_ACTIVITY_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                setUpCalendar();
                Toast.makeText(getActivity(), "Event created!", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == EDIT_EVENT_ACTIVITY_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                setUpCalendar();
                Toast.makeText(getActivity(), "Event edited!", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        }
    }
}