package com.progressifff.weather.models;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.progressifff.weather.Utils.KELVIN_TEMPERATURE;

public class Weather implements Parcelable{
    public static final String TAG = "Weather";

    public static final Creator<Weather> CREATOR = new Creator<Weather>() {
        @Override
        public Weather createFromParcel(Parcel in) {
            return new Weather(in);
        }

        @Override
        public Weather[] newArray(int size) {
            return new Weather[size];
        }
    };

    public class MainData {
        Integer pressure;
        Double temp;
        Double temp_min;
        Double temp_max;
    }

    public class Description {
        String main;
        String description;
        String icon;
    }

    public class WindData{
        Double deg;
        Double speed;
    }

    @SerializedName("id")
    private Long mCityId;

    @SerializedName("main")
    private MainData mMainData;

    @SerializedName("weather")
    private List<Description> mDescription;

    @SerializedName("name")
    private String mCity;

    @SerializedName("dt")
    private long mTimestamp; //always same value)

    @SerializedName("wind")
    private WindData mWindData;

    private Bitmap mWeatherImage;

    public Bitmap getWeatherImage() {
        return mWeatherImage;
    }

    public void setWeatherImage(Bitmap weatherImage) {
        mWeatherImage = weatherImage;
    }

    public void setTimestamp(long timestamp) {
        mTimestamp = timestamp;
    }

    public Long getCityId() {
        return mCityId;
    }

    public MainData getMainData() {
        return mMainData;
    }

    public List<Description> getDescriptions() {
        return mDescription;
    }

    public long getTimestamp() {
        return mTimestamp;
    }

    public WindData getWindData() {
        return mWindData;
    }

    protected Weather(Parcel in) {
        Weather parsed = fromJson(in.readString());
        mCityId = parsed.getCityId();
        mMainData = parsed.getMainData();
        mDescription = parsed.getDescriptions();
        mCity = parsed.getCity();
        mTimestamp = parsed.getTimestamp();
        mWindData = parsed.getWindData();
        mWeatherImage = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public static Weather fromJson(String json){
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(json, Weather.class);
    }

    public String toJson(){
        Gson gson = new GsonBuilder().create();
        return gson.toJson(this);
    }

    public String getDate() {
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, Locale.getDefault());
        return df.format(new Date(mTimestamp));
    }

    public String getTemperature() { return String.valueOf(mMainData.temp - KELVIN_TEMPERATURE) + " ℃"; }

    public String getTemperatureMin() { return String.valueOf(mMainData.temp_min - KELVIN_TEMPERATURE) + " ℃"; }

    public String getTemperatureMax() { return String.valueOf(mMainData.temp_max - KELVIN_TEMPERATURE) + " ℃"; }

    public String getWindSpeed() { return String.valueOf(mWindData.speed) + " m/s"; }

    public String getPressure() { return String.valueOf(mMainData.pressure) + " hPa"; }

    public String getCity() { return mCity; }

    public String getIcon() { return mDescription.get(0).icon + ".png"; }

    public String getDescription() { return mDescription.get(0).main; }

    public String getIconUrl() {
        return "http://openweathermap.org/img/w/" + mDescription.get(0).icon + ".png";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(toJson());
        parcel.writeParcelable(mWeatherImage, 0);
    }

    @Override
    public String toString() {
        return "Weather{" +
                "pressure=" + mMainData.pressure + " temp=" + mMainData.temp + " " + mMainData.temp_min + " " + mMainData.temp_max +
                ", description=" + mDescription.get(0).main +
                ", city='" + mCity + '\'' +
                ", timestamp=" + mTimestamp +
                ", speed=" + mWindData.speed +
                '}';
    }
}
