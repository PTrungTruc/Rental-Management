package com.example.rental_management.adapters;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rental_management.R;
import com.example.rental_management.models.Event;
import com.example.rental_management.models.Notification;
import com.example.rental_management.others.ServiceAutoLauncher;
import com.example.rental_management.views.CalendarFragment;
import com.example.rental_management.views.EditEventActivity;
import com.example.rental_management.views.UpcomingEventsFragment;

import java.util.ArrayList;
import java.util.List;

public class UpcomingEventAdapter extends RecyclerView.Adapter<UpcomingEventAdapter.ViewHolder> {

    private final String TAG = this.getClass().getSimpleName();

    private static final int EDIT_EVENT_ACTIVITY_REQUEST_CODE = 1;

    private Context context;
    private List<Event> events;
    private UpcomingEventsFragment upcomingEventsFragment;
    private ActivityResultLauncher<Intent> editEventLauncher;

    public UpcomingEventAdapter(Context context, List<Event> events, UpcomingEventsFragment upcomingEventsFragment, ActivityResultLauncher<Intent> editEventLauncher) {
        this.context = context;
        this.events = events;
        this.upcomingEventsFragment = upcomingEventsFragment;

//        this.editEventLauncher = upcomingEventsFragment.registerForActivityResult(
//                new ActivityResultContracts.StartActivityForResult(),
//                result -> {
//                    if (result.getResultCode() == UpcomingEventsFragment.RESULT_OK) {
//                        // Handle the result of the activity if necessary
//                        // You can refresh the event list here if needed
//                        upcomingEventsFragment.setUpRecyclerView();
//                        Toast.makeText(context, "Event edited!", Toast.LENGTH_SHORT).show();
//                    }
//                });
        this.editEventLauncher = editEventLauncher;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_upcoming_events_list_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder,int position) {
        final Event event = events.get(position);

        holder.eventTitleTextView.setText(event.getTitle());
        holder.eventDateTextView.setText(event.getDate());
        holder.eventTimeTextView.setText(event.getTime());
        holder.eventNoteTextView.setText(event.getNote());

        holder.optionsImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(holder.optionsImageButton, holder.getAdapterPosition());
            }
        });

        if (!event.isNotify()) {
            holder.notificationImageButton.setVisibility(View.GONE);
        }
//        if (event.isAllDay()) {
//            holder.eventTimeLinearLayout.setVisibility(View.GONE);
//        }

        if (!event.isRecurring()) {
            holder.recurringEventLinearLayout.setVisibility(View.GONE);
        }

        holder.upcomingEventLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditEventActivity.class);
                intent.putExtra("eventId", event.getId());
                intent.putExtra("eventDate", event.getDate());
                editEventLauncher.launch(intent);
//                upcomingEventsFragment.startActivityForResult(intent, EDIT_EVENT_ACTIVITY_REQUEST_CODE);
            }
        });

    }

    private void showPopupMenu(View view, int position) {
        // inflate menu
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.popup, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(position));
        popup.show();
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView eventColorImageView;
        private TextView eventTitleTextView;
        private TextView eventDateTextView;
        private TextView eventTimeTextView;
        private TextView eventNoteTextView;
        private ImageButton optionsImageButton;
        private ImageButton notificationImageButton;
        private LinearLayout eventTimeLinearLayout;
        private LinearLayout recurringEventLinearLayout;
        private LinearLayout upcomingEventLinearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            eventColorImageView = itemView.findViewById(R.id.UpcomingLayoutCell_ImageView_EventColor);
            eventTitleTextView = itemView.findViewById(R.id.UpcomingLayoutCell_TextView_EventTitle);
            eventDateTextView = itemView.findViewById(R.id.UpcomingLayoutCell_TextView_EventDate);
            eventTimeTextView = itemView.findViewById(R.id.UpcomingLayoutCell_TextView_EventTime);
            eventNoteTextView = itemView.findViewById(R.id.UpcomingLayoutCell_TextView_EventNote);
            optionsImageButton = itemView.findViewById(R.id.UpcomingLayoutCell_ImageButton_Options);
            notificationImageButton = itemView.findViewById(R.id.UpcomingLayoutCell_ImageButton_Notification);
            eventTimeLinearLayout = itemView.findViewById(R.id.UpcomingLayoutCell_LinearLayout_Time);
            recurringEventLinearLayout = itemView.findViewById(R.id.UpcomingLayoutCell_LinearLayout_Loop);
            upcomingEventLinearLayout = itemView.findViewById(R.id.UpcomingEventsFragment_LinearLayout);
        }
    }

    private class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        private int position;
        private Event mEvent;

        public MyMenuItemClickListener(int position) {
            this.position = position;
            this.mEvent = events.get(position);
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            Intent intent = null;
            switch (menuItem.getItemId()) {
                case R.id.Popup_Item_Edit:
                    intent = new Intent(context, EditEventActivity.class);
                    intent.putExtra("eventId", mEvent.getId());
                    intent.putExtra("eventDate", mEvent.getDate());
                    editEventLauncher.launch(intent);
                    return true;
                case R.id.Popup_Item_Delete:
                    if (mEvent.isRecurring()) {
                        new AlertDialog.Builder(context)
                                .setTitle("Deleting a Recurring Event")
                                .setMessage("Are you sure you want to delete this recurring event? All occurrences of this event will also be deleted.")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        new DeleteAsyncTask().execute(mEvent.getId());
                                        notifyDataSetChanged();
                                        notifyItemRemoved(position);
                                        notifyItemRangeChanged(position, events.size());
                                        events.remove(position);
                                        upcomingEventsFragment.setUpRecyclerView();
                                        Toast.makeText(context, "Event removed!", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setNegativeButton(android.R.string.no, null)
                                .setIcon(R.drawable.baseline_warning_24)
                                .show();

                    } else {
                        new DeleteAsyncTask().execute(mEvent.getId());
                        events.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, events.size());
                        notifyDataSetChanged();
                        Toast.makeText(context, "Event removed!", Toast.LENGTH_SHORT).show();
                        upcomingEventsFragment.setUpRecyclerView();
                    }

                    upcomingEventsFragment.getChildFragmentManager().beginTransaction().detach(upcomingEventsFragment).commit();
                    upcomingEventsFragment.getChildFragmentManager().beginTransaction().attach(upcomingEventsFragment).commit();

                    return true;
            }

            return false;
        }

//        private void removeAllOccurrences(int id) {
//            int size = events.size();
//            if (size > 0) {
//                for (int i = 0; i < size; i++) {
//                    if (events.get(i).getId() == id) {
//
//                        try {
//                            events.remove(i);
//                            notifyDataSetChanged();
//                        }catch (Exception e){
//                            return;
//                        }
//
//
//                    }
//                }
//
//                //notifyItemRangeRemoved(0, size);
//            }
//
//        }

    }

    private class DeleteAsyncTask extends AsyncTask<Integer, Void, Void> {
        @Override
        protected Void doInBackground(Integer... integers) {
            cancelAllNotifications(integers[0]);
            deleteEvent(integers[0]);
            return null;
        }
    }

    private void cancelAllNotifications(Integer integer) {
        cancelAlarms(readNotifications(integer));
    }

    private void cancelAlarms(List<Notification> notifications) {
        for (Notification notification : notifications) {
            cancelAlarm(notification.getId());
        }
    }

    private void cancelAlarm(int requestCode) {
        Log.d(TAG, "cancelAlarm: " + requestCode);
        Intent intent = new Intent(context.getApplicationContext(), ServiceAutoLauncher.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), requestCode, intent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        pendingIntent.cancel();
    }

    private void deleteEvent(int eventId) {

    }

    private ArrayList<Notification> readNotifications(int eventId) {
        ArrayList<Notification> notifications = new ArrayList<>();

        return notifications;
    }
}