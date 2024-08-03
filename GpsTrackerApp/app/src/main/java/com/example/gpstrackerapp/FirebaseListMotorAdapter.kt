package com.example.gpstrackerapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gpstrackerapp.databinding.CardViewMotorlistBinding
import com.example.gpstrackerapp.databinding.CardViewMotortersediaBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

class FirebaseListMotorAdapter (
    options: FirebaseRecyclerOptions<motorbikesData>,
    private val currentUserName: String?,
    private val listener: OnItemClickListener
) : FirebaseRecyclerAdapter<motorbikesData, FirebaseListMotorAdapter.ListMotorHolder>(options)  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListMotorHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.card_view_motorlist, parent, false)
        val binding = CardViewMotorlistBinding.bind(view)
        return ListMotorHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ListMotorHolder,
        position: Int,
        model: motorbikesData
    ) {
        holder.bind(model)

        // Simpan data dalam snapshot
        if (snapshot.size > position) {
            snapshot[position] = model
        } else {
            snapshot.add(model)
        }
        holder.itemView.setOnClickListener {
            listener.onItemClick(model) // Panggil fungsi onItemClick saat item diklik
        }
    }
    private var snapshot: MutableList<motorbikesData> = mutableListOf()


    fun clearRecyclerView() {
        snapshot.clear()
        notifyDataSetChanged()
    }

    inner class ListMotorHolder(private val binding: CardViewMotorlistBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: motorbikesData) {
            binding.TVharga.text="Rp.150.000"
            binding.TVnoplat.text =item.vehicleNumber

            if (item.tenant == null) {
                // Jika tenant null, berarti tersedia
                binding.TVketersediaan.text = "Tersedia"
                binding.dotKetersediaan.setImageResource(R.drawable.dot_tersedia)
            } else {
                // Jika tenant tidak null, berarti tidak tersedia
                binding.TVketersediaan.text = "Tidak Tersedia"
                binding.dotKetersediaan.setImageResource(R.drawable.dot_tdktersedia)
            }

        }

    }

}