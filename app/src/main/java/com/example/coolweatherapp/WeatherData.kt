package com.example.coolweatherapp

data class WeatherData (
        var latitude : String ,
        var longitude : String ,
        var timezone : String ,
        var current_weather : CurrentWeather ,
        var hourly : Hourly
    )

//    data class CoordinateError(
//        var error: Boolean,
//        var reason: String
//            )
    data class CurrentWeather (
        var temperature : Float ,
        var windspeed : Float ,
        var winddirection : Int ,
        var weathercode : Int ,
        var time : String
    )
    data class Hourly (
        var time : ArrayList < String > ,
        var temperature_2m : ArrayList < Float > ,
        var weathercode : ArrayList < Int > ,
        var pressure_msl : ArrayList < Double >
    )
    enum class WMOWeatherCode ( var code : Int , var image : String ) {
        CLEAR_SKY (0 ,"clear") ,
        MAINLY_CLEAR (1 , "mostly_clear") ,
        PARTLY_CLOUDY (2 ,"partly_cloudy") ,
        OVERCAST (3 ,"cloudy") ,
        FOG (45 ,"fog"),
        DEPOSITING_RIME_FOG (48 ,"fog") ,
        DRIZZLE_LIGHT (51 , "drizzle") ,
        DRIZZLE_MODERATE (53 , "drizzle") ,
        DRIZZLE_DENSE (55 ,"drizzle") ,
        FREEZING_DRIZZLE_LIGHT (56 ,"freezing_drizzle") ,
        FREEZING_DRIZZLE_DENSE (57 ,"freezing_drizzle") ,
        RAIN_SLIGHT (61 ,"rain_light") ,
        RAIN_MODERATE (63 ,"rain") ,
        RAIN_HEAVY (65 ,"rain_heavy") ,
        FREEZING_RAIN_LIGHT (66 ,"freezing_rain_light") ,
        FREEZING_RAIN_HEAVY (67 ,"freezing_rain_heavy") ,
        SNOW_FALL_SLIGHT (71 ,"snow_light") ,
        SNOW_FALL_MODERATE (73 ,"snow") ,
        SNOW_FALL_HEAVY (75 ,"snow_heavy") ,
        SNOW_GRAINS (77 , "snow") ,
        RAIN_SHOWERS_SLIGHT (80 , "rain_light") ,
        RAIN_SHOWERS_MODERATE (81 , "rain") ,
        RAIN_SHOWERS_VIOLENT (82 , "rain_heavy") ,
        SNOW_SHOWERS_SLIGHT (85 , "snow_light") ,
        SNOW_SHOWERS_HEAVY (86 , "snow_heavy") ,
        THUNDERSTORM_SLIGHT_MODERATE (95 , "tstorm") ,
        THUNDERSTORM_HAIL_SLIGHT (96 ,"tstorm") ,
        THUNDERSTORM_HAIL_HEAVY (99 ,"tstorm")
    }
    fun getWeatherCodeMap () : Map <Int,WMOWeatherCode> {
        val weatherMap = HashMap < Int , WMOWeatherCode>()
        WMOWeatherCode.values().forEach {
            weatherMap[it.code] = it
        }
        return weatherMap
    }

