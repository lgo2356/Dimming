package com.hun.dimming.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.hun.dimming.Constant
import com.hun.dimming.view_page.*

class ViewPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    private val items: ArrayList<Fragment> = ArrayList()

    init {
        items.add(FragmentGroup1())
        items.add(FragmentGroup2())
        items.add(FragmentGroup3())
        items.add(FragmentGroup4())
        items.add(FragmentGroup5())
    }

    override fun getItem(position: Int): Fragment {
        return items[position]
    }

    override fun getCount(): Int {
        return Constant.PAGE_MAX_COUNT
    }
}
