package com.example.gpstrackerapp

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.ViewUtils

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.gpstrackerapp.databinding.ActivityDetailMotorBinding
import com.example.gpstrackerapp.databinding.ActivityHalamanFormPelangganBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.properties.Delegates

class DetailMotorActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityDetailMotorBinding
    private lateinit var vehiclenumber: String
    private lateinit var db: FirebaseDatabase
    private var tenantStatus by Delegates.notNull<Boolean>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailMotorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(findViewById(R.id.toolbar))
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.title = "Motor Secure"

        }
        db = Firebase.database
        vehiclenumber= intent.getStringExtra("VEHICLE_NUMBER").toString()
        val messagesRef = db.reference.child("motorbikes/$vehiclenumber")
        messagesRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val motorvalue = snapshot.value as? Map<String, Any>
                binding.TVnokendaraan.text= motorvalue?.get("vehicleNumber") as CharSequence?
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        val tenant = messagesRef.child("tenant")
        tenant.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){


                    binding.ImageViewKetersediaan.setImageResource(R.drawable.tanda_tdktersedia)

                    binding.TVnamapeminjam.visibility=View.VISIBLE
                    binding.ImageViewKetersediaan.visibility = View.VISIBLE
                    binding.TVNIK.visibility = View.VISIBLE
                    binding.TVnomortelepon.visibility = View.VISIBLE
                    binding.rlPeminjam.visibility = View.VISIBLE

                    val tenantData = snapshot.getValue() as? Map<String, Any>
                    val tenantName = tenantData?.get("Name") as? String
                    val tenantNIK = tenantData?.get("NIK") as? String
                    val tenantPhone = tenantData?.get("Tenantphone") as? String

                            // Set TextView values for tenant information
                    binding.TVnamapeminjam.text = tenantName
                    binding.TVNIK.text = tenantNIK
                    binding.TVnomortelepon.text = tenantPhone
                    binding.TVtidakterdapatpenyewa.visibility= View.GONE
                    tenantStatus = true

                }
                else{


                    binding.ImageViewKetersediaan.setImageResource(R.drawable.tanda_tersedia)

                    binding.TVnamapeminjam.visibility=View.GONE
                    binding.TVNIK.visibility = View.GONE
                    binding.TVnomortelepon.visibility = View.GONE
                    binding.rlPeminjam.visibility = View.VISIBLE

                    binding.TVtidakterdapatpenyewa.text = "Tidak ada penyewa "
                    tenantStatus = false

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })





        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onBackPressed() {
        if (intent.getStringExtra("From")=="HalamanUtamaActivity"){

            startActivity(Intent(this@DetailMotorActivity,HalamanUtamaActivity::class.java))

        }
        else if (intent.getStringExtra("From")=="HalamanListMotorActivity"){
            startActivity(Intent(this@DetailMotorActivity,HalamanListMotorActivity::class.java))

        }
        else if(intent.getStringExtra("From")=="HalamanLokSeluruhMotor"){
            startActivity(Intent(this@DetailMotorActivity,HalamanLokSeluruhMotorActivity::class.java))
        }
        super.onBackPressed()
        finish()
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

        // Add a marker in Sydney and move the camera
        // Tambahkan listener untuk mendapatkan data lokasi dari Firebase saat peta sudah siap
        val databaseReference = db.reference.child("motorbikes/$vehiclenumber")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Bersihkan marker sebelum menambahkan yang baru
                mMap.clear()

                    // Dapatkan data dari setiap sepeda motor
                    val latitude = dataSnapshot.child("latitude").getValue(Double::class.java)
                    val longitude = dataSnapshot.child("longitude").getValue(Double::class.java)

                    // Cek jika data latitude dan longitude tidak null
                    if (latitude != null && longitude != null) {
                        // Buat LatLng object untuk marker
                        val location = LatLng(latitude, longitude)

                        // Tambahkan marker untuk setiap lokasi ke peta
                        mMap.addMarker(MarkerOptions().position(location).title("Sepeda Motor"))

                        val zoomLevel = 15f // Sesuaikan level zoom sesuai kebutuhan
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, zoomLevel))
                    } else {
                        Log.e("Firebase Error", "Latitude atau longitude null")
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
        inflater.inflate(R.menu.toolbar_detail,menu)

        val addButton = menu?.findItem(R.id.add_customer)
        val deleteButton = menu?.findItem(R.id.delete_customer)
        if (tenantStatus){
            deleteButton?.isVisible = true
            addButton?.isVisible = false
        }
        else{
            deleteButton?.isVisible = false
            addButton?.isVisible = true
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.add_customer ->{
                val intent = Intent(this, HalamanFormPelangganActivity::class.java)
                intent.putExtra("VEHICLE_NUMBER",vehiclenumber)
                intent.putExtra("From",intent.getStringExtra("From").toString())
                startActivity(intent)



            }
            R.id.delete_customer ->{
                val builder = AlertDialog.Builder(this)

                // Atur judul dan pesan dialog
                builder.setTitle("Konfirmasi")
                builder.setMessage("Apakah Anda yakin ingin Menghapus Penyewa?")

                // Tambahkan tombol "Yes"
                builder.setPositiveButton("Ya") { dialog, which ->
                    val databaseReference = db.reference.child("motorbikes/$vehiclenumber/tenant")
                    databaseReference.removeValue()
                    recreate()
                    Toast.makeText(this@DetailMotorActivity, "Data penyewa telah dihapus", Toast.LENGTH_SHORT).show()
                }

                // Tambahkan tombol "No"
                builder.setNegativeButton("Tidak") { dialog, which ->

                    // Tambahkan kode yang akan dijalankan jika pengguna memilih "No"
                    // Contoh: tampilkan pesan toast

                }

                // Buat dan tampilkan dialog
                val dialog = builder.create()
                dialog.show()

            }
        }
        return super.onOptionsItemSelected(item)
    }
}