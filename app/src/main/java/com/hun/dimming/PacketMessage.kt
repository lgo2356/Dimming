package com.hun.dimming

import android.util.Log
import java.lang.StringBuilder

private const val STX: Byte = 0x02
private const val ETX: Byte = 0x03
private const val ZERO: Byte = 0x30

class PacketMessage {

    fun makePacketMessage(groupNum: Int, colorCode: Int, dimValue: Int, onOff: Boolean): ByteArray {
        val groupNumPacket: Byte = convertToASCII(groupNum)
        val colorCodeDigits: IntArray = separateDigit(colorCode)
        val colorCode10: Byte = convertToASCII(colorCodeDigits[1])
        val colorCode1: Byte = convertToASCII(colorCodeDigits[2])
//        val colorCode10: Byte = colorCodeDigits[1].toByte()
//        val colorCode1: Byte = colorCodeDigits[2].toByte()

        val dimValueDigits: IntArray = separateDigit(dimValue)
        val dimValue100: Byte = convertToASCII(dimValueDigits[0])
        val dimValue10: Byte = convertToASCII(dimValueDigits[1])
        val dimValue1: Byte = convertToASCII(dimValueDigits[2])
//        val dimValue100: Byte = dimValueDigits[0].toByte()
//        val dimValue10: Byte = dimValueDigits[1].toByte()
//        val dimValue1: Byte = dimValueDigits[2].toByte()

        // on/off
        val onOffPacket: Byte = if (onOff) 0x31 else 0x30

        return byteArrayOf(
            STX,                                        // STX (1byte)
            ZERO, ZERO, groupNumPacket,                 // Group (3bytes)
            ZERO, ZERO, 0x31,                           // Node (Don't care) (3bytes)
            ZERO, colorCode10, colorCode1,              // Color code (3bytes)
            dimValue100, dimValue10, dimValue1,         // Dim value (3bytes)
            onOffPacket,                                // on/off (1byte)
            ETX                                         // ETX (1byte)
        )
    }

    private fun separateDigit(value: Int): IntArray {
        // 한 자리 수 일 경우
        val digit100: Int = (value / 100) % 10
        val digit10: Int = (value / 10) % 10
        val digit1: Int = value % 10

        return intArrayOf(digit100, digit10, digit1)
    }

    private fun convertToASCII(value: Int): Byte {
        val stringValue = value.toString()
        return stringValue.toCharArray()[0].toByte()
    }
}
