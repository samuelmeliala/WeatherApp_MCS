package com.example.weatherapp_mcs

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.weatherapp_mcs.databinding.ActivityMainBinding
import com.example.weatherapp_mcs.utils.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getCurrentWeather()

    }

    private fun getCurrentWeather() {
        GlobalScope.launch(Dispatchers.IO) {
            val response = try {
                RetrofitInstance.api.getCurrentWeather(
                    "Jakarta",
                    "metric",
                    "8e0b51f9909201d5a0acbe35288ec107"  
                )
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@MainActivity,
                        "IOException: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return@launch
            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@MainActivity,
                        "HttpException: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return@launch
            }

            if (response.isSuccessful && response.body() != null) {
                withContext(Dispatchers.Main) {
                    val data = response.body()!!

                    // Update lokasi dan temperature
                    binding.apply {
                        tvLocation.text = "${data.name}, ${data.sys.country}" // Lokasi
                        tvTemp.text = "${data.main.temp.toInt()}°C"  // Temperature

                        // Kamu bisa update informasi lain jika perlu
                        tvFeelsLike.text = "Feels like: ${data.main.feels_like.toInt()}°C"
                        tvMinTemp.text = "Min temp: ${data.main.temp_min.toInt()}°C"
                        tvMaxTemp.text = "Max temp: ${data.main.temp_max.toInt()}°C"
                        tvUpdateTime.text = "Last Update: ${
                            SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(data.dt * 1000)
                        }"
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@MainActivity,
                        "Failed to retrieve weather data",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}