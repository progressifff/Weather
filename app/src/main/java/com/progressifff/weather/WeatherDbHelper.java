package com.progressifff.weather;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.progressifff.weather.models.Weather;

import static android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE;

public class WeatherDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Weather.db";
    public static final String TABLE_NAME = "weather";
    public static final String COLUMN_ID = "city_id";
    public static final String COLUMN_DATA = "data";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY,"
                    + COLUMN_DATA + " TEXT"
                    + ")";

    private static WeatherDbHelper sInstance;

    public static synchronized WeatherDbHelper getInstance() {
        if (sInstance == null) {
            sInstance = new WeatherDbHelper(App.getInstance());
        }
        return sInstance;
    }

    private WeatherDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public void saveWeather(Weather weather){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, weather.getCityId());
        values.put(COLUMN_DATA, weather.toJson());
        db.insertWithOnConflict(
                TABLE_NAME,
                null,
                values,
                CONFLICT_REPLACE
                );
        db.close();
    }

    public String getWeatherData(long cityId){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME,
                new String[]{COLUMN_ID, COLUMN_DATA},
                COLUMN_ID + "=?",
                new String[]{String.valueOf(cityId)}, null, null, null, null);

        String data = null;
        if (cursor != null) {
            cursor.moveToFirst();
            data = cursor.getString(cursor.getColumnIndex(COLUMN_DATA));
            cursor.close();

        }
        return data;
    }
}
