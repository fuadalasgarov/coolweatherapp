//package com.example.coolweatherapp
//
//import android.content.res.Configuration
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.widget.Button
//import android.widget.EditText
//import android.widget.ImageView
//import android.widget.TextView
//import com.google.gson.Gson
//import java.io.InputStreamReader
//import java.net.URL
//
//class MainActivity : AppCompatActivity() {
//    private val day = false
//    override fun onCreate(savedInstanceState: Bundle?) {
//
//
//        when (resources.configuration.orientation) {
//            Configuration.ORIENTATION_PORTRAIT ->
//                if(day) setTheme (R.style.Theme_Day)
//                else setTheme (R.style.Theme_Night)
//            Configuration.ORIENTATION_LANDSCAPE ->
//                if(day) setTheme (R.style.Theme_Day_Land)
//                else setTheme (R.style.Theme_Night_Land)
//        }
//        fetchWeatherData(38.76f,-9.12f)
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        val updateButton : Button = findViewById(R.id.update)
//        updateButton.setOnClickListener {
//
//            val latitude = findViewById<TextView>(R.id.edit_text_1).text.toString().toFloat()
//
//            val longitude = findViewById<TextView>(R.id.edit_text_2).text.toString().toFloat()
//
//            fetchWeatherData(latitude, longitude).start()
//        }
//
//    }
//
//
//    private fun WeatherAPI_Call(lat: Float, long: Float) : WeatherData {
//        val reqString = buildString {
//            append ("https://api.open-meteo.com/v1/forecast?")
//            append("latitude=${lat}&longitude=${long}&")
//            append("current_weather=true&")
//            append("hourly=temperature_2m,weathercode,pressure_msl,windspeed_10m")
//        }
//        val str = reqString.toString()
//        val url = URL(reqString.toString());
//        url.openStream().use {
//            val request = Gson().fromJson(InputStreamReader(it ,"UTF-8"), WeatherData :: class.java )
//            return request
//        }
//    }
//    private fun fetchWeatherData(lat: Float, long: Float ) : Thread {
//        return Thread {
//            val weather = WeatherAPI_Call(lat, long )
//            updateUI(weather)
//        }
//    }
//    private fun updateUI (request : WeatherData ){
//        runOnUiThread {
//            val weatherImage : ImageView = findViewById(R.id.weatherImage)
//            val pressure : TextView = findViewById (R.id.pressureValue)
//            val direction : TextView = findViewById(R.id.wind_direction_value)
//            val speed : TextView = findViewById(R.id.wind_speed_value)
//            val temp_value : TextView = findViewById(R.id.temperature_value)
//            val timeValue : TextView = findViewById(R.id.timeValue)
//
//            pressure.text = request.hourly.pressure_msl.get(12).toString() + " hPa"
//            direction.text = request.current_weather.winddirection.toString()
//            speed.text = request.current_weather.windspeed.toString() + " km/h"
//            temp_value.text = request.current_weather.temperature.toString() + " °C"
//            timeValue.text = request.current_weather.time
//
//            val mapt = getWeatherCodeMap();
//            val wCode = mapt.get(request.current_weather.weathercode)
//            val wImage = when(wCode) {
//                WMO_WeatherCode.CLEAR_SKY ,
//                WMO_WeatherCode.MAINLY_CLEAR ,
//                WMO_WeatherCode.PARTLY_CLOUDY -> if(day) wCode?.image+"day" else wCode?.image +"night"
//
//                else -> wCode?.image
//            }
//            val res = getResources()
//            weatherImage.setImageResource(R.drawable.fog)
//            val resID = res.getIdentifier(wImage, "drawable", getPackageName());
//            val drawable = this.getDrawable(resID);
//            weatherImage.setImageDrawable(drawable);
//
//
//        }
//    }
//
//}