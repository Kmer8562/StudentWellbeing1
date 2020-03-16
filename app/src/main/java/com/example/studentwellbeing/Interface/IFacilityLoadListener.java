package com.example.studentwellbeing.Interface;

import com.example.studentwellbeing.Model.Department;

import java.util.List;

public interface IFacilityLoadListener {
    void onFacilityLoadSuccess(List<Department> departmentList);
    void onFacilityLoadFailed(String message);
}
