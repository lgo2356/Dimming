package com.hun.dimming

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.hun.dimming.adapter.RecyclerAdapterBTDevice
import com.hun.dimming.data.BTDevice
import kotlinx.android.synthetic.main.activity_blutooth.*

class BluetoothActivity : AppCompatActivity() {

    private val btPairedDevices: ArrayList<BTDevice> = ArrayList()
    private val btDiscoveredDevices: ArrayList<BTDevice> = ArrayList()
    private val btPairedAdapter: RecyclerAdapterBTDevice = RecyclerAdapterBTDevice(btPairedDevices)
    private val btDiscoveredAdapter: RecyclerAdapterBTDevice = RecyclerAdapterBTDevice(btDiscoveredDevices)
    private var bluetoothAdapter: BluetoothAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blutooth)

        checkPermissions(this)
        activateBluetooth()
        registerBluetoothReceive()
        startDeviceScan()

        recycler_pairing.adapter = btPairedAdapter
        recycler_pairing.layoutManager = LinearLayoutManager(this)

        recycler_discovered.adapter = btDiscoveredAdapter
        recycler_discovered.layoutManager = LinearLayoutManager(this)

        setPairedDeviceList()

        btPairedAdapter.setOnItemClickListener(
            object : RecyclerAdapterBTDevice.OnItemClickListener {
                override fun onItemClick(view: View, position: Int) {
                    val devices = btPairedAdapter.getItems()
                    val deviceAddress = devices[position].address

                    bluetoothAdapter?.cancelDiscovery()
                    val device = bluetoothAdapter?.getRemoteDevice(deviceAddress)
                }
            })

        btDiscoveredAdapter.setOnItemClickListener(
            object : RecyclerAdapterBTDevice.OnItemClickListener {
                override fun onItemClick(view: View, position: Int) {

                }
            })
    }

    private fun setProgress(isProgress: Boolean) {
        if (isProgress) {
            progressbar_discovering.visibility = View.VISIBLE
        } else {
            progressbar_discovering.visibility = View.GONE
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val newDevice: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)

                    newDevice?.let {
                        val name: String? = it.name
                        val address: String = it.address

                        if (!isDuplicatedDevice(address) && !isPairedDevice(address) && name != null) {
                            btDiscoveredAdapter.addItem(name, address)
                        }
                    }
                }

                BluetoothAdapter.ACTION_STATE_CHANGED -> {
                    Log.d("Debug", "ACTION STATE CHANGED")
                }

                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                    Log.d("Debug", "ACTION DISCOVERY STARTED")
                    setProgress(true)
                }

                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    Log.d("Debug", "ACTION DISCOVERY FINISHED")
                    setProgress(false)
                }

                null -> {
                }
            }
        }
    }

    private fun isPairedDevice(address: String): Boolean {
        var isPaired = false
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices

        if (pairedDevices != null && pairedDevices.isNotEmpty()) {
            for (device in pairedDevices) {
                if (address == device.address) {
                    isPaired = true
                    break
                }
            }
        }

        return isPaired
    }

    private fun isDuplicatedDevice(address: String): Boolean {
        var isDuplicated = false
        val discoveredDevices: List<BTDevice>? = btDiscoveredAdapter.getItems()

        if (discoveredDevices != null && discoveredDevices.isNotEmpty()) {
            for (device in discoveredDevices) {
                if (address == device.address) {
                    isDuplicated = true
                    break
                }
            }
        }

        return isDuplicated
    }

    private fun registerBluetoothReceive() {
        val filter = IntentFilter().apply {
            addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
            addAction(BluetoothDevice.ACTION_FOUND)
        }

        registerReceiver(receiver, filter)
    }

    private fun activateBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        if (bluetoothAdapter == null) {
            Toast.makeText(applicationContext, "블루투스 기능을 지원하지 않는 디바이스입니다.", Toast.LENGTH_SHORT).show()
        } else {
            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBluetoothIntent, Constant.REQUEST_ENABLE_BLUETOOTH)
        }
    }

    private fun startDeviceScan() {
        bluetoothAdapter?.cancelDiscovery()
        bluetoothAdapter?.startDiscovery()
    }

    private fun setPairedDeviceList() {
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices

        if (pairedDevices != null) {
            for (device in pairedDevices) {
                btPairedAdapter.addItem(device.name, device.address)
            }
        }
    }

    private fun checkPermissions(activity: Activity) {
        val requiredPermissions: Array<String> = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        val accessFineLocationPermission = ContextCompat.checkSelfPermission(activity, requiredPermissions[0])

        if (accessFineLocationPermission == PackageManager.PERMISSION_GRANTED) {
            return
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, requiredPermissions[0])) {
                // Explain to get permissions
                // '확인' -> get permissions
                val permissionDialog: AlertDialog.Builder = AlertDialog.Builder(activity)
                permissionDialog
                    .setTitle("권한 요청")
                    .setMessage("블루투스 연결을 위해 디바이스의 위치 정보 접근 권한이 필요합니다.")
                    .setPositiveButton("확인") { _, _ ->
                        ActivityCompat.requestPermissions(
                            activity,
                            requiredPermissions,
                            Constant.REQUEST_PERMISSIONS
                        )
                    }
                    .setNegativeButton("취소") { _, _ -> }
                    .show()
            } else {
                ActivityCompat.requestPermissions(activity, requiredPermissions, Constant.REQUEST_PERMISSIONS)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        bluetoothAdapter?.cancelDiscovery()
    }
}
