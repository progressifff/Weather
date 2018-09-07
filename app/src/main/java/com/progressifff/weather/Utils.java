package com.progressifff.weather;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.progressifff.weather.models.Weather;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Utils {
    public static final double KELVIN_TEMPERATURE = 273.15;
    public static final String SHARED_PREFERENCES_NAME = "WeatherSharedPreferences";
    public static final String CURRENT_CITY_ID_TAG = "CurrentCityId";
    public static final Long INVALID_CITY_ID_VALUE = -1L;
    public static String APPID = "APPID=377a8f5fd0942feec314b4a1882dbf25";
    public static String BY_CITY_ID_URL = "http://api.openweathermap.org/data/2.5/weather?id=%d&" + APPID;
    public static String BY_GEOLOCATION_URL = "http://api.openweathermap.org/data/2.5/weather?lat=%f&lon=%f&" + APPID;
    public static String IMG_URL = "http://openweathermap.org/img/w/";

    public static boolean isInternetConnected(){
        final ConnectivityManager cm = (ConnectivityManager)App.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isConnected = false;
        if(cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null) {
                isConnected = activeNetwork.isConnected();
            }
        }
        return isConnected;
    }

    public static boolean isInternetDisconnected(){
        return !isInternetConnected();
    }

    public static boolean isGPSTurnedOn(LocationManager locationManager) {
        return locationManager != null && (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
    }

    public static boolean isGPSTurnedOff(LocationManager locationManager) {
        return !isGPSTurnedOn(locationManager);
    }

    public static void showToast(String message){
        Toast.makeText(App.getInstance(), message, Toast.LENGTH_SHORT).show();
    }

    public static long getCurrentCityId(){
        SharedPreferences sharedPreferences = App.getInstance().getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(CURRENT_CITY_ID_TAG, INVALID_CITY_ID_VALUE);
    }

    public static void saveCurrentCityId(long cityId){
        SharedPreferences sharedPreferences = App.getInstance().getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(CURRENT_CITY_ID_TAG, cityId);
        editor.apply();
    }

    public static Weather getWeather(String url) {
        HttpURLConnection connection = null;
        InputStream inputStream = null;

        try {
            connection = (HttpURLConnection)(new URL(url)).openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.connect();

            StringBuilder buffer = new StringBuilder();
            inputStream = connection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = br.readLine()) != null) {
                buffer.append(line).append("\r\n");
            }
            return Weather.fromJson(buffer.toString());
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }

        return null;

    }

    public static Bitmap getWeatherImage(String url) {
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        try {
            connection = (HttpURLConnection)(new URL(url)).openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setDoOutput(false);
            connection.connect();

            inputStream = connection.getInputStream();
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();

            while ( inputStream.read(buffer) != -1) {
                byteArrayStream.write(buffer);
            }

            byte[] data = byteArrayStream.toByteArray();

            return BitmapFactory.decodeByteArray(data, 0, data.length);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }

    public static boolean saveImage(Bitmap image, String name){
        try(FileOutputStream os = App.getInstance().openFileOutput(name, Context.MODE_PRIVATE)) {
            image.compress(Bitmap.CompressFormat.PNG, 100, os);
            return true;
        } catch (Exception e) {
            Log.e("saveImage", e.getMessage());
        }
        return false;
    }

    public static Bitmap readImage(String name){
        File file = App.getInstance().getFileStreamPath(name);
        try(FileInputStream is = new FileInputStream(file)) {
            return BitmapFactory.decodeStream(is);
        } catch (Exception e) {
            Log.e("readImage", e.getMessage());
        }
        return null;
    }

    public static boolean hasBottomSheetBehavior(View view) {
        if (view.getLayoutParams() instanceof CoordinatorLayout.LayoutParams) {
            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams)view.getLayoutParams();
            return lp.getBehavior() instanceof BottomSheetBehavior<?>;
        } else {
            return false;
        }
    }

    public static BottomSheetBehavior<?> retrieveBottomSheetBehavior(View view) {
        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams)view.getLayoutParams();
        return (BottomSheetBehavior<?>)lp.getBehavior();
    }
}
