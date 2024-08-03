package com.example.gpstrackerapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Mendapatkan nilai dari SharedPreferences
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // Mendapatkan nilai token dari SharedPreferences
        val token = sharedPreferences.getString("user_token", "")

        // Mendapatkan nilai email dari SharedPreferences
        val email = sharedPreferences.getString("user_email", "")
        if (email!="" && token!=""){
            val firstIntent = Intent(this, HalamanUtamaActivity::class.java)
            startActivity(firstIntent)
            val intent = Intent(this, GeofenceCheckService::class.java)
            GeofenceCheckService.enqueueWork(this, intent)
            finish()

        }
        else{

            val intent = Intent(this@MainActivity,HalamanLoginActivity::class.java)
            startActivity(intent)
            finish()
        }

    }


}

