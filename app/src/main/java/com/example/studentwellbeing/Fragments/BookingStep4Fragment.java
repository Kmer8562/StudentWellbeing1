package com.example.studentwellbeing.Fragments;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.studentwellbeing.Common.Common;
import com.example.studentwellbeing.Model.BookingInformation;
import com.example.studentwellbeing.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;

public class BookingStep4Fragment extends Fragment {

    SimpleDateFormat simpleDateFormat;
    LocalBroadcastManager localBroadcastManager;
    Unbinder unbinder;

    AlertDialog dialog;

    @BindView(R.id.txt_booking_councillor_text)
    TextView txt_booking_councillor_text;
    @BindView(R.id.txt_booking_time_text)
    TextView txt_booking_time_text;
    @BindView(R.id.txt_department_address)
    TextView txt_department_address;
    @BindView(R.id.txt_department_name)
    TextView txt_department_name;
    @BindView(R.id.txt_department_open_hours)
    TextView txt_department_open_hours;
    @BindView(R.id.txt_department_phone)
    TextView txt_department_phone;
    @BindView(R.id.txt_department_website)
    TextView txt_department_website;

    @OnClick(R.id.btn_confirm)
            void confirmBooking(){

                dialog.show();

                //Process timestamp to show all future bookings
                String startTime = Common.convertTimeSlotToString(Common.currentTimeSlot);
                String [] convertTime = startTime.split("-");
                String [] startTimeConvert = convertTime[0].split(":");
                int startHourInt = Integer.parseInt(startTimeConvert[0].trim());
                int startMinInt = Integer.parseInt(startTimeConvert[1].trim());

                Calendar bookingDateWithourHouse = Calendar.getInstance();
                bookingDateWithourHouse.setTimeInMillis(Common.bookingDate.getTimeInMillis());
                bookingDateWithourHouse.set(Calendar.HOUR_OF_DAY,startHourInt);
                bookingDateWithourHouse.set(Calendar.MINUTE,startMinInt);

                //Timestamp object to apply to Booking information
                Timestamp timestamp = new Timestamp(bookingDateWithourHouse.getTime());

                //Create booking information
                BookingInformation bookingInformation = new BookingInformation();

                bookingInformation.setTimestamp(timestamp);
                bookingInformation.setDone(false);
                bookingInformation.setCouncillorId(Common.currentCouncillor.getCouncillorId());
                bookingInformation.setCouncillorName(Common.currentCouncillor.getName());
                bookingInformation.setCustomerName(Common.currentUser.getName());
                bookingInformation.setCustomerPhone(Common.currentUser.getPhoneNumber());
                bookingInformation.setDepartmentId(Common.currentDepartment.getDepartmentId());
                bookingInformation.setDepartmentAddress(Common.currentDepartment.getAddress());
                bookingInformation.setCustomerName(Common.currentDepartment.getName());
                bookingInformation.setTime(new StringBuilder(Common.convertTimeSlotToString(Common.currentTimeSlot))
                        .append(" at ")
                        .append(simpleDateFormat.format(bookingDateWithourHouse.getTime())).toString());
                bookingInformation.setSlot(Long.valueOf(Common.currentTimeSlot));

                //submit to councillor document
                DocumentReference bookingDate = FirebaseFirestore.getInstance()
                        .collection("AllDepartment")
                        .document(Common.campus)
                        .collection("Facility")
                        .document(Common.currentCouncillor.getCouncillorId())
                        .collection("Councillor")
                        .document(Common.currentCouncillor.getCouncillorId())
                        .collection(Common.simpleDateFormat.format(Common.bookingDate.getTime()))
                        .document(String.valueOf(Common.currentTimeSlot));

                //Write data
                bookingDate.set(bookingInformation)
                        .addOnSuccessListener(aVoid -> {
                            //Check to see if there is already a booking to prevent double booking
                            addToUserBooking(bookingInformation);

                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

    private void addToUserBooking(BookingInformation bookingInformation) {


        final CollectionReference userBooking = FirebaseFirestore.getInstance()
                .collection("User")
                .document(Common.currentUser.getPhoneNumber())
                .collection("Booking");

        userBooking.whereEqualTo("done", false)
                .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.getResult().isEmpty())
                                {
                                    //Get data
                                    userBooking.document()
                                            .set(bookingInformation)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    if(dialog.isShowing())
                                                        dialog.dismiss();

                                                    addToCalendar(Common.bookingDate,
                                                            Common.convertTimeSlotToString(Common.currentTimeSlot));

                                                    resetStaticData();
                                                    getActivity().finish();
                                                    Toast.makeText(getContext(), "Success!", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    if(dialog.isShowing())
                                                        dialog.dismiss();
                                                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                                else
                                {
                                    if(dialog.isShowing())
                                        dialog.dismiss();

                                    resetStaticData();
                                    getActivity().finish();
                                    Toast.makeText(getContext(), "Success!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
    }

    private void addToCalendar(Calendar bookingDate, String startDate) {
        String startTime = Common.convertTimeSlotToString(Common.currentTimeSlot);
        String [] convertTime = startTime.split("-");
        String [] startTimeConvert = convertTime[0].split(":");
        int startHourInt = Integer.parseInt(startTimeConvert[0].trim());
        int startMinInt = Integer.parseInt(startTimeConvert[1].trim());

        String [] endTimeConvert = convertTime[0].split(":");
        int endHourInt = Integer.parseInt(endTimeConvert[0].trim());
        int endMinInt = Integer.parseInt(endTimeConvert[1].trim());

        Calendar startEvent = Calendar.getInstance();
        startEvent.setTimeInMillis(bookingDate.getTimeInMillis());
        startEvent.set(Calendar.HOUR_OF_DAY, startHourInt); //set event start hour
        startEvent.set(Calendar.MINUTE, startMinInt); //set event start min

        Calendar endEvent = Calendar.getInstance();
        endEvent.setTimeInMillis(bookingDate.getTimeInMillis());
        endEvent.set(Calendar.HOUR_OF_DAY, endHourInt);
        endEvent.set(Calendar.MINUTE, endMinInt);

        SimpleDateFormat calendarDateFormat = new SimpleDateFormat("dd-MM-yyyy:mm");
        String startEventTime = calendarDateFormat.format(startEvent.getTime());
        String endEventTime = calendarDateFormat.format(endEvent.getTime());

        addToDeviceCalendar(startEventTime, endEventTime, "Counselling Appointment",
                new StringBuilder("Counselling with")
        .append(startTime)
        .append("with")
        .append(Common.currentCouncillor.getName())
        .append("at")
        .append(Common.currentDepartment.getName()).toString(),
                new StringBuilder("Address: ").append(Common.currentDepartment.getAddress()).toString());



    }

    private void addToDeviceCalendar(String startEventTime, String endEventTime, String title, String description, String location) {
        SimpleDateFormat calendarDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");

        try {
            Date start = calendarDateFormat.parse(startEventTime);
            Date end = calendarDateFormat.parse(endEventTime);

            ContentValues event = new ContentValues();

            event.put(CalendarContract.Events.CALENDAR_ID, getCalendar(getContext()));
            event.put(CalendarContract.Events.TITLE,title);
            event.put(CalendarContract.Events.DESCRIPTION,description);
            event.put(CalendarContract.Events.EVENT_LOCATION,location);

            event.put(CalendarContract.Events.DTSTART,start.getTime());
            event.put(CalendarContract.Events.DTEND,end.getTime());
            event.put(CalendarContract.Events.ALL_DAY,0);
            event.put(CalendarContract.Events.HAS_ALARM,1);

            String timeZone = TimeZone.getDefault().getID();
            event.put(CalendarContract.Events.EVENT_TIMEZONE,timeZone);

            Uri calendars ;
            if(Build.VERSION.SDK_INT >= 0)
                calendars = Uri.parse("content://com.android.calendar/calendars");
            else
                calendars = Uri.parse("content://calendar/events");


            getActivity().getContentResolver().insert(calendars,event);

        } catch (ParseException e) {
            e.printStackTrace();

        }

    }

    private String getCalendar(Context context) {
        String gmailIdCalendar = "";
        String projection[] = {"_id", "calendar_displayName"};
        Uri calendars = Uri.parse("content://com.android.calendar/calendars");

        ContentResolver contentResolver = context.getContentResolver();

        Cursor managedCursor = contentResolver.query(calendars, projection, null, null, null);
        if(managedCursor.moveToFirst())
        {
            String calName;
            int nameCol = managedCursor.getColumnIndex(projection[1]);
            int idCol = managedCursor.getColumnIndex(projection[0]);
            do{
                calName = managedCursor.getString(nameCol);
                if(calName.contains("@gmail.com"))
                {
                    gmailIdCalendar = managedCursor.getString((idCol));
                    break; //Exit once Id is retrieved
                }
            } while (managedCursor.moveToNext());
            managedCursor.close();
        }
        return gmailIdCalendar;
    };

    private void resetStaticData() {
        Common.step = 0;
        Common.currentTimeSlot = -1;
        Common.currentDepartment = null;
        Common.currentCouncillor = null;
        Common.bookingDate.add(Calendar.DATE, 0);
    }

    BroadcastReceiver confirmBookingReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setData();
        }
    };

    private void setData() {
        txt_booking_councillor_text.setText(Common.currentCouncillor.getName());
        txt_booking_time_text.setText(new StringBuilder(Common.convertTimeSlotToString(Common.currentTimeSlot))
        .append(" at ")
        .append(simpleDateFormat.format(Common.bookingDate.getTime())));

        txt_department_address.setText(Common.currentDepartment.getAddress());
        txt_department_name.setText(Common.currentDepartment.getName());
        txt_department_website.setText(Common.currentDepartment.getWebsite());
        txt_department_open_hours.setText(Common.currentDepartment.getOpenHours());

    }


    static BookingStep4Fragment instance;

    public static BookingStep4Fragment getInstance() {
        if (instance == null)
            instance = new BookingStep4Fragment();
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

        localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        localBroadcastManager.registerReceiver(confirmBookingReciever, new IntentFilter(Common.KEY_CONFIRM_BOOKING));

        dialog = new SpotsDialog.Builder().setContext(getContext()).setCancelable(false)
                .build();
    }

    @Override
    public void onDestroy() {
        localBroadcastManager.unregisterReceiver(confirmBookingReciever);
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View itemView = inflater.inflate(R.layout.fragment_booking_step_four,container, false);
        unbinder = ButterKnife.bind(this, itemView);
        return itemView;
    }
}



