package com.example.gpstrackerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.gpstrackerapp.databinding.ActivityHalamanFormPelangganBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class HalamanFormPelangganActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHalamanFormPelangganBinding
    private lateinit var db: FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityHalamanFormPelangganBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = Firebase.database
        val messagesRef = db.reference.child(MESSAGES_CHILD)

    binding.BtnKirim.setOnClickListener {
        // Membuat objek data penyewa
        val penyewa = mapOf(
            "Duration" to binding.edDurasi.text.toString().toInt(),
            "NIK" to binding.edNIK.text.toString(),
            "Name" to binding.edNamaPelanggan.text.toString(),
            "Tenantphone" to binding.edNoTelp.text.toString()
        )

        val vehicleNumber = intent.getStringExtra("VEHICLE_NUMBER").toString() // Ganti dengan vehicleNumber yang sesuai
        val penyewaPath = "$vehicleNumber/tenant" // Contoh path yang sesuai dengan struktur database Anda

//        val updates =penyewa.toMap()  // Mengubah objek penyewa menjadi Map
        messagesRef.child(penyewaPath).setValue(penyewa).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Data penyewa berhasil diupdate", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Gagal mengupdate data penyewa", Toast.LENGTH_SHORT).show()
            }
        }
        val intent = Intent(this@HalamanFormPelangganActivity,DetailMotorActivity::class.java)
        intent.putExtra("VEHICLE_NUMBER", vehicleNumber)
        intent.putExtra("From",intent.getStringExtra("From").toString())
        startActivity(intent)


    }
    }
    companion object {
        const val MESSAGES_CHILD = "motorbikes"
    }

}
