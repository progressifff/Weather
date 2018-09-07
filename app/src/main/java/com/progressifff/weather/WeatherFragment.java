package com.progressifff.weather;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.progressifff.weather.dialogs.GPSTurnOnDialog;
import com.progressifff.weather.models.Weather;
import com.progressifff.weather.presenters.WeatherPresenter;
import com.progressifff.weather.views.WeatherView;

public class WeatherFragment extends Fragment implements WeatherView{

    private View mLocationInfoContainer;
    private TextView mDetermineLocationMsg;
    private TextView mCityText;
    private TextView mUpdateDateText;
    private TextView mWeatherDescription;
    private WeatherPresenter mWeatherPresenter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mTemperatureText;
    private ImageView mWeatherImage;
    private TextView mMaxTemperatureText;
    private TextView mMinTemperatureText;
    private TextView mPressure;
    private TextView mWindSpeed;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.weather_fragment_layout, null);

        mLocationInfoContainer = view.findViewById(R.id.locationInfoContainer);
        mDetermineLocationMsg = view.findViewById(R.id.determineLocationMsg);
        mCityText = view.findViewById(R.id.cityText);
        mUpdateDateText = view.findViewById(R.id.updateDateText);

        Activity activity = getActivity();
        assert activity != null;
        mWeatherDescription = activity.findViewById(R.id.weatherDescription);
        mTemperatureText = activity.findViewById(R.id.temperature);
        mWeatherImage = activity.findViewById(R.id.weatherImage);
        mMaxTemperatureText = activity.findViewById(R.id.maxTempText);
        mMinTemperatureText = activity.findViewById(R.id.minTempText);
        mPressure = activity.findViewById(R.id.pressureText);
        mWindSpeed = activity.findViewById(R.id.windSpeedText);
        mSwipeRefreshLayout = view.findViewById(R.id.weatherRefresher);

        if(savedInstanceState == null){
            mWeatherPresenter = new WeatherPresenter();
        }
        else{
            mWeatherPresenter = (WeatherPresenter) PresentersManager.getInstance().restorePresenter(savedInstanceState);
            if(mWeatherPresenter == null){
                mWeatherPresenter = new WeatherPresenter(savedInstanceState);
            }
        }
        mSwipeRefreshLayout.setOnRefreshListener(mWeatherPresenter);
        mWeatherPresenter.bindView(this);
        view.findViewById(R.id.locationBtn).setOnClickListener(mWeatherPresenter);
        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        PresentersManager.getInstance().savePresenter(mWeatherPresenter, outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWeatherPresenter.unbindView();
    }

    @Override
    public void showLocationInfo() {
        mLocationInfoContainer.setVisibility(View.VISIBLE);
        mDetermineLocationMsg.setVisibility(View.GONE);
    }

    @Override
    public void showGPSTurnOnDialog() {
        assert getFragmentManager() != null;
        new GPSTurnOnDialog().show(getFragmentManager(), GPSTurnOnDialog.TAG);
    }

    @Override
    public void update(Weather weather) {
        mCityText.setText(weather.getCity());
        String updated = getString(R.string.updated) + weather.getDate();
        mUpdateDateText.setText(updated);
        mWeatherDescription.setText(weather.getDescription());
        mTemperatureText.setText(weather.getTemperature());
        mWeatherImage.setImageBitmap(weather.getWeatherImage());
        mMaxTemperatureText.setText(weather.getTemperatureMax());
        mMinTemperatureText.setText(weather.getTemperatureMin());
        mPressure.setText(weather.getPressure());
        mWindSpeed.setText(weather.getWindSpeed());

        showLocationInfo();
    }

    @Override
    public void dismissRefreshBar() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showRefreshBar() {
        mSwipeRefreshLayout.setRefreshing(true);
    }
}
