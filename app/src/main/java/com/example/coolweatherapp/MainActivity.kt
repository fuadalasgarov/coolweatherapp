package com.example.coolweatherapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.coolweatherapp.R.*
import com.example.coolweatherapp.R.style.*
import com.google.gson.Gson
import java.io.InputStreamReader
import java.net.URL

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.location.LocationListener
import android.util.Log
import androidx.core.app.ActivityCompat



class MainActivity : AppCompatActivity() {
    private val day = true
    private var latitude_of_loc: Float = 0.0f
    private var longitude_of_loc: Float = 0.0f
    private val REQUEST_LOCATION_PERMISSIONS = 1

    @SuppressLint("ServiceCast", "CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        when (resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT ->
                if (day) setTheme(Theme_Night)
                else setTheme(Theme_Night)
            Configuration.ORIENTATION_LANDSCAPE ->
                if (day) setTheme(Theme_Night_Land)
                else setTheme(Theme_Night_Land)
        }
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_main)



        val lat = findViewById<EditText>(id.latitudeValue)
        val long = findViewById<EditText>(id.longitudeValue)
        lat.setSelectAllOnFocus(true)
        long.setSelectAllOnFocus(true)
        val updateButton: Button = findViewById(id.updatebutton)
        updateButton.setSelectAllOnFocus(true)
        updateButton.setOnClickListener {

            val latitude = findViewById<TextView>(id.latitudeValue).text.toString().toFloat()

            val longitude = findViewById<TextView>(id.longitudeValue).text.toString().toFloat()
            fetchWeatherData(latitude, longitude).start()

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(updateButton.windowToken, 0)
        }

        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        if (ActivityCompat.checkSelfPermission(
                this,
                ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION),
                REQUEST_LOCATION_PERMISSIONS
            )
            return
        }


        locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, object :
            LocationListener {
            override fun onLocationChanged(location: Location) {


                latitude_of_loc = location.latitude.toFloat()
                longitude_of_loc = location.longitude.toFloat()
                val latitude=findViewById<EditText>(id.latitudeValue)
                val longitude=findViewById<EditText>(id.longitudeValue)
                latitude.setText(latitude_of_loc.toString())
                longitude.setText(longitude_of_loc.toString())
                fetchWeatherData(latitude_of_loc, longitude_of_loc).start()
                Log.d("MyTag", "myMethod() called")
            }


            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}

            override fun onProviderEnabled(provider: String) {}

            override fun onProviderDisabled(provider: String) {}
        }, null)


//        fetchWeatherData(20F, 20F).start()
    }


    private fun WeatherAPI_Call ( lat : Float , long : Float ) : WeatherData {
        val reqString = buildString {
            append ("https://api.open-meteo.com/v1/forecast?")
                    append ("latitude=${lat}&longitude=${long}&")
                    append ("current_weather=true&")
                    append ("hourly=temperature_2m,weathercode,pressure_msl,windspeed_10m")
        }
        val str = reqString
        val url = URL(str)
        url.openStream().use {
            return Gson().fromJson(InputStreamReader(it, "UTF-8"), WeatherData :: class.java)
        }
    }
    private fun fetchWeatherData(lat: Float , long: Float ) : Thread {
        return Thread {
            val weather = WeatherAPI_Call(lat, long)

            updateUI(weather)
        }
    }

//    @SuppressLint("UseCompatLoadingForDrawables", "DiscouragedApi", "SetTextI18n")
    @SuppressLint("SetTextI18n")
    private fun updateUI (request : WeatherData ){
        runOnUiThread {
            val weatherImage : ImageView = findViewById(id.weatherImage)
            val pressure : TextView = findViewById (id.pressureValue)
            val winddir : TextView = findViewById(id.winddirectionValue)
            val windspeed : TextView = findViewById(id.windspeedValue)
            val temp : TextView = findViewById(id.temperatureValue)
            val time : TextView = findViewById(id.timeValue)
            pressure.text = request.hourly.pressure_msl.get(12).toString() + " hPa"
            winddir.text = request.current_weather.winddirection.toString()
            windspeed.text = request.current_weather.windspeed.toString() + " km/h"
            temp.text = request.current_weather.temperature.toString() + " °C"
            time.text = request.current_weather.time
            val mapt = getWeatherCodeMap()
            val wCode = mapt.get(request.current_weather.weathercode)
            val wImage = when (wCode) {
                WMOWeatherCode.CLEAR_SKY,
                WMOWeatherCode.MAINLY_CLEAR,
                WMOWeatherCode.PARTLY_CLOUDY -> if(day) wCode.image +"_day"
                else wCode.image +"_night"
                else -> wCode?.image
            }
            val res = getResources()
            weatherImage.setImageResource(drawable.fog)
            val resID = res.getIdentifier(wImage, "drawable", getPackageName())
            val drawable = this.getDrawable ( resID )
            weatherImage.setImageDrawable ( drawable )

        }
    }
}