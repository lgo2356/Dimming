package com.hun.dimming

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.*
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.*
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
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
import java.util.*
import kotlin.collections.ArrayList

class BluetoothActivity : AppCompatActivity() {

    private val btPairedDevices: ArrayList<BTDevice> = ArrayList()
    private val btDiscoveredDevices: ArrayList<BTDevice> = ArrayList()
    private val btPairedAdapter: RecyclerAdapterBTDevice = RecyclerAdapterBTDevice(btPairedDevices)
    private val btDiscoveredAdapter: RecyclerAdapterBTDevice = RecyclerAdapterBTDevice(btDiscoveredDevices)

    private var mBluetoothAdapter: BluetoothAdapter? = null
    private val BluetoothAdapter.isDisabled: Boolean get() = !isEnabled
    private var mBluetoothLeService: BluetoothLeService? = null
    private var bluetoothLeScanner: BluetoothLeScanner? = null

    private val handler: Handler = Handler()

    private var mScanning: Boolean = false
    private var mConnected: Boolean = false
    private var mBluetoothGatt: BluetoothGatt? = null
    private var mDeviceName: String? = null
    private var mDeviceAddress: String? = null

    private val mServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName?, service: IBinder?) {
            mBluetoothLeService = (service as BluetoothLeService.LocalBinder).service

            if (mBluetoothLeService == null) {
                finish()
            }

            if (!mBluetoothLeService!!.initialize()) {
                Log.d("Debug", "Unable to initialize Bluetooth.")
                finish()
            }

//            mBluetoothLeService!!.connect(mDeviceAddress)
        }

        override fun onServiceDisconnected(componentName: ComponentName?) {
            mBluetoothLeService = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blutooth)

        checkPermissions(this)
        activateBluetooth()
//        registerBluetoothReceive()
//        startDeviceScan()

        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = bluetoothManager.adapter
        bluetoothLeScanner = mBluetoothAdapter?.bluetoothLeScanner

        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE를 지원하지 않는 디바이스 입니다.", Toast.LENGTH_SHORT).show()
        }

        recycler_pairing.adapter = btPairedAdapter
        recycler_pairing.layoutManager = LinearLayoutManager(this)

        recycler_discovered.adapter = btDiscoveredAdapter
        recycler_discovered.layoutManager = LinearLayoutManager(this)

        setPairedDeviceList()

        btPairedAdapter.setOnItemClickListener(
            object : RecyclerAdapterBTDevice.OnItemClickListener {
                override fun onItemClick(view: View, position: Int) {
                    scanLeDevice(false)

                    val devices = btPairedAdapter.getItems()
                    val address = devices[position].address
//                    val device = mBluetoothAdapter?.getRemoteDevice(address)
                    mBluetoothLeService?.connect(address)

//                    mBluetoothGatt = device?.connectGatt(applicationContext, false, gattCallback)
                }
            })

        btDiscoveredAdapter.setOnItemClickListener(
            object : RecyclerAdapterBTDevice.OnItemClickListener {
                override fun onItemClick(view: View, position: Int) {
                    scanLeDevice(false)

                    val devices = btDiscoveredAdapter.getItems()
                    val address = devices[position].address
//                    val device = mBluetoothAdapter?.getRemoteDevice(address)
                    mBluetoothLeService?.connect(address)

//                    mBluetoothGatt = device?.connectGatt(applicationContext, false, gattCallback)
                }
            })

        // Service binding
        val gattServiceIntent = Intent(this, BluetoothLeService::class.java)
        bindService(gattServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun setProgress(isProgress: Boolean) {
        if (isProgress) {
            progressbar_discovering.visibility = View.VISIBLE
        } else {
            progressbar_discovering.visibility = View.GONE
        }
    }

//    private val gattCallback = object : BluetoothGattCallback() {
//        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
//            val intentAction: String
//
//            when (newState) {
//                BluetoothProfile.STATE_CONNECTED -> {
//                    BTGatt.gatt = gatt
//
//                    intentAction = ACTION_GATT_CONNECTED
//                    broadcastUpdate(intentAction)
//                }
//
//                BluetoothProfile.STATE_DISCONNECTED -> {
//                    intentAction = ACTION_GATT_DISCONNECTED
//                    broadcastUpdate(intentAction)
//                }
//            }
//        }
//    }

    private val mGattUpdateCReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            when (intent?.action) {
                BluetoothLeService.ACTION_GATT_CONNECTED -> {
                    mConnected = true
                }

                BluetoothLeService.ACTION_GATT_DISCONNECTED -> {
                    mConnected = false
                }

                BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED -> {

                }

                BluetoothLeService.ACTION_DATA_AVAILABLE -> {

                }
            }
        }
    }

    private fun makeGattUpdateIntentFilter(): IntentFilter {
        val intentFilter = IntentFilter()
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED)
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED)
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED)
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE)
        return intentFilter
    }

//    private val receiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context, intent: Intent) {
//            when (intent.action) {
//                BluetoothDevice.ACTION_FOUND -> {
//                    val newDevice: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
//
//                    newDevice?.let {
//                        val name: String? = it.name
//                        val address: String = it.address
//
//                        if (!isDuplicatedDevice(address) && !isPairedDevice(address) && name != null) {
//                            btDiscoveredAdapter.addItem(name, address)
//                        }
//                    }
//                }
//
//                BluetoothAdapter.ACTION_STATE_CHANGED -> {
//                    Log.d("Debug", "ACTION STATE CHANGED")
//                }
//
//                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
//                    Log.d("Debug", "ACTION DISCOVERY STARTED")
//                    setProgress(true)
//                }
//
//                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
//                    Log.d("Debug", "ACTION DISCOVERY FINISHED")
//                    setProgress(false)
//                }
//
//                null -> {
//                }
//            }
//        }
//    }

    private fun isPairedDevice(address: String): Boolean {
        var isPaired = false
        val pairedDevices: Set<BluetoothDevice>? = mBluetoothAdapter?.bondedDevices

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

//    private fun registerBluetoothReceive() {
//        val filter = IntentFilter().apply {
//            addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
//            addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
//            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
//            addAction(BluetoothDevice.ACTION_FOUND)
//        }
//
//        registerReceiver(receiver, filter)
//    }

    private fun activateBluetooth() {
        mBluetoothAdapter?.takeIf { it.isDisabled }?.apply {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, Constant.REQUEST_ENABLE_BLUETOOTH)
        }
//        if (bluetoothAdapter == null) {
//            Toast.makeText(applicationContext, "블루투스 기능을 지원하지 않는 디바이스입니다.", Toast.LENGTH_SHORT).show()
//        } else {
//            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
//            startActivityForResult(enableBluetoothIntent, Constant.REQUEST_ENABLE_BLUETOOTH)
//        }
    }

    private fun scanLeDevice(enable: Boolean) {
        if (enable) {
            handler.postDelayed({
                mScanning = false
                bluetoothLeScanner?.stopScan(leScanCallback)
            }, 20000)
            mScanning = true
            bluetoothLeScanner?.startScan(leScanCallback)
        } else {
            mScanning = false
            bluetoothLeScanner?.stopScan(leScanCallback)
        }
    }

    private val leScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            processResult(result)
        }

//        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
//            super.onBatchScanResults(results)
//            if (results != null) {
//                for (result in results) {
//                    processResult(result)
//                }
//            }
//        }
//
//        override fun onScanFailed(errorCode: Int) {
//            super.onScanFailed(errorCode)
//        }

        private fun processResult(result: ScanResult?) {
            val name: String? = result?.device?.name
            val address: String = result?.device?.address!!

            Log.d("Debug", "Name: ${name.toString()} Address: $address")

            if (result.device?.name != null) {
                if (!isDuplicatedDevice(address) && !isPairedDevice(address) && name != null) {
                    btDiscoveredAdapter.addItem(name, address)
                }
            }
        }
    }

//    private val leScanCallback = BluetoothAdapter.LeScanCallback { bluetoothDevice, i, bytes ->
////        val name: String? = bluetoothDevice.name
////        val address: String = bluetoothDevice.address
//
//        if (bluetoothDevice.name != null) {
//            Log.d("Debug", bluetoothDevice.name)
//        }
//
////        if (!isDuplicatedDevice(address) && !isPairedDevice(address) && name != null) {
////            btDiscoveredAdapter.addItem(name, address)
////        }
//
////        val newDevice: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
////
////        newDevice?.let {
////            val name: String? = it.name
////            val address: String = it.address
////
////            if (!isDuplicatedDevice(address) && !isPairedDevice(address) && name != null) {
////                btDiscoveredAdapter.addItem(name, address)
////            }
////        }
//    }

    private fun setPairedDeviceList() {
        val pairedDevices: Set<BluetoothDevice>? = mBluetoothAdapter?.bondedDevices

        if (pairedDevices != null) {
            for (device in pairedDevices) {
                btPairedAdapter.addItem(device.name, device.address)
            }
        }
    }

    private fun checkPermissions(activity: Activity) {
        val requiredPermissions: Array<String> = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        val accessFineLocationPermission = ContextCompat.checkSelfPermission(activity, requiredPermissions[0])
        val accessCoarseLocationPermission = ContextCompat.checkSelfPermission(activity, requiredPermissions[1])

        if (accessFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
            accessCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
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

    override fun onResume() {
        super.onResume()
        registerReceiver(mGattUpdateCReceiver, makeGattUpdateIntentFilter())
        scanLeDevice(true)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(mGattUpdateCReceiver)
    }

    override fun onStop() {
        super.onStop()

        scanLeDevice(false)
        unbindService(mServiceConnection)
        mBluetoothLeService = null
    }
}
