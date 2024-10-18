package com.example.tasbeehcounter




import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var spinnerTasbeeh: Spinner
    private lateinit var textViewTasbeehName: TextView
    private lateinit var textViewCount: TextView
    private lateinit var imageViewTasbeeh: ImageView
    private var count = 0
    private var currentTasbeeh = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance().reference

        // Initialize Views
        spinnerTasbeeh = findViewById(R.id.spinnerTasbeeh)
        textViewTasbeehName = findViewById(R.id.textViewTasbeehName)
        textViewCount = findViewById(R.id.textViewCount)
        imageViewTasbeeh = findViewById(R.id.imageViewTasbeeh)

        // Set up Spinner
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.tasbeeh_names,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTasbeeh.adapter = adapter

        // Spinner item selected listener
        spinnerTasbeeh.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                val selectedTasbeeh = parent.getItemAtPosition(position).toString()
                if (selectedTasbeeh != currentTasbeeh) {
                    currentTasbeeh = selectedTasbeeh
                    textViewTasbeehName.text = "Selected Tasbeeh: $currentTasbeeh"
                    count = 0 // Reset count to 0 for the new Tasbeeh
                    textViewCount.text = "0"
                    loadCountFromDatabase(currentTasbeeh)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Handle case when nothing is selected if needed
            }
        }

        // Button click listener
        findViewById<Button>(R.id.buttonIncrement).setOnClickListener {
            incrementTasbeeh()
        }
    }

    private fun incrementTasbeeh() {
        count++
        textViewCount.text = count.toString()
        saveCountToDatabase(currentTasbeeh, count)
    }

    private fun loadCountFromDatabase(tasbeehName: String) {
        database.child("tasbeeh_counts").child(tasbeehName).get().addOnSuccessListener {
            if (it.exists()) {
                count = it.value.toString().toInt()
                textViewCount.text = count.toString()
            } else {
                count = 0
                textViewCount.text = "0"
            }
        }
    }

    private fun saveCountToDatabase(tasbeehName: String, count: Int) {
        database.child("tasbeeh_counts").child(tasbeehName).setValue(count)
    }
}
