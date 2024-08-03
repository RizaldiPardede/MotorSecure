package com.example.gpstrackerapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.JobIntentService
import androidx.core.app.NotificationCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class GeofenceCheckService : JobIntentService(){
    private lateinit var databaseReference: DatabaseReference

    companion object {
        const val JOB_ID = 1001

        fun enqueueWork(context: Context, work: Intent) {
            enqueueWork(context, GeofenceCheckService::class.java, JOB_ID, work)
        }
    }

    override fun onHandleWork(intent: Intent) {
        // Inisialisasi referensi Firebase Realtime Database
        databaseReference = Firebase.database.reference.child("motorbikes")

        // Menambahkan listener untuk mendengarkan perubahan lokasi perangkat
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {


                // Mendefinisikan geofence
                val geofenceLatitude = -7.954026904745615
                val geofenceLongitude = 112.61449489564667
                val geofenceRadius = 1000 // dalam meter
                for (motorbikeSnapshot in dataSnapshot.children){
                    // Mendapatkan lokasi terbaru perangkat dari Firebase Realtime Database
                    val lat = motorbikeSnapshot.child("latitude").getValue(Double::class.java)
                    val lon = motorbikeSnapshot.child("longitude").getValue(Double::class.java)

                    if (lat != null && lon != null) {
                        val distanceFromGeofence = distanceBetweenPoints(lat, lon, geofenceLatitude, geofenceLongitude)
                        val vehiclenumber = motorbikeSnapshot.child("vehicleNumber").getValue().toString()
                        if (distanceFromGeofence <= geofenceRadius) {
                            // Perangkat berada di dalam geofence
                            // Lakukan tindakan yang diinginkan, misalnya menampilkan pemberitahuan
//                            showNotification("$vehiclenumber  berada di dalam geofence")
                        } else {
                            // Perangkat berada di luar geofence
                            // Lakukan tindakan yang diinginkan, misalnya menampilkan pemberitahuan
                            showNotification("$vehiclenumber  berada di luar batas oprasional")
                        }

                    }
                    else{
                        showNotification("Tidak Ada data")
                    }

                }


            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Firebase Error", "Error fetching data", databaseError.toException())
            }
            private fun distanceBetweenPoints(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
                val earthRadius = 6371 // Radius bumi dalam kilometer
                val dLat = Math.toRadians(lat2 - lat1)
                val dLon = Math.toRadians(lon2 - lon1)
                val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                        Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2)
                val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
                return earthRadius * c * 1000 // Mengubah hasil ke dalam meter
            }

            private fun showNotification(message: String) {
                // Buat intent untuk membuka aktivitas utama ketika notifikasi diklik
                val intent = Intent(applicationContext, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                val pendingIntent = PendingIntent.getActivity(
                    applicationContext,
                    0,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
                )

                // Buat saluran notifikasi jika perangkat menjalankan Android Oreo atau versi yang lebih tinggi
                val channelId = "GeofenceCheckChannel"
                val channelName = "Geofence Check Channel"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val notificationChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
                    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.createNotificationChannel(notificationChannel)
                }

                // Bangun notifikasi
                val builder = NotificationCompat.Builder(applicationContext, channelId)
                    .setSmallIcon(R.drawable.dummymotor)
                    .setContentTitle("Cek batas oprasional")
                    .setContentText(message)
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)

                // Tampilkan notifikasi
                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(0, builder.build())
                // Tampilkan notifikasi ke pengguna dengan pesan yang diberikan
            }
        })
    }
}