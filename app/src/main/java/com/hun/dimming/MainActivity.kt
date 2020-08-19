package com.hun.dimming

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.TextView
import com.google.android.material.tabs.TabLayout
import com.hun.dimming.adapter.ViewPagerAdapter
import com.hun.dimming.view.MultiColorPicker
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val colorPicker: MultiColorPicker = findViewById(R.id.multi_color_picker)
        colorPicker.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    colorPicker.setColorPick(motionEvent.x.toInt(), motionEvent.y.toInt())
                }

                MotionEvent.ACTION_UP -> {
                    val test = colorPicker.colorPick
                    Log.d("Debug", test.toString())
                }
            }

            true
        }

//        setSupportActionBar(toolbar_main)
//        supportActionBar?.setDisplayShowTitleEnabled(false)

//        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
//        view_pager.adapter = viewPagerAdapter
//        view_pager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout_main))
//
//        tabLayout_main.addTab(tabLayout_main.newTab().setCustomView(createTabView("Group 1")))
//        tabLayout_main.addTab(tabLayout_main.newTab().setCustomView(createTabView("Group 2")))
//        tabLayout_main.addTab(tabLayout_main.newTab().setCustomView(createTabView("Group 3")))
//        tabLayout_main.addTab(tabLayout_main.newTab().setCustomView(createTabView("Group 4")))
//        tabLayout_main.addTab(tabLayout_main.newTab().setCustomView(createTabView("Group 5")))
//
//        tabLayout_main.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
//            override fun onTabReselected(tab: TabLayout.Tab?) {
//
//            }
//
//            override fun onTabUnselected(tab: TabLayout.Tab?) {
//
//            }
//
//            override fun onTabSelected(tab: TabLayout.Tab?) {
//                view_pager.currentItem = tab?.position!!
//            }
//        })
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
