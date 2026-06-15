package com.example.randget11

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val email = findViewById<EditText>(R.id.etEmailLogin)
        val password = findViewById<EditText>(R.id.etPasswordLogin)
        val loginBtn = findViewById<Button>(R.id.btnLogin)
        val goToRegister = findViewById<TextView>(R.id.tvGoToRegister)

        loginBtn.setOnClickListener {

            val userEmail = email.text.toString()
            val pass = password.text.toString()

            if (userEmail.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show()
            } else {
                // TEMP LOGIN LOGIC (replace later with DB/Firebase)
                if (userEmail == "test@test.com" && pass == "1234") {
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()

                    // Navigate to next screen
                    // startActivity(Intent(this, MainActivity::class.java))

                } else {
                    Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
                }
            }
        }

        goToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}