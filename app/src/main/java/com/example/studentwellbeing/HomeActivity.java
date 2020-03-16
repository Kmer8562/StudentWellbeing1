package com.example.studentwellbeing;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.view.View;
import android.accounts.Account;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.LayoutInflater.Filter;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.example.studentwellbeing.Common.Common;
import com.example.studentwellbeing.Fragments.BookingFragment;
import com.example.studentwellbeing.Fragments.HomeFragment;
import com.example.studentwellbeing.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;

import static com.google.firebase.firestore.core.View.*;

public class HomeActivity extends AppCompatActivity {

   @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;

   BottomSheetDialog bottomSheetDialog;

   CollectionReference userRef;

   AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(HomeActivity.this);

        userRef = FirebaseFirestore.getInstance().collection("User");
        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();

        if(getIntent() != null) {
            boolean isLogin = getIntent().getBooleanExtra(Common.IS_LOGIN, false);
            if (isLogin)
            {
                dialog.show();

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                DocumentReference currentUser = userRef.document(user.getPhoneNumber());
                currentUser.get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful())
                            {
                                DocumentSnapshot userSnapShot = task.getResult();
                                if (!userSnapShot.exists())
                                {
                                    showUpdateDialog(user.getPhoneNumber());
                                }
                                else
                                    {
                                        //If the user is already in the system
                                        Common.currentUser = userSnapShot.toObject(User.class);
                                        bottomNavigationView.setSelectedItemId(R.id.action_home);

                                    }
                                if(dialog.isShowing())
                                    dialog.dismiss();
                            }
                        });
            }
        }


        bottomNavigationView.setOnNavigationItemSelectedListener(new OnNavigationItemSelectedListener() {
            Fragment fragment = null;

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_home)
                    fragment = new HomeFragment();
                else if (menuItem.getItemId() == R.id.action_booking)
                    fragment = new BookingFragment();

                return loadFragment(fragment);

            }
        });

    }

    private boolean loadFragment(Fragment fragment) {
        if(fragment != null)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment)
                    .commit();
            return true;
        }
        return false;
    }



    private void showUpdateDialog(String phoneNumber) {



        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setTitle("One More Step");
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        bottomSheetDialog.setCancelable(false);
        View sheetView = getLayoutInflater().inflate(R.layout.layout_update_information, null);

        Button btn_update = (Button)sheetView.findViewById(R.id.btn_update);
        TextInputEditText edit_name = (TextInputEditText)sheetView.findViewById(R.id.edit_name);
        TextInputEditText edit_address = (TextInputEditText)sheetView.findViewById(R.id.edit_address);

        btn_update.setOnClickListener((view) -> {

            if(!dialog.isShowing())
                dialog.show();

            User user = new User(edit_name.getText().toString(),
                    edit_address.getText().toString(),
                    phoneNumber);
            userRef.document(phoneNumber)
                    .set(user)
                    .addOnSuccessListener(aVoid -> {
                        bottomSheetDialog.dismiss();
                        if(dialog.isShowing())
                            dialog.dismiss();

                        Common.currentUser = user;
                        bottomNavigationView.setSelectedItemId(R.id.action_home);


                        Toast.makeText(HomeActivity.this, "Thank you", Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener((e) -> {
                        bottomSheetDialog.dismiss();
                        if(dialog.isShowing())
                             dialog.dismiss();
                        Toast.makeText(HomeActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        });

        bottomSheetDialog.setContentView(sheetView);
        bottomSheetDialog.show();



    }


}
