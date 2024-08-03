package com.example.gpstrackerapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.gpstrackerapp.databinding.ActivityHalamanUtamaBinding
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HalamanUtamaActivity : AppCompatActivity(), OnMapReadyCallback, OnItemClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityHalamanUtamaBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var adapter: FirebaseMainAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHalamanUtamaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(findViewById(R.id.toolbar3))
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.title = "Motor Secure"

        }
        database = FirebaseDatabase.getInstance()

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // Mendapatkan nilai email dari SharedPreferences
        val email = sharedPreferences.getString("user_email", "")
        if (email != null) {
            binding.username.text = "Hai "+email.replace("@gmail.com", "")
        }

        // Inisialisasi RecyclerView
        val manager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        binding.recyclerview.layoutManager = manager




















        // Query untuk mendapatkan data motorbikes tanpa tenant
        val databaseReference = database.reference.child("motorbikes")

        val query = databaseReference.orderByChild("tenant").equalTo(null)

        val options = FirebaseRecyclerOptions.Builder<motorbikesData>()
            .setQuery(query, motorbikesData::class.java)
            .build()
        adapter = FirebaseMainAdapter(options, "test",this)
        binding.recyclerview.adapter = adapter

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }
    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }
    override fun onResume() {
        super.onResume()
        adapter.startListening()
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val Centerlocation = LatLng(-7.954026904745615, 112.61449489564667)
        val zoomLevel = 9.5f // Sesuaikan level zoom sesuai kebutuhan
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Centerlocation, zoomLevel))
        // Tambahkan listener untuk mendapatkan data lokasi dari Firebase saat peta sudah siap
        val databaseReference = database.reference.child("motorbikes")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Bersihkan marker sebelum menambahkan yang baru
                mMap.clear()

                // Iterasi melalui semua data sepeda motor
                for (motorbikeSnapshot in dataSnapshot.children) {
                    // Dapatkan data dari setiap sepeda motor
                    val latitude = motorbikeSnapshot.child("latitude").getValue(Double::class.java)
                    val longitude = motorbikeSnapshot.child("longitude").getValue(Double::class.java)

                    // Cek jika data latitude dan longitude tidak null
                    if (latitude != null && longitude != null) {
                        // Buat LatLng object untuk marker
                        val location = LatLng(latitude, longitude)

                        // Tambahkan marker untuk setiap lokasi ke peta
                        mMap.addMarker(MarkerOptions().position(location).title(motorbikeSnapshot.child("vehicleNumber").getValue(String::class.java)))
                    } else {
                        Log.e("Firebase Error", "Latitude atau longitude null")
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Penanganan kesalahan jika ada
                Log.e("Firebase Error", "Error fetching data", databaseError.toException())
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.toolbar_menu,menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_list ->{
                val intent = Intent(this@HalamanUtamaActivity, HalamanListMotorActivity::class.java)
                startActivity(intent)
                finish()

            }
            R.id.action_map->{
                val intent = Intent(this@HalamanUtamaActivity,HalamanLokSeluruhMotorActivity::class.java)
                startActivity(intent)
                finish()
            }
            R.id.action_keluar->{
                val intent = Intent(this@HalamanUtamaActivity,HalamanLoginActivity::class.java)
                // Mendapatkan referensi ke SharedPreferences
                val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.clear()
                editor.apply()
                startActivity(intent)
                finishAffinity()


            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onItemClick(item: motorbikesData) {

        val intent = Intent(this, DetailMotorActivity::class.java)
        intent.putExtra("VEHICLE_NUMBER", item.vehicleNumber)
        intent.putExtra("From","HalamanUtamaActivity")

        startActivity(intent)


    }
}