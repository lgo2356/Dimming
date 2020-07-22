package com.hun.dimming

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.hun.dimming.adapter.RecyclerAdapterBTDevice
import com.hun.dimming.data.BTDevice
import kotlinx.android.synthetic.main.activity_blutooth.*

class BluetoothActivity : AppCompatActivity() {

    private val btDevices: ArrayList<BTDevice> = ArrayList()
    private val btPairedAdapter: RecyclerAdapterBTDevice = RecyclerAdapterBTDevice(btDevices)
    private val btDiscoveredAdapter: RecyclerAdapterBTDevice = RecyclerAdapterBTDevice(btDevices)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blutooth)

        recycler_pairing.adapter = btPairedAdapter
        recycler_pairing.layoutManager = LinearLayoutManager(this)

        recycler_discovered.adapter = btDiscoveredAdapter
        recycler_discovered.layoutManager = LinearLayoutManager(this)
    }
}
