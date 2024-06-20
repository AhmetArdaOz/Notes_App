package com.example.notes_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notes_app.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var db: NotesDatabaseHelper
    private lateinit var notesAdapter: NotesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = NotesDatabaseHelper(this)
        notesAdapter = NotesAdapter(db.getNotes(), this)

        binding.notesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.notesRecyclerView.adapter = notesAdapter

        // Fetch data from the real-time API when the screen is created
        fetchDataFromRealTimeApi()

        // Fetch data from the mock API when the screen is created
        fetchDataFromMockApi()

        binding.addButton.setOnClickListener {
            val intent = Intent(this, AddNoteActivity::class.java)
            startActivity(intent)
        }
    }

    private fun fetchDataFromRealTimeApi() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://famous-quotes4.p.rapidapi.com/random?category=all&count=2")
            .get()
            .addHeader("X-RapidAPI-Key", "2e905c5cf8msha5bcab8c1520ce9p1fff97jsnd78b5f82d060")
            .addHeader("X-RapidAPI-Host", "famous-quotes4.p.rapidapi.com")
            .build()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response: Response = client.newCall(request).execute()

                // Process the API response here
                val responseData = response.body?.string()

                Log.d("MainActivity", "Real-time API Response: $responseData")

                runOnUiThread {
                    // Update the TextView with API response
                    val apiResponseTextView = findViewById<TextView>(R.id.apiResponseTextView)
                    apiResponseTextView.text = parseQuotes(responseData)

                    // Show or hide the API data layout based on the response
                    val apiDataLayout = findViewById<LinearLayout>(R.id.apiDataLayout)
                    apiDataLayout.visibility = if (responseData.isNullOrEmpty()) View.GONE else View.VISIBLE
                }
            } catch (e: Exception) {
                // Handle exceptions
                e.printStackTrace()
                runOnUiThread {
                    // Show an error message to the user
                    Toast.makeText(this@MainActivity, "Error fetching data from real-time API", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun fetchDataFromMockApi() {
        // Fetch data from the mock API and store it in the local database
        db.syncNotesFromMockApi()
    }

    private fun parseQuotes(apiResponse: String?): String {
        // Parse the JSON response and extract quotes
        val quotes = StringBuilder()

        try {
            val jsonArray = JSONArray(apiResponse)
            for (i in 0 until jsonArray.length()) {
                val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                val quote = jsonObject.getString("text")
                val author = jsonObject.getString("author")

                quotes.append("\"$quote\" - $author\n\n")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return quotes.toString()
    }

    override fun onResume() {
        super.onResume()
        // Refresh the RecyclerView when the screen resumes
        notesAdapter.refreshData(db.getNotes())
    }
}
