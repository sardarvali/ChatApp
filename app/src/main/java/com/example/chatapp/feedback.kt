package com.example.chatapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.RatingBar
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class feedback : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var feedbackRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_feedback)

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance()
        feedbackRef = database.getReference("feedback")

        val tv6m6 = findViewById<TextView>(R.id.tv6m6)
        val cb1m6 = findViewById<CheckBox>(R.id.cb1m6)
        val cb2m6 = findViewById<CheckBox>(R.id.cb2m6)
        val cb3m6 = findViewById<CheckBox>(R.id.cb3m6)
        val Radiom6 = findViewById<RadioGroup>(R.id.radiom6)
        val ratingBar = findViewById<RatingBar>(R.id.ratingBar)
        val ed1m6 = findViewById<EditText>(R.id.ed1m6)
        val ed2m6 = findViewById<EditText>(R.id.ed2m6)
        val btn1m6 = findViewById<Button>(R.id.btn1m6)

        btn1m6.setOnClickListener {
            val starRating = ratingBar.rating.toInt()
            val issuesList = mutableListOf<String>()

            if (cb1m6.isChecked) issuesList.add("Page not opening")
            if (cb3m6.isChecked) issuesList.add("No issues")
            if (cb2m6.isChecked) issuesList.add("App crashing")

            val issues =
                if (issuesList.isNotEmpty()) issuesList.joinToString(", ") else "No issues selected"
            val additionalFeedback =
                if (ed1m6.visibility == View.VISIBLE && ed1m6.text.isNotEmpty()) {
                    ed1m6.text.toString()
                } else {
                    "No additional feedback"
                }
            val overallExperience =
                if (ed2m6.text.isNotEmpty()) ed2m6.text.toString() else "No experience shared"

            // Store feedback in Firebase Realtime Database
            val feedback = Feedbackdata(
                starRating = starRating,
                issues = issues,
                additionalFeedback = additionalFeedback,
                overallExperience = overallExperience
            )

            val feedbackId = feedbackRef.push().key // Generate unique ID for feedback
            if (feedbackId != null) {
                feedbackRef.child(feedbackId).setValue(feedback).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Snackbar.make(
                            btn1m6,
                            "Feedback submitted successfully!",
                            Snackbar.LENGTH_SHORT
                        ).show()
                        tv6m6.text = "Feedback successfully submitted!"
                    } else {
                        Snackbar.make(
                            btn1m6,
                            "Failed to submit feedback. Try again.",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        Radiom6.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.radiobtn2m6) {
                ed1m6.visibility = View.VISIBLE
            } else {
                ed1m6.visibility = View.GONE
            }
        }
    }
}
