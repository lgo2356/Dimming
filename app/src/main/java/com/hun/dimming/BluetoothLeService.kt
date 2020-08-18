package com.hun.dimming

import android.app.Service
import android.bluetooth.*
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import java.lang.StringBuilder
import java.util.*

private const val STATE_DISCONNECTED = 0
private const val STATE_CONNECTING = 1
private const val STATE_CONNECTED = 2

class BluetoothLeService : Service() {

    private var mBluetoothManager: BluetoothManager? = null
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mBluetoothDeviceAddress: String? = null
    private var mBluetoothGatt: BluetoothGatt? = null
    private var mConnectionState = STATE_DISCONNECTED

    private val serviceUuid = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb")
    private val characteristicUuid = UUID.fromString("0000fff2-0000-1000-8000-00805f9b34fb")

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            val intentAction: String

            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    Log.d("Debug", "Connected to GATT client. Attempting to start service discovery.")
                    gatt?.discoverServices()
                    intentAction = ACTION_GATT_CONNECTED
                    broadcastUpdate(intentAction)
                }

                BluetoothProfile.STATE_DISCONNECTED -> {
                    Log.d("Debug", "Disconnected form GATT client.")
                    intentAction = ACTION_GATT_DISCONNECTED
                    broadcastUpdate(intentAction)
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                val services: List<BluetoothGattService>? = gatt?.services
                val characteristic = gatt?.getService(serviceUuid)?.getCharacteristic(characteristicUuid)
                gatt?.setCharacteristicNotification(characteristic, true)

                BTGatt.gatt = gatt
                BTGatt.characteristic = characteristic

                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED)
            } else {
                Log.d("Debug", "onServicesDiscovered received: $status")
            }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic)
            }
        }
    }

    private fun broadcastUpdate(action: String) {
        val intent = Intent(action)
        sendBroadcast(intent)
    }

    private fun broadcastUpdate(action: String, characteristic: BluetoothGattCharacteristic?) {
        val intent = Intent(action)

        if (serviceUuid == characteristic?.uuid) {
            val flag = characteristic?.properties
            var format: Int = -1

            if (flag != null) {
                if ((flag and 0x01) != 0) {
                    format = BluetoothGattCharacteristic.FORMAT_UINT16
                    Log.d("Debug", "Heart rate format UINT16.")
                } else {
                    format = BluetoothGattCharacteristic.FORMAT_UINT8
                    Log.d("Debug", "Heart rate format UINT8.")
                }
            }

            val heartRate = characteristic?.getIntValue(format, 1)
            intent.putExtra(EXTRA_DATA, heartRate.toString())
        } else {
            val data: ByteArray? = characteristic?.value

            if (data?.isNotEmpty() == true) {
                val stringBuilder = StringBuilder(data.size)

                for (byteChar in data) {
                    stringBuilder.append(String.format("%02X ", byteChar))
                }

                intent.putExtra(EXTRA_DATA, String(data) + "\n" + stringBuilder.toString())
            }
        }

        sendBroadcast(intent)
    }

    override fun onBind(intent: Intent?): IBinder? {
//        mBluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        return mBinder
    }

    private val mBinder: IBinder = LocalBinder()

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    fun initialize(): Boolean {
        if (mBluetoothManager == null) {
            mBluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager

            if (mBluetoothManager == null) {
                Log.d("Debug", "Unable to initialize BluetoothManager.")
                return false
            }
        }



        mBluetoothAdapter = mBluetoothManager!!.adapter

        if (mBluetoothAdapter == null) {
            Log.d("Debug", "Unable to obtain a BluetoothAdapter.")
            return false
        }

        return true
    }

    fun connect(address: String?): Boolean {
        if (mBluetoothAdapter == null || address == null) {
            Log.d("Debug", "BluetoothAdapter not initialized or unspecified address.")
            return false
        }

        if (mBluetoothDeviceAddress != null && address == mBluetoothDeviceAddress && mBluetoothGatt != null) {
            Log.d("Debug", "Trying to use an existing mBluetoothGatt for connection.")

            return if (mBluetoothGatt!!.connect()) {
                mConnectionState = STATE_CONNECTING
                true
            } else {
                false
            }
        }

        val device: BluetoothDevice? = mBluetoothAdapter?.getRemoteDevice(address)

        if (device == null) {
            Log.d("Debug", "Device not found. Unable to connect.")
            return false
        }

        mBluetoothGatt = device.connectGatt(this, false, gattCallback)
        Log.d("Debug", "Trying to create a new connection.")
        mBluetoothDeviceAddress = address
        mConnectionState = STATE_CONNECTING
        return true
    }

    fun disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null || BTGatt.gatt == null) {
            Log.d("Debug", "BluetoothAdapter not initialized")
            return
        }

        BTGatt.gatt?.disconnect()
    }

    fun close() {
        if (BTGatt.gatt == null) {
            return
        }

        BTGatt.gatt?.close()
        BTGatt.gatt = null
    }

//    fun write() {
//        mBluetoothGatt?.writeCharacteristic()
//    }

    inner class LocalBinder : Binder() {
        val service: BluetoothLeService
            get() = this@BluetoothLeService
    }

//    class LocalBinder : Binder() {
//        val service: BluetoothLeService
//            get() = this@BluetoothLeService
//    }

    companion object {
        const val ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED"
        const val ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED"
        const val ACTION_GATT_SERVICES_DISCOVERED = "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED"
        const val ACTION_DATA_AVAILABLE = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE"
        const val EXTRA_DATA = "com.example.bluetooth.le.EXTRA_DATA"
    }
}
