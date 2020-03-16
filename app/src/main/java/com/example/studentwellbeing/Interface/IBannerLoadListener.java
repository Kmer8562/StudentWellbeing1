package com.example.studentwellbeing.Interface;

import com.example.studentwellbeing.Model.Banner;

import java.util.List;

public interface IBannerLoadListener {

    void onBannerLoadSuccess(List<Banner> banners);
    void onBannerLoadFailed(String message);
}
