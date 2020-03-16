package com.example.studentwellbeing.Interface;

import com.example.studentwellbeing.Model.Banner;

import java.util.List;

public interface IWelcomeStudentsLoadListener {
    void onWelcomeStudentsLoadSuccess(List<Banner> banners);
    void onWelcomeStudentsLoadFailed(String message);
}
