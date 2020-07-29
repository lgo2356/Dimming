package com.hun.dimming

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothSocket
import java.io.InputStream
import java.io.OutputStream

class BTGatt {

    companion object {
        var gatt: BluetoothGatt? = null
        var characteristic: BluetoothGattCharacteristic? = null
    }
}
