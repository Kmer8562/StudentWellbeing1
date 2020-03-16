package com.example.studentwellbeing.Interface;

import java.util.List;

public interface IAllDepartmentLoadListener {

    void onAllDepartmentSuccess(List<String> areaNameList);
    void onAllDepartmentFailed(String message);
}
