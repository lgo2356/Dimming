package com.hun.dimming.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hun.dimming.R
import com.hun.dimming.data.BTDevice

class RecyclerAdapterBTDevice(private val devices: ArrayList<BTDevice>) :
    RecyclerView.Adapter<RecyclerAdapterBTDevice.ViewHolder>() {

    private var listener: OnItemClickListener? = null

    private lateinit var progressBar: ProgressBar

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }

    interface SetItemConnectingProgress {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.layout_bluetooth_device, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = devices[position].name
        progressBar = holder.progressBar

        holder.itemView.setOnClickListener {
            listener?.onItemClick(it, position)
        }
    }

    override fun getItemCount(): Int {
        return devices.size
    }

    fun setProgress(isProgress: Boolean) {
        if (::progressBar.isInitialized) {
            if (isProgress) {
                progressBar.visibility = View.VISIBLE
            } else {
                progressBar.visibility = View.GONE
            }
        }
    }

    fun addItem(name: String, address: String) {
        val item = BTDevice(name, address)
        devices.add(item)
        notifyDataSetChanged()
    }

    fun getItems(): List<BTDevice> {
        return this.devices
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.text_name)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressbar_connecting)
    }
}
