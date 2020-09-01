package com.hun.dimming.view_page

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGattCharacteristic
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.fragment.app.Fragment
import com.hun.dimming.BTGatt
import com.hun.dimming.PacketMessage
import com.hun.dimming.R
import com.hun.dimming.view.VerticalSeekBar
import kotlin.math.roundToInt

class FragmentGroup1 : Fragment() {

    private var colorCode: Int = 0

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layoutInflater: View = inflater.inflate(R.layout.fragment_group1, container, false)

        val packetMessage = PacketMessage()

        val textDimming: TextView = layoutInflater.findViewById(R.id.text_dimming_value)
        val textColor: TextView = layoutInflater.findViewById(R.id.text_color_value)
        val buttonOn: Button = layoutInflater.findViewById(R.id.button_on)
        val buttonOff: Button = layoutInflater.findViewById(R.id.button_off)
        val seekBarDimming: VerticalSeekBar = layoutInflater.findViewById(R.id.seekBar_dimming)
        val colorPickerContainer: LinearLayout = layoutInflater.findViewById(R.id.container_color_picker)

        val colorPicks: ArrayList<View> = ArrayList()
        val colorPickLocations: ArrayList<Int> = ArrayList()
        val colorPickCount: Int = colorPickerContainer.childCount

        for (i in 0 until colorPickCount) {
            colorPicks.add(colorPickerContainer.getChildAt(i))
        }

        colorPickerContainer.setOnTouchListener { view, motionEvent ->
            for (i in 0 until 24) {
                val location = IntArray(2)
                colorPicks[i].getLocationOnScreen(location)
                colorPickLocations.add(location[1] - 960)
            }

            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                }

                MotionEvent.ACTION_UP -> {
                    view.performClick()
                }

                MotionEvent.ACTION_MOVE -> {
                    if (colorPickLocations[0] < motionEvent.y && motionEvent.y <= colorPickLocations[1]) {
                        Log.d("Debug", "Red")
                        textColor.text = "Red"
                        colorCode = 0
                    }

                    if (colorPickLocations[1] < motionEvent.y && motionEvent.y <= colorPickLocations[2]) {
                        Log.d("Debug", "Vermilion")
                        textColor.text = "Vermilion"
                        colorCode = 1
                    }

                    if (colorPickLocations[2] < motionEvent.y && motionEvent.y <= colorPickLocations[3]) {
                        Log.d("Debug", "Orange")
                        textColor.text = "Orange"
                        colorCode = 2
                    }

                    if (colorPickLocations[3] < motionEvent.y && motionEvent.y <= colorPickLocations[4]) {
                        Log.d("Debug", "Amber")
                        textColor.text = "Amber"
                        colorCode = 3
                    }

                    if (colorPickLocations[4] < motionEvent.y && motionEvent.y <= colorPickLocations[5]) {
                        Log.d("Debug", "Yellow")
                        textColor.text = "Yellow"
                        colorCode = 4
                    }

                    if (colorPickLocations[5] < motionEvent.y && motionEvent.y <= colorPickLocations[6]) {
                        Log.d("Debug", "Lime Green")
                        textColor.text = "Lime Green"
                        colorCode = 5
                    }

                    if (colorPickLocations[6] < motionEvent.y && motionEvent.y <= colorPickLocations[7]) {
                        Log.d("Debug", "Chartreuse Green")
                        textColor.text = "Chartreuse Green"
                        colorCode = 6
                    }

                    if (colorPickLocations[7] < motionEvent.y && motionEvent.y <= colorPickLocations[8]) {
                        Log.d("Debug", "Green")
                        textColor.text = "Green"
                        colorCode = 7
                    }

                    if (colorPickLocations[8] < motionEvent.y && motionEvent.y <= colorPickLocations[9]) {
                        Log.d("Debug", "Turquoise")
                        textColor.text = "Turquoise"
                        colorCode = 8
                    }

                    if (colorPickLocations[9] < motionEvent.y && motionEvent.y <= colorPickLocations[10]) {
                        Log.d("Debug", "Cyan")
                        textColor.text = "Cyan"
                        colorCode = 9
                    }

                    if (colorPickLocations[10] < motionEvent.y && motionEvent.y <= colorPickLocations[11]) {
                        Log.d("Debug", "Cerulean")
                        textColor.text = "Cerulean"
                        colorCode = 10
                    }

                    if (colorPickLocations[11] < motionEvent.y && motionEvent.y <= colorPickLocations[12]) {
                        Log.d("Debug", "Azure")
                        textColor.text = "Azure"
                        colorCode = 11
                    }

                    if (colorPickLocations[12] < motionEvent.y && motionEvent.y <= colorPickLocations[13]) {
                        Log.d("Debug", "Sapphire Blue")
                        textColor.text = "Sapphire Blue"
                        colorCode = 12
                    }

                    if (colorPickLocations[13] < motionEvent.y && motionEvent.y <= colorPickLocations[14]) {
                        Log.d("Debug", "Blue")
                        textColor.text = "Blue"
                        colorCode = 13
                    }

                    if (colorPickLocations[14] < motionEvent.y && motionEvent.y <= colorPickLocations[15]) {
                        Log.d("Debug", "Indigo")
                        textColor.text = "Indigo"
                        colorCode = 14
                    }

                    if (colorPickLocations[15] < motionEvent.y && motionEvent.y <= colorPickLocations[16]) {
                        Log.d("Debug", "Violet")
                        textColor.text = "Violet"
                        colorCode = 15
                    }

                    if (colorPickLocations[16] < motionEvent.y && motionEvent.y <= colorPickLocations[17]) {
                        Log.d("Debug", "Mulberry")
                        textColor.text = "Mulberry"
                        colorCode = 16
                    }

                    if (colorPickLocations[17] < motionEvent.y && motionEvent.y <= colorPickLocations[18]) {
                        Log.d("Debug", "Magenta")
                        textColor.text = "Magenta"
                        colorCode = 17
                    }

                    if (colorPickLocations[18] < motionEvent.y && motionEvent.y <= colorPickLocations[19]) {
                        Log.d("Debug", "Fuchsia")
                        textColor.text = "Fuchsia"
                        colorCode = 18
                    }

                    if (colorPickLocations[19] < motionEvent.y && motionEvent.y <= colorPickLocations[20]) {
                        Log.d("Debug", "Rose")
                        textColor.text = "Rose"
                        colorCode = 19
                    }

                    if (colorPickLocations[20] < motionEvent.y && motionEvent.y <= colorPickLocations[21]) {
                        Log.d("Debug", "Crimson")
                        textColor.text = "Crimson"
                        colorCode = 20
                    }

                    if (colorPickLocations[21] < motionEvent.y && motionEvent.y <= colorPickLocations[22]) {
                        Log.d("Debug", "3000K")
                        textColor.text = "3000K"
                        colorCode = 21
                    }

                    if (colorPickLocations[22] < motionEvent.y && motionEvent.y <= colorPickLocations[23]) {
                        Log.d("Debug", "4500K")
                        textColor.text = "4500K"
                        colorCode = 22
                    }

                    if (colorPickLocations[23] < motionEvent.y && motionEvent.y <=
                        colorPickLocations[23] + (colorPickLocations[23] - colorPickLocations[22])
                    ) {
                        Log.d("Debug", "6000K")
                        textColor.text = "6000K"
                        colorCode = 23
                    }
                }
            }

            true
        }

        seekBarDimming.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, p1: Int, p2: Boolean) {
                val progress: Float = seekBar?.progress?.toFloat() ?: 0F
                val editDimmingValue: Int = progress.div(2.55F).roundToInt()
                textDimming.text = editDimmingValue.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })

        buttonOn.setOnClickListener {
            val packet: ByteArray = packetMessage.makePacketMessage(1, colorCode, seekBarDimming.progress, true)
            Log.d("Debug", "Packet: $packet")

            if (BTGatt.gatt != null) {
                BTGatt.characteristic?.value = packet
//                BTGatt.characteristic?.writeType = BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
                BTGatt.characteristic?.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                BTGatt.gatt?.writeCharacteristic(BTGatt.characteristic)
            } else {
                Toast.makeText(context, "블루투스 연결해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        buttonOff.setOnClickListener {
            val packet = packetMessage.makePacketMessage(1, colorCode, seekBarDimming.progress, false)
            Log.d("Debug", "Packet: $packet")

            if (BTGatt.gatt != null) {
                BTGatt.characteristic?.value = packet
                BTGatt.characteristic?.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                BTGatt.gatt?.writeCharacteristic(BTGatt.characteristic)
            } else {
                Toast.makeText(context, "블루투스 연결해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        return layoutInflater
    }
}
