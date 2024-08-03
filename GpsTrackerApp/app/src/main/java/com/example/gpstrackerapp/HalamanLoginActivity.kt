package com.example.gpstrackerapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.gpstrackerapp.databinding.ActivityHalamanLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase


class HalamanLoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHalamanLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHalamanLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Inisialisasi Firebase Authentication
        auth = FirebaseAuth.getInstance()

        // Inisialisasi SharedPreferences
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        binding.btnLogin.setOnClickListener{
            val email = binding.edEmail.text.toString()
            val password = binding.edPassword.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Email dan password harus diisi", Toast.LENGTH_SHORT).show()
            }
        }

    }
    private fun loginUser(email: String, password: String) {
        // Lakukan proses login dengan email dan password menggunakan Firebase Authentication
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Login berhasil
                    Toast.makeText(this, "Login berhasil", Toast.LENGTH_SHORT).show()
                    // Simpan token
                    val user = auth.currentUser
                    val token = user?.uid ?: ""
                    val userEmail = user?.email ?: ""
                    val editor = sharedPreferences.edit()

                    editor.putString("user_token", token)
                    editor.putString("user_email", userEmail)
                    editor.apply()
                    val intent = Intent(this@HalamanLoginActivity, HalamanUtamaActivity::class.java)
                    startActivity(intent)
                    finish()
                    // Lanjutkan ke aktivitas berikutnya atau lakukan tindakan lain yang sesuai
                } else {
                    // Login gagal, tampilkan pesan kesalahan
                    Toast.makeText(this, "Login gagal: Email atau password tidak sesuai dengan akun manapun", Toast.LENGTH_SHORT).show()
                }
            }
    }
}