package com.example.gpstrackerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gpstrackerapp.databinding.ActivityHalamanListMotorBinding
import com.firebase.ui.database.FirebaseRecyclerOptions

import com.google.firebase.database.FirebaseDatabase

class HalamanListMotorActivity : AppCompatActivity() , OnItemClickListener{
    private lateinit var binding: ActivityHalamanListMotorBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var adapter: FirebaseListMotorAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHalamanListMotorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        database = FirebaseDatabase.getInstance()
        // Inisialisasi RecyclerView
        val manager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.recyclerview.layoutManager = manager

        // Query untuk mendapatkan data motorbikes tanpa tenant
        val databaseReference = database.reference.child("motorbikes")

        val options = FirebaseRecyclerOptions.Builder<motorbikesData>()
            .setQuery(databaseReference, motorbikesData::class.java)
            .build()
        adapter = FirebaseListMotorAdapter(options, "test",this)
        binding.recyclerview.adapter = adapter

        // Setel listener pada SearchView
        val queryTextListener = object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {

                    if (it.isNotEmpty()) {
                        // Buat query baru berdasarkan nomor kendaraan
                        val filteredQuery = database.reference.child("motorbikes")
                            .orderByChild("vehicleNumber")
                            .startAt(query)
                            .endAt(query + "\uf8ff")

                        // Buat opsi adapter baru dengan query yang difilter
                        val filteredOptions = FirebaseRecyclerOptions.Builder<motorbikesData>()
                            .setQuery(filteredQuery, motorbikesData::class.java)
                            .build()

                        // Bersihkan dataset dan perbarui tampilan RecyclerView
                        adapter.clearRecyclerView()
                        adapter.updateOptions(filteredOptions)
                        adapter.notifyDataSetChanged()
                    }
                    if (it.isEmpty()) {
                        // Jika query kosong, kembalikan ke semua data motorbikes

                        val defaultOptions = FirebaseRecyclerOptions.Builder<motorbikesData>()
                            .setQuery(databaseReference, motorbikesData::class.java)
                            .build()

                        // Bersihkan dataset dan perbarui tampilan RecyclerView
                        adapter.clearRecyclerView()
                        adapter.updateOptions(defaultOptions)
                        adapter.notifyDataSetChanged()
                    }

                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    if (it.isEmpty()) {
                        // Jika input kosong, kembalikan ke semua data motorbikes
                        val defaultQuery = database.reference.child("motorbikes")

                        // Buat opsi adapter baru dengan query awal
                        val defaultOptions = FirebaseRecyclerOptions.Builder<motorbikesData>()
                            .setQuery(defaultQuery, motorbikesData::class.java)
                            .build()

                        // Bersihkan dataset dan perbarui tampilan RecyclerView
                        adapter.clearRecyclerView()
                        adapter.updateOptions(defaultOptions)
                        adapter.notifyDataSetChanged()
                    }
                    if (it.isNotEmpty()){
                        // Buat query baru berdasarkan nomor kendaraan yang mengandung huruf yang dimasukkan
                        val filteredQuery = database.reference.child("motorbikes")
                            .orderByChild("vehicleNumber")
                            .startAt(newText)
                            .endAt(newText + "\uf8ff")

                        // Buat opsi adapter baru dengan query yang difilter
                        val filteredOptions = FirebaseRecyclerOptions.Builder<motorbikesData>()
                            .setQuery(filteredQuery, motorbikesData::class.java)
                            .build()

                        // Bersihkan dataset dan perbarui tampilan RecyclerView
                        adapter.clearRecyclerView()
                        adapter.updateOptions(filteredOptions)
                        adapter.notifyDataSetChanged()


                    }
                }
                return true

            }
        }

        binding.SearchBarMotor.setOnQueryTextListener(queryTextListener)
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onBackPressed() {
        startActivity(Intent(this@HalamanListMotorActivity,HalamanUtamaActivity::class.java))
        finish()
        super.onBackPressed()
    }

    override fun onItemClick(item: motorbikesData) {
        val intent = Intent(this, DetailMotorActivity::class.java)
        intent.putExtra("VEHICLE_NUMBER", item.vehicleNumber)
        intent.putExtra("From","HalamanListMotorActivity")

        startActivity(intent)
    }
}