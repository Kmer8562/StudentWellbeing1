package com.example.studentwellbeing.Fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.studentwellbeing.Adapter.HomeSliderAdapter;
import com.example.studentwellbeing.Adapter.WelcomeStudentsAdapter;
import com.example.studentwellbeing.BookingActivity;
import com.example.studentwellbeing.Common.Common;
import com.example.studentwellbeing.Interface.IBannerLoadListener;
import com.example.studentwellbeing.Interface.IBookingInfoLoadListener;
import com.example.studentwellbeing.Interface.IWelcomeStudentsLoadListener;
import com.example.studentwellbeing.Model.Banner;
import com.example.studentwellbeing.Model.BookingInformation;
import com.example.studentwellbeing.R;
import com.example.studentwellbeing.Service.PicassoImageLoadingService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TooManyListenersException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ss.com.bannerslider.Slider;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements IWelcomeStudentsLoadListener, IBannerLoadListener, IBookingInfoLoadListener {

    private Unbinder unbinder;

    @BindView(R.id.layout_user_information)
    LinearLayout layout_user_information;
    @BindView(R.id.txt_user_name)
    TextView txt_user_name;
    @BindView(R.id.banner_slider)
    Slider banner_slider;
    @BindView(R.id.recycler_all_students_welcome)
    RecyclerView recycler_all_students_welcome;

    @BindView(R.id.card_booking_info)
    CardView card_booking_info;
    @BindView(R.id.txt_department_address)
    TextView txt_department_address;
    @BindView(R.id.txt_councillor_name)
    TextView txt_councillor_name;
    @BindView(R.id.txt_time)
    TextView txt_time;
    @BindView(R.id.txt_time_remain)
    TextView txt_time_remain;


    @OnClick(R.id.card_view_booking)
    void booking()
    {
        startActivity(new Intent(getActivity(), BookingActivity.class));
    }

    //Firestore
    CollectionReference bannerRef,welcomeStudentsRef;

    //Interface
    IBannerLoadListener iBannerLoadListener;
    IWelcomeStudentsLoadListener iWelcomeStudentsLoadListener;
    IBookingInfoLoadListener iBookingInfoLoadListener;


    public HomeFragment() {
        bannerRef = FirebaseFirestore.getInstance().collection("Banner");
        welcomeStudentsRef = FirebaseFirestore.getInstance().collection("Welcome Students");

    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserBooking();
    }

    private void loadUserBooking() {
        CollectionReference userBooking = FirebaseFirestore.getInstance()
                .collection("User")
                .document(Common.currentUser.getPhoneNumber())
                .collection("Booking");

        //get current date
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);

        Timestamp toDayTimeStamp = new Timestamp(calendar.getTime());

        //retrieve booking information from firebase
        userBooking
                .whereGreaterThanOrEqualTo("timestamp", toDayTimeStamp)
                .whereEqualTo("done", false)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if(task.isSuccessful())
                        {
                            if(!task.getResult().isEmpty())
                            {
                                for(QueryDocumentSnapshot queryDocumentSnapshot:task.getResult())
                                {
                                    BookingInformation bookingInformation = queryDocumentSnapshot.toObject(BookingInformation.class);
                                    iBookingInfoLoadListener.onBookingInfoLoadSuccess(bookingInformation);
                                    break;
                                }
                            }
                            else
                                iBookingInfoLoadListener.onBookingInfoLoadEmpty();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                    iBookingInfoLoadListener.onBookingInfoLoadFailed(e.getMessage());
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this,view);

        Slider.init(new PicassoImageLoadingService());
        iBannerLoadListener = this;
        iWelcomeStudentsLoadListener = this;
        iBookingInfoLoadListener = this;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user !=null)
        {
            setUserInformation();
            loadBanner();
            loadWelcomeStudents();
            loadUserBooking();
        }
        return view;
    }

    private void loadWelcomeStudents() {
        welcomeStudentsRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<Banner> welcomeStudent = new ArrayList<>();
                        if(task.isSuccessful())
                        {
                            for(QueryDocumentSnapshot bannerSnapShot:task.getResult())
                            {
                                Banner banner = bannerSnapShot.toObject(Banner.class);
                                welcomeStudent.add(banner);
                            }
                            iWelcomeStudentsLoadListener.onWelcomeStudentsLoadSuccess(welcomeStudent);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    iWelcomeStudentsLoadListener.onWelcomeStudentsLoadFailed(e.getMessage());
            }
        });
}

    private void loadBanner() {
        bannerRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<Banner> banners = new ArrayList<>();
                        if(task.isSuccessful())
                        {
                            for(QueryDocumentSnapshot bannerSnapShot:task.getResult())
                            {
                                Banner banner = bannerSnapShot.toObject(Banner.class);
                                banners.add(banner);
                            }
                            iBannerLoadListener.onBannerLoadSuccess(banners);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                iBannerLoadListener.onBannerLoadFailed(e.getMessage());
            }
        });
    }

    private void setUserInformation() {
        layout_user_information.setVisibility(View.VISIBLE);
        txt_user_name.setText(Common.currentUser.getName());
    }

    @Override
    public void onWelcomeStudentsLoadSuccess(List<Banner> banners) {
        recycler_all_students_welcome.setHasFixedSize(true);
        recycler_all_students_welcome.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler_all_students_welcome.setAdapter(new WelcomeStudentsAdapter(getActivity(),banners));
    }

    @Override
    public void onWelcomeStudentsLoadFailed(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBannerLoadSuccess(List<Banner> banners) {
        banner_slider.setAdapter(new HomeSliderAdapter(banners));
    }

    @Override
    public void onBannerLoadFailed(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBookingInfoLoadEmpty() {
        card_booking_info.setVisibility(View.GONE);
    }

    @Override
    public void onBookingInfoLoadSuccess(BookingInformation bookingInformation) {
        txt_department_address.setText(bookingInformation.getDepartmentAddress());
        txt_councillor_name.setText(bookingInformation.getCouncillorName());
        txt_time.setText(bookingInformation.getTime());
        String dateRemain = DateUtils.getRelativeTimeSpanString(
                Long.valueOf(bookingInformation.getTimestamp().toDate().getTime()),
                Calendar.getInstance().getTimeInMillis(), 0).toString();

        txt_time_remain.setText(dateRemain);

        card_booking_info.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBookingInfoLoadFailed(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
