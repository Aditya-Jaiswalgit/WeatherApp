package com.weatherapp.android.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.weatherapp.android.Adapter.HourlyAdapters;
import com.weatherapp.android.Domains.Hourly;
import com.weatherapp.android.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private String API_URL;
    private TextView tvdatetime, tv_currenttemp, tv_currentstate, tv_windkph, tv_humiditypercent, tv_aqi;
    private RequestQueue queue;
    private RecyclerView recyclerView;
    private HourlyAdapters adapterHourly;
    private ArrayList<Hourly> hourlyItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.background_color)); // Dark gray background
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(
                    getWindow().getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            ); // White icons/text
        }

        // Initialize Views
        tvdatetime = findViewById(R.id.tv_date_time);
        tv_currenttemp = findViewById(R.id.tv_currentTempCelsius);
        tv_currentstate = findViewById(R.id.tv_currentstate);
        tv_windkph = findViewById(R.id.tv_windkph);
        tv_humiditypercent = findViewById(R.id.tv_humiditypercent);
        tv_aqi = findViewById(R.id.tv_aqi);
        recyclerView = findViewById(R.id.recyclerView);

        // Initialize Location Services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        queue = Volley.newRequestQueue(this);

        // Initialize RecyclerView data
        hourlyItems = new ArrayList<>();
        initRecyclerView();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            showPermissionDialog();
        } else {
            getLocation();
        }

        // Set Date and Time
        updateDateTime();
    }

    private void updateDateTime() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            String formattedDatetime = now.format(formatter);
            tvdatetime.setText(formattedDatetime);
        }
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        getCityName(latitude, longitude);
                    } else {
                        Toast.makeText(MainActivity.this, "Could not get location!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void getCityName(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                String cityName = addresses.get(0).getLocality();
                Log.d("City", "User's city: " + cityName);
                fetchWeatherData(cityName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void fetchWeatherData(String city) {
        API_URL = "https://api.weatherapi.com/v1/forecast.json?key=" + getString(R.string.weather_api_key) + "&q=" + city + "&aqi=yes&days=1";
        Log.d("API URL", "Fetching weather for: " + city + ", URL: " + API_URL);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, API_URL,
                new Response.Listener<String>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject current = jsonObject.getJSONObject("current");
                            double temperature = current.getDouble("temp_c");
                            int humidity = current.getInt("humidity");
                            double windSpeed = current.getDouble("wind_kph");
                            String condition = current.getJSONObject("condition").getString("text");

                            // Update current weather UI
                            tv_currenttemp.setText(" " + temperature + "°C");
                            tv_humiditypercent.setText(humidity + "%");
                            tv_windkph.setText(windSpeed + " kph");
                            tv_currentstate.setText(condition);

                            JSONObject airQuality = current.getJSONObject("air_quality");
                            int usEpaIndex = airQuality.getInt("us-epa-index");

                            String aqiDescription;
                            switch (usEpaIndex) {
                                case 1:
                                    aqiDescription = "Good";
                                    break;
                                case 2:
                                    aqiDescription = "Moderate";
                                    break;
                                case 3:
                                    aqiDescription = "Unhealthy for Sensitive Groups";
                                    break;
                                case 4:
                                    aqiDescription = "Unhealthy";
                                    break;
                                case 5:
                                    aqiDescription = "Very Unhealthy";
                                    break;
                                case 6:
                                    aqiDescription = "Hazardous";
                                    break;
                                default:
                                    aqiDescription = "Unknown";
                            }


                            tv_aqi.setText(aqiDescription);

                            // Parse hourly forecast data
                            JSONObject forecast = jsonObject.getJSONObject("forecast");
                            JSONArray forecastDayArray = forecast.getJSONArray("forecastday");
                            JSONObject firstDay = forecastDayArray.getJSONObject(0);
                            JSONArray hourArray = firstDay.getJSONArray("hour");

                            // Define desired hours
                            String[] desiredHours = {"20:00", "21:00", "22:00", "23:00", "00:00"};
                            hourlyItems.clear();

                            // Extract data for desired hours
                            for (int i = 0; i < hourArray.length(); i++) {
                                JSONObject hour = hourArray.getJSONObject(i);
                                String time = hour.getString("time"); // e.g., "2025-03-26 20:00"
                                double tempC = hour.getDouble("temp_c");
                                JSONObject conditionObj = hour.getJSONObject("condition");
                                String iconUrl = conditionObj.getString("icon");

                                for (String desiredHour : desiredHours) {
                                    if (time.endsWith(desiredHour)) {
                                        String hourDisplay = convertTo12HourFormat(desiredHour);
                                        hourlyItems.add(new Hourly(hourDisplay, (int) tempC, iconUrl));
                                    }
                                }
                            }

                            // Update RecyclerView
                            adapterHourly.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Error parsing JSON", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Error fetching data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(stringRequest);
    }

    private String convertTo12HourFormat(String time24) {
        int hour = Integer.parseInt(time24.split(":")[0]);
        String period = (hour >= 12) ? "pm" : "am";
        hour = (hour == 0) ? 12 : (hour > 12) ? hour - 12 : hour;
        return hour + " " + period;
    }

    private void showPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Location Permission Required!");
        builder.setMessage("This app needs location to fetch your city's weather.");
        builder.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("Deny", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                Toast.makeText(this, "Permission Denied! Cannot fetch location.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapterHourly = new HourlyAdapters(hourlyItems);
        recyclerView.setAdapter(adapterHourly);
    }
}