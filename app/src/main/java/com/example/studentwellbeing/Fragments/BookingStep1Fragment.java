package com.example.studentwellbeing.Fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentwellbeing.Adapter.MyDepartmentAdapter;
import com.example.studentwellbeing.Common.Common;
import com.example.studentwellbeing.Common.SpacesItemDecoration;
import com.example.studentwellbeing.Interface.IAllDepartmentLoadListener;
import com.example.studentwellbeing.Interface.IFacilityLoadListener;
import com.example.studentwellbeing.Model.Department;
import com.example.studentwellbeing.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.List;


import javax.annotation.Nullable;
import javax.annotation.Nonnull;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;

public class BookingStep1Fragment extends Fragment implements IAllDepartmentLoadListener, IFacilityLoadListener {

    CollectionReference allDepartmentRef;
    CollectionReference facilityRef;

    IAllDepartmentLoadListener iAllDepartmentLoadListener;
    IFacilityLoadListener iFacilityLoadListener;

    @BindView(R.id.spinner)
    MaterialSpinner spinner;
    @BindView(R.id.recycler_department)
    RecyclerView recycler_department;

    Unbinder unbinder;

    AlertDialog dialog;

    static BookingStep1Fragment instance;

    public static BookingStep1Fragment getInstance() {
        if(instance == null)
            instance = new BookingStep1Fragment();
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        allDepartmentRef = FirebaseFirestore.getInstance().collection("AllDepartments");
        iAllDepartmentLoadListener = this;
        iFacilityLoadListener = this;

        dialog = new SpotsDialog.Builder().setContext(getActivity()).setCancelable(false).build();
    }

    @Nullable
    @Override
    public View onCreateView(@Nonnull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View itemView = inflater.inflate(R.layout.fragment_booking_step_one,container, false);
        unbinder = ButterKnife.bind(this,itemView);

        initView();
        loadAllDepartment();


        return itemView;
    }

    private void initView() {
        recycler_department.setHasFixedSize(true);
        recycler_department.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recycler_department.addItemDecoration(new SpacesItemDecoration(4));
    }

    private void loadAllDepartment() {
        allDepartmentRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@Nonnull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            List<String> list = new ArrayList<>();
                            list.add("Please choose department");
                            for(QueryDocumentSnapshot documentSnapshot:task.getResult())
                                list.add(documentSnapshot.getId());
                            iAllDepartmentLoadListener.onAllDepartmentSuccess(list);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception e) {
                iAllDepartmentLoadListener.onAllDepartmentFailed(e.getMessage());
            }
        });
    }

    @Override
    public void onAllDepartmentSuccess(List<String> areaNameList) {
        spinner.setItems(areaNameList);
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                if(position > 0)
                {
                    loadFacilityOfCampus(item.toString());
                }
                else
                    recycler_department.setVisibility(View.GONE);
            }
        });
    }

    private void loadFacilityOfCampus(String campusName) {
        dialog.show();

        Common.campus = campusName;

        facilityRef = FirebaseFirestore.getInstance()
                .collection("AllDepartment")
                .document(campusName)
                .collection("Facility");

        facilityRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<Department> list = new ArrayList<>();
                if(task.isSuccessful())
                {
                    for(QueryDocumentSnapshot documentSnapshot:task.getResult())
                    {
                        Department department = documentSnapshot.toObject(Department.class);
                        department.setDepartmentId(documentSnapshot.getId());
                        list.add(department);
                    }
                    iFacilityLoadListener.onFacilityLoadSuccess(list);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    @Override
    public void onAllDepartmentFailed(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFacilityLoadSuccess(List<Department> departmentList) {
        MyDepartmentAdapter adapter = new MyDepartmentAdapter(getActivity(),departmentList);
        recycler_department.setAdapter(adapter);
        recycler_department.setVisibility(View.VISIBLE);

        dialog.dismiss();
    }

    @Override
    public void onFacilityLoadFailed(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        dialog.dismiss();
    }
}
