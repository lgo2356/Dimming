package com.hun.dimming

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.Handler
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*

class BluetoothService(handler: Handler, device: BluetoothDevice) {

    private val uuidInsecure: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    private val mSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
        device.createInsecureRfcommSocketToServiceRecord(uuidInsecure)
    }
    private lateinit var mOutputStream: OutputStream
    private lateinit var mInputStream: InputStream

    fun connect() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                mSocket?.connect()

                if (mSocket?.isConnected == true) {
                    mOutputStream = mSocket?.outputStream!!
                    mInputStream = mSocket?.inputStream!!
                } else {
                    throw IOException("IO stream 생성에 실패했습니다")
                }
            } catch (e: IOException) {
                Log.d("Debug", "블루투스 연결에 실패했습니다", e)
                // 블루투스 연결 실패 토스트 메세지
            }
        }
    }

    fun write(packet: ByteArray) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                if (mSocket?.isConnected == true) {
                    if (::mOutputStream.isInitialized) {
                        mOutputStream.write(packet)
                    } else {
                        throw UninitializedPropertyAccessException("Output stream must be must be initialized")
                    }
                } else {
                    // 블루투스 연결 요청 토스트 메세지
                }
            } catch (e: IOException) {
                Log.d("Debug", "패킷 전송에 실패했습니다", e)
                // 패킷 전송 실패 토스트 메세지
            } catch (e: UninitializedPropertyAccessException) {
                // 블루투스 연결 재시도 요청 토스트 메세지
            }
        }
    }
}
