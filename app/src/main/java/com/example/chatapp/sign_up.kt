package com.example.chatapp


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.chatapp.User
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.logger.Logger

class sign_up : AppCompatActivity() {

    private lateinit var etFullName: EditText
    private lateinit var etSignUpEmail: EditText
    private lateinit var etDOB: EditText
    private lateinit var etSignUpPassword: EditText
    private lateinit var btnSignUpSubmit: Button
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private lateinit var etSignUpConfirmPassword: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)
        supportActionBar?.hide()

        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference("users")
        etFullName = findViewById(R.id.etFullName)
        etDOB = findViewById(R.id.etDOB)
        etSignUpEmail = findViewById(R.id.etSignUpEmail)
        etSignUpConfirmPassword = findViewById(R.id.etSignUpConfirmPassword)
        etSignUpPassword = findViewById(R.id.etSignUpPassword)
        btnSignUpSubmit = findViewById(R.id.btnSignUpSubmit)

        btnSignUpSubmit.setOnClickListener {
            val FullName = etFullName.text.toString()
            val DOB = etDOB.text.toString()
            val email = etSignUpEmail.text.toString()
            val password = etSignUpPassword.text.toString()
            val confirmPassword = etSignUpConfirmPassword.text.toString().trim()

            if (FullName.isEmpty() || DOB.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            } else if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            } else {
                signUp(FullName, DOB, email, password)
            }
        }

    }

    private fun signUp(FullName: String, DOB: String, email: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    mAuth.currentUser?.let { user ->
                        addUserToDatabase(FullName, DOB, email, user.uid)
                    }
                    val intent = Intent(this@sign_up, Welcomepage::class.java)
                    intent.putExtra("username", FullName)
                    startActivity(intent)
                    finish()
                } else {
                    val exception = task.exception
                    if (exception is FirebaseAuthUserCollisionException) {
                        Toast.makeText(this, "This email is already registered", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(this, "Error: ${exception?.message}", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
    }

    private fun addUserToDatabase(FullName: String, DOB: String, email: String, uid: String) {

        val user = User(FullName, DOB, email, uid)
        mDbRef.child(uid).setValue(user)
            .addOnSuccessListener {
                Toast.makeText(this, "User saved successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    this,
                    "Failed to save user: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

}

