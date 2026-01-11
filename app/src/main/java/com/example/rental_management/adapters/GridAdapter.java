package com.example.rental_management.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.rental_management.R;
import com.example.rental_management.Utils;
import com.example.rental_management.models.Event;
import com.example.rental_management.models.RecurringPattern;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class GridAdapter extends ArrayAdapter {

    private final String DAILY = "Repeat Daily";
    private final String WEEKLY = "Repeat Weekly";
    private final String MONTHLY = "Repeat Monthly";
    private final String YEARLY = "Repeat Yearly";

    private List<Date> dates;
    private Calendar selectedCalendar;
    private List<Event> events;
    private LayoutInflater layoutInflater;
    private TextView dayTextView;
    private TextView eventCountTextView;


    public GridAdapter(@NonNull Context context, List<Date> dates, Calendar selectedCalendar, List<Event> events) {
        super(context, R.layout.layout_cell);

        this.dates = dates;
        this.selectedCalendar = selectedCalendar;
        this.events = events;
        this.layoutInflater = LayoutInflater.from(context);
    }


    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Date viewDate = dates.get(position);
        Calendar viewCalendar = Calendar.getInstance();
        viewCalendar.setTime(viewDate);

        int viewMonth = viewCalendar.get(Calendar.MONTH);
        int viewYear = viewCalendar.get(Calendar.YEAR);
        int viewDayOfMonth = viewCalendar.get(Calendar.DAY_OF_MONTH);
        int viewDayOfWeek = viewCalendar.get(Calendar.DAY_OF_WEEK);

        int selectedMonth = selectedCalendar.get(Calendar.MONTH);
        int selectedYear = selectedCalendar.get(Calendar.YEAR);

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.layout_cell, parent, false);
            dayTextView = (TextView) convertView.findViewById(R.id.LayoutCell_TextView_Day);
            eventCountTextView = (TextView) convertView.findViewById(R.id.LayoutCell_TextView_EventCount);

            dayTextView.setText(String.valueOf(viewDayOfMonth));
        }

        TextView dayTextView = convertView.findViewById(R.id.LayoutCell_TextView_Day);
        TextView eventCountTextView = convertView.findViewById(R.id.LayoutCell_TextView_EventCount);
        LinearLayout bgLinearLayout = convertView.findViewById(R.id.LayoutCell_LinearLayout);


        if (viewYear == selectedYear && viewMonth == selectedMonth) {
            // Active dates
            convertView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.Grey800));
            dayTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
            eventCountTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));

        } else {
            // Inactive dates
            dayTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.darkGrey));
            eventCountTextView.setVisibility(View.GONE);
        }

        // Highlight current day on the calendar
        Calendar mCalendar = Calendar.getInstance();
        if (viewYear == mCalendar.get(Calendar.YEAR) && viewMonth == mCalendar.get(Calendar.MONTH) && viewDayOfMonth == mCalendar.get(Calendar.DAY_OF_MONTH)) {
            bgLinearLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
            dayTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
            eventCountTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        }


        // Find event count
        List<Integer> eventIDs = new ArrayList<>();
        List<RecurringPattern> recurringPatterns = readRecurringPatterns();
        for (RecurringPattern recurringPattern : recurringPatterns) {
            switch (recurringPattern.getPattern()) {
                case DAILY:
                    eventIDs.add(recurringPattern.getEventId());
                    break;
                case WEEKLY:
                    if (viewDayOfWeek == recurringPattern.getDayOfWeek()) {
                        eventIDs.add(recurringPattern.getEventId());
                    }
                    break;
                case MONTHLY:
                    if (viewDayOfMonth == recurringPattern.getDayOfMonth()) {
                        eventIDs.add(recurringPattern.getEventId());
                    }
                    break;
                case YEARLY:
                    if (viewMonth == recurringPattern.getMonthOfYear() && viewDayOfMonth == recurringPattern.getDayOfMonth()) {
                        eventIDs.add(recurringPattern.getEventId());
                    }
                    break;
            }
        }

        mCalendar = Calendar.getInstance();
        for (Event event : events) {
            if(event.getDate()!=null){
                mCalendar.setTime(Utils.convertStringToDate(event.getDate()));
                if (viewDayOfMonth == mCalendar.get(Calendar.DAY_OF_MONTH) && viewMonth == mCalendar.get(Calendar.MONTH) && viewYear == mCalendar.get(Calendar.YEAR) && !eventIDs.contains(event.getId())) {
                    eventIDs.add(event.getId());
                }
            }

        }

        if (!eventIDs.isEmpty()) {
            eventCountTextView.setText(Integer.toString(eventIDs.size()));
        }

        return convertView;
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

    @Override
    public int getCount() {
        return dates.size();
    }

    @Override
    public int getPosition(@Nullable Object item) {
        return dates.indexOf(item);
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return dates.get(position);
    }
}
