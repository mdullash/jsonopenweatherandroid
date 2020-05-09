package com.example.jsonweatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    //public static String API_KEY = "0b3f6e5fc365f8477251df1d1e907991";
    private TextView textView;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textviewId);
        editText =findViewById(R.id.cityEdittextId);
    }

    public void getWeather(View view) {
        String cityName = editText.getText().toString();

        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute("https://api.openweathermap.org/data/2.5/weather?q=" +cityName+"&appid=0b3f6e5fc365f8477251df1d1e907991");
    }

    public class DownloadTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... urls) {
            String result="";
            URL url;
            HttpURLConnection urlConnection=null;
            try {
                url = new URL(urls[0]);
                Log.e("url", String.valueOf(url));
                try {
                    urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream inputStream = urlConnection.getInputStream();
                    InputStreamReader reader = new InputStreamReader(inputStream);
                    int data = reader.read();
                    while (data !=-1){
                        char current = (char) data;
                        result += current;
                        data =reader.read();
                    }
                    return result;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s);
                String weather = jsonObject.getString("weather");
                String sys = jsonObject.getString("sys");
                String main = jsonObject.getString("main");
                //Log.e("weatherInfo",weatherInfo);
                JSONArray jsonArray = new JSONArray(weather);

                for(int i=0;i<jsonArray.length();i++) {
                    JSONObject jsonPart = jsonArray.getJSONObject(i);
                    textView.setText(jsonPart.getString("main")+"\n");
                }
                JSONObject jsononj= new JSONObject(main);
                String temp = jsononj.optString("temp");
                float celsius = Float.parseFloat(temp) - 273.15F;
                int c =  (int)Math.round(celsius);
                textView.append(c+"°C"+"\n");

                JSONObject jsononj2= new JSONObject(sys);
                String country = jsononj2.optString("country");
                textView.append(country+"\n");

                String sunrise = jsononj2.optString("sunrise");
                Date date = new Date(Long.parseLong(sunrise) * 1000);
                SimpleDateFormat sdf = new SimpleDateFormat("EEEE,MMMM d,yyyy h:mm,a");
                String sunris = sdf.format(date);
                textView.append(sunris+"\n");

                String sunset = jsononj2.optString("sunset");
                Date date2 = new Date(Long.parseLong(sunset) * 1000);
                String sunst = sdf.format(date2);
                textView.append(sunst+"\n");

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
