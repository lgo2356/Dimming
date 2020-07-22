package com.hun.dimming.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hun.dimming.R
import com.hun.dimming.data.BTDevice
import kotlinx.android.synthetic.main.activity_blutooth.view.*

class RecyclerAdapterBTDevice(private val devices: ArrayList<BTDevice>) :
    RecyclerView.Adapter<RecyclerAdapterBTDevice.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.layout_bluetooth_device, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = devices[position].name
    }

    override fun getItemCount(): Int {
        return devices.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.text_pairing)
    }
}
