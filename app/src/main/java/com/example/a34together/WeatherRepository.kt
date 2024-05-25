package com.example.a34together
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherRepository {
    private val BASE_URL = "https://api.openweathermap.org/"
    private val API_KEY = "ca8a6bf71bc586cf36c1ff5e9c3f3579"
    private val weatherService: WeatherService
    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        weatherService = retrofit.create(WeatherService::class.java)
    }

    fun getWeatherByCity(cityName: String, callback: retrofit2.Callback<WeatherResponse>) {
        val call = weatherService.getCurrentWeather(cityName, API_KEY)
        call.enqueue(callback)
    }

    fun getWeatherByLocation(lat: Double, lon: Double, callback: retrofit2.Callback<WeatherResponse>) {
        val call = weatherService.getCurrentWeatherByLocation(lat, lon, API_KEY)
        call.enqueue(callback)
    }
}