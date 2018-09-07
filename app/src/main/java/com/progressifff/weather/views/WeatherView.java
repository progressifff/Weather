package com.progressifff.weather.views;

import com.progressifff.weather.models.Weather;

public interface WeatherView {
    void showLocationInfo();
    void showGPSTurnOnDialog();
    void update(Weather weather);
    void dismissRefreshBar();
    void showRefreshBar();
}
