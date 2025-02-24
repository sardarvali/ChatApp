package com.example.chatapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.example.chatapp.R
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging


class login_page : AppCompatActivity() {

    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var btnLogIn: Button
    private lateinit var tvSignUp: Button
    private lateinit var btnGoogleLogin: ImageView
    private lateinit var btnFacebookLogin: ImageView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    private companion object {
        const val RC_SIGN_IN = 100
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login_page)
        supportActionBar?.hide()

        mAuth = FirebaseAuth.getInstance()
        username = findViewById(R.id.etUsername)
        password = findViewById(R.id.etPassword)
        btnLogIn = findViewById(R.id.btnLogIn)
        tvSignUp = findViewById(R.id.tvSignUp)
        btnGoogleLogin = findViewById(R.id.btnGoogleLogin)
        btnFacebookLogin = findViewById(R.id.btnFacebookLogin)


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        btnGoogleLogin.setOnClickListener {
            signInWithGoogle()
        }

        btnLogIn.setOnClickListener {
            val email = username.text.toString()
            val passwordtext = password.text.toString()
            var isValid = true
            if (email.isEmpty()) {
                username.error = "Email cannot be empty"
                isValid = false
            }
            if (passwordtext.isEmpty()) {
                password.error = "Password cannot be empty"
                isValid = false
            }
            if (isValid) {
                login(email, passwordtext)
            }
        }

        tvSignUp.setOnClickListener {
            val intent = Intent(this, sign_up::class.java)
            startActivity(intent)
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Toast.makeText(this, "Google sign-in failed: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Google sign-in failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun login(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "user does not exist", Toast.LENGTH_SHORT).show()
            }
        }
    }


}