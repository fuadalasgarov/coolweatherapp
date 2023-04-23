package com.example.coolweatherapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.example.coolweatherapp.R.*
import com.example.coolweatherapp.R.style.*
import com.google.gson.Gson
import java.io.InputStreamReader
import java.net.URL

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.location.LocationListener
import android.widget.*
import androidx.core.app.ActivityCompat
import com.example.coolweatherapp.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private val day = true
    private var latitude_of_loc: Float = 0.0f
    private var longitude_of_loc: Float = 0.0f
    private val REQUEST_LOCATION_PERMISSIONS = 1
    private lateinit var binding: ActivityMainBinding

    @SuppressLint("ServiceCast", "CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        val lat = findViewById<EditText>(id.latitudeValue)
        val long = findViewById<EditText>(id.longitudeValue)
        lat.setSelectAllOnFocus(true)
        long.setSelectAllOnFocus(true)
        binding.updatebutton.setSelectAllOnFocus(true)
        binding.updatebutton.setOnClickListener {

            fetchWeatherData().start()

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.updatebutton.windowToken, 0)
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


                binding.latitudeValue.setText(latitude_of_loc.toString())
                binding.longitudeValue.setText(longitude_of_loc.toString())

                fetchWeatherData().start()
            }


            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}

            override fun onProviderEnabled(provider: String) {}

            override fun onProviderDisabled(provider: String) {}
        }, null)


    }


    private fun WeatherAPI_Call ( lat : Float , long : Float ) : WeatherData {

        val reqString = buildString {
            append ("https://api.open-meteo.com/v1/forecast?")
                    append ("latitude=${lat}&longitude=${long}&")
                    append ("current_weather=true&")
                    append ("hourly=temperature_2m,weathercode,pressure_msl,windspeed_10m")
        }
        val url = URL(reqString)


        url.openStream().use {
            return Gson().fromJson(InputStreamReader(it, "UTF-8"), WeatherData :: class.java)
        }


    }
    private fun fetchWeatherData(): Thread {
        return Thread {
            val latString = binding.latitudeValue.text.toString().trim()
            val longString = binding.longitudeValue.text.toString().trim()

            if (latString.isEmpty() || longString.isEmpty()) {
                runOnUiThread {
                    Toast.makeText(this, "Please enter both latitude and longitude", Toast.LENGTH_SHORT).show()
                }
                return@Thread
            }

            val lat = latString.toFloatOrNull()
            val long = longString.toFloatOrNull()

            if (lat == null || long == null) {
                runOnUiThread {
                    Toast.makeText(this, "Invalid latitude or longitude", Toast.LENGTH_SHORT).show()
                }
                return@Thread
            }

            if ((lat > 90 || lat < -90) || (long > 90 || long < -90)) {
                runOnUiThread {
                    Toast.makeText(this, "Latitude and Longitude should be between -90 and 90", Toast.LENGTH_SHORT).show()
                }
                return@Thread
            }


            val weather = WeatherAPI_Call(lat, long)
                runOnUiThread {
                    updateUI(weather)
                }
        }
    }


    @SuppressLint("SetTextI18n")
    private fun updateUI (request : WeatherData ){
        runOnUiThread {
            val weatherImage : ImageView = findViewById(id.weatherImage)
            val pressure : TextView = findViewById (id.pressureValue)
            val winddirimage : ImageView = findViewById(id.arrow)
            val windspeed : TextView = findViewById(id.windspeedValue)
            val temp : TextView = findViewById(id.temperatureValue)
            val time : TextView = findViewById(id.timeValue)
            pressure.text = request.hourly.pressure_msl[12].toString() + " hPa"
            val winddir = request.current_weather.winddirection.toFloat()
            winddirimage.rotation = 119f+winddir
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