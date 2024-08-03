package com.example.gpstrackerapp

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.gpstrackerapp.databinding.ActivityHalamanLokSeluruhMotorBinding
import com.example.gpstrackerapp.databinding.ActivityHalamanUtamaBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HalamanLokSeluruhMotorActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityHalamanLokSeluruhMotorBinding
    private lateinit var database: FirebaseDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHalamanLokSeluruhMotorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        database = FirebaseDatabase.getInstance()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


    }
    override fun onBackPressed() {
        val viewRl = binding.rlBottomlocation.visibility
        if (viewRl == View.VISIBLE){
            val anim = AnimationUtils.loadAnimation(this,R.anim.slide_down)
            binding.rlBottomlocation.startAnimation(anim)
            binding.rlBottomlocation.visibility = View.GONE

        }
        else{
            startActivity(Intent(this@HalamanLokSeluruhMotorActivity,HalamanUtamaActivity::class.java))
            finish()
            super.onBackPressed()
        }


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
        val Centerlocation = LatLng(2.596142, -261.196944)
        val zoomLevel = 11f // Sesuaikan level zoom sesuai kebutuhan
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Centerlocation, zoomLevel))
        // Tambahkan listener untuk mendapatkan data lokasi dari Firebase saat peta sudah siap
        val databaseReference = database.reference.child("motorbikes")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                // Bersihkan marker sebelum menambahkan yang baru
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
        mMap.setOnMarkerClickListener { marker ->
            // Ketika marker ditekan, lakukan sesuatu di sini
            // Contohnya, tampilkan pesan atau tindakan lainnya
            if(binding.rlBottomlocation.visibility==View.VISIBLE){
                val anim = AnimationUtils.loadAnimation(this,R.anim.slide_down)
                binding.rlBottomlocation.startAnimation(anim)
                binding.rlBottomlocation.visibility = View.GONE
            }



            // Membuat InfoWindow
            marker.showInfoWindow()


            val animation = AnimationUtils.loadAnimation(this, R.anim.slide_up)
            Handler().postDelayed({
                marker.showInfoWindow()
                binding.rlBottomlocation.startAnimation(animation)
                binding.rlBottomlocation.visibility = View.VISIBLE
            }, 100)
            val vehiclenumber= marker.title
            val messagesRef = database.reference.child("motorbikes/$vehiclenumber")
            messagesRef.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val motorvalue = snapshot.value as? Map<String, Any>
                    binding.TvPlatMotor.text= motorvalue?.get("vehicleNumber") as CharSequence?
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
            val tenant = messagesRef.child("tenant")
            tenant.addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){


                        binding.dotKetersediaan.setImageResource(R.drawable.tanda_tdktersedia)

                        binding.TVnamapeminjam.visibility=View.VISIBLE
                        binding.TVNIK.visibility = View.VISIBLE
                        binding.TVnomortelepon.visibility = View.VISIBLE
                        binding.TVdurasi.visibility = View.VISIBLE

                        val tenantData = snapshot.getValue() as? Map<String, Any>
                        val tenantName = tenantData?.get("Name") as? String
                        val tenantNIK = tenantData?.get("NIK") as? String
                        val tenantPhone = tenantData?.get("Tenantphone") as? String
                        val tenantDuration = tenantData?.get("Duration")
                        // Set TextView values for tenant information
                        binding.detailpenyewa.text= "Detail Penyewa"
                        binding.TVnamapeminjam.text = tenantName
                        binding.TVNIK.text = tenantNIK
                        binding.TVnomortelepon.text = tenantPhone
                        binding.TVdurasi.text =tenantDuration.toString()+" Jam"


                    }
                    else{


                        binding.dotKetersediaan.setImageResource(R.drawable.tanda_tersedia)

                        binding.TVnamapeminjam.visibility=View.GONE
                        binding.TVNIK.visibility = View.GONE
                        binding.TVnomortelepon.visibility = View.GONE
                        binding.TVdurasi.visibility = View.GONE
                        binding.detailpenyewa.text = "Tidak ada penyewa"



                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
            binding.IBcekdetail.setOnClickListener{
                val intent = Intent(this, DetailMotorActivity::class.java)
                intent.putExtra("VEHICLE_NUMBER", marker.title)
                intent.putExtra("From","HalamanLokSeluruhMotor")

                startActivity(intent)
            }
            // Return true untuk menunjukkan bahwa Anda telah menangani klik pada marker
            true
        }
        mMap.setOnMapClickListener { latLng ->
            if (binding.rlBottomlocation.visibility==View.VISIBLE){
                val anim = AnimationUtils.loadAnimation(this,R.anim.slide_down)
                binding.rlBottomlocation.startAnimation(anim)
                // Ketika pengguna menekan area di peta di luar marker, sembunyikan RelativeLayout
                binding.rlBottomlocation.visibility = View.GONE
            }


        }
    }
}