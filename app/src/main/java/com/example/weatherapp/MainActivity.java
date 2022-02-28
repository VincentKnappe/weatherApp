package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    EditText et_city;
    TextView tv_result, tv_cityName;

    public void getWeather(View view){

        try {
            String cityName = et_city.getText().toString();
            DownloadContent content = new DownloadContent();
            content.execute("http://api.weatherstack.com/current?access_key="+ BuildConfig.API_KEY +"&query=" + cityName);
            tv_result.setVisibility(View.VISIBLE);
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(et_city.getWindowToken(), 0);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_city = findViewById(R.id.et_cityName);
        tv_result = findViewById(R.id.tv_result);
        tv_cityName = findViewById(R.id.tv_cityName);
        tv_cityName.setVisibility(View.INVISIBLE);
    }

    public class DownloadContent extends AsyncTask<String,Void, String>{

        @Override
        protected String doInBackground(String... urls) {
           String result = "";
           java.net.URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while (data != -1){
                    char current = (char) data;
                    result += current;
                    data= reader.read();
                }
                return result;
            }catch(Exception e){
                e.printStackTrace();

                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s);
                String weatherInfo = jsonObject.getString("current");
                String locationInfo = jsonObject.getString("location");

                JSONObject currentJSONObject = new JSONObject(weatherInfo);
                JSONObject locationJSONObject = new JSONObject(locationInfo);

                String temperature = currentJSONObject.getString("temperature") + "°C";
                String location = locationJSONObject.getString("name");

                if (!temperature.equals("") && !location.equals("")){
                    tv_cityName.setVisibility(View.VISIBLE);
                    tv_cityName.setText(location);
                    tv_result.setText(temperature);
                }else{
                    Toast.makeText(MainActivity.this, "Temperature is not available.", Toast.LENGTH_SHORT).show();
                }


            }catch (Exception e){
                e.printStackTrace();
                tv_result.setVisibility(View.INVISIBLE);
            }
        }
    }
}