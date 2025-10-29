package com.example.weatherapp

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class WeatherResponse(val current_weather: CurrentWeather)
data class CurrentWeather(val temperature: Double, val windspeed: Double)

interface WeatherApi {
    @GET("v1/forecast")
    fun getWeather(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("current_weather") current: Boolean = true
    ): Call<WeatherResponse>
}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tvResult = findViewById<TextView>(R.id.tvResult)
        val btnLoad = findViewById<Button>(R.id.btnLoad)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(WeatherApi::class.java)

        btnLoad.setOnClickListener {
            // <-- NOTE: no "callback =" here, just enqueue(object : Callback<...> { ... })
            api.getWeather(23.8103, 90.4125).enqueue(object : Callback<WeatherResponse> {
                override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                    val w = response.body()?.current_weather
                    tvResult.text = if (w != null) {
                        "Temp: ${w.temperature}°C\nWind: ${w.windspeed} km/h"
                    } else {
                        "No data (code: ${response.code()})"
                    }
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    tvResult.text = "Error: ${t.message}"
                }
            })
        }
    }
}