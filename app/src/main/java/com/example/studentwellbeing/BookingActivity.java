package com.example.studentwellbeing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import com.example.studentwellbeing.Adapter.MyViewPageAdapter;
import com.example.studentwellbeing.Common.Common;
import com.example.studentwellbeing.Common.NonSwipeViewPager;
import com.example.studentwellbeing.Model.Councillor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.shuhart.stepview.StepView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dmax.dialog.SpotsDialog;

public class BookingActivity extends AppCompatActivity {

    LocalBroadcastManager localBroadcastManager;
    AlertDialog dialog;
    CollectionReference councillorRef;

    @BindView(R.id.step_view)
    StepView stepView;
    @BindView(R.id.view_pager)
    NonSwipeViewPager viewPager;
    @BindView(R.id.btn_previous_step)
    Button btn_previous_step;
    @BindView(R.id.btn_next_step)
    Button btn_next_step;

    //Event
    @OnClick(R.id.btn_previous_step)
    void previousStep(){
        if(Common.step == 3 || Common.step > 0)
        {
            Common.step--;
            viewPager.setCurrentItem(Common.step);
            if(Common.step < 3)
            {
                btn_next_step.setEnabled(true);
                setColorButton();
            }
        }
    }
    @OnClick(R.id.btn_next_step)
    void nextClick(){
       if(Common.step < 3 || Common.step == 0)
       {
           Common.step++;
           if(Common.step == 1)
           {
               if(Common.currentDepartment != null)
                   loadCouncillorByDepartment(Common.currentDepartment.getDepartmentId());
           }
           else if(Common.step == 2)
           {
               if(Common.currentCouncillor != null)
                   loadTimeSlotOfCouncillor(Common.currentCouncillor.getCouncillorId());
           }
           else if(Common.step == 3)
           {
               if(Common.currentTimeSlot != -1)
                   confirmBooking();
           }
           viewPager.setCurrentItem(Common.step);
       }
    }

    private void confirmBooking() {
        Intent intent = new Intent(Common.KEY_CONFIRM_BOOKING);
        localBroadcastManager.sendBroadcast(intent);
    }

    private void loadTimeSlotOfCouncillor(String councillorId) {
        Intent intent = new Intent(Common.KEY_DISPLAY_TIME_SLOT);
        localBroadcastManager.sendBroadcast(intent);

    }

    private void loadCouncillorByDepartment(String departmentId) {
        dialog.show();

        //Select councillor of that department
        ///AllDepartments/Jordanstown/Facility/V6Y49PzoCfLiPbavteWU/Councillors
        if(!TextUtils.isEmpty(Common.campus))
        {
            councillorRef = FirebaseFirestore.getInstance()
                    .collection("AllDepartment")
                    .document(Common.campus)
                    .collection("Facility")
                    .document(departmentId)
                    .collection("Councillor");

            councillorRef.get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            ArrayList<Councillor> councillors = new ArrayList<>();
                            for(QueryDocumentSnapshot councillorSnapShot:task.getResult())
                            {
                                Councillor councillor = councillorSnapShot.toObject(Councillor.class);
                                councillor.setPassword("");
                                councillor.setCouncillorId(councillorSnapShot.getId());

                                councillors.add(councillor);
                            }

                            Intent intent = new Intent(Common.KEY_COUNCILLOR_LOAD_DONE);
                            intent.putParcelableArrayListExtra(Common.KEY_COUNCILLOR_LOAD_DONE, councillors);
                            localBroadcastManager.sendBroadcast(intent);

                            dialog.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                        }
                    });
        }

    }

    //Broadcast Receiver
    private BroadcastReceiver buttonNextReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            int step = intent.getIntExtra(Common.KEY_STEP, 0);
            if(step == 1)
                Common.currentDepartment = intent.getParcelableExtra(Common.KEY_DEPARTMENT_SERVICE);
            else if(step == 2)
                Common.currentCouncillor = intent.getParcelableExtra(Common.KEY_COUNCILLOR_SELECTED);
            else if(step == 3)
                Common.currentTimeSlot = intent.getIntExtra(Common.KEY_TIME_SLOT, -1);

            btn_next_step.setEnabled(true);
            setColorButton();
        }
    };

    @Override
    protected void onDestroy() {
        localBroadcastManager.unregisterReceiver(buttonNextReceiver);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        ButterKnife.bind(BookingActivity.this);

        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();

        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(buttonNextReceiver, new IntentFilter(Common.KEY_ENABLE_BUTTON_NEXT));

        setupStepView();
        setColorButton();

        viewPager.setAdapter(new MyViewPageAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(4); //4 fragments
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {

                stepView.go(i, true);
                if(i == 0)
                    btn_previous_step.setEnabled(false);
                else
                    btn_previous_step.setEnabled(true);

                btn_next_step.setEnabled(false);
                setColorButton();
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    private void setColorButton() {
        if(btn_next_step.isEnabled())
        {
            btn_next_step.setBackgroundResource(R.color.colorButton);
        }
        else
        {
            btn_next_step.setBackgroundResource(android.R.color.darker_gray);
        }
        if(btn_previous_step.isEnabled())
        {
            btn_previous_step.setBackgroundResource(R.color.colorButton);
        }
        else
        {
            btn_previous_step.setBackgroundResource(android.R.color.darker_gray);
        }
    }

    private void setupStepView() {
        List<String> stepList = new ArrayList<>();
        stepList.add("Department");
        stepList.add("Councillor");
        stepList.add("Time");
        stepList.add("Confirm");
        stepView.setSteps(stepList);
    }
}
