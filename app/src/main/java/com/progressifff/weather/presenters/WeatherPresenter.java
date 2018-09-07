package com.progressifff.weather.presenters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.progressifff.weather.App;
import com.progressifff.weather.R;
import com.progressifff.weather.Utils;
import com.progressifff.weather.WeatherDbHelper;
import com.progressifff.weather.models.Weather;
import com.progressifff.weather.views.WeatherView;

import static com.progressifff.weather.Utils.BY_CITY_ID_URL;
import static com.progressifff.weather.Utils.BY_GEOLOCATION_URL;
import static com.progressifff.weather.Utils.getWeatherImage;

public class WeatherPresenter extends BasePresenter<Weather, WeatherView> implements
        View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener {

    private boolean mIsLoadingData = false;
    private final LocationManager mLocationManager = (LocationManager) App.getInstance().getSystemService(Context.LOCATION_SERVICE);
    private final FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(App.getInstance());

    private LocationRequest mLocationRequest;

    private final LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult != null) {
                mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                Location location = locationResult.getLastLocation();
                mIsLoadingData = true;
                new LoadDataTask().execute(String.format(BY_GEOLOCATION_URL, location.getLatitude(), location.getLongitude()));
            }
        }
    };

    public WeatherPresenter() {
        init();
    }

    public WeatherPresenter(Bundle savedState) {
        mModel = savedState.getParcelable(Weather.TAG);
        if (mModel == null) {
            init();
        }
    }

    private void init() {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        Long currentCityId = Utils.getCurrentCityId();
        if (!currentCityId.equals(Utils.INVALID_CITY_ID_VALUE)) {

            if (Utils.isInternetConnected()) {
                loadData();
            } else {
                String weatherData = WeatherDbHelper.getInstance().getWeatherData(currentCityId);
                if (weatherData != null) {
                    try {
                        mModel = Weather.fromJson(weatherData);
                        mModel.setWeatherImage(Utils.readImage(mModel.getIcon()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void bindView(@NonNull WeatherView v) {
        super.bindView(v);
        if (mModel != null) {
            updateView();
        }
    }

    @Override
    protected void updateView() {
        getView().update(mModel);
    }

    @Override
    public void saveState(Bundle outState) {
        outState.putParcelable(Weather.TAG, mModel);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.locationBtn:
                if (Utils.isGPSTurnedOff(mLocationManager)) {
                    getView().showGPSTurnOnDialog();
                } else if (Utils.isInternetDisconnected()) {
                    Utils.showToast(App.getInstance().getString(R.string.no_internet_connection));
                } else {
                    getView().showRefreshBar();
                    loadData();
                }
                break;
        }
    }

    @Override
    public void onRefresh() {
        if (Utils.isInternetDisconnected()) {
            Utils.showToast(App.getInstance().getString(R.string.no_internet_connection));
            getView().dismissRefreshBar();
        } else {
            loadData();
        }
    }

    @SuppressLint("MissingPermission")
    private void loadData() {
        if (!mIsLoadingData) {
            Long currentCityId = Utils.getCurrentCityId();
            if (!currentCityId.equals(Utils.INVALID_CITY_ID_VALUE)) {
                mIsLoadingData = true;
                new LoadDataTask().execute(String.format(BY_CITY_ID_URL, currentCityId));
            } else {
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class LoadDataTask extends AsyncTask<String, Void, Weather> {
        @Override
        protected Weather doInBackground(String... params) {
            try {
                Weather weather = Utils.getWeather(params[0]);
                if(weather != null){
                    weather.setTimestamp(System.currentTimeMillis());
                    Bitmap image = getWeatherImage(weather.getIconUrl());
                    Utils.saveImage(image, weather.getIcon());
                    weather.setWeatherImage(image);
                }
                return weather;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Weather weather) {
            super.onPostExecute(weather);
            mIsLoadingData = false;
            if (weather != null) {
                mModel = weather;
                Utils.saveCurrentCityId(mModel.getCityId());
                WeatherDbHelper.getInstance().saveWeather(mModel);
                if (isViewAttached()) {
                    getView().dismissRefreshBar();
                    updateView();
                }
            }
        }
    }
}