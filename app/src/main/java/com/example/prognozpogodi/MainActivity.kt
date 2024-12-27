package com.example.prognozpogodi

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity()
{
    private val url: String = "https://api.weather.yandex.ru/v2/forecast"
    private val apiKey: String = TODO() // Your Yandex API key
    private var lat: String = ""
    private var lon: String = ""

    private var textViewCityName: TextView? = null
    private var textViewCityTemp: TextView? = null
    private var textViewDate1: TextView? = null
    private var textViewDateTemp1: TextView? = null
    private var textViewDate2: TextView? = null
    private var textViewDateTemp2: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        textViewCityName = findViewById(R.id.textViewCityName)
        textViewCityTemp = findViewById(R.id.textViewCityTemp)
        textViewDate1 = findViewById(R.id.textViewDate1)
        textViewDateTemp1 = findViewById(R.id.textViewDateTemp1)

        textViewDate2 = findViewById(R.id.textViewDate2)
        textViewDateTemp2 = findViewById(R.id.textViewDateTemp2)

        val requestQueue = Volley.newRequestQueue(this)

        getWeather(requestQueue, url, apiKey)
    }

    private fun getWeather(requestQueue: RequestQueue, url: String, apiKey: String,
                           lat: String = "55.7522", lon: String = "37.6156")
    {
        var buildUrl: String = url

        if(lat.isNotEmpty() && lon.isNotEmpty())
        {
            buildUrl += "?lat=$lat&lon=$lon"
        }

        val request = object : JsonObjectRequest(
            Method.GET,
            buildUrl,
            null,
            { response ->
                try
                {
                    textViewCityName?.text = response.getJSONObject("info").
                    getJSONObject("tzinfo").
                    getString("name")

                    textViewCityTemp?.text = response.getJSONObject("fact").
                    getInt("temp").
                    toString() + "°C"


                    var element = response.getJSONArray("forecasts").
                    getJSONObject(0)

                    textViewDate1?.text =  getDate(element)
                    textViewDateTemp1?.text = "from ${getMin(element)}°C to ${getMax(element)}°C"

                    element = response.getJSONArray("forecasts").
                    getJSONObject(1)
                    textViewDate2?.text =  getDate(element)
                    textViewDateTemp2?.text = "from ${getMin(element)}°C to ${getMax(element)}°C"
                }
                catch (e: JSONException)
                {
                    e.printStackTrace()
                }
            },
            { error ->
                error.printStackTrace()
            }
        )
        {
            override fun getHeaders(): MutableMap<String, String>
            {
                val headers = mutableMapOf<String, String>()
                headers["X-Yandex-Weather-Key"] = apiKey
                return headers
            }
        }

        requestQueue.add(request)
    }

    private fun getDate(element: JSONObject): String
    {
        return element.getString("date")
    }

    private fun getMin(element: JSONObject): Int
    {
        return element.
        getJSONObject("parts").
        getJSONObject("day").
        getInt("temp_min")
    }

    private fun getMax(element: JSONObject): Int
    {
        return element.
        getJSONObject("parts").
        getJSONObject("day").
        getInt("temp_max")
    }
}