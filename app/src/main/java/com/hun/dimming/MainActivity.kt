package com.hun.dimming

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGattCharacteristic
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.hun.dimming.view.MultiColorPicker
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.roundToInt
import kotlin.math.sqrt

class MainActivity : AppCompatActivity() {

    private var selectedColorCode: Int = 0
    private var dimmingValue: Int = 0
    private val packetMessage = PacketMessage()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val colorPicker: MultiColorPicker = findViewById(R.id.multi_color_picker)

        colorPicker.setOnTouchListener { view, motionEvent ->
            val x = motionEvent.x.toInt()
            val y = motionEvent.y.toInt()
            val cx = x - view.width / 2
            val cy = y - view.height / 2
            val d: Double = sqrt((cx * cx + cy * cy).toDouble())

            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (d <= colorPicker.outerWheelRadius) {
                        colorPicker.setColorPick(motionEvent.x.toInt(), motionEvent.y.toInt())

                        selectedColorCode = colorPicker.colorPick

                        if (selectedColorCode == 24) {
                            colorPicker.setOffButtonBgChange(true)
                        }
                    }
                }

                MotionEvent.ACTION_UP -> {
                    if (d <= colorPicker.outerWheelRadius) {
                        selectedColorCode = colorPicker.colorPick
                        Log.d("Debug", selectedColorCode.toString())

                        if (getColorTextValue(selectedColorCode).isNotEmpty()) {
                            text_color_value.text = getColorTextValue(selectedColorCode)
                        }

                        if (getColorDrawable(selectedColorCode) != null) {
                            image_color_value.setImageDrawable(getColorDrawable(selectedColorCode))
                        }

                        // clicked off button
                        if (selectedColorCode == 24) {
                            colorPicker.setOffButtonBgChange(false)

                            if (selectedColorCode < 0 || selectedColorCode > 24) {
                                Toast.makeText(applicationContext, "컬러를 선택하지 않았습니다", Toast.LENGTH_SHORT).show()
                            }

                            val packet = packetMessage.makePacketMessage(
                                1,
                                selectedColorCode,
                                seekBar_dimming.progress,
                                false
                            )

                            if (BTGatt.gatt != null) {
                                BTGatt.characteristic?.value = packet
                                BTGatt.characteristic?.writeType = BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
                                BTGatt.gatt?.writeCharacteristic(BTGatt.characteristic)
                            } else {
                                Toast.makeText(applicationContext, "블루투스 연결해주세요.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }

            true
        }

        seekBar_dimming.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, p1: Int, p2: Boolean) {
                val progress: Float = seekBar?.progress?.toFloat() ?: 0f
                dimmingValue = progress.div(2.55f).roundToInt()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }
        })

        button_send.setOnClickListener {
            if (selectedColorCode < 0 || selectedColorCode > 24) {
                Toast.makeText(applicationContext, "컬러를 선택하지 않았습니다", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val packet = packetMessage.makePacketMessage(1, selectedColorCode, seekBar_dimming.progress, true)

            if (writePacket(packet) == false || writePacket(packet) == null) {
                Toast.makeText(applicationContext, "패킷 전송 실패", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(applicationContext, "패킷 전송 성공", Toast.LENGTH_SHORT).show()
            }

//            if (BTGatt.gatt != null) {
//                BTGatt.characteristic?.value = packet
//                BTGatt.characteristic?.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
//                BTGatt.gatt?.writeCharacteristic(BTGatt.characteristic)
//            } else {
//                Toast.makeText(applicationContext, "블루투스 연결해주세요.", Toast.LENGTH_SHORT).show()
//            }
        }
    }

    private fun getColorTextValue(code: Int): String {
        when (code) {
            Constant.COLOR_CODE_RED -> {
                return "Red"
            }

            Constant.COLOR_CODE_VERMILION -> {
                return "Vermilion"
            }

            Constant.COLOR_CODE_ORANGE -> {
                return "Orange"
            }

            Constant.COLOR_CODE_AMBER -> {
                return "Amber"
            }

            Constant.COLOR_CODE_YELLOW -> {
                return "Yellow"
            }

            Constant.COLOR_CODE_LIME_GREEN -> {
                return "Lime green"
            }

            Constant.COLOR_CODE_CHARTREUSE_GREEN -> {
                return "Chartreuse green"
            }

            Constant.COLOR_CODE_GREEN -> {
                return "Green"
            }

            Constant.COLOR_CODE_TURQUOISE -> {
                return "Turquoise"
            }

            Constant.COLOR_CODE_CYAN -> {
                return "Cyan"
            }

            Constant.COLOR_CODE_CERULEAN -> {
                return "Cerulean"
            }

            Constant.COLOR_CODE_AZURE -> {
                return "Azure"
            }

            Constant.COLOR_CODE_SAPPHIRE_BLUE -> {
                return "Sapphire blue"
            }

            Constant.COLOR_CODE_BLUE -> {
                return "Blue"
            }

            Constant.COLOR_CODE_INDIGO -> {
                return "Indigo"
            }

            Constant.COLOR_CODE_VIOLET -> {
                return "Violet"
            }

            Constant.COLOR_CODE_MULBERRY -> {
                return "Mulberry"
            }

            Constant.COLOR_CODE_MAGENTA -> {
                return "Magenta"
            }

            Constant.COLOR_CODE_FUCHSIA -> {
                return "Fuchsia"
            }

            Constant.COLOR_CODE_ROSE -> {
                return "Rose"
            }

            Constant.COLOR_CODE_CRIMSON -> {
                return "Crimson"
            }

            Constant.COLOR_CODE_3000K -> {
                return "3000K"
            }

            Constant.COLOR_CODE_4500K -> {
                return "4500K"
            }

            Constant.COLOR_CODE_6000K -> {
                return "6000K"
            }

            Constant.COLOR_CODE_BLACK -> {
                return "Black"
            }

            else -> {
                return ""
            }
        }
    }

    private fun getColorDrawable(code: Int): Drawable? {
        when (code) {
            Constant.COLOR_CODE_RED -> {
                return getDrawable(R.color.colorRed)
            }

            Constant.COLOR_CODE_VERMILION -> {
                return getDrawable(R.color.colorVermilion)
            }

            Constant.COLOR_CODE_ORANGE -> {
                return getDrawable(R.color.colorOrange)
            }

            Constant.COLOR_CODE_AMBER -> {
                return getDrawable(R.color.colorAmber)
            }

            Constant.COLOR_CODE_YELLOW -> {
                return getDrawable(R.color.colorYellow)
            }

            Constant.COLOR_CODE_LIME_GREEN -> {
                return getDrawable(R.color.colorLimeGreen)
            }

            Constant.COLOR_CODE_CHARTREUSE_GREEN -> {
                return getDrawable(R.color.colorChartreuseGreen)
            }

            Constant.COLOR_CODE_GREEN -> {
                return getDrawable(R.color.colorGreen)
            }

            Constant.COLOR_CODE_TURQUOISE -> {
                return getDrawable(R.color.colorTurquoise)
            }

            Constant.COLOR_CODE_CYAN -> {
                return getDrawable(R.color.colorCyan)
            }

            Constant.COLOR_CODE_CERULEAN -> {
                return getDrawable(R.color.colorCerulean)
            }

            Constant.COLOR_CODE_AZURE -> {
                return getDrawable(R.color.colorAzure)
            }

            Constant.COLOR_CODE_SAPPHIRE_BLUE -> {
                return getDrawable(R.color.colorSapphireBlue)
            }

            Constant.COLOR_CODE_BLUE -> {
                return getDrawable(R.color.colorBlue)
            }

            Constant.COLOR_CODE_INDIGO -> {
                return getDrawable(R.color.colorIndigo)
            }

            Constant.COLOR_CODE_VIOLET -> {
                return getDrawable(R.color.colorViolet)
            }

            Constant.COLOR_CODE_MULBERRY -> {
                return getDrawable(R.color.colorMulberry)
            }

            Constant.COLOR_CODE_MAGENTA -> {
                return getDrawable(R.color.colorMagenta)
            }

            Constant.COLOR_CODE_FUCHSIA -> {
                return getDrawable(R.color.colorFuchsia)
            }

            Constant.COLOR_CODE_ROSE -> {
                return getDrawable(R.color.colorRose)
            }

            Constant.COLOR_CODE_CRIMSON -> {
                return getDrawable(R.color.colorCrimson)
            }

            Constant.COLOR_CODE_3000K -> {
                return getDrawable(R.color.color3000K)
            }

            Constant.COLOR_CODE_4500K -> {
                return getDrawable(R.color.color4500K)
            }

            Constant.COLOR_CODE_6000K -> {
                return getDrawable(R.color.color6000K)
            }

            Constant.COLOR_CODE_BLACK -> {
                return getDrawable(R.color.colorBlack)
            }

            else -> {
                return null
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.appbar_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_bluetooth_setting -> {
                // Request code 넘겨주기
                val intent = Intent(this, BluetoothActivity::class.java)
                startActivity(intent)
                true
            }

            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun writePacket(packet: ByteArray): Boolean? {
        return if (BTGatt.gatt != null) {
            BTGatt.characteristic?.value = packet
            BTGatt.characteristic?.writeType = BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
            BTGatt.gatt?.writeCharacteristic(BTGatt.characteristic)
        } else {
            Toast.makeText(applicationContext, "블루투스 연결해주세요.", Toast.LENGTH_SHORT).show()
            false
        }
    }

    private fun createTabView(name: String): View {
        val view = LayoutInflater.from(applicationContext).inflate(R.layout.layout_tab, null)
        val textTab: TextView = view.findViewById(R.id.text_tab)
        textTab.text = name
        val layoutParams: ViewGroup.LayoutParams =
            ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        view.layoutParams = layoutParams
        return view
    }
}
