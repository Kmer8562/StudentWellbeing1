package com.example.studentwellbeing.Common;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.text.TextUtils;

import com.example.studentwellbeing.Model.Councillor;
import com.example.studentwellbeing.Model.Department;
import com.example.studentwellbeing.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import io.paperdb.Paper;

public class Common {
    public static final String KEY_ENABLE_BUTTON_NEXT = "ENABLE_BUTTON_NEXT";
    public static final String KEY_DEPARTMENT_SERVICE = "DEPARTMENT_SAVE";
    public static final String KEY_COUNCILLOR_LOAD_DONE = "COUNCILLOR_LOAD_DONE";
    public static final String KEY_DISPLAY_TIME_SLOT = "DISPLAY_TIME_SLOT";
    public static final String KEY_STEP = "STEP" ;
    public static final String KEY_COUNCILLOR_SELECTED = "COUNCILLOR_SELECTED";
    public static final int TIME_SLOT_TOTAL = 15;
    public static String IS_LOGIN = "IsLogin";
    public static User currentUser;
    public static Department currentDepartment;
    public static int step = 0;
    public static String campus ="";
    public static Councillor currentCouncillor;

    public static void updateToken(Context context,final String s) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user !=null)
        {
            MyToken myToken = new MyToken();
            myToken.setToken(s);
            myToken.setTokenType(TOKEN_TYPE.CLIENT);
            myToken.setUserPhone(user.getPhoneNumer());

            FirebaseFirestore.getInstance()
                    .collection("Tokens")
                    .document(user.getPhoneNumber())
                    .set(myToken)
                    .addOnCompleteListener(task -> {

                    });

        }
        else
        {
            Paper.init(context);
            String localUser = Paper.book().read(Common.LOGGED_KEY);
            if(localUser != null)
            {
                if(!TextUtils.isEmpty(localUser))
                {
                    MyToken myToken = new MyToken();
                    myToken.setToken(s);
                    myToken.setTokenType(TOKEN_TYPE.CLIENT);
                    myToken.setUserPhone(localUser);

                    FirebaseFirestore.getInstance()
                            .collection("Tokens")
                            .document(localUser)
                            .set(myToken)
                            .addOnCompleteListener(task -> {

                            });
                }
            }
        }
    }

    public static String convertTimeSlotToString(int slot) {
                switch (slot)
                {
                    case 0:
                        return "9:00-9:30";
                    case 1:
                        return "9:30-10:00";
                    case 2:
                        return "10:00-10:30";
                    case 3:
                        return "10:30-11:00";
                    case 4:
                        return "11:00-11:30";
                    case 5:
                        return "11:30-12:00";
                    case 6:
                        return "12:00-12:30";
                    case 7:
                        return "12:30-13:00";
                    case 8:
                        return "13:00-13:30";
                    case 9:
                        return "13:30-14:00";
                    case 10:
                        return "14:00-14:30";
                    case 11:
                        return "14:30-15:00";
                    case 12:
                        return "15:00-15:30";
                    case 13:
                        return "15:30-16:00";
                    case 14:
                        return "16:00-16:30";
                    case 15:
                        return "16:30-17:00";
                    default:
                        return "Closed";
                }
    }
}
