package com.example.gpstrackerapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gpstrackerapp.databinding.CardViewMotortersediaBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
interface OnItemClickListener {
    fun onItemClick(item: motorbikesData)
}
class FirebaseMainAdapter (
    options: FirebaseRecyclerOptions<motorbikesData>,
    private val currentUserName: String?,
    private val listener: OnItemClickListener
) : FirebaseRecyclerAdapter<motorbikesData, FirebaseMainAdapter.MainViewHolder>(options) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.card_view_motortersedia, parent, false)
        val binding = CardViewMotortersediaBinding.bind(view)
        return MainViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: MainViewHolder,
        position: Int,
        model: motorbikesData
    ) {
        holder.bind(model)
        holder.itemView.setOnClickListener {
            listener.onItemClick(model) // Panggil fungsi onItemClick saat item diklik
        }
    }

    inner class MainViewHolder(private val binding: CardViewMotortersediaBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: motorbikesData) {
//            binding.tvMessage.text = item.text
//            setTextColor(item.name, binding.tvMessage)
//            binding.tvMessenger.text = item.name
//            Glide.with(itemView.context)
//                .load(item.photoUrl)
//                .circleCrop()
//                .into(binding.ivMessenger)
//            if (item.timestamp != null) {
//                binding.tvTimestamp.text = DateUtils.getRelativeTimeSpanString(item.timestamp)
//            }
            binding.TVharga.text="Rp.150.000"
            binding.TvPlatMotor.text =item.vehicleNumber
        }

    }
}